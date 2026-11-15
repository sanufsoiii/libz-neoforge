package net.libz.port;

import java.util.List;
import org.slf4j.Logger;

/**
 * Loads every class targeted by a mixin of this mod, so that the mixin transformer has to apply the mixin for real.
 * {@code MixinEnvironment.audit()} only checks mixins of classes that are already loaded.
 * <p>
 * Only used by the smoke self-checks ({@code -Dlibz.port.serverSmoke=true} / {@code -Dlibz.port.exitOnTitle=true}).
 */
public final class MixinTargetLoader {

    private MixinTargetLoader() {
    }

    public static int load(Logger logger, List<String> targets) {
        int failures = 0;
        ClassLoader loader = MixinTargetLoader.class.getClassLoader();
        for (String target : targets) {
            try {
                // initialize=false: load and transform the class, but do not run its static initializers.
                Class.forName(target, false, loader);
            } catch (Throwable throwable) {
                failures++;
                logger.error("[libz-port] Could not load mixin target {}", target, throwable);
            }
        }
        return failures;
    }

    /** Targets of the mixins in the "mixins" section of libz.mixins.json. */
    public static final List<String> COMMON = List.of(
            "me.shedaniel.autoconfig.AutoConfig",
            "me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.impl.MarshallerImpl",
            "net.minecraft.server.level.ServerPlayerGameMode"
    );

    /** Targets of the mixins in the "client" section of libz.mixins.json (client runs only). */
    public static final List<String> CLIENT = List.of(
            "net.minecraft.client.gui.screens.inventory.AbstractContainerScreen",
            "net.minecraft.client.gui.screens.inventory.InventoryScreen",
            "net.minecraft.client.gui.screens.worldselection.WorldOpenFlows",
            "net.minecraft.client.MouseHandler"
    );
}
