package net.vertisoft.vectorlib.mixin.suppress;

import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.VectorSystems;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Debug(export = true)
@Mixin(WorldOpenFlows.class)
public class WorldOpenFlowsMixin
{
    @ModifyVariable(method = "openWorldCheckWorldStemCompatibility", at = @At(value = "STORE"), ordinal = 1)
    private boolean vectorLib$suppressWarn(boolean value)
    {
        if (VectorLib.CONFIG.doSuppressExperimentalWarn())
        {
            VectorLib.LOGGER.warn(VectorSystems.SUPPRESSION_WARNING);
            return false;
        }
        return value;
    }
}
