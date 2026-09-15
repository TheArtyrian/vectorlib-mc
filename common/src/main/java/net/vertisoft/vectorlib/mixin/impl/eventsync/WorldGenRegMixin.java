package net.vertisoft.vectorlib.mixin.impl.eventsync;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.vertisoft.vectorlib.mixin_intf.event.VectorLevelAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldGenRegion.class)
public abstract class WorldGenRegMixin implements VectorLevelAccess
{
    @Override public void vectorLib$fireEvent(@Nullable Player player, String mod, int type, BlockPos pos, int data) {}
    @Override public void vectorLib$fireDual(@Nullable Player player, String mod, int type, Vec3 pos1, Vec3 pos2, int data) {}
    @Override public void vectorLib$fireEntity(@Nullable Player player, String mod, int type, Entity entity, int data) {}
    @Override public void vectorLib$fireGlobal(@Nullable Player player, String mod, int type, BlockPos pos, int data) {}
}
