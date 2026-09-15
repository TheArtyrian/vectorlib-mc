package net.vertisoft.vectorlib.mixin.impl.chunksync;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.vertisoft.vectorlib.agnostic.networking.chunksync.VectorChunkSync;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Debug(export = true)
@Mixin(ChunkSerializer.class)
public class ChunkSerialMixin
{
    @WrapOperation(method = "read", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/Heightmap;primeHeightmaps(Lnet/minecraft/world/level/chunk/ChunkAccess;Ljava/util/Set;)V")
    )
    private static void vectorLib$chunkSyncRead(ChunkAccess heightmap$types, Set<Heightmap.Types> heightmap, Operation<Void> original, @Local(argsOnly = true) CompoundTag tag)
    {
        VectorChunkSync.read(heightmap$types, tag);
        original.call(heightmap$types, heightmap);
    }

    @WrapOperation(method = "write", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/NbtUtils;addCurrentDataVersion(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;")
    )
    private static CompoundTag vectorLib$chunkSyncWrite(CompoundTag tag, Operation<CompoundTag> original, @Local(argsOnly = true) ChunkAccess chunk)
    {
        VectorChunkSync.write(chunk, tag);
        return original.call(tag);
    }
}
