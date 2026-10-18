package net.vertisoft.vectorlib.exclusive.datagen.soundfile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.vertisoft.vectorlib.exclusive.datagen.VectorDatagen;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/** A robust data generator for {@code sounds.json}.
 *
 */
public abstract class VectorSoundsheetGen implements DataProvider
{
    protected static final String MINECRAFT = ResourceLocation.DEFAULT_NAMESPACE;

    // All common sound names
    private static final String JSON_NAME = "sounds.json";
    private static final String SOUNDS = "sounds";
    private static final String SUB = "subtitle";
    private static final String NAME = "name";
    private static final String VOLUME = "volume";
    private static final String PITCH = "pitch";
    private static final String STREAM = "stream";

    protected final FabricDataOutput dataOutput;
    private final String mod;
    private final CompletableFuture<HolderLookup.Provider> registryLookup;

    protected VectorSoundsheetGen(String modId, FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup)
    {
        this.mod = modId;
        this.dataOutput = dataOutput;
        this.registryLookup = registryLookup;
    }

    public abstract void generateSounds(HolderLookup.Provider lookup, SoundsFactory factory);

    @Override
    public CompletableFuture<?> run(CachedOutput output)
    {
        return this.registryLookup.thenCompose((lookup) -> {
            JsonObject jason = new JsonObject();

            TreeMap<String, Pair<List<SoundDefinition>, VectorDatagen.Caption>> mapper = new TreeMap<>();
            this.generateSounds(lookup, (path, soundslist, caption) -> {
                Objects.requireNonNull(path);
                Objects.requireNonNull(soundslist);
                if (mapper.containsKey(path)) throw new IllegalArgumentException(String.format("Duplicate definition for %1s", path));
                else mapper.put(path, Pair.of(soundslist, caption));
            });

            for (String path : mapper.keySet())
            {
                Pair<List<SoundDefinition>, VectorDatagen.Caption> pathSet = mapper.get(path);
                VectorDatagen.Caption caption = pathSet.getSecond();

                JsonObject nested = new JsonObject();
                JsonArray sounds = new JsonArray();

                for (SoundDefinition loc : pathSet.getFirst()) loc.putTo(sounds);

                nested.add(SOUNDS, sounds);
                if (caption != null)
                {
                    nested.addProperty(SUB, caption.id());
                    if (caption.text() != null) VectorDatagen.CAPTIONS.add(caption);
                }

                jason.add(path, nested);
            }

            // Bundle the JSON together into a neat little package.
            return DataProvider.saveStable(
                    output,
                    jason,
                    this.dataOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(this.mod).resolve(JSON_NAME)
            );
        });
    }

    /**
     * Returns a list with several counts of the same commonpath with numbers appended to the end, in cases of sound variation.
     * <p>
     * For instance, say you have four files located at {@code assets/<modpath>/sounds/block/myblock/}, and each one is simply
     * called {@code place<#>.ogg} in ascending order. Provide {@code "<modpath>"}, {@code "block/myblock/place"}, and {@code 4};
     * a list of all the files will be returned.
     * @param id The folder in {@code assets/} where your sound is located. Can also be {@code "minecraft"} for Vanilla sounds.
     * @param commonpath The path + filename where your sound is located without numbers, i.e {@code blocks/specialdirt/mine}.
     * @param counts How many variations of this sound exist.
     **/
    protected static List<SoundDefinition> multiple(String id, String commonpath, int counts)
    {
        List<SoundDefinition> returnable = new ArrayList<>();
        for (int i = 0; i < counts; i++)
        {
            returnable.add(SoundDefinition.of(ResourceLocation.fromNamespaceAndPath(id, commonpath + (i + 1))));
        }
        return returnable;
    }

    /** Registers a single sound event. */
    protected static List<SoundDefinition> addOne(String id, String path)
    {
        return List.of(SoundDefinition.of(ResourceLocation.fromNamespaceAndPath(id, path)));
    }

    /** Adds all sound events from a list of strings. In the event you have several sounds that share a prefix, use this. */
    protected static List<SoundDefinition> addAll(String id, String prefix, List<String> strings)
    {
        List<String> appender = new ArrayList<>();
        for (String string : strings)
        {
            appender.add(prefix + string);
        }
        return addAll(id, appender);
    }

