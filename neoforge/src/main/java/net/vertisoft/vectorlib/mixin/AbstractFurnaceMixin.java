package net.vertisoft.vectorlib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.vertisoft.vectorlib.platform.VectorRegNF;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceMixin
{
    @ModifyReturnValue(method = "getFuel", at = @At(value = "RETURN", ordinal = 1))
    private static Map<Item, Integer> vectorLib$fuelGrab(Map<Item, Integer> original)
    {
        if (original != null && !original.isEmpty())
        {
            for (ItemLike like : VectorRegNF.NF_FUELS.keySet())
            {
                original.put(like.asItem(), VectorRegNF.NF_FUELS.get(like));
            }
        }
        return original;
    }
}
