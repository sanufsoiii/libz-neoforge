package net.libz.network;

import net.libz.access.ServerPlayerInteractionManagerAccess;
import net.libz.network.packet.BlockScreenPacket;
import net.libz.network.packet.ConfigPacket;
import net.libz.network.packet.MousePacket;
import net.libz.util.ConfigHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class LibzServerPacket {

    public static void init(IEventBus modBus) {
        modBus.addListener(RegisterPayloadHandlersEvent.class, LibzServerPacket::register);
    }

    private static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // Client-bound handlers live in LibzClientPacket (client only). Lambdas with a plain static call, not method
        // references: the call is resolved lazily, so a dedicated server never loads LibzClientPacket.
        registrar.playToClient(ConfigPacket.PACKET_ID, ConfigPacket.PACKET_CODEC, (payload, context) -> LibzClientPacket.handleConfig(payload, context));
        registrar.playToClient(MousePacket.PACKET_ID, MousePacket.PACKET_CODEC, (payload, context) -> LibzClientPacket.handleMouse(payload, context));

        registrar.playToServer(BlockScreenPacket.PACKET_ID, BlockScreenPacket.PACKET_CODEC, (payload, context) -> {
            ServerPlayer player = (ServerPlayer) context.player();
            BlockPos blockPos = payload.blockPos();
            int mouseX = payload.mouseX();
            int mouseY = payload.mouseY();
            player.getServer().execute(() -> {
                if (!blockPos.equals(((ServerPlayerInteractionManagerAccess) player.gameMode).getLastScreenBlockPos())) {
                    return;
                }
                ServerLevel world = player.serverLevel();
                if (!player.canInteractWithBlock(blockPos, 1.0D) || !world.mayInteract(player, blockPos)) {
                    return;
                }
                MenuProvider factory = world.getBlockState(blockPos).getMenuProvider(world, blockPos);
                if (factory != null && player.openMenu(factory).isPresent()) {
                    writeS2CMousePositionPacket(player, mouseX, mouseY);
                }
            });
        });
    }

    public static void writeS2CConfigPacket(ServerPlayer serverPlayerEntity, String configName, boolean gson) {
        byte[] bytes = ConfigHelper.getConfigBytes(configName, gson, true);
        if (bytes != null) {
            PacketDistributor.sendToPlayer(serverPlayerEntity, new ConfigPacket(configName, gson, bytes));
        }
    }

    public static void writeS2CMousePositionPacket(ServerPlayer serverPlayerEntity, int mouseX, int mouseY) {
        PacketDistributor.sendToPlayer(serverPlayerEntity, new MousePacket(mouseX, mouseY));
    }

}
