package net.vertisoft.vectorlib.agnostic.networking.data.payloads;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorPayloads;

public record NetSyncPayload(int id, CompoundTag nbt) implements CustomPacketPayload
{
    public static final Type<NetSyncPayload> ID = new Type<>(VectorPayloads.VECTOR_NETSYNC);
    public static final StreamCodec<RegistryFriendlyByteBuf, NetSyncPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            NetSyncPayload::id,
            ByteBufCodecs.fromCodec(CompoundTag.CODEC),
            NetSyncPayload::nbt,
            NetSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return ID;
    }
}
