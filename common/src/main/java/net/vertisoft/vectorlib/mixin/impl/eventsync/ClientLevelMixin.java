package net.vertisoft.vectorlib.mixin.impl.eventsync;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.vertisoft.vectorlib.mixin_intf.event.VectorLevelAccess;
import net.vertisoft.vectorlib.mixin_intf.event.VectorLevelRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements VectorLevelAccess
{
    @Shadow @Final private LevelRenderer levelRenderer;

    @Override
    public void vectorLib$fireEvent(@Nullable Player player, String mod, int type, BlockPos pos, int data)
    {
        try
        {
            ((VectorLevelRenderer)this.levelRenderer).vectorLib$runGameEvent(mod, type, pos, data);
        }
        catch (Throwable throwable)
        {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Playing VectorLib level event - THIS IS NOT A VANILLA MINECRAFT ERROR, REPORT THIS TO ARTYRIAN! ");
            CrashReportCategory crashReportCategory = crashReport.addCategory("VectorLib event being played");
            crashReportCategory.setDetail("Block coordinates", CrashReportCategory.formatLocation((ClientLevel)(Object)this, pos));
            crashReportCategory.setDetail("Event source", player);
            crashReportCategory.setDetail("Event type", type);
            crashReportCategory.setDetail("Event data", data);
            throw new ReportedException(crashReport);
        }
    }

    @Override
    public void vectorLib$fireDual(@Nullable Player player, String mod, int type, Vec3 pos1, Vec3 pos2, int data)
    {
        try
        {
            ((VectorLevelRenderer)this.levelRenderer).vectorLib$runDualEvent(mod, type, pos1, pos2, data);
        }
        catch (Throwable throwable)
        {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Playing VectorLib dual-position event: THIS IS NOT A MINECRAFT-BASED ERROR, REPORT THIS TO ARTYRIAN! ");
            CrashReportCategory crashReportCategory = crashReport.addCategory("VectorLib dual-pos event being played");
            crashReportCategory.setDetail("1st position coordinates", CrashReportCategory.formatLocation((ClientLevel)(Object)this, pos1.x, pos1.y, pos1.z));
            crashReportCategory.setDetail("2nd position coordinates", CrashReportCategory.formatLocation((ClientLevel)(Object)this, pos2.x, pos2.y, pos2.z));
            crashReportCategory.setDetail("Event source", player);
            crashReportCategory.setDetail("Event type", type);
            crashReportCategory.setDetail("Event data", data);
            throw new ReportedException(crashReport);
        }
    }

    @Override
    public void vectorLib$fireEntity(@Nullable Player player, String mod, int type, Entity entity, int data)
    {
        try
        {
            ((VectorLevelRenderer)this.levelRenderer).vectorLib$runEntityEvent(mod, type, entity, data);
        }
        catch (Throwable throwable)
        {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Playing VectorLib entity event: THIS IS NOT A MINECRAFT-BASED ERROR, REPORT THIS TO ARTYRIAN! ");
            CrashReportCategory crashReportCategory = crashReport.addCategory("VectorLib event being played");
            crashReportCategory.setDetail("Entity source", entity);
            crashReportCategory.setDetail("Event source", player);
            crashReportCategory.setDetail("Event type", type);
            crashReportCategory.setDetail("Event data", data);
            throw new ReportedException(crashReport);
        }
    }

    @Override
    public void vectorLib$fireGlobal(@Nullable Player player, String mod, int type, BlockPos pos, int data)
    {
        ((VectorLevelRenderer)this.levelRenderer).vectorLib$runGlobalEvent(mod, type, pos, data);
    }
}
