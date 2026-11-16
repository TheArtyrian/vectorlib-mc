package net.vertisoft.vectorlib.mixin_intf;

import net.minecraft.resources.ResourceLocation;
import net.vertisoft.vectorlib.VectorLib;

public interface ParrotRenderIntf
{
    public final ResourceLocation KAZOOIE_TEXTURE = ResourceLocation.fromNamespaceAndPath(VectorLib.ID, "textures/entity/parrot/parrot_kazooie.png");
    public final ResourceLocation LOVEBIRB_TEXTURE = ResourceLocation.fromNamespaceAndPath(VectorLib.ID, "textures/entity/parrot/parrot_lb.png");
    public final ResourceLocation KEYNIS_TEXTURE = ResourceLocation.fromNamespaceAndPath(VectorLib.ID, "textures/entity/parrot/parrot_keynis.png");

    ResourceLocation vectorlib$getTextureFromName(ResourceLocation og, String name);
}
