package net.vertisoft.vectorlib.agnostic.networking.eventsync;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.vertisoft.vectorlib.VectorLib;

import java.util.NoSuchElementException;

/** The class is used to tell clients how to act when they receive an EventSync packet.
 * <p>
 *  As stated in {@link net.vertisoft.vectorlib.agnostic.networking.eventsync.VectorEventSync VectorEventSync}, you must first register
 *  your events on the common-side of your mod, then register their actions on the client-side. Use the methods provided here to do that.
 */
public class VectorEventSyncClient
{
    private static final String LOCAL = "Local";
    private static final String DUAL = "Dual";
    private static final String ENT = "Entity";
    private static final String GLOB = "Global";

    public static void assignLocal(VectorEventSync.EventData event, VecEventAlias alias)
    {
        assertRightConditions(event, VectorEventSync.EventType.LOCAL, LOCAL);

        ResourceLocation location = VectorLib.id(event.getMod(), event.getName());
        if (!VectorEventSync.Local.MAP_FREEZER.containsKey(location)) throw new NoSuchElementException(throwNoExist(LOCAL, location));

        int listorder = VectorEventSync.Local.MAP_FREEZER.get(location);
        EventSyncHolder holder = VectorEventSync.Local.EVENT_MAPDEX.get(event.getMod()).get(listorder);
        holder.setEvent(alias);
    }

    public static void assignDual(VectorEventSync.EventData event, VecDualEventAlias alias)
    {
        assertRightConditions(event, VectorEventSync.EventType.DUAL, DUAL);

        ResourceLocation location = VectorLib.id(event.getMod(), event.getName());
        if (!VectorEventSync.Dual.MAP_FREEZER.containsKey(location)) throw new NoSuchElementException(throwNoExist(DUAL, location));

        int listorder = VectorEventSync.Dual.MAP_FREEZER.get(location);
        EventSyncHolder holder = VectorEventSync.Dual.EVENT_MAPDEX.get(event.getMod()).get(listorder);
        holder.setEvent(alias);
    }

    public static void assignEntity(VectorEventSync.EventData event, VecEntityEventAlias alias)
    {
        assertRightConditions(event, VectorEventSync.EventType.ENTITY, ENT);

        ResourceLocation location = VectorLib.id(event.getMod(), event.getName());
        if (!VectorEventSync.Entity.MAP_FREEZER.containsKey(location)) throw new NoSuchElementException(throwNoExist(ENT, location));

        int listorder = VectorEventSync.Entity.MAP_FREEZER.get(location);
        EventSyncHolder holder = VectorEventSync.Entity.EVENT_MAPDEX.get(event.getMod()).get(listorder);
        holder.setEvent(alias);
    }

    public static void assignGlobal(VectorEventSync.EventData event, VecGlobalEventAlias alias)
    {
        assertRightConditions(event, VectorEventSync.EventType.GLOBAL, GLOB);

        ResourceLocation location = VectorLib.id(event.getMod(), event.getName());
        if (!VectorEventSync.Global.MAP_FREEZER.containsKey(location)) throw new NoSuchElementException(throwNoExist(GLOB, location));

        int listorder = VectorEventSync.Global.MAP_FREEZER.get(location);
        EventSyncHolder holder = VectorEventSync.Global.EVENT_MAPDEX.get(event.getMod()).get(listorder);
        holder.setEvent(alias);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static void assertRightConditions(VectorEventSync.EventData event, VectorEventSync.EventType type, String side)
    {
        if (!VectorLib.PLATFORM.isClient()) throw new IllegalArgumentException(throwstr("Called VectorEventSyncClient functions on the server-side"));
        if (event.type() != type) throw new IllegalArgumentException(throwstr(side));
    }

    private static String throwstr(String type) { return String.format("The provided EventData is not of type %s", type); }
    private static String throwNoExist(String type, ResourceLocation loc) { return String.format("The %1s event map does not contain an event of type %2s", type, loc); }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @FunctionalInterface
    public interface VecEventAlias extends IVecEvent
    {
        void execute(Level level, Minecraft minecraft, BlockPos pos, int data);
    }

    @FunctionalInterface
    public interface VecDualEventAlias extends IVecEvent
    {
        void execute(Level level, Minecraft minecraft, Vec3 pos1, Vec3 pos2, int data);
    }

    @FunctionalInterface
    public interface VecEntityEventAlias extends IVecEvent
    {
        void execute(Level level, Minecraft minecraft, net.minecraft.world.entity.Entity entity, int data);
    }

    @FunctionalInterface
    public interface VecGlobalEventAlias extends IVecEvent
    {
        void execute(Level level, Minecraft minecraft, Vec3 pos, int data);
    }
}
