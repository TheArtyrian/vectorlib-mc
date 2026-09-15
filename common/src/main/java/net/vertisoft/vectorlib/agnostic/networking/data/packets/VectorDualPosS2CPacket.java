package net.vertisoft.vectorlib.agnostic.networking.data.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.phys.Vec3;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorPayloads;
import net.vertisoft.vectorlib.mixin_intf.event.VectorClientPlay;

public class VectorDualPosS2CPacket implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, VectorDualPosS2CPacket> CODEC = Packet.codec(
            VectorDualPosS2CPacket::write, VectorDualPosS2CPacket::new
    );
    private final String modId;
    private final int type;
    private final Vec3 pos1;
    private final Vec3 pos2;
    private final int data;

    public VectorDualPosS2CPacket(String modId, int type, Vec3 pos1, Vec3 pos2, int data)
    {
        this.modId = modId;
        this.type = type;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.data = data;
    }

    private VectorDualPosS2CPacket(FriendlyByteBuf buf)
    {
        this.modId = buf.readUtf();
        this.type = buf.readInt();
        this.pos1 = buf.readVec3();
        this.pos2 = buf.readVec3();
        this.data = buf.readInt();
    }

    private void write(FriendlyByteBuf buf)
    {
        buf.writeUtf(this.modId);
        buf.writeInt(this.type);
        buf.writeVec3(this.pos1);
        buf.writeVec3(this.pos2);
        buf.writeInt(this.data);
    }

    @Override public PacketType<VectorDualPosS2CPacket> type() { return VectorPayloads.VECTOR_DUALSYNC; }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener)
    {
        ((VectorClientPlay)clientGamePacketListener).vectorLib$handleDualSync(this);
    }

    public int getType() {
        return this.type;
    }
    public String getModID() { return this.modId; }
    public int getData() {
        return this.data;
    }
    public Vec3 get1stPos() { return this.pos1; }
    public Vec3 get2ndPos() { return this.pos2; }
}
