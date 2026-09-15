package net.vertisoft.vectorlib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.resources.PlayerSkin;
import net.vertisoft.vectorlib.VectorLib;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin
{
    // Special method that makes cool transparent capes render as they should
    @WrapOperation(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
                    ordinal = 0
            )
    )
    private VertexConsumer vectorLib$GetSpecialBuff(
            MultiBufferSource instance,
            RenderType renderLayer,
            Operation<VertexConsumer> original,
            @Local(argsOnly = true) AbstractClientPlayer abstractClientPlayerEntity,
            @Local PlayerSkin tex)
    {
        if (VectorLib.SYSTEM.TRANSPARENT_CAPES.contains(abstractClientPlayerEntity.getStringUUID()))
        {
            return original.call(instance, RenderType.entityCutoutNoCull(tex.capeTexture()));
        }
        else
        {
            return original.call(instance, renderLayer);
        }
    }
}
