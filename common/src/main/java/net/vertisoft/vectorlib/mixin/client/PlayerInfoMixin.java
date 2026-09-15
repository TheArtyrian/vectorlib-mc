package net.vertisoft.vectorlib.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.vertisoft.vectorlib.VectorLib;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

// Special thanks to https://github.com/Yirmiri/ for the guidance :)
@Mixin(PlayerInfo.class)
public abstract class PlayerInfoMixin
{
    @Shadow public abstract GameProfile getProfile();
    @Shadow @Final private Supplier<PlayerSkin> skinLookup;

    @Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
    private void vectorLib$AwYeahCoolCapesLole(CallbackInfoReturnable<PlayerSkin> cir)
    {
        ResourceLocation texture = null;
        String uuid = getProfile().getId().toString();

        if (VectorLib.CONFIG.doSpecialCapeEnabled() && VectorLib.SYSTEM.CONTRIBUTOR_CAPES.containsKey(uuid))
        {
            texture = VectorLib.SYSTEM.CONTRIBUTOR_CAPES.get(uuid);
        }

        if (texture != null)
        {
            PlayerSkin textures = skinLookup.get();
            cir.setReturnValue(new PlayerSkin(textures.texture(), textures.textureUrl(), texture, texture, textures.model(), textures.secure()));
        }
    }
}
