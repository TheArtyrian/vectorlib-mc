package net.vertisoft.vectorlib.mixin.impl.eventsync;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorDualPosS2CPacket;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorEntityEventS2CPacket;
import net.vertisoft.vectorlib.agnostic.networking.data.packets.VectorEventS2CPacket;
import net.vertisoft.vectorlib.mixin_intf.event.VectorLevelAccess;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements VectorLevelAccess
{
    @Shadow @Final private MinecraftServer server;

    @Override
    public void vectorLib$fireEvent(@Nullable Player player, String mod, int type, BlockPos pos, int data)
    {
        this.server.getPlayerList()
                .broadcast(
                        player,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        64.0,
                        ((ServerLevel)(Object)this).dimension(),
                        new VectorEventS2CPacket(mod, type, pos, data, false)
                );
    }

    @Override
    public void vectorLib$fireDual(@Nullable Player player, String mod, int type, Vec3 pos1, Vec3 pos2, int data)
    {
        this.server.getPlayerList()
                .broadcast(
                        player,
                        pos1.x(),
                        pos1.y(),
                        pos1.z(),
                        64.0,
                        ((ServerLevel)(Object)this).dimension(),
                        new VectorDualPosS2CPacket(mod, type, pos1, pos2, data)
                );
    }

    @Override
    public void vectorLib$fireEntity(@Nullable Player player, String mod, int type, Entity entity, int data)
    {
        BlockPos pos = entity.blockPosition();
        this.server.getPlayerList()
                .broadcast(
                        player,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        64.0,
                        ((ServerLevel)(Object)this).dimension(),
                        new VectorEntityEventS2CPacket(mod, type, entity, data)
                );
    }

    @Override
    public void vectorLib$fireGlobal(@Nullable Player player, String mod, int type, BlockPos pos, int data)
    {
        if (((ServerLevel)(Object)this).getGameRules().getBoolean(GameRules.RULE_GLOBAL_SOUND_EVENTS))
        {
            this.server.getPlayerList().broadcastAll(new VectorEventS2CPacket(mod, type, pos, data, true));
        }
    }
}
