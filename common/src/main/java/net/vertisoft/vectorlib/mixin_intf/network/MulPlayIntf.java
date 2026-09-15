package net.vertisoft.vectorlib.mixin_intf.network;

import net.minecraft.network.protocol.Packet;

public interface MulPlayIntf
{
    void vectorLib$sendPacketOnConnection(Packet<?> packet);
}
