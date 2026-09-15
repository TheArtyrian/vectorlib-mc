package net.vertisoft.vectorlib.mixin.impl;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;
import net.vertisoft.vectorlib.agnostic.util.VectorMixinCommons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(ReloadableServerRegistries.class)
public class ReloadableServerRegMixinFabric
{
    @Unique private static final WeakHashMap<RegistryOps<JsonElement>, HolderLookup.Provider> VCTR$HOLDERLOOKUP = new WeakHashMap<>();

    @WrapOperation(
            method = "reload",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/ReloadableServerRegistries$EmptyTagLookupWrapper;createSerializationContext(Lcom/mojang/serialization/DynamicOps;)Lnet/minecraft/resources/RegistryOps;"
            )
    )
    private static RegistryOps<JsonElement> vectorLib$catchOperationTable(
            @Coerce HolderLookup.Provider instance,
            DynamicOps<JsonElement> dynamicOps,
            Operation<RegistryOps<JsonElement>> original)
    {
        return VectorMixinCommons.catchTable(instance, dynamicOps, original, VCTR$HOLDERLOOKUP);
    }

    @WrapOperation(
            method = "reload",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/CompletableFuture;thenApplyAsync(Ljava/util/function/Function;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private static CompletableFuture<LayeredRegistryAccess<RegistryLayer>> vectorLib$releaseOperationTable(
            CompletableFuture<List<WritableRegistry<?>>> future,
            Function<? super List<WritableRegistry<?>>, ? extends LayeredRegistryAccess<RegistryLayer>> fn,
            Executor executor,
            Operation<CompletableFuture<LayeredRegistryAccess<RegistryLayer>>> original,
            @Local RegistryOps<JsonElement> pooler
    ) {
        return VectorMixinCommons.releaseTable(future, fn, executor, original, pooler, VCTR$HOLDERLOOKUP);
    }

    @WrapOperation(method = "method_58278", at = @At(
            value = "INVOKE",
            target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"
    ))
    private static <T> void vectorLib$prepareModifyOrReplaceFabric(
            Optional<T> table,
            Consumer<? super T> action,
            Operation<Void> original,
            @Local(argsOnly = true) ResourceLocation id,
            @Local(argsOnly = true) RegistryOps<JsonElement> ops
    )
    {
        VectorMixinCommons.lootTableMod(table, action, original, id, ops, VCTR$HOLDERLOOKUP);
    }
}
