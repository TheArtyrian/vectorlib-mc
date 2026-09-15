package net.vertisoft.vectorlib.agnostic.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.Vec3;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.networking.eventsync.VectorEventSync;
import net.vertisoft.vectorlib.agnostic.util.VLibUtil;
import net.vertisoft.vectorlib.mixin_intf.event.VectorLevelAccess;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

/** A debugging class meant for calling level events at specific locations. */
public class VLEventCommand
{
    private static final String VECTORLIB = "vectorlib";
    private static final String VANILLA = "vanilla";

    private static final String VL_LOCAL = "local";
    private static final String VL_DUAL = "dual";
    private static final String VL_ENT = "entity";
    private static final String VL_GLOB = "global";

    private static final String NAMESPACE = "namespace";
    private static final String EV_NAME = "event_name";
    private static final String DATA = "data";
    private static final String POS = "pos";

    public VLEventCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
            // VL_EVENT base
            Commands.literal("vl_event").requires((stack) -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS) && VectorLib.CONFIG.doVectorCommands())
                // VectorEventSync
                .then(
                    Commands.literal(VECTORLIB)
                    .then(
                            // List
                            Commands.literal("list")
                                    .then(
                                            Commands.literal(VL_LOCAL)
                                                .executes((stack) -> listVLEvents(stack, EventType.LOCAL, null))
                                                .then(Commands.argument(NAMESPACE, StringArgumentType.word())
                                                    .executes((stack) -> listVLEvents(stack, EventType.LOCAL, StringArgumentType.getString(stack, NAMESPACE)))
                                                )
                                    )
                                    .then(
                                            Commands.literal(VL_DUAL)
                                                .executes((stack) -> listVLEvents(stack, EventType.DUAL, null))
                                                .then(Commands.argument(NAMESPACE, StringArgumentType.word())
                                                    .executes((stack) -> listVLEvents(stack, EventType.DUAL, StringArgumentType.getString(stack, NAMESPACE)))
                                                )
                                    )
                                    .then(
                                            Commands.literal(VL_ENT)
                                                .executes((stack) -> listVLEvents(stack, EventType.ENTITY, null))
                                                .then(Commands.argument(NAMESPACE, StringArgumentType.word())
                                                    .executes((stack) -> listVLEvents(stack, EventType.ENTITY, StringArgumentType.getString(stack, NAMESPACE)))
                                                )
                                    )
                                    .then(
                                            Commands.literal(VL_GLOB)
                                                .executes((stack) -> listVLEvents(stack, EventType.GLOBAL, null))
                                                .then(Commands.argument(NAMESPACE, StringArgumentType.word())
                                                    .executes((stack) -> listVLEvents(stack, EventType.GLOBAL, StringArgumentType.getString(stack, NAMESPACE)))
                                                )
                                    )
                    )
                    .then(
                        // Local Events
                        Commands.literal(VL_LOCAL)
                        .then(Commands.argument(NAMESPACE, StringArgumentType.word())
                            .then(Commands.argument(EV_NAME, StringArgumentType.word())
                                .then(Commands.argument(POS, BlockPosArgument.blockPos())
                                    .then(Commands.argument(DATA, IntegerArgumentType.integer())
                                        .executes((stack) -> executeVLLocal(
                                                stack,
                                                StringArgumentType.getString(stack, NAMESPACE),
                                                StringArgumentType.getString(stack, EV_NAME),
                                                BlockPosArgument.getLoadedBlockPos(stack, POS),
                                                IntegerArgumentType.getInteger(stack, DATA)
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                    .then(
                        // Dual Events
                        Commands.literal(VL_DUAL)
                        .then(Commands.argument(NAMESPACE, StringArgumentType.word())
                            .then(Commands.argument(EV_NAME, StringArgumentType.word())
                                .then(Commands.argument("primary_pos", Vec3Argument.vec3())
                                    .then(Commands.argument("secondary_pos", Vec3Argument.vec3())
                                        .then(Commands.argument(DATA, IntegerArgumentType.integer())
                                            .executes((stack) -> executeVLDual(
                                                    stack,
                                                    StringArgumentType.getString(stack, NAMESPACE),
                                                    StringArgumentType.getString(stack, EV_NAME),
                                                    Vec3Argument.getVec3(stack, "primary_pos"),
                                                    Vec3Argument.getVec3(stack, "secondary_pos"),
                                                    IntegerArgumentType.getInteger(stack, DATA)
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                    .then(
                        // Entity Events
                        Commands.literal(VL_ENT)
                        .then(Commands.argument(NAMESPACE, StringArgumentType.word())
                            .then(Commands.argument(EV_NAME, StringArgumentType.word())
                                .then(Commands.argument("entity", EntityArgument.entity())
                                    .then(Commands.argument(DATA, IntegerArgumentType.integer())
                                        .executes((stack) -> executeVLEntity(
                                                stack,
                                                StringArgumentType.getString(stack, NAMESPACE),
                                                StringArgumentType.getString(stack, EV_NAME),
                                                EntityArgument.getEntity(stack, "entity"),
                                                IntegerArgumentType.getInteger(stack, DATA)
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                    .then(
                        // Global Events
                        Commands.literal(VL_GLOB)
                        .then(Commands.argument(NAMESPACE, StringArgumentType.word())
                            .then(Commands.argument(EV_NAME, StringArgumentType.word())
                                .then(Commands.argument(POS, BlockPosArgument.blockPos())
                                    .then(Commands.argument(DATA, IntegerArgumentType.integer())
                                        .executes((stack) -> executeVLGlobal(
                                                stack,
                                                StringArgumentType.getString(stack, NAMESPACE),
                                                StringArgumentType.getString(stack, EV_NAME),
                                                BlockPosArgument.getLoadedBlockPos(stack, POS),
                                                IntegerArgumentType.getInteger(stack, DATA)
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
                // Vanilla Level Events
                .then(
                    Commands.literal(VANILLA)
                    .then(Commands.argument("event_id", IntegerArgumentType.integer())
                        .then(Commands.argument(POS, BlockPosArgument.blockPos())
                            .then(Commands.argument(DATA, IntegerArgumentType.integer())
                                .executes((stack) -> executeVanillaAtSet(
                                        stack,
                                        IntegerArgumentType.getInteger(stack, "event_id"),
                                        BlockPosArgument.getLoadedBlockPos(stack, POS),
                                        IntegerArgumentType.getInteger(stack, DATA),
                                        false
                                    )
                                )
                                .then(Commands.literal(VL_GLOB)
                                    .executes((stack) -> executeVanillaAtSet(
                                            stack,
                                            IntegerArgumentType.getInteger(stack, "event_id"),
                                            BlockPosArgument.getLoadedBlockPos(stack, POS),
                                            IntegerArgumentType.getInteger(stack, DATA),
                                            true
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
        );
    }

    private static int listVLEvents(CommandContext<CommandSourceStack> stack, EventType type, @Nullable String namespace)
    {
        String mode;
        List<String> spacesToCheck = new ArrayList<>();
        Map<String, List<String>> returnable = new HashMap<>();
        try
        {
            switch (type)
            {
                case LOCAL -> {
                    mode = VLibUtil.capital(VL_LOCAL);
                    if (namespace != null)
                    {
                        if (VectorEventSync.Local.EVENT_MAPDEX.containsKey(namespace)) spacesToCheck.add(namespace);
                        else
                        {
                            stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.eventsync_no_namespace", VLibUtil.capital(VL_LOCAL)));
                            return 0;
                        }
                    }
                    else spacesToCheck.addAll(VectorEventSync.Local.EVENT_MAPDEX.keySet());


                    for (String loc : spacesToCheck)
                    {
                        if (!returnable.containsKey(loc))
                        {
                            List<String> reference = new ArrayList<>();
                            returnable.put(loc, reference);

                            for (ResourceLocation loc2 : VectorEventSync.Local.MAP_FREEZER.keySet())
                            {
                                if (loc2.getNamespace().equals(loc)) reference.add(loc2.getPath());
                            }
                        }
                    }
                }
                case DUAL -> {
                    mode = VLibUtil.capital(VL_DUAL);
                    if (namespace != null)
                    {
                        if (VectorEventSync.Dual.EVENT_MAPDEX.containsKey(namespace)) spacesToCheck.add(namespace);
                        else
                        {
                            stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.eventsync_no_namespace", VLibUtil.capital(VL_DUAL)));
                            return 0;
                        }
                    }
                    else spacesToCheck.addAll(VectorEventSync.Dual.EVENT_MAPDEX.keySet());

                    for (String loc : spacesToCheck)
                    {
                        if (!returnable.containsKey(loc))
                        {
                            List<String> reference = new ArrayList<>();
                            returnable.put(loc, reference);

                            for (ResourceLocation loc2 : VectorEventSync.Dual.MAP_FREEZER.keySet())
                            {
                                if (loc2.getNamespace().equals(loc)) reference.add(loc2.getPath());
                            }
                        }
                    }
                }
                case ENTITY -> {
                    mode = VLibUtil.capital(VL_ENT);
                    if (namespace != null)
                    {
                        if (VectorEventSync.Entity.EVENT_MAPDEX.containsKey(namespace)) spacesToCheck.add(namespace);
                        else
                        {
                            stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.eventsync_no_namespace", VLibUtil.capital(VL_ENT)));
                            return 0;
                        }
                    }
                    else spacesToCheck.addAll(VectorEventSync.Entity.EVENT_MAPDEX.keySet());

                    for (String loc : spacesToCheck)
                    {
                        if (!returnable.containsKey(loc))
                        {
                            List<String> reference = new ArrayList<>();
                            returnable.put(loc, reference);

                            for (ResourceLocation loc2 : VectorEventSync.Entity.MAP_FREEZER.keySet())
                            {
                                if (loc2.getNamespace().equals(loc)) reference.add(loc2.getPath());
                            }
                        }
                    }
                }
                case GLOBAL -> {
                    mode = VLibUtil.capital(VL_GLOB);
                    if (namespace != null)
                    {
                        if (VectorEventSync.Global.EVENT_MAPDEX.containsKey(namespace)) spacesToCheck.add(namespace);
                        else
                        {
                            stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.eventsync_no_namespace", VLibUtil.capital(VL_GLOB)));
                            return 0;
                        }
                    }
                    else spacesToCheck.addAll(VectorEventSync.Global.EVENT_MAPDEX.keySet());

                    for (String loc : spacesToCheck)
                    {
                        if (!returnable.containsKey(loc))
                        {
                            List<String> reference = new ArrayList<>();
                            returnable.put(loc, reference);

                            for (ResourceLocation loc2 : VectorEventSync.Global.MAP_FREEZER.keySet())
                            {
                                if (loc2.getNamespace().equals(loc)) reference.add(loc2.getPath());
                            }
                        }
                    }
                }
                default -> { throw new NoSuchElementException("EventType of " + type.name()); }
            }

            if (!returnable.isEmpty())
            {
                for (String mod : returnable.keySet())
                {
                    StringBuilder fulllist = new StringBuilder();

                    Iterator<String> itr = returnable.get(mod).iterator();
                    while (itr.hasNext())
                    {
                        String xyz = itr.next();
                        fulllist.append(xyz);
                        if (itr.hasNext()) fulllist.append(", ");
                    }

                    stack.getSource().sendSuccess(
                            () -> Component.translatable("commands.vectorlib.event.eventsync.list_events",
                                    mode,
                                    Component.literal(mod).withStyle(ChatFormatting.YELLOW),
                                    Component.literal(fulllist.toString()).withStyle(ChatFormatting.AQUA)),
                            true);
                }
                return 1;
            }
            else
            {
                stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.eventsync.list_fail", mode));
                return 0;
            }
        }
        catch (NoSuchElementException excp)
        {
            stack.getSource().sendFailure(Component.translatable("command.failed"));
            VectorLib.LOGGER.error("listVLEvents couldn't execute because somehow someone provided an illegal fucking eventtype, genuinely what are we doing", excp);
        }
        return 0;
    }

    private static int executeVLLocal(CommandContext<CommandSourceStack> stack, String namespace, String id, BlockPos pos, int data)
    {
        if (VectorEventSync.Local.EVENT_MAPDEX.containsKey(namespace))
        {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, id);
            if (VectorEventSync.Local.MAP_FREEZER.containsKey(location))
            {
                int ord = VectorEventSync.Local.MAP_FREEZER.get(location);
                VectorEventSync.Local.fireEventRaw(null, (VectorLevelAccess)stack.getSource().getLevel(), pos, namespace, ord, data);

                stack.getSource().sendSuccess(
                        () -> Component.translatable(
                                "commands.vectorlib.event.eventsync.local_success",
                                String.format("%1s, %2s, %3s", pos.getX(), pos.getY(), pos.getZ())
                        ),
                        true);
                return 1;
            }
            else stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.eventsync_no_action", VLibUtil.capital(VL_LOCAL), id));
        }
        else stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.eventsync_no_namespace", VLibUtil.capital(VL_LOCAL)));
        return 0;
    }

    private static int executeVLDual(CommandContext<CommandSourceStack> stack, String namespace, String id, Vec3 pos1, Vec3 pos2, int data)
    {
        if (VectorEventSync.Dual.EVENT_MAPDEX.containsKey(namespace))
        {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, id);
            if (VectorEventSync.Dual.MAP_FREEZER.containsKey(location))
            {
                int ord = VectorEventSync.Dual.MAP_FREEZER.get(location);
                VectorEventSync.Dual.fireEventRaw(null, (VectorLevelAccess)stack.getSource().getLevel(), pos1, pos2, namespace, ord, data);

                stack.getSource().sendSuccess(
                        () -> Component.translatable(
                                "commands.vectorlib.event.eventsync.dual_success",
                                String.format("%1s, %2s, %3s", pos1.x(), pos1.y(), pos1.z()),
                                String.format("%1s, %2s, %3s", pos2.x(), pos2.y(), pos2.z())
                        ),
                        true);
                return 1;
            }
            else stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.eventsync_no_action", VLibUtil.capital(VL_DUAL), id));
        }
        else stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.eventsync_no_namespace", VLibUtil.capital(VL_DUAL)));
        return 0;
    }

    private static int executeVLEntity(CommandContext<CommandSourceStack> stack, String namespace, String id, Entity entity, int data)
    {
        if (VectorEventSync.Entity.EVENT_MAPDEX.containsKey(namespace))
        {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, id);
            if (VectorEventSync.Entity.MAP_FREEZER.containsKey(location))
            {
                int ord = VectorEventSync.Entity.MAP_FREEZER.get(location);
                VectorEventSync.Entity.fireEventRaw(null, (VectorLevelAccess)stack.getSource().getLevel(), entity, namespace, ord, data);

                stack.getSource().sendSuccess(
                        () -> Component.translatable(
                                "commands.vectorlib.event.eventsync.entity_success",
                                entity.getDisplayName()
                        ),
                        true);
                return 1;
            }
            else stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.eventsync_no_action", VLibUtil.capital(VL_ENT), id));
        }
        else stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.eventsync_no_namespace", VLibUtil.capital(VL_ENT)));
        return 0;
    }

    private static int executeVLGlobal(CommandContext<CommandSourceStack> stack, String namespace, String id, BlockPos pos, int data)
    {
        if (VectorEventSync.Global.EVENT_MAPDEX.containsKey(namespace))
        {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, id);
            if (VectorEventSync.Global.MAP_FREEZER.containsKey(location))
            {
                int ord = VectorEventSync.Global.MAP_FREEZER.get(location);
                VectorEventSync.Global.fireEventRaw(null, (VectorLevelAccess)stack.getSource().getLevel(), pos, namespace, ord, data);

                stack.getSource().sendSuccess(
                        () -> Component.translatable(
                                "commands.vectorlib.event.eventsync.global_success",
                                String.format("%1s, %2s, %3s", pos.getX(), pos.getY(), pos.getZ())
                        ),
                        true);
                return 1;
            }
            else stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.eventsync_no_action", VLibUtil.capital(VL_GLOB), id));
        }
        else stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.eventsync_no_namespace", VLibUtil.capital(VL_GLOB)));
        return 0;
    }

    private static int executeVanillaAtSet(CommandContext<CommandSourceStack> stack, int event, BlockPos pos, int data, boolean global)
    {
        try
        {
            for (Field field : LevelEvent.class.getFields())
            {
                if (field.getGenericType().equals(Integer.TYPE))
                {
                    var integer = (Integer)field.get(null);
                    if (integer == event)
                    {
                        if (global) stack.getSource().getLevel().globalLevelEvent(event, pos, data);
                        else stack.getSource().getLevel().levelEvent(event, pos, data);
                        stack.getSource().sendSuccess(
                                () -> Component.translatable(
                                        "commands.vectorlib.event.vanilla.success",
                                        event,
                                        String.format("%1s, %2s, %3s", pos.getX(), pos.getY(), pos.getZ())
                                ),
                                true);
                        return 1;
                    }
                }
            }
            stack.getSource().sendFailure(Component.translatable("commands.vectorlib.event.vanilla_no_exist"));
            return 0;
        }
        catch (IllegalAccessException illegal)
        {
            stack.getSource().sendFailure(Component.translatable("command.failed"));
            VectorLib.LOGGER.error("Something happened while trying to check existing level events, see below:", illegal);
        }
        return 0;
    }

    private enum EventType
    {
        LOCAL,
        DUAL,
        ENTITY,
        GLOBAL
    }
}
