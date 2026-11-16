package net.vertisoft.vectorlib.mixin.server;

import net.minecraft.server.MinecraftServer;
import net.vertisoft.vectorlib.mixin_intf.server.IVectorMCServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IVectorMCServer
{
    @Unique protected boolean vectorlib$forceCrash = false;

    @Override public void vectorlib$setUpUsTheTNT() { this.vectorlib$forceCrash = true; }

    @Inject(method = "tickServer", at = @At("HEAD"))
    private void vectorLib$tickServer(BooleanSupplier hasTimeLeft, CallbackInfo ci)
    {
        if (this.vectorlib$forceCrash)
        {
            throw new RuntimeException("The server was shot in the face with a 12-gauge per the request of a server operator");
        }
    }
}
