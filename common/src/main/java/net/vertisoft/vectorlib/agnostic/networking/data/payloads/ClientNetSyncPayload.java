package net.vertisoft.vectorlib.agnostic.networking.data.payloads;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorS2CPayloads;

public record ClientNetSyncPayload(int id, CompoundTag nbt) implements CustomPacketPayload
{
    public static final Type<ClientNetSyncPayload> ID = new Type<>(VectorS2CPayloads.VECTOR_NETSYNC);
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientNetSyncPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ClientNetSyncPayload::id,
            ByteBufCodecs.fromCodec(CompoundTag.CODEC),
            ClientNetSyncPayload::nbt,
            ClientNetSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return ID;
    }
}
