package net.vertisoft.vectorlib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.NetSyncPayload;

public class VectorLibFabric implements ModInitializer
{
    public static void bootstrap()
    {

    }

    @Override
    public void onInitialize()
    {
        VectorLib.bootstrap();
        sidedBoot();
    }

    private static void sidedBoot()
    {
        PayloadTypeRegistry.playS2C().register(NetSyncPayload.ID, NetSyncPayload.CODEC);
    }
}
