package net.vertisoft.vectorlib.platform;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.vertisoft.vectorlib.agnostic.util.VectorItemTab;
import net.vertisoft.vectorlib.agnostic.util.VectorTrade;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface VectorRegistryIntf
{
    <T> Supplier<T> register(String modid, String id, Registry<T> registry, Supplier<T> supplier);
    <T> Holder<T> registerHolder(String modid, String id, Registry<T> registry, Supplier<T> holder);
    <T> Holder.Reference<T> registerHolderRef(String modid, String id, Registry<T> registry, Supplier<T> holder);

    /** Registers both the Block and Item to their respective Minecraft registry.*/
    default <T extends Block> Supplier<T> registerBlock(String mod, String id, Supplier<T> block)
    {
        Supplier<T> registered = registerBlockNoItem(mod, id, block);
        registerItem(mod, id, () -> new BlockItem(registered.get(), new Item.Properties()));
        return registered;
    }

    /** Registers both the Block and Item to their respective Minecraft registry. Allows custom item definition. */
    default <T extends Block> Supplier<T> registerBlock(String modid, String id, Supplier<T> block, Item.Properties itemProperties)
    {
        Supplier<T> registeredBlock = registerBlockNoItem(modid, id, block);
        registerItem(modid, id, () -> new BlockItem(registeredBlock.get(), itemProperties));
        return registeredBlock;
    }

    /** Registers ONLY the Block to the respective Minecraft registry. */
    @SuppressWarnings("unchecked")
    default <T extends Block> Supplier<T> registerBlockNoItem(String modid, String id, Supplier<T> block)
    {
        return register(modid, id, (Registry<T>) BuiltInRegistries.BLOCK, block);
    }

    /** Registers an Item to its respective Minecraft registry. */
    @SuppressWarnings("unchecked")
    default <T extends Item> Supplier<T> registerItem(String modid, String id, Supplier<T> item)
    {
        return register(modid, id, (Registry<T>) BuiltInRegistries.ITEM, item);
    }

    /** Registers a Statistic to its respective Minecraft registry. */
    @SuppressWarnings("unchecked")
    default <T extends ResourceLocation> Supplier<T> registerStat(String modid, String id, Supplier<T> stat)
    {
        return register(modid, id, (Registry<T>) BuiltInRegistries.CUSTOM_STAT, stat);
    }

    /** Registers a flammable. */
    void registerFuel(ItemLike item, int ticks);

    /** Registers a compostable. */
    void registerCompostable(Item item, float chance);

    /** Registers a flammable. */
    default void registerFlammable(Block block, int burnChance, int spreadChance)
    {
        ((FireBlock)Blocks.FIRE).setFlammable(block, burnChance, spreadChance);
    }

    /** Registers a Block Entity type to the respective Minecraft registry. */
    <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(String modid, String id, Supplier<BlockEntityType<T>> type);

    /** Registers a Block Entity to the respective Minecraft registry. */
    <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(BlockEntityData<T> data, Supplier<Block>... blocks);

    /** Registers an Entity type to the respective Minecraft registry. */
    <T extends EntityType<?>> Supplier<T> registerEntityType(String modid, String id, Supplier<T> entity);

    /** Registers a Sound Event its respective Minecraft registry. */
    @SuppressWarnings("unchecked")
    default <T extends SoundEvent> Supplier<T> registerSoundEvent(String modid, String id, Supplier<T> soundevent)
    {
        return register(modid, id, (Registry<T>) BuiltInRegistries.SOUND_EVENT, soundevent);
        // () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(modid, id))
    }

    /** Registers a Sound Event reference holder - good for things like Jukebox songs. */
    Holder<SoundEvent> registerSoundReference(String modid, String id);

    /** Registers an Entity type to the respective Minecraft registry. */
    <T> Supplier<DataComponentType<T>> registerComponentType(String modid, String id, UnaryOperator<DataComponentType.Builder<T>> component_unary);

    /** Registers a Particle to the respective Minecraft registry. */
    Supplier<SimpleParticleType> registerParticleType(String modid, String id);

    /** Registers a Particle to the respective Minecraft registry. Can take in multiple parameters. */
    <T extends ParticleOptions> Supplier<ParticleType<T>> registerParticleType(String modid, String id, Function<ParticleType<T>, MapCodec<T>> codec, Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamcodec);

    /** Registers a Screen type to the respective Minecraft registry. */
    <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenu(String modid, String id, MenuData<T> data);

    /** Registers a Feature to the respective Minecraft registry. */
    <C extends FeatureConfiguration, F extends Feature<C>> Supplier<F> registerFeature(String modid, String id, Supplier<F> feature);

    /** Registers a Recipe type to the respective Minecraft registry. */
    <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipeType(String modid, String id);

    /** Registers a Recipe serializer to the respective Minecraft registry. */
    <T extends Recipe<?>> Supplier<RecipeSerializer<T>> registerRecipeSerializer(String modid, String id, RecipeSerializer<T> serializer);

    /** Registers an Advancement criterion to the respective Minecraft registry. */
    <T extends CriterionTrigger<?>> Supplier<T> registerAdvCriteria(String modid, String id, Supplier<T> criterion);

    /** Registers a Structure Piece. */
    <T extends StructurePieceType> Supplier<T> registerStructurePiece(String modid, String id, Supplier<T> pieceType);

    /** Registers a Structure Type. */
    <T extends Structure> Supplier<StructureType<T>> registerStructureType(String modid, String id, Supplier<MapCodec<T>> pieceType);

    /** Registers a Loot condition. */
    <T extends LootItemCondition> Supplier<LootItemConditionType> registerLootCondition(String modid, String id, Supplier<MapCodec<T>> loot);

    /** Registers a POI type. */
    Supplier<PoiType> registerPoiType(String modId, String id, Set<BlockState> matchingStates, int maxTickets, int validRange);

    /** Registers a built-in Resource Pack. */
    void registerResourcePack(String requiredMod, String packId, Component name, boolean enforce, boolean defaultEnabled);

    /** Registers a built-in Resource Pack (if the enable boolean is true). */
    default void registerResourcePackConditionally(boolean enable, String requiredMod, String packId, Component name, boolean enforce, boolean defaultEnabled)
    {
        if (enable) this.registerResourcePack(requiredMod, packId, name, enforce, defaultEnabled);
    }

    /** Registers a regular Villager trade. */
    void registerVillagerTrade(Supplier<VectorTrade.Profession> trade);

    /** Registers a Wandering Trader trade. */
    void registerWanderingTrade(Supplier<VectorTrade.Wandering> trade);

    /** Adds all data from a VectorItemTab to its attached Creative Mode tab. */
    void addToCreativeTab(VectorItemTab tab, VectorItemTab.AddMode mode);

    /** Registers a command. */
    void registerCommand(Consumer<CommandDispatcher<CommandSourceStack>> consumer);

    /** Gets a value from a registry, useful for mod integrations, etc.*/
    <T> Supplier<T> getFromRegistry(Registry<T> registry, ResourceLocation location, T fallback);

    /** NexusLib is literally carrying me btw dont sue me hecco */
    @FunctionalInterface
    interface BlockEntityData<T extends BlockEntity>
    {
        @NotNull T create(BlockPos pos, BlockState state);
    }

    @FunctionalInterface
    interface MenuData<T extends AbstractContainerMenu>
    {
        @NotNull T create(int num, Inventory inventory);
    }
}
