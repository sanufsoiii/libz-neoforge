package net.libz.access;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface ServerPlayerInteractionManagerAccess {

    @Nullable
    public BlockPos getLastScreenBlockPos();

}
