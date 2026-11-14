package net.libz.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.libz.access.ServerPlayerInteractionManagerAccess;
import net.libz.network.packet.BlockScreenPacket;
import net.libz.network.packet.ConfigPacket;
import net.libz.network.packet.MousePacket;
import net.libz.util.ConfigHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public class LibzServerPacket {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(ConfigPacket.PACKET_ID, ConfigPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(MousePacket.PACKET_ID, MousePacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(BlockScreenPacket.PACKET_ID, BlockScreenPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(BlockScreenPacket.PACKET_ID, (payload, context) -> {
            ServerPlayer player = context.player();
            BlockPos blockPos = payload.blockPos();
            int mouseX = payload.mouseX();
            int mouseY = payload.mouseY();
            player.getServer().execute(() -> {
                if (!blockPos.equals(((ServerPlayerInteractionManagerAccess) player.gameMode).getLastScreenBlockPos())) {
                    return;
                }
                ServerLevel world = player.serverLevel();
                if (!player.canInteractWithBlock(blockPos, 1.0D) || !world.mayInteract(player, blockPos)) {
                    return;
                }
                MenuProvider factory = world.getBlockState(blockPos).getMenuProvider(world, blockPos);
                if (factory != null && player.openMenu(factory).isPresent()) {
                    writeS2CMousePositionPacket(player, mouseX, mouseY);
                }
            });
        });
    }

    public static void writeS2CConfigPacket(ServerPlayer serverPlayerEntity, String configName, boolean gson) {
        byte[] bytes = ConfigHelper.getConfigBytes(configName, gson, true);
        if (bytes != null) {
            ServerPlayNetworking.send(serverPlayerEntity, new ConfigPacket(configName, gson, bytes));
        }
    }

    public static void writeS2CMousePositionPacket(ServerPlayer serverPlayerEntity, int mouseX, int mouseY) {
        ServerPlayNetworking.send(serverPlayerEntity, new MousePacket(mouseX, mouseY));
    }

}
