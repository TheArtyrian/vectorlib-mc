package net.vertisoft.vectorlib.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.vertisoft.vectorlib.agnostic.util.VectorClientCommons;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ParrotOnShoulderLayer.class, priority = 500)
public class ParrotShoulderMixinFabric<T extends Player>
{
    @Shadow @Final private ParrotModel model;

    // ALSO HAS IMPL IN A NEOFORGE CLASS!
    @ModifyVariable(method = "method_17958", at = @At(value = "STORE"), ordinal = 0)
    private VertexConsumer mixingIntoSynthesisWowHelp(
            VertexConsumer vertexConsumer,
            @Local CompoundTag nbtCompound,
            @Local Parrot.Variant variant,
            @Local(argsOnly = true) MultiBufferSource vertexConsumers
    )
    {
        return VectorClientCommons.parrotShoulderCustom(vertexConsumer, this.model, nbtCompound, vertexConsumers);
    }
}
