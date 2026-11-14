package net.libz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.libz.api.Tab;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> implements Tab {

    public InventoryScreenMixin(InventoryMenu screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text);
    }

}
