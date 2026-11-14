package net.libz;

import net.libz.init.*;
import net.libz.network.LibzServerPacket;
import net.libz.port.ServerSmoke;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(LibzMain.MOD_ID)
public class LibzMain {

    public static final String MOD_ID = "libz";

    public LibzMain(IEventBus modBus) {
        // Same order as the original onInitialize()
        ConfigInit.init();
        LibzServerPacket.init(modBus);
        EventInit.init(NeoForge.EVENT_BUS);

        // Port self-check for the dedicated server (runServerSmoke), off unless -Dlibz.port.serverSmoke=true.
        ServerSmoke.register(NeoForge.EVENT_BUS);
    }

}
