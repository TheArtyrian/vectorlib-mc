package net.vertisoft.vectorlib.agnostic.networking.data.payloads;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorS2CPayloads;

import java.util.UUID;

public record ClientClearCapePayload(UUID player) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientClearCapePayload> ID = new CustomPacketPayload.Type<>(VectorS2CPayloads.VECTOR_CLEAR_CAPE);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientClearCapePayload> CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeUUID(packet.player());
                    },
                    (buffer) -> new ClientClearCapePayload(buffer.readUUID()));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
