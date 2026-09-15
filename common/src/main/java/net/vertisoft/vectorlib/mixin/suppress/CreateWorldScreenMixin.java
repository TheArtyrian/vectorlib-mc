package net.vertisoft.vectorlib.mixin.suppress;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.VectorSystems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin
{
    // https://www.youtube.com/watch?v=iOaCQkR8FXU
    // (its 1:18 am the day after ludum dare 57 help)
    @ModifyArg(method = "onCreate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/WorldOpenFlows;confirmWorldCreation(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;Lcom/mojang/serialization/Lifecycle;Ljava/lang/Runnable;Z)V"))
    private boolean vectorLib$replaceLifecycler(boolean value)
    {
        if (VectorLib.CONFIG.doSuppressExperimentalWarn())
        {
            VectorLib.LOGGER.warn(VectorSystems.SUPPRESSION_WARNING);
            return true;
        }
        return value;
    }
}
