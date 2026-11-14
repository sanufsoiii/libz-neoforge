package net.libz.init;

import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.libz.api.ConfigSync;
import net.libz.mixin.config.AutoConfigAccess;
import net.libz.network.LibzServerPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class EventInit {

    public static void init(IEventBus gameBus) {
        // Fabric ServerPlayConnectionEvents.JOIN -> PlayerLoggedInEvent (end of PlayerList.placeNewPlayer).
        gameBus.addListener(PlayerEvent.PlayerLoggedInEvent.class, event -> {
            if (!(event.getEntity() instanceof ServerPlayer player)) {
                return;
            }
            MinecraftServer server = player.getServer();
            // check local
            if (ConfigInit.CONFIG.syncConfig) {
                if (server.isDedicatedServer()) {
                    AutoConfigAccess.getHolders().forEach((data, holder) -> {
                        if (holder.getConfig() instanceof ConfigSync) {
                            ConfigSerializer<?> configSerializer = ((ConfigManager<?>) holder).getSerializer();

                            if (configSerializer instanceof GsonConfigSerializer || configSerializer instanceof JanksonConfigSerializer) {
                                LibzServerPacket.writeS2CConfigPacket(player, ((ConfigManager<?>) holder).getDefinition().name(), configSerializer instanceof GsonConfigSerializer);
                            }
                        }
                    });
                } else {
                    AutoConfigAccess.getHolders().forEach((data, holder) -> {
                        if (holder.getConfig() instanceof ConfigSync) {
                            holder.load();
                            ((ConfigSync) holder.getConfig()).updateConfig(holder.getConfig());
                        }
                    });
                }
            }
        });
    }

}
