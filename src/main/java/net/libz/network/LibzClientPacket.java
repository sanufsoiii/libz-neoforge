package net.libz.network;

import java.util.Iterator;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.api.SyntaxError;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.ConfigManager;
import net.libz.access.MouseAccessor;
import net.libz.api.ConfigSync;
import net.libz.mixin.config.AutoConfigAccess;
import net.libz.network.packet.BlockScreenPacket;
import net.libz.network.packet.ConfigPacket;
import net.libz.network.packet.MousePacket;
import net.libz.util.ConfigHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("unchecked")
public class LibzClientPacket {

    // Fabric ClientPlayNetworking.registerGlobalReceiver -> handlers registered with the payload registrar in
    // LibzServerPacket (RegisterPayloadHandlersEvent covers both directions on NeoForge 21.1).
    public static void handleConfig(ConfigPacket payload, IPayloadContext context) {
        String configName = payload.configName();
        boolean gson = payload.gson();
        byte[] jsonBytes = payload.bytes();

        context.enqueueWork(() -> {
            JsonObject oldJsonNode = ConfigHelper.getConfigNode(configName, gson, false);
            JsonObject newJsonNode = ConfigHelper.readJsonTree(jsonBytes);

            if (oldJsonNode != null && newJsonNode != null) {
                for (String key : newJsonNode.keySet()) {
                    JsonElement newValue = newJsonNode.get(key);
                    oldJsonNode.put(key, newValue);
                }

                Jankson jankson = Jankson.builder().build();

                for (ConfigHolder<?> holder : AutoConfigAccess.getHolders().values()) {
                    if (((ConfigManager<?>) holder).getDefinition().name().equals(configName)) {
                        try {
                            String mergedJson = oldJsonNode.toJson();
                            ConfigData data = (ConfigData) jankson.fromJson(jankson.load(mergedJson), holder.getConfigClass());

                            ((ConfigManager<ConfigData>) holder).setConfig(data);

                            if (holder.getConfig() instanceof ConfigSync) {
                                ((ConfigSync) holder.getConfig()).updateConfig(data);
                            }

                        } catch (SyntaxError e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        });
    }

    @SuppressWarnings("resource")
    public static void handleMouse(MousePacket payload, IPayloadContext context) {
        int mouseX = payload.mouseX();
        int mouseY = payload.mouseY();
        context.enqueueWork(() -> {
            ((MouseAccessor) Minecraft.getInstance().mouseHandler).setMousePosition(mouseX, mouseY);
        });
    }

    public static void writeC2SBlockScreenPacket(BlockPos blockPos, int mouseX, int mouseY) {
        PacketDistributor.sendToServer(new BlockScreenPacket(blockPos, mouseX, mouseY));
    }
}
