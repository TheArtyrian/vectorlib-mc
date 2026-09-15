package net.vertisoft.vectorlib.agnostic.networking.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorDualPosS2CPacket;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorEntityEventS2CPacket;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorEventS2CPacket;
import net.vertisoft.vectorlib.agnostic.networking.netsync.VectorNetSync;
import net.vertisoft.vectorlib.agnostic.networking.netsync.VectorSyncable;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.NetSyncPayload;

public class VectorPayloads
{
    // Payloads
    public static final ResourceLocation VECTOR_NETSYNC = ResourceLocation.fromNamespaceAndPath(VectorLib.ID, "netsynchro");

    // Packets
    public static final PacketType<VectorEventS2CPacket> VECTOR_EVENTSYNC = doS2CPacket("vectorlib_event_synchro");
    public static final PacketType<VectorDualPosS2CPacket> VECTOR_DUALSYNC = doS2CPacket("vectorlib_dualpos_synchro");
    public static final PacketType<VectorEntityEventS2CPacket> VECTOR_ENTITYEVENT = doS2CPacket("vectorlib_entityevent_synchro");

    public static void netsync(NetSyncPayload payload, Level level)
    {
        String ERROR_CODE = "Ran into an error while handling a VectorNetSync packet, suppressing without changes";
        Entity target = level.getEntity(payload.id());

        try
        {
            if (!(target instanceof VectorSyncable synchro)) throw new IllegalArgumentException("The targeted entity does not inherit VectorSyncable!");

            VectorNetSync sync = synchro.getVectorLibNetsync();
            CompoundTag tag = payload.nbt();

            if (!sync.identityMatches(tag)) throw new IllegalArgumentException("The provided inbound NetSync NBT's ID does not match the existing one!");

            sync.getTag().merge(tag);
            synchro.vectorLibNetsyncPost();
        }
        catch (IllegalArgumentException excpr) { VectorLib.LOGGER.error(ERROR_CODE, excpr); }
    }

    private static <T extends Packet<ClientGamePacketListener>> PacketType<T> doS2CPacket(String id)
    {
        return new PacketType<>(PacketFlow.CLIENTBOUND, VectorLib.id(id));
    }

    public static void chainS2CPackets(ProtocolInfoBuilder<ClientGamePacketListener, RegistryFriendlyByteBuf> builder)
    {
        builder
                .addPacket(VectorPayloads.VECTOR_EVENTSYNC, VectorEventS2CPacket.CODEC)
                .addPacket(VectorPayloads.VECTOR_DUALSYNC, VectorDualPosS2CPacket.CODEC)
                .addPacket(VectorPayloads.VECTOR_ENTITYEVENT, VectorEntityEventS2CPacket.CODEC);
    }
}
