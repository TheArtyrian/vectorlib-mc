package net.vertisoft.vectorlib.agnostic.util;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.loot_tables.VectorLootMod;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

public class VectorMixinCommons
{
    /** Used for catching a RegistryOps element when starting up loot table set. */
    public static RegistryOps<JsonElement> catchTable(
            HolderLookup.Provider lookup,
            DynamicOps<JsonElement> dynaOps,
            Operation<RegistryOps<JsonElement>> operation,
            WeakHashMap<RegistryOps<JsonElement>, HolderLookup.Provider> hashMap
    )
    {
        RegistryOps<JsonElement> pooler = operation.call(lookup, dynaOps);
        hashMap.put(pooler, lookup);
        VectorLib.LOGGER.info("[VECTORLIB]: Successfully cached loot table RegistryOps to lookup system - beginning loot table modifications.");
        return pooler;
    }

    /** Used for releasing a RegistryOps element when finishing up + freezing loot table set. */
    public static CompletableFuture<LayeredRegistryAccess<RegistryLayer>> releaseTable(
            CompletableFuture<List<WritableRegistry<?>>> future,
            Function<? super List<WritableRegistry<?>>, ? extends LayeredRegistryAccess<RegistryLayer>> funct,
            Executor executor,
            Operation<CompletableFuture<LayeredRegistryAccess<RegistryLayer>>> original,
            RegistryOps<JsonElement> pooler,
            WeakHashMap<RegistryOps<JsonElement>, HolderLookup.Provider> hashMap
    )
    {
        return original.call(
                future.thenApply((retina) -> {
                    hashMap.remove(pooler);
                    VectorLib.LOGGER.info("[VECTORLIB]: Successfully released loot table RegistryOps from lookup system - registry should now be frozen.");
                    return retina;
                }),
                funct,
                executor
        );
    }

    /** Used to actually modify loot tables filtered through the reader. */
    public static <T> void lootTableMod(
            Optional<T> loot,
            Consumer<? super T> action,
            Operation<Void> original,
            ResourceLocation id,
            RegistryOps<JsonElement> ops,
            WeakHashMap<RegistryOps<JsonElement>, HolderLookup.Provider> hashMap
    )
    {
        original.call(loot.map(table -> VectorLootMod.runModifier(table, id, ops, hashMap)), action);
    }
}