package net.vertisoft.vectorlib.mixin;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import net.vertisoft.vectorlib.agnostic.registrars.VectorPropertyReg;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ComposterBlock.class)
public class ComposterMixin
{
    @Inject(method = "getValue", at = @At("HEAD"), cancellable = true)
    private static void vectorLib$composterMyWayIsBetter(ItemStack item, CallbackInfoReturnable<Float> cir)
    {
        Map<ItemLike, Float> map = VectorPropertyReg.Compost.get();
        Item itemTarg = item.getItem();
        if (map.containsKey(itemTarg))
        {
            cir.setReturnValue(map.get(itemTarg));
            cir.cancel();
        }
    }
}
