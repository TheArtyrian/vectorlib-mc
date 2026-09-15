package net.vertisoft.vectorlib.mixin_intf.event;

import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorDualPosS2CPacket;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorEntityEventS2CPacket;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorEventS2CPacket;

public interface VectorClientPlay
{
    void vectorLib$handleGameEvent(VectorEventS2CPacket packet);
    void vectorLib$handleDualSync(VectorDualPosS2CPacket packet);
    void vectorLib$handleEntityEvent(VectorEntityEventS2CPacket packet);
}
