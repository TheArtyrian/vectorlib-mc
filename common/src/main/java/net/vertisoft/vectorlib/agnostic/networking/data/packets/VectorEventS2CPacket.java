package net.vertisoft.vectorlib.agnostic.networking.data.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorPayloads;
import net.vertisoft.vectorlib.mixin_intf.event.VectorClientPlay;

public class VectorEventS2CPacket implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, VectorEventS2CPacket> CODEC = Packet.codec(
            VectorEventS2CPacket::write, VectorEventS2CPacket::new
    );
    private final String modId;
    private final int type;
    private final BlockPos pos;
    private final int data;
    private final boolean globalEvent;

    public VectorEventS2CPacket(String modId, int type, BlockPos pos, int data, boolean globalEvent)
    {
        this.modId = modId;
        this.type = type;
        this.pos = pos;
        this.data = data;
        this.globalEvent = globalEvent;
    }

    private VectorEventS2CPacket(FriendlyByteBuf buf)
    {
        this.modId = buf.readUtf();
        this.type = buf.readInt();
        this.pos = buf.readBlockPos();
        this.data = buf.readInt();
        this.globalEvent = buf.readBoolean();
    }

    private void write(FriendlyByteBuf buf)
    {
        buf.writeUtf(this.modId);
        buf.writeInt(this.type);
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.data);
        buf.writeBoolean(this.globalEvent);
    }

    @Override public PacketType<VectorEventS2CPacket> type() { return VectorPayloads.VECTOR_EVENTSYNC; }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener)
    {
        ((VectorClientPlay)clientGamePacketListener).vectorLib$handleGameEvent(this);
    }

    public boolean isGlobalEvent() {
        return this.globalEvent;
    }
    public String getModID() {
        return this.modId;
    }
    public int getType() { return this.type; }
    public int getData() {
        return this.data;
    }
    public BlockPos getPos() {
        return this.pos;
    }
}
