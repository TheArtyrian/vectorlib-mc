package net.vertisoft.vectorlib.agnostic.networking.eventsync;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorDualPosS2CPacket;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorEntityEventS2CPacket;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorEventS2CPacket;
import net.vertisoft.vectorlib.mixin_intf.event.VectorLevelAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Allows definition of client-side "level events" completely seperate of Vanilla.<p>
 * To gain a basic understanding of a level event in Minecraft, here's a few examples:
 * <ul>
 * <li>Monster Spawner spawning effects (flames + smoke)
 * <li>Bone Meal growth particles
 * <li>Copper wax on/off</li>
 * </ul>
 * In short, level events sum up to performing common effects on clients, and can be called from either client or server.
 * This class allows for defining custom ones. There's several kinds:
 * <ul>
 * <li> {@code Local}: Fires off to players who can see it nearby - good for particles and sfx
 * <li> {@code Dual}: Fires off to nearby players, but takes in 2 positions - good for advanced effects
 * <li> {@code Entity}: Fires off to nearby players, but uses an entity as the target - good for effects that require entity data
 * <li> {@code Global}: Fires off to ALL connected players - good for boss summon sfx and important sound-related events
 * </ul>
 * <p>------------------------------------<p><b>READ BELOW BEFORE GOING ANY FURTHER!!!</b><p>------------------------------------<p>
 * Registering events needs to be done in TWO phases - one in your main mod class, and one in your client class. Since events use
 * classes that can ONLY be loaded on the client - especially {@link net.minecraft.client.Minecraft the Minecraft client class}
 * - the events need to be declared on client-side <b>only</b>; they will remain null on the server-side since all they need to know
 * is that an event exists.
 * <p>
 * Make a seperate class for your VectorEvents, and register them using the event's respective {@code register()}
 * method. You'll want to reference that in your mod's main initializer. Then, you'll want to make a separate class for your
 * client-side initializer, and call a method that registers the actual functions to the events you registered prior. ONLY call this method
 * in your client-side initializer; doing otherwise will cause a hard crash on server instances.
 * <p>
 * All of the client events can be registered via {@link net.vertisoft.vectorlib.agnostic.networking.eventsync.VectorEventSyncClient}.
 */
public class VectorEventSync
{
    private static final String WTF = "The provided LevelAccessor doesn't extend VectorLevelAccess?????";

    public static class Local
    {
        public static final Map<String, List<EventSyncHolder>> EVENT_MAPDEX = new HashMap<>();
        public static final Map<ResourceLocation, Integer> MAP_FREEZER = new HashMap<>();

        public static EventData register(ResourceLocation mapper)
        {
            if (!EVENT_MAPDEX.containsKey(mapper.getNamespace()))
            {
                EVENT_MAPDEX.put(mapper.getNamespace(), new ArrayList<>());
            }
            List<EventSyncHolder> list = EVENT_MAPDEX.get(mapper.getNamespace());
            int ordinal;

            if (!MAP_FREEZER.containsKey(mapper))
            {
                EventSyncHolder event = new EventSyncHolder(EventType.LOCAL, mapper.getPath());
                list.add(event);
                ordinal = list.indexOf(event);
                MAP_FREEZER.put(mapper, ordinal);
            }
            else throw new IllegalArgumentException(String.format("VectorEventSync local list already contains event that maps to %s", mapper));

            return new EventData(mapper, ordinal, EventType.LOCAL);
        }

        public static void fireEvent(LevelAccessor level, BlockPos pos, EventData event, int data)
        {
            VectorEventSync.Local.fireEvent(null, level, pos, event, data);
        }

        public static void fireEvent(@Nullable Player player, LevelAccessor level, BlockPos pos, EventData event, int data)
        {
            try
            {
                if (level instanceof VectorLevelAccess vecacc)
                {
                    if (EVENT_MAPDEX.containsKey(event.getMod())) VectorEventSync.Local.fireEventRaw(player, vecacc, pos, event.getMod(), event.getId(), data);
                    else throw new IllegalArgumentException(String.format("Namespace %1s doesn't exist for %2s VectorEvent", event.getMod(), "local"));
                }
                else throw new IllegalArgumentException(WTF);
            }
            catch (IllegalArgumentException exc)
            {
                VectorLib.LOGGER.error("Couldn't fire local VectorEvent, see below", exc);
            }
        }

