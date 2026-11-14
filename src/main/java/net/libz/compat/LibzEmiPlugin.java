package net.libz.compat;

import java.util.List;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.mixin.accessor.HandledScreenAccessor;
import net.libz.api.InventoryTab;
import net.libz.init.ConfigInit;
import net.libz.util.DrawTabHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

// Fabric entrypoint "emi" -> EMI NeoForge discovers plugins by this annotation.
@EmiEntrypoint
public class LibzEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addGenericExclusionArea((screen, consumer) -> {
            if (!ConfigInit.CONFIG.inventoryButton || !(screen instanceof AbstractContainerScreen<?>)) {
                return;
            }
            List<InventoryTab> list = DrawTabHelper.getTabList(screen);
            if (list == null) {
                return;
            }
            Minecraft client = Minecraft.getInstance();
            int shownTabs = 0;
            for (InventoryTab inventoryTab : list) {
                if (inventoryTab.shouldShow(client)) {
                    shownTabs++;
                }
            }
            if (shownTabs > 0) {
                int left = ((HandledScreenAccessor) screen).getX();
                int top = ((HandledScreenAccessor) screen).getY() - 23;
                consumer.accept(new Bounds(left, top, shownTabs * 25, 23));
            }
        });
    }
}
