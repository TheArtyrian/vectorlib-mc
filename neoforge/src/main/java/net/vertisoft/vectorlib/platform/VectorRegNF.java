package net.vertisoft.vectorlib.platform;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.registrars.VectorPropertyReg;
import net.vertisoft.vectorlib.agnostic.util.VectorItemTab;
import net.vertisoft.vectorlib.agnostic.util.VectorTrade;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class VectorRegNF implements VectorRegistryIntf
{
    public static final List<Supplier<VectorTrade.Profession>> NF_TRADES_PROF = new ArrayList<>();
    public static final List<Supplier<VectorTrade.Wandering>> NF_TRADES_WAND = new ArrayList<>();
    public static final List<Consumer<CommandDispatcher<CommandSourceStack>>> NF_COMMANDS = new ArrayList<>();
    public static final Map<ItemLike, Integer> NF_FUELS = new HashMap<>();

    private static final Map<String, Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>>> MOD_REG = new HashMap<>();
    private static IEventBus EVENT_BUS = (ModList.get() != null) ? ModLoadingContext.get().getActiveContainer().getEventBus() : null;

    public void setEventBus(IEventBus eventBus) { EVENT_BUS = eventBus; }

    private Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> initRegistry(String modId)
    {
        if (!MOD_REG.containsKey(modId)) MOD_REG.put(modId, new HashMap<>());
        return MOD_REG.get(modId);
    }

    @Override
    public <T> Supplier<T> register(String modid, String id, Registry<T> registry, Supplier<T> supplier)
    {
        DeferredRegister<T> deferredRegister = DeferredRegister.create(registry.key(), modid);
        deferredRegister.register(EVENT_BUS);
        deferredRegister.register(id, supplier);
        return () -> registry.get(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> Holder<T> registerHolder(String modid, String id, Registry<T> registry, Supplier<T> holder)
    {
        DeferredRegister<T> defReg;
        var registries = initRegistry(modid);
        if (!registries.containsKey(registry.key())) {
            var i = DeferredRegister.create((ResourceKey)registry.key(), modid);
            i.register(EVENT_BUS);
            registries.put(registry.key(), i);
        }
        defReg = (DeferredRegister<T>)registries.get(registry.key());
        var register = defReg.register(id, holder);
        return register;
    }

    @Override
    public <T> Holder.Reference<T> registerHolderRef(String modid, String id, Registry<T> registry, Supplier<T> holder)
    {
        return (Holder.Reference<T>) registerHolder(modid, id, registry, holder);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(String modid, String id, Supplier<BlockEntityType<T>> type)
    {
        var registryKey = Registries.BLOCK_ENTITY_TYPE;
        var registries = initRegistry(modid);

        if (!registries.containsKey(registryKey))
        {
            var i = DeferredRegister.create((ResourceKey)registryKey, modid);
            i.register(EVENT_BUS);
            registries.put(registryKey, i);
        }
        var registry = (DeferredRegister<BlockEntityType<?>>) registries.get(registryKey);
        return registry.register(id, type);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(BlockEntityData<T> data, Supplier<Block>... blocks)
    {
        return BlockEntityType.Builder.of(data::create, Arrays.stream(blocks).map(Supplier::get).toArray(Block[]::new)).build(null);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends EntityType<?>> Supplier<T> registerEntityType(String modid, String id, Supplier<T> entity)
    {
        DeferredRegister<T> registry;
        var registries = initRegistry(modid);

        if (!registries.containsKey(Registries.ENTITY_TYPE))
        {
            var i = DeferredRegister.create(Registries.ENTITY_TYPE, modid);
            i.register(EVENT_BUS);
            registries.put(Registries.ENTITY_TYPE, i);
        }
        registry = (DeferredRegister<T>) registries.get(Registries.ENTITY_TYPE);
        registry.register(id, entity);
        return () -> (T) BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Holder<SoundEvent> registerSoundReference(String modid, String id)
    {
        DeferredRegister<SoundEvent> registry;
        var registries = initRegistry(modid);
        if (!registries.containsKey(Registries.SOUND_EVENT))
        {
            var i = DeferredRegister.create((ResourceKey) Registries.SOUND_EVENT, modid);
            i.register(EVENT_BUS);
            registries.put(Registries.SOUND_EVENT, i);
        }
        registry = (DeferredRegister<SoundEvent>) registries.get(Registries.SOUND_EVENT);
        var register = registry.register(id, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(modid, id)));
        return register;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> Supplier<DataComponentType<T>> registerComponentType(String modid, String id, UnaryOperator<DataComponentType.Builder<T>> component_unary)
    {
        DeferredRegister.DataComponents registry;
        var registries = initRegistry(modid);
        if (!registries.containsKey(Registries.DATA_COMPONENT_TYPE))
        {
            registry = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, modid);
            registry.register(EVENT_BUS);
            registries.put(Registries.DATA_COMPONENT_TYPE, registry);
        }
        else
        {
            registry = (DeferredRegister.DataComponents) registries.get(Registries.DATA_COMPONENT_TYPE);
        }
        registry.registerComponentType(id, component_unary);
        return () -> (DataComponentType<T>) BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Supplier<SimpleParticleType> registerParticleType(String modid, String id)
    {
        DeferredRegister<ParticleType<?>> registry;
        var registries = initRegistry(modid);
        if (!registries.containsKey(Registries.PARTICLE_TYPE))
        {
            var i = DeferredRegister.create(Registries.PARTICLE_TYPE, modid);
            i.register(EVENT_BUS);
            registries.put(Registries.PARTICLE_TYPE, i);
        }
        registry = (DeferredRegister<ParticleType<?>>) registries.get(Registries.PARTICLE_TYPE);
        registry.register(id, () -> new SimpleParticleType(false));
        return () -> (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends ParticleOptions> Supplier<ParticleType<T>> registerParticleType(String modid, String id, Function<ParticleType<T>, MapCodec<T>> codec, Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamcodec)
    {
        DeferredRegister<ParticleType<?>> registry;
        var registries = initRegistry(modid);
        if (!registries.containsKey(Registries.PARTICLE_TYPE))
        {
            var i = DeferredRegister.create(Registries.PARTICLE_TYPE, modid);
            i.register(EVENT_BUS);
            registries.put(Registries.PARTICLE_TYPE, i);
        }
        registry = (DeferredRegister<ParticleType<?>>) registries.get(Registries.PARTICLE_TYPE);

        registry.register(id, () -> new ParticleType<T>(false)
        {
            @NotNull public MapCodec<T> codec() { return codec.apply(this); }
            @NotNull public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() { return streamcodec.apply(this); }
        });

        return () -> (ParticleType<T>) BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenu(String modid, String id, MenuData<T> data)
    {
        DeferredRegister<MenuType<?>> registry;
        var registries = initRegistry(modid);
        if (!registries.containsKey(Registries.MENU))
        {
            var i = DeferredRegister.create(Registries.MENU, modid);
            i.register(EVENT_BUS);
            registries.put(Registries.MENU, i);
        }
        registry = (DeferredRegister<MenuType<?>>) registries.get(Registries.MENU);
        return registry.register(id, () -> new MenuType<>(data::create, FeatureFlags.DEFAULT_FLAGS));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <C extends FeatureConfiguration, F extends Feature<C>> Supplier<F> registerFeature(String modid, String id, Supplier<F> feature)
    {
        DeferredRegister<Feature<?>> registry;
        var registries = initRegistry(modid);
        if (!registries.containsKey(Registries.FEATURE))
        {
            var i = DeferredRegister.create(Registries.FEATURE, modid);
            i.register(EVENT_BUS);
            registries.put(Registries.FEATURE, i);
        }
        registry = (DeferredRegister<Feature<?>>) registries.get(Registries.FEATURE);
        return registry.register(id, feature);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipeType(String modid, String id)
    {
        DeferredRegister<RecipeType<?>> registry;
        var registries = initRegistry(modid);
        if (!registries.containsKey(Registries.RECIPE_TYPE))
        {
            var i = DeferredRegister.create(Registries.RECIPE_TYPE, modid);
            i.register(EVENT_BUS);
            registries.put(Registries.RECIPE_TYPE, i);
        }
        registry = (DeferredRegister<RecipeType<?>>) registries.get(Registries.RECIPE_TYPE);

        return registry.register(id, () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(modid, id)));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends Recipe<?>> Supplier<RecipeSerializer<T>> registerRecipeSerializer(String modid, String id, RecipeSerializer<T> serializer)
    {
        DeferredRegister<RecipeSerializer<?>> registry;
        var registries = initRegistry(modid);
        if (!registries.containsKey(Registries.RECIPE_SERIALIZER))
        {
            var i = DeferredRegister.create(Registries.RECIPE_SERIALIZER, modid);
            i.register(EVENT_BUS);
            registries.put(Registries.RECIPE_SERIALIZER, i);
        }
        registry = (DeferredRegister<RecipeSerializer<?>>) registries.get(Registries.RECIPE_SERIALIZER);

        return registry.register(id, () -> serializer);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends CriterionTrigger<?>> Supplier<T> registerAdvCriteria(String modid, String id, Supplier<T> criterion)
    {
        DeferredRegister<CriterionTrigger<?>> registry;
        var registries = initRegistry(modid);
        if (!registries.containsKey(Registries.TRIGGER_TYPE))
        {
            var i = DeferredRegister.create(Registries.TRIGGER_TYPE, modid);
            i.register(EVENT_BUS);
            registries.put(Registries.TRIGGER_TYPE, i);
        }
        registry = (DeferredRegister<CriterionTrigger<?>>) registries.get(Registries.TRIGGER_TYPE);

        return registry.register(id, criterion);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends StructurePieceType> Supplier<T> registerStructurePiece(String modid, String id, Supplier<T> pieceType)
    {
        DeferredRegister<StructurePieceType> registry;
        var registries = initRegistry(modid);
        if (!registries.containsKey(Registries.STRUCTURE_PIECE))
        {
            var i = DeferredRegister.create(Registries.STRUCTURE_PIECE, modid);
            i.register(EVENT_BUS);
            registries.put(Registries.STRUCTURE_PIECE, i);
        }
        registry = (DeferredRegister<StructurePieceType>) registries.get(Registries.STRUCTURE_PIECE);

        return registry.register(id, pieceType);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends Structure> Supplier<StructureType<T>> registerStructureType(String modid, String id, Supplier<MapCodec<T>> pieceType)
    {
        DeferredRegister<StructureType<T>> registry;
        var registries = initRegistry(modid);
        if (!registries.containsKey(Registries.STRUCTURE_TYPE))
        {
            var i = DeferredRegister.create(Registries.STRUCTURE_TYPE, modid);
            i.register(EVENT_BUS);
            registries.put(Registries.STRUCTURE_TYPE, i);
        }
        registry = (DeferredRegister<StructureType<T>>) registries.get(Registries.STRUCTURE_TYPE);

        return registry.register(id, () -> pieceType::get);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends LootItemCondition> Supplier<LootItemConditionType> registerLootCondition(String modid, String id, Supplier<MapCodec<T>> loot)
    {
        DeferredRegister<LootItemConditionType> registry;
        var registries = initRegistry(modid);
        if (!registries.containsKey(Registries.LOOT_CONDITION_TYPE))
        {
            var i = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, modid);
            i.register(EVENT_BUS);
            registries.put(Registries.LOOT_CONDITION_TYPE, i);
        }
        registry = (DeferredRegister<LootItemConditionType>) registries.get(Registries.LOOT_CONDITION_TYPE);
        return registry.register(id, () -> new LootItemConditionType(loot.get()));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Supplier<PoiType> registerPoiType(String modId, String id, Set<BlockState> matchingStates, int maxTickets, int validRange)
    {
        DeferredRegister<PoiType> registry;
        var registries = initRegistry(modId);

        if (!registries.containsKey(Registries.POINT_OF_INTEREST_TYPE))
        {
            var i = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, modId);
            i.register(EVENT_BUS);
            registries.put(Registries.POINT_OF_INTEREST_TYPE, i);
        }

        registry = (DeferredRegister<PoiType>) registries.get(Registries.POINT_OF_INTEREST_TYPE);
        return registry.register(id, () -> new PoiType(matchingStates, maxTickets, validRange));
    }

    @Override
    public void registerResourcePack(String requiredMod, String packId, Component name, boolean enforce, boolean defaultEnabled)
    {
        EVENT_BUS.addListener((AddPackFindersEvent event) -> {
            PackType packer = event.getPackType();
            if (packer.equals(PackType.CLIENT_RESOURCES))
            {
                Path to = ModList.get().getModFileById(requiredMod).getFile().findResource("resourcepacks/" + packId);
                event.addRepositorySource((source) ->
                {
                    PackLocationInfo location =                             new PackLocationInfo(requiredMod + ":" + packId, name, PackSource.BUILT_IN, Optional.empty());
                    PathPackResources.PathResourcesSupplier pathSupple =    new PathPackResources.PathResourcesSupplier(to);
                    Pack.Metadata metadat =                                 Pack.readPackMetadata(location, pathSupple, SharedConstants.getCurrentVersion().getPackVersion(packer));

                    if (metadat != null) source.accept(new Pack(location, pathSupple, metadat, new PackSelectionConfig(enforce, Pack.Position.TOP, false)));
                });
            }
        });
    }

    @Override
    public void registerVillagerTrade(Supplier<VectorTrade.Profession> trade)
    {
        if (!NF_TRADES_PROF.contains(trade)) NF_TRADES_PROF.add(trade);
        else throw new IllegalArgumentException("The professions trade list already contains this exact same trade");
    }

    @Override
    public void registerWanderingTrade(Supplier<VectorTrade.Wandering> trade)
    {
        if (!NF_TRADES_WAND.contains(trade)) NF_TRADES_WAND.add(trade);
        else throw new IllegalArgumentException("The wandering trade list already contains this exact same trade");
    }


    @Override
    public void addToCreativeTab(VectorItemTab tab, VectorItemTab.AddMode mode)
    {
        EVENT_BUS.addListener((BuildCreativeModeTabContentsEvent event) ->
        {
            Pair<ResourceKey<CreativeModeTab>, List<Pair<ItemStack, ItemStack>>> list = tab.unpackPairs(mode);

            ResourceKey<CreativeModeTab> key = list.getFirst();
            List<Pair<ItemStack, ItemStack>> pairs = list.getSecond();

            if (event.getTabKey().equals(key))
            {
                for (Pair<ItemStack, ItemStack> entry : pairs)
                {
                    if (mode == VectorItemTab.AddMode.BEFORE)
                    {
                        event.insertBefore(
                                entry.getFirst(),
                                entry.getSecond(),
                                CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
                        );
                    }
                    else
                    {
                        event.insertAfter(
                                entry.getFirst(),
                                entry.getSecond(),
                                CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
                        );
                    }
                }
            }
        });
    }

    @Override
    public void registerCommand(Consumer<CommandDispatcher<CommandSourceStack>> consumer)
    {
        if (!NF_COMMANDS.contains(consumer)) NF_COMMANDS.add(consumer);
        else throw new IllegalArgumentException("Another command exactly like the provided one is already marked for registration");
    }

    @Override
    public <T> Supplier<T> getFromRegistry(Registry<T> registry, ResourceLocation location, T fallback)
    {
        return () -> {
            Optional<T> test = registry.getOptional(location);
            return test.orElse(fallback);
        };
    }

    @Override
    public void registerFuel(ItemLike item, int ticks)
    {
        if (!NF_FUELS.containsKey(item)) NF_FUELS.put(item, ticks);
        else throw new IllegalArgumentException("The provided item is already registered as a fuel");
    }

    @Override
    public void registerCompostable(Item item, float chance)
    {
        VectorPropertyReg.Compost.get().put(item, chance);
    }
}
