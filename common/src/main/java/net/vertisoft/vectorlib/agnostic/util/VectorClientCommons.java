package net.vertisoft.vectorlib.agnostic.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.cosmetic.capes.VecClientCapeMan;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.SetCapePayload;

/**
 * Common client code across loaders.
 */
public class VectorClientCommons
{
    public static void onJoinServer(ClientPacketListener handler, Minecraft client)
    {
        VectorLib.NETWORK.sendToServer(new SetCapePayload(VecClientCapeMan.capeID()));
    }
}
