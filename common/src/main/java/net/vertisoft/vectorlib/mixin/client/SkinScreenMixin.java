package net.vertisoft.vectorlib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.network.chat.Component;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.cosmetic.capes.VecClientCapeMan;
import net.vertisoft.vectorlib.agnostic.cosmetic.capes.VectorCapeMan;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.SetCapePayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.UUID;

@Mixin(SkinCustomizationScreen.class)
public abstract class SkinScreenMixin extends OptionsSubScreen
{
    // Irrelevant
    public SkinScreenMixin(Screen lastScreen, Options options, Component title) { super(lastScreen, options, title); }

    @WrapOperation(method = "addOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/OptionsList;addSmall(Ljava/util/List;)V"))
    private void vectorlib$addCapeman(OptionsList instance, List<AbstractWidget> options, Operation<Void> original)
    {
        Minecraft minecraft = Minecraft.getInstance();
        UUID uuid = minecraft.getGameProfile().getId();
        if (uuid != null && VecClientCapeMan.isCapeWorthy())
        {
            String capeid = VecClientCapeMan.capeID();
            Component comp = (capeid.isEmpty()) ? VectorCapeMan.OFF_CAPE : Component.translatable(VectorCapeMan.getAssociatedCape(capeid).getComponentID());

            Button button = Button.builder(
                    Component.translatable("vectorlib.button.capeman", comp),
                    candidate -> {
                        String toSet = "";
                        Component name;

                        if (minecraft.player != null && minecraft.getConnection() != null) {
                            toSet = VectorCapeMan.next(uuid);
                            name = VectorCapeMan.getSelectedName(uuid);
                            VectorLib.NETWORK.sendToServer(new SetCapePayload(toSet));
                        }
                        else {
                            Pair<String, Component> pair = VectorCapeMan.getNextCapeNotInGame(VecClientCapeMan.capeID(), uuid);
                            toSet = pair.getFirst();
                            name = pair.getSecond();
                        }

                        candidate.setMessage(Component.translatable("vectorlib.button.capeman", name));
                        VecClientCapeMan.setCape(toSet);
                    }
            ).build();
            options.add(button);
        }
        original.call(instance, options);
    }
}
