package net.vertisoft.vectorlib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.vertisoft.vectorlib.agnostic.cosmetic.capes.VecClientCapeMan;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorS2CPayloads;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.ClientClearCapePayload;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.ClientNetSyncPayload;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.ClientSyncCapePayload;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.SetCapePayload;
import net.vertisoft.vectorlib.agnostic.util.VectorClientCommons;

public class VectorLibFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        VectorLibClient.bootstrap();
        sidedBoot();
    }

    private static void sidedBoot()
    {
        registerEvents();
        S2CRegister();
    }

    //////////////////////////////////////////////////////

    private static void registerEvents()
    {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
                VectorClientCommons.onJoinServer(handler, client));
    }

    private static void S2CRegister()
    {
        ClientPlayNetworking.registerGlobalReceiver(ClientNetSyncPayload.ID, (payload, context) ->
                VectorS2CPayloads.netsync(payload, context.player().level()));

        ClientPlayNetworking.registerGlobalReceiver(ClientSyncCapePayload.ID, (payload, context) ->
                VectorS2CPayloads.syncCape(payload));

        ClientPlayNetworking.registerGlobalReceiver(ClientClearCapePayload.ID, (payload, context) ->
                VectorS2CPayloads.clearCape(payload));
    }
}
