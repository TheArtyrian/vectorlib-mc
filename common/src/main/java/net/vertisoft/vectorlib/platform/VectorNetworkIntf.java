package net.vertisoft.vectorlib.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkSource;
import net.vertisoft.vectorlib.mixin.acc.ChunkMapIntf;
import net.vertisoft.vectorlib.mixin.acc.ChunkTrackIntf;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public interface VectorNetworkIntf
{
    default void sendToAllInChunk(ServerLevel level, BlockPos pos, CustomPacketPayload payload)
    {
        for (ServerPlayer player : getAllTrackingChunk(level, pos,false))
        {
            sendToPlayer(player, payload);
        }
    }

    default void sendToAllTrackingEntity(Entity entity, CustomPacketPayload payload)
    {
        for (ServerPlayer player : getAllTrackingEntity(entity))
        {
            sendToPlayer(player, payload);
        }
    }

    default void sendToEveryone(ServerLevel level, CustomPacketPayload payload)
    {
        for (ServerPlayer player : level.players())
        {
            sendToPlayer(player, payload);
        }
    }

    default List<ServerPlayer> getAllTrackingEntity(Entity entity)
    {
        Objects.requireNonNull(entity, "[VectorLib] Entity was null or not provided!");

        ChunkSource manager = entity.level().getChunkSource();
        if (manager instanceof ServerChunkCache)
        {
            ChunkMap chunkLoadingManager = ((ServerChunkCache)manager).chunkMap;
            ChunkTrackIntf chunktrack = (ChunkTrackIntf)((ChunkMapIntf)chunkLoadingManager).getEntityTrackers().get(entity.getId());

            if (chunktrack != null)
            {
                return chunktrack.getTrackers().stream().map(ServerPlayerConnection::getPlayer).toList();
            }

            return Collections.emptyList();
        }
        else
        {
            throw new IllegalArgumentException("[VectorLib] A tracker check attempt was made on a client world - this is a server only action.");
        }
    }

    default List<ServerPlayer> getAllTrackingChunk(ServerLevel level, BlockPos pos, boolean bounds)
    {
        return level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), bounds);
    }

    void sendToServer(CustomPacketPayload payload);
    void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);
    default void sendToPlayer(ServerPlayer player, Packet<?> packet) { player.connection.send(packet); }
}
