package net.vertisoft.vectorlib.platform;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.commands.CommandSourceStack;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
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
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.util.VectorItemTab;
import net.vertisoft.vectorlib.agnostic.util.VectorTrade;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class VectorRegFabric implements VectorRegistryIntf
{
    @Override
    public <T> Supplier<T> register(String modid, String id, Registry<T> registry, Supplier<T> supplier)
    {
        T suppl = Registry.register(
                registry,
                ResourceLocation.fromNamespaceAndPath(modid, id),
                supplier.get()
        );
        return () -> suppl;
    }

    @Override
    public <T> Holder<T> registerHolder(String modid, String id, Registry<T> registry, Supplier<T> holder)
    {
        return Registry.registerForHolder(
                registry,
                ResourceLocation.fromNamespaceAndPath(modid, id),
                holder.get()
        );
    }

    @Override
    public <T> Holder.Reference<T> registerHolderRef(String modid, String id, Registry<T> registry, Supplier<T> holder)
    {
        return Registry.registerForHolder(
                registry,
                ResourceLocation.fromNamespaceAndPath(modid, id),
                holder.get()
        );
    }

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(String modid, String id, Supplier<BlockEntityType<T>> type)
    {
        BlockEntityType<T> register = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(modid, id), type.get());
        return () -> register;
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(BlockEntityData<T> data, Supplier<Block>... blocks)
    {
        var register = BlockEntityType.Builder.of(data::create, Arrays.stream(blocks).map(Supplier::get).toArray(Block[]::new)).build();
        return register;
    }

    @Override
    public <T extends EntityType<?>> Supplier<T> registerEntityType(String modid, String id, Supplier<T> entity)
    {
        var registrar = Registry.register(BuiltInRegistries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(modid, id), entity.get());
        return () -> registrar;
    }

    @Override
    public Holder<SoundEvent> registerSoundReference(String modid, String id)
    {
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT,
                ResourceLocation.fromNamespaceAndPath(modid, id), SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(modid, id)));
    }

    @Override
    public <T> Supplier<DataComponentType<T>> registerComponentType(String modid, String id, UnaryOperator<DataComponentType.Builder<T>> component_unary)
    {
        DataComponentType<T> instance = component_unary.apply(DataComponentType.builder()).build();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(modid, id), instance);
        return () -> instance;
    }

    @Override
    public Supplier<SimpleParticleType> registerParticleType(String modid, String id)
    {
        var register = Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(modid, id), FabricParticleTypes.simple());;
        return () -> register;
    }

    @Override
    public <T extends ParticleOptions> Supplier<ParticleType<T>> registerParticleType(String modid, String id, Function<ParticleType<T>, MapCodec<T>> codec, Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamcodec)
    {
        var register = Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(modid, id), new ParticleType<T>(false) {
            @NotNull public MapCodec<T> codec() { return codec.apply(this); }
            @NotNull public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() { return streamcodec.apply(this); }
        });
        return () -> register;
    }

    @Override
    public <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenu(String modid, String id, MenuData<T> data)
    {
        MenuType<T> registered = Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(modid, id), new MenuType<>(data::create, FeatureFlags.DEFAULT_FLAGS));
        return () -> registered;
    }

    @Override
    public <C extends FeatureConfiguration, F extends Feature<C>> Supplier<F> registerFeature(String modid, String id, Supplier<F> feature)
    {
        F registered = Registry.register(BuiltInRegistries.FEATURE, ResourceLocation.fromNamespaceAndPath(modid, id), feature.get());
        return () -> registered;
    }

    @Override
    public <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipeType(String modid, String id)
    {
        var registered = Registry.register(BuiltInRegistries.RECIPE_TYPE, ResourceLocation.fromNamespaceAndPath(modid, id), new RecipeType<T>() {});
        return () -> registered;
    }

    @Override
    public <T extends Recipe<?>> Supplier<RecipeSerializer<T>> registerRecipeSerializer(String modid, String id, RecipeSerializer<T> serializer)
    {
        var registered = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath(modid, id), serializer);
        return () -> registered;
    }

    @Override
    public <T extends CriterionTrigger<?>> Supplier<T> registerAdvCriteria(String modid, String id, Supplier<T> criterion)
    {
        var registered = Registry.register(BuiltInRegistries.TRIGGER_TYPES, ResourceLocation.fromNamespaceAndPath(modid, id), criterion.get());
        return () -> registered;
    }

    @Override
    public <T extends StructurePieceType> Supplier<T> registerStructurePiece(String modid, String id, Supplier<T> pieceType)
    {
        var registered = Registry.register(BuiltInRegistries.STRUCTURE_PIECE, ResourceLocation.fromNamespaceAndPath(modid, id), pieceType.get());
        return () -> registered;
    }

    @Override
    public <T extends Structure> Supplier<StructureType<T>> registerStructureType(String modid, String id, Supplier<MapCodec<T>> pieceType)
    {
        var registered = Registry.register(BuiltInRegistries.STRUCTURE_TYPE, ResourceLocation.fromNamespaceAndPath(modid, id), () ->  (MapCodec<Structure>)pieceType.get());
        return () -> (StructureType<T>)registered;
    }

    @Override
    public <T extends LootItemCondition> Supplier<LootItemConditionType> registerLootCondition(String modid, String id, Supplier<MapCodec<T>> loot)
    {
        var registered = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, ResourceLocation.fromNamespaceAndPath(modid, id), new LootItemConditionType(loot.get()));
        return () -> registered;
    }

    @Override
    public Supplier<PoiType> registerPoiType(String modId, String id, Set<BlockState> matchingStates, int maxTickets, int validRange)
    {
        var registered = PointOfInterestHelper.register(ResourceLocation.fromNamespaceAndPath(modId, id), maxTickets, validRange, matchingStates);
        return () -> registered;
    }

    @Override
    public void registerResourcePack(String requiredMod, String packId, Component name, boolean enforce, boolean defaultEnabled)
    {
        Optional<ModContainer> target = FabricLoader.getInstance().getModContainer(requiredMod);
        target.ifPresent(modContainer -> ResourceManagerHelper.registerBuiltinResourcePack(
                VectorLib.id(requiredMod, packId),
                modContainer,
                name,
                (enforce)
                        ? ResourcePackActivationType.ALWAYS_ENABLED
                        : (defaultEnabled ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL)
                )
        );
    }

    @Override
    public void registerVillagerTrade(Supplier<VectorTrade.Profession> trade)
    {
        VectorTrade.Profession unpacked = trade.get();
        TradeOfferHelper.registerVillagerOffers(unpacked.getJob(), unpacked.getLvl(), factories -> factories.add(unpacked.getTrade()));
    }

    @Override
    public void registerWanderingTrade(Supplier<VectorTrade.Wandering> trade)
    {
        VectorTrade.Wandering unpacked = trade.get();
        TradeOfferHelper.registerWanderingTraderOffers(unpacked.isRare() ? 2 : 1, factories -> factories.add(unpacked.getTrade()));
    }

    @Override
    public void addToCreativeTab(VectorItemTab tab, VectorItemTab.AddMode mode)
    {
        Pair<ResourceKey<CreativeModeTab>, List<Pair<ItemStack, ItemStack>>> list = tab.unpackPairs(mode);

        ResourceKey<CreativeModeTab> key = list.getFirst();
        List<Pair<ItemStack, ItemStack>> pairs = list.getSecond();

        ItemGroupEvents.modifyEntriesEvent(key).register(entries ->
        {
            for (Pair<ItemStack, ItemStack> entry : pairs)
            {
                if (mode == VectorItemTab.AddMode.BEFORE) entries.addBefore(entry.getFirst(), entry.getSecond());
                else entries.addAfter(entry.getFirst(), entry.getSecond());
            }
        });
    }

    @Override
    public void registerCommand(Consumer<CommandDispatcher<CommandSourceStack>> consumer)
    {
        CommandRegistrationCallback.EVENT.register((dispatcher, ctx, selection) -> consumer.accept(dispatcher));
    }

    @Override
    public <T> Supplier<T> getFromRegistry(Registry<T> registry, ResourceLocation location, T fallback)
    {
        return () -> registry.get(location);
    }

    @Override
    public void registerFuel(ItemLike item, int ticks)
    {
        FuelRegistry.INSTANCE.add(item, ticks);
    }

    @Override
    public void registerCompostable(Item item, float chance)
    {
        ComposterBlock.COMPOSTABLES.put(item, chance);
    }
}
