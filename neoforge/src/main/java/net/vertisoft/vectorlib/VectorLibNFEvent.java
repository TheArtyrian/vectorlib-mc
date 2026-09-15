package net.vertisoft.vectorlib;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.vertisoft.vectorlib.agnostic.util.VectorTrade;
import net.vertisoft.vectorlib.platform.VectorRegNF;

import java.util.function.Consumer;
import java.util.function.Supplier;

@EventBusSubscriber(modid = VectorLib.ID, bus = EventBusSubscriber.Bus.GAME)
public class VectorLibNFEvent
{
    @SubscribeEvent
    public static void villagerTradeReg(VillagerTradesEvent event)
    {
        for (Supplier<VectorTrade.Profession> trade : VectorRegNF.NF_TRADES_PROF)
        {
            VectorTrade.Profession prof = trade.get();
            if (event.getType() == prof.getJob()) event.getTrades().get(prof.getLvl()).add(prof.getTrade());
        }
    }

    @SubscribeEvent
    public static void wanderingTradeReg(WandererTradesEvent event)
    {
        for (Supplier<VectorTrade.Wandering> trade : VectorRegNF.NF_TRADES_WAND)
        {
            VectorTrade.Wandering wand = trade.get();
            if (wand.isRare()) event.getRareTrades().add(wand.getTrade());
            else event.getGenericTrades().add(wand.getTrade());
        }
    }

    @SubscribeEvent
    public static void commandsReg(RegisterCommandsEvent event)
    {
        for (Consumer<CommandDispatcher<CommandSourceStack>> parameter : VectorRegNF.NF_COMMANDS)
        {
            parameter.accept(event.getDispatcher());
        }
    }
}