        public static void fireToPlayer(ServerPlayer player, EventData event, BlockPos pos, int data)
        {
            VectorLib.NETWORK.sendToPlayer(player, new VectorEventS2CPacket(event.getMod(), event.getId(), pos, data, false));
        }

        public static void fireEventRaw(@Nullable Player player, VectorLevelAccess level, BlockPos pos, String mod, int id, int data)
        {
            level.vectorLib$fireEvent(player, mod, id, pos, data);
        }
    }

    public static class Dual
    {
        public static final Map<String, List<EventSyncHolder>> EVENT_MAPDEX = new HashMap<>();
        public static final Map<ResourceLocation, Integer> MAP_FREEZER = new HashMap<>();

        public static EventData register(ResourceLocation mapper)
        {
            if (!EVENT_MAPDEX.containsKey(mapper.getNamespace()))
            {
                EVENT_MAPDEX.put(mapper.getNamespace(), new ArrayList<>());
            }
            List<EventSyncHolder> list = EVENT_MAPDEX.get(mapper.getNamespace());
            int ordinal;

            if (!MAP_FREEZER.containsKey(mapper))
            {
                EventSyncHolder event = new EventSyncHolder(EventType.DUAL, mapper.getPath());
                list.add(event);
                ordinal = list.indexOf(event);
                MAP_FREEZER.put(mapper, ordinal);
            }
            else throw new IllegalArgumentException(String.format("VectorEventSync dual-pos list already contains event that maps to %s", mapper));

            return new EventData(mapper, ordinal, EventType.DUAL);
        }

        public static void fireEvent(LevelAccessor level, Vec3 pos1, Vec3 pos2, EventData event, int data)
        {
            VectorEventSync.Dual.fireEvent(null, level, pos1, pos2, event, data);
        }

        public static void fireEvent(@Nullable Player player, LevelAccessor level, Vec3 pos1, Vec3 pos2, EventData event, int data)
        {
            try
            {
                if (level instanceof VectorLevelAccess vecacc)
                {
                    if (EVENT_MAPDEX.containsKey(event.getMod())) VectorEventSync.Dual.fireEventRaw(player, vecacc, pos1, pos2, event.getMod(), event.getId(), data);
                    else throw new IllegalArgumentException(String.format("Namespace %1s doesn't exist for %2s VectorEvent", event.getMod(), "dual-pos"));
                }
                else throw new IllegalArgumentException(WTF);
            }
            catch (IllegalArgumentException exc)
            {
                VectorLib.LOGGER.error("Couldn't fire dual-pos VectorEvent, see below", exc);
            }
        }

        public static void fireToPlayer(ServerPlayer player, EventData event, Vec3 pos1, Vec3 pos2, int data)
        {
            VectorLib.NETWORK.sendToPlayer(player, new VectorDualPosS2CPacket(event.getMod(), event.getId(), pos1, pos2, data));
        }

        public static void fireEventRaw(@Nullable Player player, VectorLevelAccess level, Vec3 pos1, Vec3 pos2, String mod, int id, int data)
        {
            level.vectorLib$fireDual(player, mod, id, pos1, pos2, data);
        }
    }

    public static class Entity
    {
        public static final Map<String, List<EventSyncHolder>> EVENT_MAPDEX = new HashMap<>();
        public static final Map<ResourceLocation, Integer> MAP_FREEZER = new HashMap<>();

        public static EventData register(ResourceLocation mapper)
        {
            if (!EVENT_MAPDEX.containsKey(mapper.getNamespace()))
            {
                EVENT_MAPDEX.put(mapper.getNamespace(), new ArrayList<>());
            }
            List<EventSyncHolder> list = EVENT_MAPDEX.get(mapper.getNamespace());
            int ordinal;

            if (!MAP_FREEZER.containsKey(mapper))
            {
                EventSyncHolder event = new EventSyncHolder(EventType.ENTITY, mapper.getPath());
                list.add(event);
                ordinal = list.indexOf(event);
                MAP_FREEZER.put(mapper, ordinal);
            }
            else throw new IllegalArgumentException(String.format("VectorEventSync entity list already contains event that maps to %s", mapper));

            return new EventData(mapper, ordinal, EventType.ENTITY);
        }

        public static void fireEvent(LevelAccessor level, net.minecraft.world.entity.Entity entity, EventData event, int data)
        {
            VectorEventSync.Entity.fireEvent(null, level, entity, event, data);
        }

