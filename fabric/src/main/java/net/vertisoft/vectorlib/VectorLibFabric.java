package net.vertisoft.vectorlib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorC2SPayloads;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.ClientClearCapePayload;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.ClientSyncCapePayload;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.ClientNetSyncPayload;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.SetCapePayload;
import net.vertisoft.vectorlib.agnostic.util.VectorEventCommons;

public class VectorLibFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        VectorLib.bootstrap();
        sidedBoot();
    }

    private static void sidedBoot()
    {
        eventReg();
        payloadReg();
        C2SRegister();
    }

    //////////////////////////////////////////////////////

    private static void eventReg()
    {
        ServerPlayConnectionEvents.JOIN.register((serverHandler, sender, server) -> VectorEventCommons.serverLogin(serverHandler.player));
        ServerPlayConnectionEvents.DISCONNECT.register((serverHandler, server) -> VectorEventCommons.serverLogout(serverHandler.player));
    }

    private static void payloadReg()
    {
        // To Client
        PayloadTypeRegistry.playS2C().register(ClientNetSyncPayload.ID, ClientNetSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ClientSyncCapePayload.ID, ClientSyncCapePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ClientClearCapePayload.ID, ClientClearCapePayload.CODEC);

        // To Server
        PayloadTypeRegistry.playC2S().register(SetCapePayload.ID, SetCapePayload.CODEC);
    }

    private static void C2SRegister()
    {
        ServerPlayNetworking.registerGlobalReceiver(SetCapePayload.ID, (packet, context) -> {
            VectorC2SPayloads.setCape(context.player(), packet);
        });
    }
}
