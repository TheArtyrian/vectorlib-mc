package net.vertisoft.vectorlib.mixin_intf.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import java.util.List;
import java.util.Optional;

public interface VectorLootTableImpl
{
    List<LootPool> vectorLib$getPoolList();
    List<LootItemFunction> vectorLib$getFuncts();
    Optional<ResourceLocation> vectorLib$sequenceId();
}