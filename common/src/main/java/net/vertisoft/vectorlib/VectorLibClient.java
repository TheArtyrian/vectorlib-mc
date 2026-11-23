package net.vertisoft.vectorlib;

import net.minecraft.client.gui.components.SplashRenderer;
import net.vertisoft.vectorlib.agnostic.cosmetic.capes.VecClientCapeMan;
import net.vertisoft.vectorlib.agnostic.splash.VectorSplash;
import net.vertisoft.vectorlib.agnostic.util.VectorDateEvents;

public class VectorLibClient
{
    public static void bootstrap()
    {
        VecClientCapeMan.initialize();
        splashes();
    }

    private static void splashes()
    {
        // Customs
        VectorSplash.registerSpecial((random) -> new SplashRenderer("Happy birthday, Artyrian!"), VectorDateEvents::isBadDay);
        VectorSplash.registerSpecial((random) -> new SplashRenderer("Happy birthday, Xenona!"), VectorDateEvents::isXensBday);
        VectorSplash.registerSpecial((random) -> new SplashRenderer("Happy birthday, Yurjezich!"), VectorDateEvents::isYurBday);
        VectorSplash.registerSpecial((random) -> new SplashRenderer("Happy birthday, Hecco!"), VectorDateEvents::isHeccoBday);
        VectorSplash.registerSpecial((random) -> new SplashRenderer("Happy birthday, Yirmiri!"), VectorDateEvents::isYirmiriBday);
    }
}
