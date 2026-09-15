package net.vertisoft.vectorlib.agnostic.networking.data.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorPayloads;
import net.vertisoft.vectorlib.mixin_intf.event.VectorClientPlay;

public class VectorEntityEventS2CPacket implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, VectorEntityEventS2CPacket> CODEC = Packet.codec(
            VectorEntityEventS2CPacket::write, VectorEntityEventS2CPacket::new
    );
    private final String modId;
    private final int eventType;
    private final int entityID;
    private final int data;

    public VectorEntityEventS2CPacket(String modId, int eventType, Entity entity, int data)
    {
        this.modId = modId;
        this.eventType = eventType;
        this.entityID = entity.getId();
        this.data = data;
    }

    private VectorEntityEventS2CPacket(FriendlyByteBuf buffer)
    {
        this.modId = buffer.readUtf();
        this.eventType = buffer.readInt();
        this.entityID = buffer.readInt();
        this.data = buffer.readInt();
    }

    private void write(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.modId);
        buffer.writeInt(this.eventType);
        buffer.writeInt(this.entityID);
        buffer.writeInt(this.data);
    }

    public PacketType<VectorEntityEventS2CPacket> type() {
        return VectorPayloads.VECTOR_ENTITYEVENT;
    }
    public void handle(ClientGamePacketListener clientGamePacketListener)
    {
        ((VectorClientPlay)clientGamePacketListener).vectorLib$handleEntityEvent(this);
    }

    public Entity getEntity(Level level) { return level.getEntity(this.entityID); }
    public int getData() { return this.data; }
    public int getEventType() { return this.eventType; }
    public String getModID() { return this.modId; }
}
