package net.vertisoft.vectorlib.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.cosmetic.joinmsg.VectorJoinMsg;
import net.vertisoft.vectorlib.agnostic.cosmetic.joinmsg.VectorJoinMsgMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin
{
    @WrapOperation(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void vectorLib$makeFunOfArtyrianBecauseIHateHim(
            PlayerList instance,
            Component message,
            boolean overlay,
            Operation<Void> original,
            @Local(argsOnly = true) ServerPlayer player,
            @Local(ordinal = 0) String string
            )
    {
        VectorJoinMsg msg = VectorJoinMsgMan.getMsg(player.getUUID());
        if (msg != null)
        {
            boolean formerName = !(player.getGameProfile().getName().equalsIgnoreCase(string));

            Component newType = msg.changeJoin(message, player, formerName, string);
            original.call(instance, newType, overlay);
        }
        else original.call(instance, message, overlay);
    }
}
