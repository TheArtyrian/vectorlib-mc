package net.vertisoft.vectorlib.mixin.impl.tool;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.vertisoft.vectorlib.agnostic.registrars.VectorToolActions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;
import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin
{
    // thanks neoforge for having events for literally everything but good content
    // yes i stole this from nexuslib i literally wrote it
    @ModifyVariable(method = "evaluateNewBlockState", at = @At("STORE"), ordinal = 0)
    private Optional<BlockState> vectorLib$toolableAxe(Optional<BlockState> value, @Local(argsOnly = true) BlockState state)
    {
        if (value.isEmpty())
        {
            Map<Block, Block> STRIPS_2 = VectorToolActions.getStrippables();

            Optional<BlockState> stripgrab = Optional.ofNullable(STRIPS_2.get(state.getBlock())).map((block) ->
            {
                return block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
            });

            if (stripgrab.isPresent()) return stripgrab;
        }
        return value;
    }
}