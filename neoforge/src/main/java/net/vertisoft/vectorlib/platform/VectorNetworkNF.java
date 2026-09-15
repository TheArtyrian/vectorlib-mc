package net.vertisoft.vectorlib.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class VectorNetworkNF implements VectorNetworkIntf
{
    @Override
    public void sendToServer(CustomPacketPayload payload)
    {
        PacketDistributor.sendToServer(payload);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload)
    {
        if (player instanceof ServerPlayer) PacketDistributor.sendToPlayer(player, payload);
    }
}
