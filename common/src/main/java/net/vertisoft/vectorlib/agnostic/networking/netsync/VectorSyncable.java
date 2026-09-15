package net.vertisoft.vectorlib.agnostic.networking.netsync;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

/** Any entity you plan on modifying with this MUST implement this class and provide the created netsyncer. */
public interface VectorSyncable
{
    VectorNetSync getVectorLibNetsync();

    default void vectorLibNetsyncPost()
    {

    }

    default void vectorLibNetSyncUpdate(ServerPlayer player)
    {
        this.getVectorLibNetsync().sendToPlayer(player);
    }
}
