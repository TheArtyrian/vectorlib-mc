package net.vertisoft.vectorlib.mixin.client;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.protocol.Packet;
import net.vertisoft.vectorlib.mixin_intf.network.MulPlayIntf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerSendMixin implements MulPlayIntf
{
    @Shadow @Final private ClientPacketListener connection;

    @Override
    public void vectorLib$sendPacketOnConnection(Packet<?> packet)
    {
        this.connection.send(packet);
    }
}
