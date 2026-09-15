package net.vertisoft.vectorlib.mixin_intf.loot;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.List;

public interface VectorLootPoolImpl
{
    NumberProvider vectorLib$rolls();
    NumberProvider vectorLib$bonusRolls();
    List<LootPoolEntryContainer> vectorLib$entries();
    List<LootItemCondition> vectorLib$conditions();
    List<LootItemFunction> vectorLib$functions();
}
