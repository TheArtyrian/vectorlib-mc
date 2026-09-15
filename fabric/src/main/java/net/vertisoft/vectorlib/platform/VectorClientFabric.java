package net.vertisoft.vectorlib.platform;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.vertisoft.vectorlib.mixin_intf.network.MulPlayIntf;

public class VectorClientFabric implements VectorClientIntf
{
    @Override
    public void setRenderLayer(Block block, RenderType renderType)
    {
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
    }

    @Override
    public void registerPredicate(Item item, ResourceLocation location, ClampedItemPropertyFunction function)
    {
        ItemProperties.register(item, location, function);
    }

    @Override
    public <T extends ParticleOptions> void registerParticle(ParticleType<T> type, ParticleProvider<T> provider)
    {
        ParticleFactoryRegistry.getInstance().register(type, provider);
    }

    @Override
    public void sendViaGamemode(MultiPlayerGameMode gameMode, Packet<?> packet)
    {
        ((MulPlayIntf)gameMode).vectorLib$sendPacketOnConnection(packet);
    }
}
