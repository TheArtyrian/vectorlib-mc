package net.vertisoft.vectorlib;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorPayloads;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.NetSyncPayload;
import net.vertisoft.vectorlib.agnostic.util.VectorTrade;
import net.vertisoft.vectorlib.exclusive.NFLootModSet;
import net.vertisoft.vectorlib.platform.VectorRegNF;

import java.util.function.Consumer;
import java.util.function.Supplier;

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
            // NetSync
            payreg.playToClient(NetSyncPayload.ID, NetSyncPayload.CODEC, (payload, ctx) ->
                    ctx.enqueueWork(() -> VectorPayloads.netsync(payload, ctx.player().level())));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    private static void sidedBoot(IEventBus eventBus)
    {
        NFLootModSet.REGISTRY.register(eventBus);
    }
}
