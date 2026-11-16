package net.vertisoft.vectorlib.agnostic.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.cosmetic.capes.VecClientCapeMan;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.SetCapePayload;
import net.vertisoft.vectorlib.mixin_intf.ParrotRenderIntf;

/**
 * Common client code across loaders.
 */
public class VectorClientCommons
{
    public static void onJoinServer(ClientPacketListener handler, Minecraft client)
    {
        VectorLib.NETWORK.sendToServer(new SetCapePayload(VecClientCapeMan.capeID()));
    }

    /** Used in custom Parrot shoulder rendering. */
    public static VertexConsumer parrotShoulderCustom(VertexConsumer original, ParrotModel model, CompoundTag nbtCompound, MultiBufferSource vertexConsumers)
    {
        if (VectorLib.CONFIG.doEntitySkins())
        {
            String name = nbtCompound.getString("CustomName");

            if ("\"Kazooie\"".equals(name)) return vertexConsumers.getBuffer(model.renderType(ParrotRenderIntf.KAZOOIE_TEXTURE));
            else if ("\"Lovebirb\"".equals(name)) return vertexConsumers.getBuffer(model.renderType(ParrotRenderIntf.LOVEBIRB_TEXTURE));
            else if ("\"Keynis\"".equals(name)) return vertexConsumers.getBuffer(model.renderType(ParrotRenderIntf.KEYNIS_TEXTURE));
        }

        return original;
    }
}
