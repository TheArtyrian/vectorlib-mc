package net.vertisoft.vectorlib.platform;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.entity.item.ItemEvent;
import net.vertisoft.vectorlib.mixin_intf.network.MulPlayIntf;

public class VectorClientNF implements VectorClientIntf
{
    private IEventBus EVENT_BUS = ModLoadingContext.get().getActiveContainer().getEventBus();
    public void setEventBus(IEventBus eventBus) {
        this.EVENT_BUS = eventBus;
    }

    @Override
    public void setRenderLayer(Block block, RenderType renderType)
    {
        ItemBlockRenderTypes.setRenderLayer(block, renderType);
    }

    @Override
    public void registerPredicate(Item item, ResourceLocation location, ClampedItemPropertyFunction function)
    {
        ItemProperties.register(item, location, function);
    }

    @Override
    public <T extends ParticleOptions> void registerParticle(ParticleType<T> type, ParticleProvider<T> provider)
    {
        EVENT_BUS.addListener((RegisterParticleProvidersEvent event) -> event.registerSpecial(type, provider));
    }

    @Override
    public void sendViaGamemode(MultiPlayerGameMode gameMode, Packet<?> packet)
    {
        ((MulPlayIntf)gameMode).vectorLib$sendPacketOnConnection(packet);
    }
}