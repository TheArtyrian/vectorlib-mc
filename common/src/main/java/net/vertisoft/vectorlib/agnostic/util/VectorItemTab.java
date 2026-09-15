package net.vertisoft.vectorlib.agnostic.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.vertisoft.vectorlib.VectorLib;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** A cross-platform class used to register items to existing creative tabs. */
public class VectorItemTab
{
    private final ResourceKey<CreativeModeTab> key;
    private final boolean enabled;
    private final List<Pair<ItemStack, ItemStack>> BEFORE = new ArrayList<>();
    private final List<Pair<ItemStack, ItemStack>> AFTER = new ArrayList<>();
    private final List<ItemStack> ORDERED = new ArrayList<>();

    public static VectorItemTab create(VanillaTab tab) { return new VectorItemTab(tab.key(), true, null); }
    public static VectorItemTab create(VanillaTab tab, List<VectorItemTab> iterator) { return new VectorItemTab(tab.key(), true, iterator); }
    public static VectorItemTab create(ResourceKey<CreativeModeTab> key, boolean enabled, @Nullable List<VectorItemTab> iterator) { return new VectorItemTab(key, enabled, iterator); }

    private VectorItemTab(ResourceKey<CreativeModeTab> key, boolean enabled, @Nullable List<VectorItemTab> iterator)
    {
        this.key = key;
        this.enabled = enabled;
        if (iterator != null)
        {
            iterator.add(this);
        }
    }

    public void addBefore(ItemLike before, ItemLike whatToAdd) { this.addBefore(before.asItem().getDefaultInstance(), whatToAdd.asItem().getDefaultInstance(), null); }
    public void addBefore(ItemStack before, ItemLike whatToAdd) { this.addBefore(before, whatToAdd.asItem().getDefaultInstance(), null); }
    public void addBefore(ItemStack before, ItemStack whatToAdd) { this.addBefore(before, whatToAdd, null); }
    public void addBefore(ItemLike before, ItemLike whatToAdd, VectorItemTab consolidator) { this.addBefore(before.asItem().getDefaultInstance(), whatToAdd.asItem().getDefaultInstance(), consolidator); }
    public void addBefore(ItemStack before, ItemLike whatToAdd, VectorItemTab consolidator) { this.addBefore(before, whatToAdd.asItem().getDefaultInstance(), consolidator); }
    public void addBefore(ItemStack before, ItemStack whatToAdd, @Nullable VectorItemTab consolidator)
    {
        this.BEFORE.add(Pair.of(before, whatToAdd));
    }

    public void addAfter(ItemLike after, ItemLike whatToAdd) { this.addAfter(after.asItem().getDefaultInstance(), whatToAdd.asItem().getDefaultInstance(), null); }
    public void addAfter(ItemStack after, ItemLike whatToAdd) { this.addAfter(after, whatToAdd.asItem().getDefaultInstance(), null); }
    public void addAfter(ItemStack after, ItemStack whatToAdd) { this.addAfter(after, whatToAdd, null); }
    public void addAfter(ItemLike after, ItemLike whatToAdd, VectorItemTab consolidator) { this.addAfter(after.asItem().getDefaultInstance(), whatToAdd.asItem().getDefaultInstance(), consolidator); }
    public void addAfter(ItemStack after, ItemLike whatToAdd, VectorItemTab consolidator) { this.addAfter(after, whatToAdd.asItem().getDefaultInstance(), consolidator); }
    public void addAfter(ItemStack after, ItemStack whatToAdd, @Nullable VectorItemTab consolidator)
    {
        this.AFTER.add(Pair.of(after, whatToAdd));
    }

    public String keyNamespace() { return this.key.location().getNamespace(); }
    public String keyPath() { return this.key.location().getPath(); }

    public void build()
    {
        if (this.enabled)
        {
            if (!this.BEFORE.isEmpty()) VectorLib.REGISTRY.addToCreativeTab(this, AddMode.BEFORE);
            if (!this.AFTER.isEmpty()) VectorLib.REGISTRY.addToCreativeTab(this, AddMode.AFTER);
        }
        else VectorLib.LOGGER.warn("VectorItemTab with ResourceKey {} is disabled, skipping build phase", this.key);
    }

    public Pair<ResourceKey<CreativeModeTab>, List<Pair<ItemStack, ItemStack>>> unpackPairs(VectorItemTab.AddMode mode)
    {
        return Pair.of(
                this.key,
                (mode == VectorItemTab.AddMode.BEFORE) ? this.BEFORE : this.AFTER
        );
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof VectorItemTab tab)) return false;
        return this.key.equals(tab.key) && this.BEFORE.equals(tab.BEFORE) && this.AFTER.equals(tab.AFTER);
    }

    public enum AddMode
    {
        BEFORE,
        AFTER,
    }

    public enum VanillaTab
    {
        BUILDING(createKey("building_blocks")),
        COLORED(createKey("colored_blocks")),
        NATURAL(createKey("natural_blocks")),
        FUNCTIONAL(createKey("functional_blocks")),
        REDSTONE(createKey("redstone_blocks")),
        TOOLS(createKey("tools_and_utilities")),
        COMBAT(createKey("combat")),
        FOOD(createKey("food_and_drinks")),
        INGREDIENTS(createKey("ingredients")),
        SPAWN_EGGS(createKey("spawn_eggs")),
        OPERATOR(createKey("op_blocks"));

        private final ResourceKey<CreativeModeTab> key;

        VanillaTab(ResourceKey<CreativeModeTab> key) { this.key = key; }

        public ResourceKey<CreativeModeTab> key() { return this.key;}

        private static ResourceKey<CreativeModeTab> createKey(String name) { return ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.withDefaultNamespace(name)); }
    }
}
