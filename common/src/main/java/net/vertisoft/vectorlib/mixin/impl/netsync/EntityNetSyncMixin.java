package net.vertisoft.vectorlib.mixin.impl.netsync;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.vertisoft.vectorlib.agnostic.networking.netsync.VectorSyncable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityNetSyncMixin
{
    @Inject(method = "startSeenByPlayer", at = @At("TAIL"))
    public void vectorLib$startTrackingNetsynchro(ServerPlayer player, CallbackInfo ci)
    {
        if (this instanceof VectorSyncable synchro)
        {
            synchro.vectorLibNetSyncUpdate(player);
        }
    }
}
