package net.vertisoft.vectorlib.mixin.entity.rabbit;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;
import net.vertisoft.vectorlib.VectorLib;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RabbitRenderer.class)
public class RabbitRenderMixin
{
    @Unique
    private static final ResourceLocation VLIB_OOPR_TEXTURE = ResourceLocation.fromNamespaceAndPath(VectorLib.ID,"textures/entity/rabbit/oopr.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Rabbit;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void vectorlib$oopr(Rabbit entity, CallbackInfoReturnable<ResourceLocation> cir, @Local(argsOnly = true) Rabbit rabbit)
    {
        if (VectorLib.CONFIG.doEntitySkins())
        {
            String name = ChatFormatting.stripFormatting(rabbit.getName().getString());

            boolean oopr = (name.equals("Oopr") || name.equals("SuperSMZ"));
            if (oopr) cir.setReturnValue(VLIB_OOPR_TEXTURE);
        }
    }
}
