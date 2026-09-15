package net.vertisoft.vectorlib.agnostic.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Stores information for a villager trade. */
public class VectorTrade
{
    public static final float LOW_MULT = 0.05F;
    public static final float HIGH_MULT = 0.2F;

    /** A trade meant for Wandering Traders. */
    public static class Wandering
    {
        private final VillagerTrades.ItemListing offer;
        private final boolean rare;

        public Wandering(boolean rare, VillagerTrades.ItemListing offer)
        {
            this.rare = rare;
            this.offer = offer;
        }

        public VillagerTrades.ItemListing getTrade() { return offer; }
        public boolean isRare() { return this.rare; }
    }

    /** A trade meant for villagers with professions. */
    public static class Profession
    {
        private final VillagerTrades.ItemListing offer;
        private final VillagerProfession profession;
        private final int level;

        public Profession(VillagerProfession prof, int level, VillagerTrades.ItemListing offer)
        {
            this.profession = prof;
            this.level = level;
            this.offer = offer;
        }

        public int getLvl() { return level; }
        public VillagerTrades.ItemListing getTrade() { return offer; }
        public VillagerProfession getJob() { return this.profession; }
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    /** Creates a trade that takes in items for emeralds */
    public static MerchantOffer forEmeralds(ItemLike desired, int count, int maxUses, int exp, int emeralds)
    {
        return new MerchantOffer(
                new ItemCost(desired, count),
                new ItemStack(Items.EMERALD, emeralds),
                maxUses,
                exp,
                LOW_MULT
        );
    }

    /** That one trade offer format everyone hates - a specific number of items for one emerald. */
    public static MerchantOffer singleEmerald(ItemLike desired, int count, int maxUses, int exp) { return forEmeralds(desired, count, maxUses, exp, 1); }

    /** Creates a MerchantOffer that gives items in exchange for emeralds. */
    public static MerchantOffer itemPurchase(ItemLike product, int count, int emeralds, int maxUses, int villagerXp, float multiplier)
    {
        return itemPurchaseRaw(product, count, Optional.empty(), emeralds, maxUses, villagerXp, multiplier);
    }

    /** Creates a MerchantOffer that gives items in exchange for emeralds. Takes in another item as a "material" - like Fishermen fish cooking trades. */
    public static MerchantOffer itemPurchase(ItemLike product, int productcount, ItemLike material, int materialcount, int emeralds, int maxUses, int villagerXp, float multiplier)
    {
        return itemPurchaseRaw(product, productcount, Optional.of(new ItemCost(material, materialcount)), emeralds, maxUses, villagerXp, multiplier);
    }

    /** Raw itemPurchase method - you shouldn't need to use this. */
    private static MerchantOffer itemPurchaseRaw(ItemLike product, int productCount, Optional<ItemCost> material, int emeralds, int maxUses, int villagerXp, float multiplier)
    {
        return new MerchantOffer(
                new ItemCost(Items.EMERALD, emeralds),
                material,
                new ItemStack(product, productCount),
                maxUses,
                villagerXp,
                multiplier
        );
    }

    /** Creates a MerchantOffer that provides a desired Explorer Map. */
    public static MerchantOffer explorerMap(
            Entity merchant, RandomSource random, int cost, TagKey<Structure> structure, String name, Holder<MapDecorationType> icon, int maxUses, int villagerXp
    )
    {
        if (!(merchant.level() instanceof ServerLevel serverlevel)) return null;

        BlockPos blockpos = serverlevel.findNearestMapStructure(structure, merchant.blockPosition(), 100, true);
        if (blockpos == null) return null;

        ItemStack itemstack = MapItem.create(serverlevel, blockpos.getX(), blockpos.getZ(), (byte)2, true, true);
        MapItem.renderBiomePreviewMap(serverlevel, itemstack);
        MapItemSavedData.addTargetDecoration(itemstack, blockpos, "+", icon);
        itemstack.set(DataComponents.ITEM_NAME, Component.translatable(name));
        return new MerchantOffer(
                new ItemCost(Items.EMERALD, cost), Optional.of(new ItemCost(Items.COMPASS)), itemstack, maxUses, villagerXp, 0.2F
        );
    }
}
