package net.vertisoft.vectorlib.mixin_intf.loot;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Collection;

public interface VectorPoolBuilderImpl
{
    LootPool.Builder vectorLib$addEntry(LootPoolEntryContainer entry);
    LootPool.Builder vectorLib$addEntry(Collection<? extends LootPoolEntryContainer> entries);

    LootPool.Builder vectorLib$addCondition(LootItemCondition condition);
    LootPool.Builder vectorLib$addCondition(Collection<? extends LootItemCondition> conditions);

    LootPool.Builder vectorLib$applyFunction(LootItemFunction function);
    LootPool.Builder vectorLib$applyFunction(Collection<? extends LootItemFunction> functions);

    /** "Deconstructs" a Loot Pool into a builder, allowing for modifications. */
    static LootPool.Builder deconstruct(LootPool pool)
    {
        LootPool.Builder builder = LootPool.lootPool();

        VectorLootPoolImpl impl = ((VectorLootPoolImpl)pool);
        VectorPoolBuilderImpl buildimpl = ((VectorPoolBuilderImpl)builder);

        builder.setRolls(impl.vectorLib$rolls());
        builder.setBonusRolls(impl.vectorLib$bonusRolls());

        buildimpl.vectorLib$addEntry(impl.vectorLib$entries());
        buildimpl.vectorLib$addCondition(impl.vectorLib$conditions());
        buildimpl.vectorLib$applyFunction(impl.vectorLib$functions());

        return builder;
    }
}
