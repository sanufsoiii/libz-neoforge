package net.libz.mixin.server;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.libz.access.ServerPlayerInteractionManagerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerInteractionManagerMixin implements ServerPlayerInteractionManagerAccess {

    @Unique
    @Nullable
    private BlockPos lastScreenBlockPos = null;

    @Inject(method = "useItemOn", at = @At("RETURN"))
    private void interactBlockMixin(ServerPlayer player, Level world, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> info) {
        if (info.getReturnValue().consumesAction() && player.containerMenu != player.inventoryMenu) {
            this.lastScreenBlockPos = hitResult.getBlockPos().immutable();
        }
    }

    @Nullable
    @Override
    public BlockPos getLastScreenBlockPos() {
        return this.lastScreenBlockPos;
    }

}
