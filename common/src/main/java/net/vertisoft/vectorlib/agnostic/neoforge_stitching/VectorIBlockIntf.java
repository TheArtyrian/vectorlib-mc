package net.vertisoft.vectorlib.agnostic.neoforge_stitching;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

/** Loader-independent interface that will handle rendering box distance for NeoForge. */
public interface VectorIBlockIntf<T extends BlockEntity>
{
    default AABB getVectorLibIntfRenderBox(T blockEntity) {
        return new AABB(blockEntity.getBlockPos());
    }
}
