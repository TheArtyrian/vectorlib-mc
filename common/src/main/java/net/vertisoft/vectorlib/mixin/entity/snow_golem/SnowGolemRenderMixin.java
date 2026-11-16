package net.vertisoft.vectorlib.mixin.entity.snow_golem;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.SnowGolem;
import net.vertisoft.vectorlib.VectorLib;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SnowGolemRenderer.class)
public abstract class SnowGolemRenderMixin
{
    @Unique private static final ResourceLocation VLIB_ROMEO_TEXTURE = ResourceLocation.fromNamespaceAndPath(VectorLib.ID,"textures/entity/snow_golem_romeo.png");

    @ModifyReturnValue(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/SnowGolem;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"))
    private ResourceLocation vectorlib$romeo(ResourceLocation original, @Local(argsOnly = true) SnowGolem snowy)
    {
        if (VectorLib.CONFIG.doEntitySkins())
        {
            String name = ChatFormatting.stripFormatting(snowy.getName().getString());
            boolean isromeo = (name.equals("Romeo") || name.equals("Admin"));
            if (isromeo) return VLIB_ROMEO_TEXTURE;
        }
        return original;
    }
}
