package net.libz.port;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.MixinEnvironment;

import net.libz.LibzMain;
import net.libz.init.ConfigInit;
import net.libz.mixin.config.AutoConfigAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Dedicated-server self-check of the port (run configuration {@code serverSmoke}). Active only with
 * {@code -Dlibz.port.serverSmoke=true}, does nothing otherwise.
 * <p>
 * On {@link ServerStartedEvent}: forces every common mixin of the pack to apply ({@code MixinEnvironment.audit()}), spawns one
 * instance of every {@link EntityType} of the mod at the world spawn (LibZ registers none), lets the server tick {@value #TICKS}
 * times, logs {@code [libz-port] SERVER_SMOKE_OK entities=<N>} and stops the server.
 */
public final class ServerSmoke {

    public static final String PROPERTY = "libz.port.serverSmoke";
    private static final Logger LOGGER = LoggerFactory.getLogger("libz");
    private static final int TICKS = 100;

    private static int ticksLeft = -1;
    private static int spawned = 0;
    private static int entityTypes = 0;

    private ServerSmoke() {
    }

    public static void register(IEventBus gameBus) {
        if (!Boolean.getBoolean(PROPERTY)) {
            return;
        }
        gameBus.addListener(ServerStartedEvent.class, ServerSmoke::onServerStarted);
        gameBus.addListener(ServerTickEvent.Post.class, ServerSmoke::onServerTick);
    }

    private static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        ServerLevel level = server.overworld();
        BlockPos spawn = level.getSharedSpawnPos();
        LOGGER.info("[libz-port] SERVER_SMOKE_START dedicated={} spawn={}", server.isDedicatedServer(), spawn.toShortString());

        // Apply every pending mixin now, so a broken target fails here and not when some class is first used in game.
        MixinEnvironment.getCurrentEnvironment().audit();
        LOGGER.info("[libz-port] SERVER_SMOKE mixin audit done");

        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            if (!LibzMain.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace())) {
                continue;
            }
            entityTypes++;
            Entity entity = type.spawn(level, spawn.above(), MobSpawnType.COMMAND);
            if (entity != null) {
                spawned++;
            } else {
                LOGGER.error("[libz-port] SERVER_SMOKE could not spawn {}", BuiltInRegistries.ENTITY_TYPE.getKey(type));
            }
        }

        LOGGER.info("[libz-port] SERVER_SMOKE config: syncConfig={} inventoryButton={} config_holders={}", ConfigInit.CONFIG.syncConfig,
                ConfigInit.CONFIG.inventoryButton, AutoConfigAccess.getHolders().size());
        ticksLeft = TICKS;
    }

    private static void onServerTick(ServerTickEvent.Post event) {
        if (ticksLeft < 0) {
            return;
        }
        if (ticksLeft-- > 0) {
            return;
        }
        if (spawned != entityTypes) {
            LOGGER.error("[libz-port] SERVER_SMOKE_FAILED entity types={} spawned={}", entityTypes, spawned);
        } else {
            LOGGER.info("[libz-port] SERVER_SMOKE_OK entities={} ticks={}", spawned, TICKS);
        }
        event.getServer().halt(false);
    }

}
