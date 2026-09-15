package net.vertisoft.vectorlib.mixin.impl.eventsync;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.PacketUtils;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorDualPosS2CPacket;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorEntityEventS2CPacket;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorEventS2CPacket;
import net.vertisoft.vectorlib.mixin_intf.event.VectorClientPlay;
import net.vertisoft.vectorlib.mixin_intf.event.VectorLevelAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPacketListener.class)
public class ClientPacketMixin extends ClientCommonHandlerMixin implements VectorClientPlay
{
    @Shadow private ClientLevel level;

    @Override
    public void vectorLib$handleGameEvent(VectorEventS2CPacket packet)
    {
        PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener)(Object)this, this.minecraft);
        if (packet.isGlobalEvent())
        {
            ((VectorLevelAccess)this.minecraft.level).vectorLib$fireGlobal(null, packet.getModID(), packet.getType(), packet.getPos(), packet.getData());
        }
        else
        {
            ((VectorLevelAccess)this.minecraft.level).vectorLib$fireEvent(null, packet.getModID(), packet.getType(), packet.getPos(), packet.getData());
        }
    }

    @Override
    public void vectorLib$handleDualSync(VectorDualPosS2CPacket packet)
    {
        PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener)(Object)this, this.minecraft);
        ((VectorLevelAccess)this.minecraft.level).vectorLib$fireDual(null, packet.getModID(), packet.getType(), packet.get1stPos(), packet.get2ndPos(), packet.getData());
    }

    @Override
    public void vectorLib$handleEntityEvent(VectorEntityEventS2CPacket packet)
    {
        PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener)(Object)this, this.minecraft);
        ((VectorLevelAccess)this.minecraft.level).vectorLib$fireEntity(null, packet.getModID(), packet.getEventType(), packet.getEntity(this.level), packet.getData());
    }
}
