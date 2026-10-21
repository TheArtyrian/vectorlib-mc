package net.vertisoft.vectorlib.agnostic.networking.data.payloads;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorS2CPayloads;

import java.util.UUID;

public record ClientSyncCapePayload(UUID player, String id) implements CustomPacketPayload
{
    public static final Type<ClientSyncCapePayload> ID = new Type<>(VectorS2CPayloads.VECTOR_SYNC_CAPE);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientSyncCapePayload> CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeUUID(packet.player());
                        buffer.writeUtf(packet.id(), 128);
                    },
                    (buffer) -> new ClientSyncCapePayload(buffer.readUUID(), buffer.readUtf()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
