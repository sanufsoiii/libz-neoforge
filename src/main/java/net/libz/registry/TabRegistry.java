package net.libz.registry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.libz.LibzClient;
import net.libz.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Class to register tabs for screens. Use only on the client.
 *
 * @version 1.0
 */
@Environment(EnvType.CLIENT)
public class TabRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger("LibZ");

    /**
     * Registers a player inventory tab.
     *
     * @param tab An instance of an InventoryTab class.
     */
    public static void registerInventoryTab(InventoryTab tab) {
        if (isDuplicate(LibzClient.inventoryTabs, tab)) {
            return;
        }
        LibzClient.inventoryTabs.add(tab);
        sortTabs(LibzClient.inventoryTabs);
    }

    /**
     * Registers a tab for all screens.
     *
     * <p>
     * Handled screens do not need to call DrawTabHelper.drawTab on the render method and DrawTabHelper.onTabButtonClick on the mouseClicked method. Client screens need to call DrawTabHelper.drawTab
     * on the render method and DrawTabHelper.onTabButtonClick on the mouseClicked method.
     *
     * @param tab         An instance of an InventoryTab class.
     * @param parentClass The parent class of the screen class where the tab will get added.
     */
    public static void registerOtherTab(InventoryTab tab, Class<?> parentClass) {
        List<InventoryTab> list = LibzClient.otherTabs.computeIfAbsent(parentClass, key -> new ArrayList<InventoryTab>());
        if (isDuplicate(list, tab)) {
            return;
        }
        list.add(tab);
        sortTabs(list);
    }

    public static void registerBlockScreenTab(InventoryTab tab, Class<?> parentClass) {
        List<InventoryTab> list = LibzClient.otherTabs.computeIfAbsent(parentClass, key -> new ArrayList<InventoryTab>());
        if (list.stream().noneMatch(BlockScreenTab.class::isInstance)) {
            list.removeIf(existingTab -> existingTab.isSelectedScreen(parentClass));
            list.add(new BlockScreenTab(parentClass));
            sortTabs(list);
        }
        registerOtherTab(tab, parentClass);
    }

    public static void setBlockScreenTabTexture(Class<?> parentClass, ResourceLocation texture) {
        if (hasBlockScreenTabIcon(parentClass, texture.toString())) {
            return;
        }
        LibzClient.blockScreenTabTextures.put(parentClass, texture);
    }

    public static void setBlockScreenTabItemStack(Class<?> parentClass, ItemStack itemStack) {
        if (hasBlockScreenTabIcon(parentClass, itemStack.toString())) {
            return;
        }
        LibzClient.blockScreenTabItemStacks.put(parentClass, itemStack.copy());
    }

    private static boolean hasBlockScreenTabIcon(Class<?> parentClass, String rejectedIcon) {
        Object existingIcon = LibzClient.blockScreenTabTextures.containsKey(parentClass) ? LibzClient.blockScreenTabTextures.get(parentClass) : LibzClient.blockScreenTabItemStacks.get(parentClass);
        if (existingIcon != null) {
            LOGGER.warn("Block screen tab icon for {} is already set to {}, ignoring {}", parentClass.getSimpleName(), existingIcon, rejectedIcon);
            return true;
        }
        return false;
    }

    private static boolean isDuplicate(List<InventoryTab> list, InventoryTab tab) {
        for (InventoryTab existingTab : list) {
            for (Class<?> screenClass : tab.getScreenClasses()) {
                if (existingTab.isSelectedScreen(screenClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void sortTabs(List<InventoryTab> list) {
        list.sort(Comparator.comparingInt(tab -> tab.getPreferedPos() == -1 ? 99 : tab.getPreferedPos()));
    }
}
