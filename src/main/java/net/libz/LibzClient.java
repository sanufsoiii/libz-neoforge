package net.libz;

import me.shedaniel.autoconfig.AutoConfig;
import net.libz.api.InventoryTab;
import net.libz.config.LibzConfig;
import net.libz.init.ClientEventInit;
import net.libz.init.KeyInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mod(value = LibzMain.MOD_ID, dist = Dist.CLIENT)
public class LibzClient {

    private static final Logger LOGGER = LoggerFactory.getLogger("libz");
    private static boolean titleReached = false;

    public static final List<InventoryTab> inventoryTabs = new ArrayList<InventoryTab>();
    public static final HashMap<Class<?>, List<InventoryTab>> otherTabs = new HashMap<Class<?>, List<InventoryTab>>();

    public static final HashMap<Class<?>, ResourceLocation> blockScreenTabTextures = new HashMap<Class<?>, ResourceLocation>();
    public static final HashMap<Class<?>, ItemStack> blockScreenTabItemStacks = new HashMap<Class<?>, ItemStack>();

    @Nullable
    public static BlockPos lastInteractedBlockPos = null;

    public static final ResourceLocation tabTexture = ResourceLocation.parse("libz:textures/gui/icons.png");

    public LibzClient(IEventBus modBus, ModContainer container) {
        // ModMenu integration of the original -> NeoForge config screen extension point.
        container.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> AutoConfig.getConfigScreen(LibzConfig.class, parent).get());

        // Same order as the original onInitializeClient(). LibzClientPacket.init() of the original registered the
        // client-bound receivers; on NeoForge they are registered together with the server ones in LibzServerPacket.
        KeyInit.init();
        ClientEventInit.init(NeoForge.EVENT_BUS);

        // Port self-check: log when the title screen is reached, optionally exit (runClientSmoke).
        NeoForge.EVENT_BUS.addListener(ScreenEvent.Opening.class, LibzClient::onScreenOpening);
    }

    private static void onScreenOpening(ScreenEvent.Opening event) {
        if (titleReached || !(event.getNewScreen() instanceof TitleScreen)) {
            return;
        }
        titleReached = true;
        LOGGER.info("[libz-port] TITLE_SCREEN_REACHED");
        if (Boolean.getBoolean("libz.port.exitOnTitle")) {
            Thread exitThread = new Thread(() -> {
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException ignored) {
                }
                Minecraft.getInstance().execute(() -> Minecraft.getInstance().stop());
            }, "libz-port-smoke-exit");
            exitThread.setDaemon(true);
            exitThread.start();
        }
    }

}
