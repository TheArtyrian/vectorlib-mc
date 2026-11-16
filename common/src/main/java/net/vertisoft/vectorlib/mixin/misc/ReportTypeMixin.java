package net.vertisoft.vectorlib.mixin.misc;

import net.minecraft.ReportType;
import net.minecraft.Util;
import net.vertisoft.vectorlib.VectorLib;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Debug(export = true)
@Mixin(ReportType.class)
public abstract class ReportTypeMixin
{
    @Shadow @Final @Mutable private List<String> nuggets;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void vctr$init(String header, List nuggets, CallbackInfo ci)
    {
        this.nuggets = new ArrayList<>();
        this.nuggets.addAll(nuggets);
    }

    // Fixes some bullshit with getting the error comment - will return to loop if an exception happens
    @Inject(method = "getErrorComment", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), cancellable = true)
    private void vctr$maybeICanDoBetter(CallbackInfoReturnable<String> cir)
    {
        try {
            String st = this.nuggets.get((int)Math.round(Math.random() * (this.nuggets.size() - 1)));
            cir.setReturnValue(st);
        }
        catch (Throwable d) {
            VectorLib.LOGGER.error("Failed to do replacement code for ReportType.getErrorComment() - Artyrian is useless xd");
        }
    }
}