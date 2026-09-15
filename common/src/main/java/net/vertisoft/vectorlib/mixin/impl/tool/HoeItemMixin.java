package net.vertisoft.vectorlib.mixin.impl.tool;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.vertisoft.vectorlib.agnostic.registrars.VectorToolActions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(HoeItem.class)
public class HoeItemMixin
{
    @ModifyVariable(method = "useOn", at = @At("STORE"), ordinal = 0)
    private Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> vectorLib$toolableHoe(
            Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> original,
            @Local(argsOnly = true) UseOnContext context
    )
    {
        if (original == null)
        {
            Level level = context.getLevel();
            BlockPos blockpos = context.getClickedPos();

            Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> TILLS = VectorToolActions.getTillables();

            Optional<Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> output = Optional.ofNullable(
                    TILLS.get(level.getBlockState(blockpos).getBlock())
            );

            if (output.isPresent()) return output.get();
        }
        return original;
    }
}
