package net.vertisoft.vectorlib.agnostic.networking.data.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorS2CPayloads;
import net.vertisoft.vectorlib.mixin_intf.event.VectorClientPlay;

public class VectorBombS2CPacket implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, VectorBombS2CPacket> CODEC = Packet.codec(
            (packet, buffer) -> {}, (buf) -> new VectorBombS2CPacket()
    );

    @Override public PacketType<VectorBombS2CPacket> type() { return VectorS2CPayloads.VECTOR_COMMANDBOMB; }

    public VectorBombS2CPacket() {}
    private void write(FriendlyByteBuf buf) {}

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener)
    {
        ((VectorClientPlay)clientGamePacketListener).vectorLib$handleCommandBomb(this);
    }
}