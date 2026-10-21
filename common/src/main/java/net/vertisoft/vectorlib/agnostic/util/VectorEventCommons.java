package net.vertisoft.vectorlib.agnostic.util;

import net.minecraft.server.level.ServerPlayer;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.cosmetic.capes.VectorCapeMan;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.ClientClearCapePayload;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.ClientSyncCapePayload;

/**
 * Common event code across loaders.
 */
public class VectorEventCommons
{
    public static void serverLogin(ServerPlayer player)
    {
        VectorCapeMan.directIDSet(player.getUUID(), -1);

        for (ServerPlayer other : player.serverLevel().players())
        {
            VectorLib.NETWORK.sendToPlayer(player, new ClientSyncCapePayload(other.getUUID(), VectorCapeMan.toClientID(other.getUUID())));

            if (other != player)
            {
                VectorLib.NETWORK.sendToPlayer(other, new ClientSyncCapePayload(player.getUUID(), VectorCapeMan.toClientID(player.getUUID())));
            }
        }
    }

    public static void serverLogout(ServerPlayer player)
    {
        VectorCapeMan.free(player.getUUID());

        for (ServerPlayer other : player.serverLevel().players())
        {
            VectorLib.NETWORK.sendToPlayer(other, new ClientClearCapePayload(player.getUUID()));
        }
    }
}
