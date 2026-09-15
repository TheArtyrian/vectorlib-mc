package net.vertisoft.vectorlib.mixin.impl.loot;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.vertisoft.vectorlib.mixin_intf.loot.VectorLootPoolImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LootPool.class)
public class LootPoolMixin implements VectorLootPoolImpl
{
    @Shadow @Final private NumberProvider rolls;
    @Shadow @Final private NumberProvider bonusRolls;
    @Shadow @Final private List<LootPoolEntryContainer> entries;
    @Shadow @Final private List<LootItemCondition> conditions;
    @Shadow @Final private List<LootItemFunction> functions;

    @Override public NumberProvider vectorLib$rolls() { return this.rolls; }
    @Override public NumberProvider vectorLib$bonusRolls() { return this.bonusRolls; }
    @Override public List<LootPoolEntryContainer> vectorLib$entries() { return this.entries; }
    @Override public List<LootItemCondition> vectorLib$conditions() { return this.conditions; }
    @Override public List<LootItemFunction> vectorLib$functions() { return this.functions; }
}
