package net.vertisoft.vectorlib.mixin.impl.loot;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.vertisoft.vectorlib.mixin_intf.loot.VectorLootBuilderImpl;
import net.vertisoft.vectorlib.mixin_intf.loot.VectorPoolBuilderImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

@Mixin(LootTable.Builder.class)
public class LootBuilderMixin implements VectorLootBuilderImpl
{
    @Shadow @Final private ImmutableList.Builder<LootItemFunction> functions;
    @Shadow @Final @Mutable public ImmutableList.Builder<LootPool> pools;

    @Override
    public LootTable.Builder vectorLib$addPools(List<LootPool> list)
    {
        this.pools.addAll(list);
        return (LootTable.Builder)(Object)this;
    }

    @Override
    public LootTable.Builder vectorLib$addFuncts(List<? extends LootItemFunction> list)
    {
        this.functions.addAll(list);
        return (LootTable.Builder)(Object)this;
    }

    @Override
    public LootTable.Builder vLib$withAll(Consumer<? super LootPool.Builder> modifier)
    {
        ArrayList<LootPool> pools_built = new ArrayList<>(this.pools.build());
        ListIterator<LootPool> runthrough = pools_built.listIterator();

        while (runthrough.hasNext())
        {
            LootPool.Builder copy = VectorPoolBuilderImpl.deconstruct(runthrough.next());
            modifier.accept(copy);
            runthrough.set(copy.build());
        }

        this.pools = ImmutableList.builder();
        this.pools.addAll(pools_built);
        return (LootTable.Builder)(Object)this;
    }
}
