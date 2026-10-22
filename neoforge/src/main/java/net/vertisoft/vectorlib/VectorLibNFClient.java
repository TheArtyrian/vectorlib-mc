package net.vertisoft.vectorlib;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.vertisoft.vectorlib.agnostic.util.VectorClientCommons;

@EventBusSubscriber(modid = VectorLib.ID, value = Dist.CLIENT)
public class VectorLibNFClient
{
    @SubscribeEvent
    public static void logIn(ClientPlayerNetworkEvent.LoggingIn event)
    {
        VectorClientCommons.onJoinServer(event.getPlayer().connection, Minecraft.getInstance());
    }
}