        public static void fireEvent(@Nullable Player player, LevelAccessor level, net.minecraft.world.entity.Entity entity, EventData event, int data)
        {
            try
            {
                if (level instanceof VectorLevelAccess vecacc)
                {
                    if (EVENT_MAPDEX.containsKey(event.getMod()))
                    {
                        VectorEventSync.Entity.fireEventRaw(player, vecacc, entity, event.getMod(), event.getId(), data);
                    }
                    else throw new IllegalArgumentException(String.format("Namespace %1s doesn't exist for %2s VectorEvent", event.getMod(), "entity"));
                }
                else throw new IllegalArgumentException(WTF);
            }
            catch (IllegalArgumentException exc)
            {
                VectorLib.LOGGER.error("Couldn't fire entity VectorEvent, see below", exc);
            }
        }

        public static void fireToPlayer(ServerPlayer player, EventData event, net.minecraft.world.entity.Entity entity, int data)
        {
            VectorLib.NETWORK.sendToPlayer(player, new VectorEntityEventS2CPacket(event.getMod(), event.getId(), entity, data));
        }

        public static void fireEventRaw(@Nullable Player player, VectorLevelAccess level, net.minecraft.world.entity.Entity entity, String mod, int id, int data)
        {
            level.vectorLib$fireEntity(player, mod, id, entity, data);
        }
    }

    public static class Global
    {
        public static final Map<String, List<EventSyncHolder>> EVENT_MAPDEX = new HashMap<>();
        public static final Map<ResourceLocation, Integer> MAP_FREEZER = new HashMap<>();

        public static EventData register(ResourceLocation mapper)
        {
            if (!EVENT_MAPDEX.containsKey(mapper.getNamespace()))
            {
                EVENT_MAPDEX.put(mapper.getNamespace(), new ArrayList<>());
            }
            List<EventSyncHolder> list = EVENT_MAPDEX.get(mapper.getNamespace());
            int ordinal;

            if (!MAP_FREEZER.containsKey(mapper))
            {
                EventSyncHolder event = new EventSyncHolder(EventType.GLOBAL, mapper.getPath());
                list.add(event);
                ordinal = list.indexOf(event);
                MAP_FREEZER.put(mapper, ordinal);
            }
            else throw new IllegalArgumentException(String.format("VectorEventSync global list already contains event that maps to %s", mapper));

            return new EventData(mapper, ordinal, EventType.GLOBAL);
        }

        public static void fireEvent(LevelAccessor level, BlockPos pos, EventData event, int data)
        {
            VectorEventSync.Global.fireEvent(null, level, pos, event, data);
        }

        public static void fireEvent(@Nullable Player player, LevelAccessor level, BlockPos pos, EventData event, int data)
        {
            try
            {
                if (level instanceof VectorLevelAccess vecacc)
                {
                    if (EVENT_MAPDEX.containsKey(event.getMod())) VectorEventSync.Global.fireEventRaw(player, vecacc, pos, event.getMod(), event.getId(), data);
                    else throw new IllegalArgumentException(String.format("Namespace %1s doesn't exist for %2s VectorEvent", event.getMod(), "global"));
                }
                else throw new IllegalArgumentException(WTF);
            }
            catch (IllegalArgumentException exc)
            {
                VectorLib.LOGGER.error("Couldn't fire global VectorEvent, see below", exc);
            }
        }

        public static void fireToPlayer(ServerPlayer player, EventData event, BlockPos pos, int data)
        {
            VectorLib.NETWORK.sendToPlayer(player, new VectorEventS2CPacket(event.getMod(), event.getId(), pos, data, true));
        }

        public static void fireEventRaw(@Nullable Player player, VectorLevelAccess level, BlockPos pos, String mod, int id, int data)
        {
            level.vectorLib$fireGlobal(player, mod, id, pos, data);
        }
    }

    public static class EventData
    {
        private final ResourceLocation location;
        private final int id;
        private final EventType type;

        public EventData(ResourceLocation location, int id, EventType type)
        {
            this.location = location;
            this.id = id;
            this.type = type;
        }

        public int getId() { return this.id; }
        public String getMod() { return this.location.getNamespace(); }
        public String getName() { return this.location.getPath(); }
        public EventType type() { return this.type; }
    }

    public enum EventType
    {
        LOCAL,
        DUAL,
        ENTITY,
        GLOBAL
    }
}
