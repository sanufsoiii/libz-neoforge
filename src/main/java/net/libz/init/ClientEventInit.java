package net.libz.init;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.libz.LibzClient;
import net.libz.api.BlockScreenTab;
import net.libz.api.InventoryTab;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;

@Environment(EnvType.CLIENT)
public class ClientEventInit {

    public static void init() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClientSide()) {
                LibzClient.lastInteractedBlockPos = hitResult.getBlockPos().immutable();
            }
            return InteractionResult.PASS;
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            BlockPos blockPos = LibzClient.lastInteractedBlockPos;
            List<InventoryTab> list = LibzClient.otherTabs.get(screen.getClass());
            if (list != null && blockPos != null && client.level != null) {
                for (InventoryTab tab : list) {
                    if (tab instanceof BlockScreenTab blockScreenTab) {
                        blockScreenTab.setBlock(blockPos, client.level.getBlockState(blockPos).getBlock());
                    }
                }
            }
        });
    }

}