    /** Adds all sound events from a list of strings. */
    protected static List<SoundDefinition> addAll(String id, List<String> strings)
    {
        List<SoundDefinition> returnable = new ArrayList<>();
        for (String string : strings)
        {
            returnable.add(SoundDefinition.of(ResourceLocation.fromNamespaceAndPath(id, string)));
        }
        return returnable;
    }

    @Override public String getName() { return "VectorLib sounds.json generator"; }

    ////////////////////////////////////////////////////////////////////////////////////

    protected static class SoundDefinition
    {
        private final ResourceLocation location;
        private final Optional<Float> pitch;
        private final Optional<Float> volume;
        private final Optional<Boolean> streamed;

        public static SoundDefinition of(ResourceLocation location) { return new SoundDefinition(location, Optional.empty(), Optional.empty(), Optional.empty()); }
        public static SoundDefinition ofPitch(ResourceLocation location, float pitch) { return new SoundDefinition(location, Optional.of(pitch), Optional.empty(), Optional.empty()); }
        public static SoundDefinition ofVolume(ResourceLocation location, float volume) { return new SoundDefinition(location, Optional.empty(), Optional.of(volume), Optional.empty()); }
        public static SoundDefinition ofPitchVolume(ResourceLocation location, float pitch, float volume) { return new SoundDefinition(location, Optional.of(pitch), Optional.of(volume), Optional.empty()); }
        public static SoundDefinition of(ResourceLocation location, boolean streamed) { return new SoundDefinition(location, Optional.empty(), Optional.empty(), Optional.of(streamed)); }
        public static SoundDefinition of(ResourceLocation location, float pitch, float volume, boolean streamed) { return new SoundDefinition(location, Optional.of(pitch), Optional.of(volume), Optional.of(streamed)); }

        private SoundDefinition(ResourceLocation location, Optional<Float> pitch, Optional<Float> volume, Optional<Boolean> streamed)
        {
            this.location = location;
            this.pitch = pitch;
            this.volume = volume;
            this.streamed = streamed;
        }

        private void putTo(JsonArray array)
        {
            if (this.noAdditionalData())
            {
                array.add(this.parseLocation());
            }
            else
            {
                JsonObject object = new JsonObject();
                object.addProperty(NAME, this.parseLocation());
                this.pitch.ifPresent(pitchX -> object.addProperty(PITCH, pitchX));
                this.volume.ifPresent(volX -> object.addProperty(VOLUME, volX));
                this.streamed.ifPresent(streamedX -> object.addProperty(STREAM, streamedX));

                array.add(object);
            }
        }

        private boolean noAdditionalData() { return this.pitch.isEmpty() && this.volume.isEmpty() && this.streamed.isEmpty(); }
        private String parseLocation() { return (this.location.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) ? this.location.getPath() : this.location.toString(); }
    }

    ////////////////////////////////////////////////////////////////////////////////////

    @FunctionalInterface @NonExtendable
    public interface SoundsFactory
    {
        /** Adds a music entry, i.e ambient music, music discs, etc. The file will be marked for streaming and no caption will be provided. */
        default void addMusic(SoundEvent sound, String mod, String path)
        {
            this.addSound(sound, List.of(SoundDefinition.of(ResourceLocation.fromNamespaceAndPath(mod, path), true)), null);
        }

        /** Adds a sound entry. Takes in a sound event, plus a list of sound definitions that contain paths, pitches, volumes, etc.
         * An optional {@code Caption} can be provided, which can then be used in the lang generator.
         */
        default void addSound(SoundEvent sound, List<SoundDefinition> soundfiles, @Nullable VectorDatagen.Caption captions)
        {
            this.add(sound.getLocation().getPath(), soundfiles, captions);
        }

        /**
         * Adds a sound entry.
         * <p></p>
         * You should NOT be using this directly, and should be using {@code addSound()} and its derivatives instead!
         * Using this raw could lead to several issues - mainly registered sound events that have no registry entry; which will crash your mod.
         */
        void add(String name, List<SoundDefinition> soundfiles, @Nullable VectorDatagen.Caption caption);
    }
}
