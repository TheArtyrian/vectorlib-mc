package net.vertisoft.vectorlib.mixin.player;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.GameRules;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.VectorSystems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin
{
    @Inject(method = "die", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V")
    )
    private void vectorLib$iHateThatArtyrianGuy(DamageSource damageSource, CallbackInfo ci)
    {
        ServerPlayer me = (ServerPlayer)(Object)(this);
        boolean do_loot = me.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);

        if (do_loot && me.getUUID().equals(VectorSystems.CONTRIBUTORS.get("Artyrian")))
        {
            ItemStack itemStack = new ItemStack(Items.BEETROOT);
            me.spawnAtLocation(itemStack);
        }
    }
}
