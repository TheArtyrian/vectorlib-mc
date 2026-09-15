package net.vertisoft.vectorlib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.splash.VectorSplash;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(SplashManager.class)
public class SplashMixin
{
    @Shadow @Final private static RandomSource RANDOM;

    @ModifyReturnValue(
            method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/List;",
            at = @At(value = "RETURN", ordinal = 0)
    )
    protected List<String> vectorLib$appendAllLists(List<String> original, @Local(argsOnly = true) ResourceManager resourceManager, @Local(argsOnly = true) ProfilerFiller profiler)
    {
        int hashcode = 125780783;
        for (ResourceLocation reader_id : VectorLib.SYSTEM.SPLASHES.textfiles())
        {
            try
            {
                BufferedReader bufferedReader = Minecraft.getInstance().getResourceManager().openAsReader(reader_id);
                List<String> catchinwards;

                try { catchinwards = bufferedReader.lines().map(String::trim).filter(splashText -> splashText.hashCode() != hashcode).toList(); }
                catch (Throwable throwable)
                {
                    if (bufferedReader != null)
                    {
                        try { bufferedReader.close(); }
                        catch (Throwable var6) { throwable.addSuppressed(var6); }
                    }

                    throw throwable;
                }

                if (bufferedReader != null) bufferedReader.close();
                boolean worked = original.addAll(catchinwards);

                if (worked) VectorLib.LOGGER.info("Successfully added splash texts from file {}.", reader_id);
                else VectorLib.LOGGER.error("Unable to add splash texts from file {}.", reader_id);
            }
            catch (IOException ioException)
            {
                VectorLib.LOGGER.error("Critical error in IOStream, skipping. See below:", ioException);
            }
        }

        // Add all splashes from raw texts
        original.addAll(VectorLib.SYSTEM.SPLASHES.rawTexts());

        if (VectorLib.SYSTEM.SPLASHES.DEBUG_PRINTER)
        {
            String debug_printer = "";
            for (String og : original)
            {
                debug_printer = debug_printer + "\n    " + og;
            }
            VectorLib.LOGGER.info("All splashes appended. List of splashes: {}", debug_printer);
        }

        return original;
    }

    @Inject(method = "getSplash", at = @At("HEAD"), cancellable = true)
    private void vectorLib$replaceAutocule(CallbackInfoReturnable<SplashRenderer> cir)
    {
        Map<VectorSplash.RenderContext, Supplier<Boolean>> vecMap = VectorLib.SYSTEM.SPLASHES.renderMap();
        for (VectorSplash.RenderContext context : vecMap.keySet())
        {
            if (vecMap.get(context).get()) cir.setReturnValue(context.create(RANDOM));
        }
    }
}
