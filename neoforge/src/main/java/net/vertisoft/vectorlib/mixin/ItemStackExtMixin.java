package net.vertisoft.vectorlib.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import net.vertisoft.vectorlib.agnostic.registrars.VectorPropertyReg;
import net.vertisoft.vectorlib.platform.VectorRegNF;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** I hate you, NeoForge. :) */
@Mixin(IItemStackExtension.class)
public interface ItemStackExtMixin
{
    @Shadow ItemStack self();

    @Inject(method = "getBurnTime", at = @At("HEAD"), cancellable = true)
    default void vectorLib$burnTimeStopInPlaceRightNow(@Nullable RecipeType<?> recipeType, CallbackInfoReturnable<Integer> cir)
    {
        Item item = this.self().getItem();
        if (VectorRegNF.NF_FUELS.containsKey(item)) cir.setReturnValue(VectorRegNF.NF_FUELS.get(item));
    }
}
