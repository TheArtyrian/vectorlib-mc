package net.vertisoft.vectorlib.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.cosmetic.joinmsg.VectorJoinMsg;
import net.vertisoft.vectorlib.agnostic.cosmetic.joinmsg.VectorJoinMsgMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketMixin
{
    @Shadow public ServerPlayer player;

    @WrapOperation(method = "removePlayerFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void vectorLib$hashtagRipBozo(PlayerList instance, Component message, boolean overlay, Operation<Void> original)
    {
        VectorJoinMsg msg = VectorJoinMsgMan.getMsg(this.player.getUUID());
        if (msg != null)
        {
            Component newType = msg.changeLeave(message, this.player);
            original.call(instance, newType, overlay);
        }
        else original.call(instance, message, overlay);
    }
}