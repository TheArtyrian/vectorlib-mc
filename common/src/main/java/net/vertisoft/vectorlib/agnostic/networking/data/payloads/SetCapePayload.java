package net.vertisoft.vectorlib.agnostic.networking.data.payloads;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorC2SPayloads;

public record SetCapePayload(String id) implements CustomPacketPayload
{
    public static final Type<SetCapePayload> ID = new Type<>(VectorC2SPayloads.VECTOR_SET_CAPE);

    public static final StreamCodec<RegistryFriendlyByteBuf, SetCapePayload> CODEC = StreamCodec.of(
            (buffer, packet) -> buffer.writeUtf(packet.id(), 128),
            (buffer) -> new SetCapePayload(buffer.readUtf()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
