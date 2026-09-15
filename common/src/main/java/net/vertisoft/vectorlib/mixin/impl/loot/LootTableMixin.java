package net.vertisoft.vectorlib.mixin.impl.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.vertisoft.vectorlib.mixin_intf.loot.VectorLootTableImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;

@Mixin(LootTable.class)
public class LootTableMixin implements VectorLootTableImpl
{
    @Shadow @Final private List<LootPool> pools;
    @Shadow @Final private List<LootItemFunction> functions;
    @Shadow @Final private Optional<ResourceLocation> randomSequence;

    @Override public List<LootPool> vectorLib$getPoolList() { return this.pools; }
    @Override public List<LootItemFunction> vectorLib$getFuncts() { return this.functions; }
    @Override public Optional<ResourceLocation> vectorLib$sequenceId() { return this.randomSequence; }
}