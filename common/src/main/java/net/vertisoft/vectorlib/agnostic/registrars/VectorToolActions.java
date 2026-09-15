package net.vertisoft.vectorlib.agnostic.registrars;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.vertisoft.vectorlib.VectorLib;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/** ...to be fair, I was the one that added this to NexusLib. :sob: */
public class VectorToolActions
{
    /** A list of registered axe strippables. */
    private static final Map<Block, Block> LOGS = new LinkedHashMap<>();
    /** Returns the strippables list. */
    public static Map<Block, Block> getStrippables() { return LOGS; }
    /** Adds a strippable pair to the list. Will fail if both blocks don't have the AXIS property from {@link RotatedPillarBlock}. */
    public static void addStrippable(Block log, Block stripped)
    {
        boolean noLogAxis = !log.defaultBlockState().hasProperty(RotatedPillarBlock.AXIS);
        boolean noStripAxis = !stripped.defaultBlockState().hasProperty(RotatedPillarBlock.AXIS);

        if (noLogAxis || noStripAxis)
        {
            String output;
            if (noLogAxis && noStripAxis) output = "all";
            else output = (noLogAxis) ? log.toString() : stripped.toString();

            VectorLib.LOGGER.error("Unable to register stripping behavior for {} and {} (missing rotation property in {})", log, stripped, output);
        }
        else
        {
            LOGS.put(log, stripped);
        }
    }
    /** Adds a strippable pair to the list. */
    public static void addStrippable(Supplier<Block> log, Supplier<Block> stripped) { addStrippable(log.get(), stripped.get()); }

/////////////////////////////////////////////////////////////////
    /** A list of registered shovel pathables. */
    private static final Map<Block, BlockState> PATHS = new LinkedHashMap<>();
    /** Returns the pathables list. */
    public static Map<Block, BlockState> getPathables() { return PATHS; }
    /** Adds a pathable pair to the list. */
    public static void addPathable(Block block, Block path) {
        PATHS.put(block, path.defaultBlockState());
    }
    /** Adds a pathable pair to the list. Uses suppliers.*/
    public static void addPathable(Supplier<Block> block, Supplier<Block> path) { addPathable(block.get(), path.get()); }

/////////////////////////////////////////////////////////////////
    /** A list of registered hoe tillables. */
    private static final Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> TILLABLES = new LinkedHashMap<>();
    /** Returns the tillables list. */
    public static Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> getTillables() { return TILLABLES; }
    /** Adds a tillable to the list. Allows the most control over adding: provide a predicate and consumer. */
    public static void addTillableAdv(Block block, Predicate<UseOnContext> predicate, Consumer<UseOnContext> consumer)
    {
        TILLABLES.put(block, Pair.of(predicate, consumer));
    }
    /** Adds a basic tillable to the list. */
    public static void addTillable(Block block, Block tilled)
    {
        addTillableAdv(block, HoeItem::onlyIfAirAbove, HoeItem.changeIntoState(tilled.defaultBlockState()));
    }
    /** Adds a tillable to the list. When tilled, it will drop an item. */
    public static void addTillableWithDrop(Block block, Block tilled, ItemLike output, boolean requireAirAbove)
    {
        Predicate<UseOnContext> inputPred = (requireAirAbove) ? HoeItem::onlyIfAirAbove : (blah) -> true;
        addTillableAdv(block, inputPred, HoeItem.changeIntoStateAndDropItem(tilled.defaultBlockState(), output));
    }

    // who tf are you lmfaooooooooo
    public static void addTillable(Supplier<Block> block, Supplier<Block> tilled) { addTillable(block.get(), tilled.get()); }
    public static void addTillableWithDrop(Supplier<Block> block, Supplier<Block> tilled, ItemLike output, boolean requireAirAbove) { addTillableWithDrop(block.get(), tilled.get(), output, requireAirAbove); }
}
