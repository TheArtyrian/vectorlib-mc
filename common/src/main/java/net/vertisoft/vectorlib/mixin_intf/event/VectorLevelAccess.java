package net.vertisoft.vectorlib.mixin_intf.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface VectorLevelAccess
{
    void vectorLib$fireEvent(@Nullable Player player, String mod, int type, BlockPos pos, int data);
    void vectorLib$fireDual(@Nullable Player player, String mod, int type, Vec3 pos1, Vec3 pos2, int data);
    void vectorLib$fireEntity(@Nullable Player player, String mod, int type, Entity entity, int data);
    void vectorLib$fireGlobal(@Nullable Player player, String mod, int type, BlockPos pos, int data);
}
