package net.vertisoft.vectorlib.mixin.entity.parrot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.mixin_intf.ParrotRenderIntf;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Debug(export = true)
@Mixin(value = ParrotOnShoulderLayer.class, priority = 500)
public abstract class ParrotShoulderRenderMixin<T extends Player>
{
    @Shadow @Final private ParrotModel model;

    /** This code re-implements the Supplementaries shoulder party parrot behavior, since doing a mixin of a mixin is beyond feasible for me.
     *
     * The original code can be found at:
     * <a href="https://github.com/MehVahdJukaar/Supplementaries/blob/1.21/common/src/main/java/net/mehvahdjukaar/supplementaries/mixins/ParrotLayerMixin.java">...</a>
     * */
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/player/Player;FFFFZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/nbt/CompoundTag;getString(Ljava/lang/String;)Ljava/lang/String;",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void doRenderParrotSuppFixPleaseGodHelpMe(
            PoseStack matrixStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbAngle, float limbDistance, float headYaw, float headPitch, boolean leftShoulder, CallbackInfo ci, CompoundTag nbtCompound
    )
    {
        if (VectorLib.PLATFORM.isModLoaded("supplementaries") && VectorLib.CONFIG.doEntitySkins())
        {
            if (nbtCompound.getBoolean("record_playing"))
            {
                EntityType.byString(nbtCompound.getString("id")).filter((entityType) -> entityType == EntityType.PARROT).ifPresent((entityType) ->
                {
                    matrixStack.pushPose();
                    matrixStack.translate(leftShoulder ? 0.4000000059604645 : -0.4000000059604645, livingEntity.isCrouching() ? -1.2999999523162842 : -1.5, 0.0);
                    Parrot.Variant variant = Parrot.Variant.byId(nbtCompound.getInt("Variant"));

                    // Buffer reimplement for vl because mixing into mixins is like cutting yourself with rusty knives
                    String name = nbtCompound.getString("CustomName");
                    VertexConsumer vertexConsumer;
                    if ("\"Kazooie\"".equals(name)) { vertexConsumer = buffer.getBuffer(this.model.renderType(ParrotRenderIntf.KAZOOIE_TEXTURE)); }
                    else if ("\"Lovebirb\"".equals(name)) { vertexConsumer = buffer.getBuffer(this.model.renderType(ParrotRenderIntf.LOVEBIRB_TEXTURE));}
                    else if ("\"Keynis\"".equals(name)) { vertexConsumer = buffer.getBuffer(this.model.renderType(ParrotRenderIntf.KEYNIS_TEXTURE));}
                    else vertexConsumer = buffer.getBuffer(this.model.renderType(ParrotRenderer.getVariantTexture(variant)));

                    veclib$SuppReimpHELP(this.model, matrixStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, limbAngle, limbDistance, headYaw, headPitch, livingEntity.tickCount, 0.0F);
                    matrixStack.popPose();
                });

                if (VectorLib.PLATFORM.isModLoaded("cpm")) matrixStack.popPose();
                ci.cancel();
            }
        }
    }

    @Unique
    private static void veclib$SuppReimpHELP(ParrotModel model, PoseStack poseStack, VertexConsumer buffer,
                                                      int packedLight, int packedOverlay, float limbSwing, float limbSwingAmount,
                                                      float netHeadYaw, float headPitch, int tickCount, float bob)
    {
        model.prepare(ParrotModel.State.PARTY);
        model.setupAnim(ParrotModel.State.PARTY, tickCount, limbSwing, limbSwingAmount, bob, netHeadYaw, headPitch);
        model.root().render(poseStack, buffer, packedLight, packedOverlay);
    }
}
