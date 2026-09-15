package net.vertisoft.vectorlib.mixin.impl.loot;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.vertisoft.vectorlib.mixin_intf.loot.VectorPoolBuilderImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;

@Mixin(LootPool.Builder.class)
public class LootPoolBuilderMixin implements VectorPoolBuilderImpl
{
    @Shadow @Final private ImmutableList.Builder<LootPoolEntryContainer> entries;
    @Shadow @Final private ImmutableList.Builder<LootItemCondition> conditions;
    @Shadow @Final private ImmutableList.Builder<LootItemFunction> functions;

    @Override
    public LootPool.Builder vectorLib$addEntry(LootPoolEntryContainer entry)
    {
        this.entries.add(entry);
        return (LootPool.Builder)(Object)this;
    }

    @Override
    public LootPool.Builder vectorLib$addEntry(Collection<? extends LootPoolEntryContainer> entries)
    {
        this.entries.addAll(entries);
        return (LootPool.Builder)(Object)this;
    }

    @Override
    public LootPool.Builder vectorLib$addCondition(LootItemCondition condition)
    {
        this.conditions.add(condition);
        return (LootPool.Builder)(Object)this;
    }

    @Override
    public LootPool.Builder vectorLib$addCondition(Collection<? extends LootItemCondition> conditions)
    {
        this.conditions.addAll(conditions);
        return (LootPool.Builder)(Object)this;
    }

    @Override
    public LootPool.Builder vectorLib$applyFunction(LootItemFunction function)
    {
        this.functions.add(function);
        return (LootPool.Builder)(Object)this;
    }

    @Override
    public LootPool.Builder vectorLib$applyFunction(Collection<? extends LootItemFunction> functions)
    {
        this.functions.addAll(functions);
        return (LootPool.Builder)(Object)this;
    }
}
