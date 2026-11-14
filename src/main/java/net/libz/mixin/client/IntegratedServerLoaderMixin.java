package net.libz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;

@Mixin(value = WorldOpenFlows.class, priority = 999)
public class IntegratedServerLoaderMixin {

    @ModifyVariable(method = "confirmWorldCreation", at = @At("HEAD"), ordinal = 0)
    private static boolean startMixin(boolean original) {
        return false;
    }

}
