package net.vertisoft.vectorlib.agnostic.networking.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.cosmetic.capes.VectorCapeMan;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.ClientSyncCapePayload;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.SetCapePayload;

/**
 * Goes from Client -> Server.
 */
public class VectorC2SPayloads
{
    // Payloads
    public static final ResourceLocation VECTOR_SET_CAPE = VectorLib.id("set_cape");

    //////////////////////////////////////////////////////

    public static void setCape(ServerPlayer player, SetCapePayload payload)
    {
        String id = payload.id();

        VectorCapeMan.sync(player.getUUID(), id);

        ClientSyncCapePayload packet = new ClientSyncCapePayload(player.getUUID(), VectorCapeMan.toClientID(player.getUUID()));
        VectorLib.NETWORK.sendToPlayer(player, packet);

        for (ServerPlayer other : player.serverLevel().players()) {
            if (other != player) VectorLib.NETWORK.sendToPlayer(other, packet);
        }
    }
}
