package net.vertisoft.vectorlib.mixin.impl;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GameProtocols;
import net.vertisoft.vectorlib.agnostic.networking.data.VectorPayloads;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameProtocols.class)
public class GameProtocolMixinNF
{
    @Inject(method = "lambda$static$1", at = @At(value = "TAIL"))
    private static void vectorLib$appenderS2C(ProtocolInfoBuilder<ClientGamePacketListener, RegistryFriendlyByteBuf> builder, CallbackInfo ci)
    {
        VectorPayloads.chainS2CPackets(builder);
    }
}
