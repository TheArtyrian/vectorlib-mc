package net.vertisoft.vectorlib.agnostic.splash;

import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Registers custom splashes. */
public class VectorSplash
{
    public static final boolean DEBUG_PRINTER = false;

    private static final List<ResourceLocation> TEXTFILES = new ArrayList<>();
    private static final List<String> RAW_TEXT = new ArrayList<>();
    private static final Map<RenderContext, Supplier<Boolean>> CUSTOM_WITH_CONDITION = new HashMap<>();

    /** Registers a path for a splash textfile. Supply mod ID and the full path from root. */
    public static void registerTextList(String modID, String path)
    {
        TEXTFILES.add(ResourceLocation.fromNamespaceAndPath(modID, path));
    }

    /** Registers a single string to add to the splash pool. Not recommended over {@link VectorSplash#registerTextList(String, String) using a textfile},
     * but exists as an option just in case. */
    public static void registerString(String text)
    {
        RAW_TEXT.add(text);
    }

    /** Registers a special splash renderer that will only show when a condition is met. */
    public static void registerSpecial(RenderContext renderer, Supplier<Boolean> condition)
    {
        CUSTOM_WITH_CONDITION.put(renderer, condition);
    }

    public static List<ResourceLocation> textfiles() { return TEXTFILES; }
    public static List<String> rawTexts() { return RAW_TEXT; }
    public static Map<RenderContext, Supplier<Boolean>> renderMap() { return CUSTOM_WITH_CONDITION; }

    @FunctionalInterface
    public interface RenderContext <T extends SplashRenderer>
    {
         T create(RandomSource random);
    }
}
