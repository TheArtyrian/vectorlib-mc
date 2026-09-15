package net.vertisoft.vectorlib.platform;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.vertisoft.vectorlib.mixin_intf.network.MulPlayIntf;

public interface VectorClientIntf
{
    void setRenderLayer(Block block, RenderType renderType);

    void registerPredicate(Item item, ResourceLocation location, ClampedItemPropertyFunction function);

    <T extends ParticleOptions> void registerParticle(ParticleType<T> type, ParticleProvider<T> provider);

    void sendViaGamemode(MultiPlayerGameMode gameMode, Packet<?> packet);
}
