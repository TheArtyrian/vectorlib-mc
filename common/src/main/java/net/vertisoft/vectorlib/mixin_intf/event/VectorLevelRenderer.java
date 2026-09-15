package net.vertisoft.vectorlib.mixin_intf.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface VectorLevelRenderer
{
    void vectorLib$runGameEvent(String mod, int type, BlockPos pos, int data);
    void vectorLib$runDualEvent(String mod, int type, Vec3 pos1, Vec3 pos2, int data);
    void vectorLib$runEntityEvent(String mod, int type, Entity entity, int data);
    void vectorLib$runGlobalEvent(String mod, int type, BlockPos pos, int data);
}
