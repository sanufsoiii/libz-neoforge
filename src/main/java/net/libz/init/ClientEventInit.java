package net.libz.init;

import java.util.List;

import net.libz.LibzClient;
import net.libz.api.BlockScreenTab;
import net.libz.api.InventoryTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class ClientEventInit {

    public static void init(IEventBus gameBus) {
        // Fabric UseBlockCallback (client side: MultiPlayerGameMode.useItemOn before the packet is sent).
        // Only remembers the position and never cancels, like the original returning PASS.
        gameBus.addListener(PlayerInteractEvent.RightClickBlock.class, event -> {
            if (event.getLevel().isClientSide()) {
                LibzClient.lastInteractedBlockPos = event.getPos().immutable();
            }
        });

        // Fabric ScreenEvents.AFTER_INIT (tail of Screen.init and Screen.resize) -> ScreenEvent.Init.Post.
        gameBus.addListener(ScreenEvent.Init.Post.class, event -> {
            Minecraft client = Minecraft.getInstance();
            Screen screen = event.getScreen();
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
