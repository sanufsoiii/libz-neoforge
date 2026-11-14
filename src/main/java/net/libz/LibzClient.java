package net.libz;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.libz.api.InventoryTab;
import net.libz.init.ClientEventInit;
import net.libz.init.KeyInit;
import net.libz.network.LibzClientPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public class LibzClient implements ClientModInitializer {

    public static final List<InventoryTab> inventoryTabs = new ArrayList<InventoryTab>();
    public static final HashMap<Class<?>, List<InventoryTab>> otherTabs = new HashMap<Class<?>, List<InventoryTab>>();

    public static final HashMap<Class<?>, ResourceLocation> blockScreenTabTextures = new HashMap<Class<?>, ResourceLocation>();
    public static final HashMap<Class<?>, ItemStack> blockScreenTabItemStacks = new HashMap<Class<?>, ItemStack>();

    @Nullable
    public static BlockPos lastInteractedBlockPos = null;

    public static final ResourceLocation tabTexture = ResourceLocation.parse("libz:textures/gui/icons.png");

    @Override
    public void onInitializeClient() {
        LibzClientPacket.init();
        KeyInit.init();
        ClientEventInit.init();
    }

}
