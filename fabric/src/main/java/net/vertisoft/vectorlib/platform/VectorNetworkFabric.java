package net.vertisoft.vectorlib.platform;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class VectorNetworkFabric implements VectorNetworkIntf
{
    @Override
    public void sendToServer(CustomPacketPayload payload)
    {
        ClientPlayNetworking.send(payload);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload)
    {
        ServerPlayNetworking.send(player, payload);
    }
}
