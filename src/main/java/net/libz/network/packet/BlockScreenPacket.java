package net.libz.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BlockScreenPacket(BlockPos blockPos, int mouseX, int mouseY) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BlockScreenPacket> PACKET_ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("libz", "block_screen_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockScreenPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeBlockPos(value.blockPos);
        buf.writeInt(value.mouseX);
        buf.writeInt(value.mouseY);
    }, buf -> new BlockScreenPacket(buf.readBlockPos(), buf.readInt(), buf.readInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
