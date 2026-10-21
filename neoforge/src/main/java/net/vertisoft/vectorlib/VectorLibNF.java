package net.vertisoft.vectorlib;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorS2CPayloads;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorC2SPayloads;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.ClientClearCapePayload;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.ClientNetSyncPayload;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.ClientSyncCapePayload;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.SetCapePayload;
import net.vertisoft.vectorlib.exclusive.NFLootModSet;
import net.vertisoft.vectorlib.platform.VectorRegNF;

@Mod(VectorLib.ID)
public class VectorLibNF
{
    public VectorLibNF(IEventBus eventBus)
    {
        ((VectorRegNF)VectorLib.REGISTRY).setEventBus(ModLoadingContext.get().getActiveContainer().getEventBus());

        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::payloadSetup);

        VectorLib.bootstrap();
        sidedBoot(eventBus);
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent event)
    {
        VectorLibClient.bootstrap();
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event)
    {

    }

    @SubscribeEvent
    public void payloadSetup(final RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar payreg = event.registrar("VLib_V1").executesOn(HandlerThread.NETWORK);

        if (true)
        {
            // TO CLIENT
            // NetSync
            payreg.playToClient(ClientNetSyncPayload.ID, ClientNetSyncPayload.CODEC, (payload, ctx) ->
                    ctx.enqueueWork(() -> VectorS2CPayloads.netsync(payload, ctx.player().level())));
            payreg.playToClient(ClientClearCapePayload.ID, ClientClearCapePayload.CODEC, (payload, ctx) ->
                    ctx.enqueueWork(() -> VectorS2CPayloads.clearCape(payload)));
            payreg.playToClient(ClientSyncCapePayload.ID, ClientSyncCapePayload.CODEC, (payload, ctx) ->
                    ctx.enqueueWork(() -> VectorS2CPayloads.syncCape(payload)));

            // TO SERVER
            // Set Cape
            payreg.playToServer(SetCapePayload.ID, SetCapePayload.CODEC, (payload, ctx) ->
                    ctx.enqueueWork(() -> VectorC2SPayloads.setCape((ServerPlayer)ctx.player(), payload)));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    private static void sidedBoot(IEventBus eventBus)
    {
        NFLootModSet.REGISTRY.register(eventBus);
    }
}
