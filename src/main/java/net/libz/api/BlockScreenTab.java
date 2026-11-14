package net.libz.api;

import org.jetbrains.annotations.Nullable;

import net.libz.LibzClient;
import net.libz.network.LibzClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockScreenTab extends InventoryTab {

    private final Class<?> parentClass;
    @Nullable
    private BlockPos blockPos;
    @Nullable
    private Block block;
    @Nullable
    private ItemStack itemStack;

    public BlockScreenTab(Class<?> parentClass) {
        super(Component.empty(), null, Integer.MIN_VALUE, parentClass);
        this.parentClass = parentClass;
    }

    public void setBlock(BlockPos blockPos, Block block) {
        this.blockPos = blockPos.immutable();
        this.block = block;
        this.itemStack = new ItemStack(block);
    }

    @Override
    public Component getTitle() {
        return this.block != null ? this.block.getName() : super.getTitle();
    }

    @Nullable
    @Override
    public ResourceLocation getTexture() {
        return LibzClient.blockScreenTabTextures.get(this.parentClass);
    }

    @Nullable
    @Override
    public ItemStack getItemStack(Minecraft client) {
        ItemStack customItemStack = LibzClient.blockScreenTabItemStacks.get(this.parentClass);
        return customItemStack != null ? customItemStack : this.itemStack;
    }

    @Override
    public boolean shouldShow(Minecraft client) {
        return this.blockPos != null && this.block != null && this.blockPos.equals(LibzClient.lastInteractedBlockPos) && client.level != null && client.player != null
                && client.level.getBlockState(this.blockPos).is(this.block) && client.player.canInteractWithBlock(this.blockPos, 1.0D);
    }

    @Override
    public void onClick(Minecraft client) {
        super.onClick(client);
        LibzClientPacket.writeC2SBlockScreenPacket(this.blockPos, (int) client.mouseHandler.xpos(), (int) client.mouseHandler.ypos());
    }

}
