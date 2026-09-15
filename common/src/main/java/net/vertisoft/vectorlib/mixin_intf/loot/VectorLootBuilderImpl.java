package net.vertisoft.vectorlib.mixin_intf.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@NonExtendable
public interface VectorLootBuilderImpl
{
    LootTable.Builder vectorLib$addPools(List<LootPool> list);
    LootTable.Builder vectorLib$addFuncts(List<? extends LootItemFunction> list);

    /** Modifies all loot pools in a loot table using the provided consumer. */
    LootTable.Builder vLib$withAll(Consumer<? super LootPool.Builder> modifier);

    /** "Deconstructs" a Loot Table into a builder, allowing for modifications. */
    static LootTable.Builder deconstruct(LootTable table)
    {
        LootTable.Builder builder = LootTable.lootTable();
        builder.setParamSet(table.getParamSet());

        VectorLootTableImpl impl = ((VectorLootTableImpl)table);
        VectorLootBuilderImpl buildimpl = ((VectorLootBuilderImpl)builder);

        buildimpl.vectorLib$addPools(impl.vectorLib$getPoolList());
        buildimpl.vectorLib$addFuncts(impl.vectorLib$getFuncts());

        Optional<ResourceLocation> sequence = impl.vectorLib$sequenceId();
        sequence.ifPresent(builder::setRandomSequence);

        return builder;
    }
}