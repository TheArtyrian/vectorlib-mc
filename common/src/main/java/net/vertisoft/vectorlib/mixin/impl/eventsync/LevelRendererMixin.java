package net.vertisoft.vectorlib.mixin.impl.eventsync;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.vertisoft.vectorlib.agnostic.networking.eventsync.EventSyncHolder;
import net.vertisoft.vectorlib.agnostic.networking.eventsync.VectorEventSync;
import net.vertisoft.vectorlib.agnostic.networking.eventsync.VectorEventSyncClient;
import net.vertisoft.vectorlib.mixin_intf.event.VectorLevelRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.lang.instrument.IllegalClassFormatException;
import java.util.List;
import java.util.NoSuchElementException;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin implements VectorLevelRenderer
{
    @Shadow @Nullable private ClientLevel level;
    @Shadow @Final private Minecraft minecraft;

    @Override
    public void vectorLib$runGameEvent(String mod, int type, BlockPos pos, int data)
    {
        if (VectorEventSync.Local.EVENT_MAPDEX.containsKey(mod))
        {
            List<EventSyncHolder> qxl = VectorEventSync.Local.EVENT_MAPDEX.get(mod);
            if (type >= 0 && type < qxl.size())
            {
                EventSyncHolder holder = qxl.get(type);
                if (holder.event instanceof VectorEventSyncClient.VecEventAlias alias) alias.execute(this.level, this.minecraft, pos, data);
                else throw new NoSuchElementException("Provided event is not of VecEventAlias - likely not registered properly");
            }
            else throw new ArrayIndexOutOfBoundsException(String.format("Value %s out of bounds in the local VectorEventSync list", type));
        }
        else throw new IllegalArgumentException("A VectorEventType entry does not exist for the provided namespace");
    }

    @Override
    public void vectorLib$runDualEvent(String mod, int type, Vec3 pos1, Vec3 pos2, int data)
    {
        if (VectorEventSync.Dual.EVENT_MAPDEX.containsKey(mod))
        {
            List<EventSyncHolder> qxl = VectorEventSync.Dual.EVENT_MAPDEX.get(mod);
            if (type >= 0 && type < qxl.size())
            {
                EventSyncHolder holder = qxl.get(type);
                if (holder.event instanceof VectorEventSyncClient.VecDualEventAlias alias) alias.execute(this.level, this.minecraft, pos1, pos2, data);
                else throw new NoSuchElementException("Provided event is not of VecDualEventAlias - likely not registered properly");
            }
            else throw new ArrayIndexOutOfBoundsException(String.format("Value %s out of bounds in the dual-pos VectorEventSync list", type));
        }
        else throw new IllegalArgumentException("A VectorEventType entry does not exist for the provided namespace");
    }

    @Override
    public void vectorLib$runEntityEvent(String mod, int type, Entity entity, int data)
    {
        if (VectorEventSync.Entity.EVENT_MAPDEX.containsKey(mod))
        {
            List<EventSyncHolder> qxl = VectorEventSync.Entity.EVENT_MAPDEX.get(mod);
            if (type >= 0 && type < qxl.size())
            {
                EventSyncHolder holder = qxl.get(type);
                if (holder.event instanceof VectorEventSyncClient.VecEntityEventAlias alias) alias.execute(this.level, this.minecraft, entity, data);
                else throw new NoSuchElementException("Provided event is not of VecEntityEventAlias - likely not registered properly");
            }
            else throw new ArrayIndexOutOfBoundsException(String.format("Value %s out of bounds in the entity VectorEventSync list", type));
        }
        else throw new IllegalArgumentException("A VectorEventType entry does not exist for the provided namespace");
    }

    @Override
    public void vectorLib$runGlobalEvent(String mod, int type, BlockPos pos, int data)
    {
        if (VectorEventSync.Global.EVENT_MAPDEX.containsKey(mod))
        {
            List<EventSyncHolder> qxl = VectorEventSync.Global.EVENT_MAPDEX.get(mod);
            Camera camera = this.minecraft.gameRenderer.getMainCamera();

            if (camera.isInitialized())
            {
                double diffX = (double)pos.getX() - camera.getPosition().x;
                double diffY = (double)pos.getY() - camera.getPosition().y;
                double diffZ = (double)pos.getZ() - camera.getPosition().z;
                double sqrt = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
                double camX = camera.getPosition().x;
                double camY = camera.getPosition().y;
                double camZ = camera.getPosition().z;
                if (sqrt > 0.0)
                {
                    camX += diffX / sqrt * 2.0;
                    camY += diffY / sqrt * 2.0;
                    camZ += diffZ / sqrt * 2.0;
                }

                if (type >= 0 && type < qxl.size())
                {
                    EventSyncHolder holder = qxl.get(type);
                    if (holder.event instanceof VectorEventSyncClient.VecGlobalEventAlias alias) alias.execute(this.level, this.minecraft, new Vec3(camX, camY, camZ), data);
                    else throw new NoSuchElementException("Provided event is not of VecGlobalEventAlias - likely not registered properly");
                }
                else throw new ArrayIndexOutOfBoundsException(String.format("Value %s out of bounds in the global VectorEventSync list", type));
            }
        }
        else throw new IllegalArgumentException("A VectorEventType entry does not exist for the provided namespace");
    }
}
