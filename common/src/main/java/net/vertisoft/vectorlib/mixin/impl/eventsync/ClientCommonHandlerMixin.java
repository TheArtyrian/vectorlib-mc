package net.vertisoft.vectorlib.mixin.impl.eventsync;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientCommonPacketListenerImpl.class)
public abstract class ClientCommonHandlerMixin
{
    @Shadow public abstract DisconnectionDetails createDisconnectionInfo(Component reason, Throwable error);

    @Shadow @Final protected Connection connection;
    @Final @Shadow protected Minecraft minecraft;
}