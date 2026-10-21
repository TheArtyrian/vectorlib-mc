package net.vertisoft.vectorlib;

import net.minecraft.resources.ResourceLocation;
import net.vertisoft.vectorlib.agnostic.VectorSystems;
import net.vertisoft.vectorlib.agnostic.commands.VLEventCommand;
import net.vertisoft.vectorlib.agnostic.cosmetic.capes.VectorCapeMan;
import net.vertisoft.vectorlib.agnostic.cosmetic.joinmsg.VectorJoinMsgMan;
import net.vertisoft.vectorlib.platform.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

/*
    Codebase uses or is heavily inspired by the following projects:
        https://github.com/jaredlll08/MultiLoader-Template
        https://github.com/Heccology/NexusLib
        https://github.com/Yirmiri/RunicLib
    To the projects listed above, thank you so much!

    Also NO I DID NOT NAME THIS AFTER THE DESPICABLE ME CHARACTER
 */
public class VectorLib
{
    // ID + Logger
    public static final String ID = "vectorlib";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    // Cross-platform systems
    public static final VectorPlatformIntf PLATFORM = load(VectorPlatformIntf.class);
    public static VectorRegistryIntf REGISTRY = load(VectorRegistryIntf.class);
    public static final VectorNetworkIntf NETWORK = load(VectorNetworkIntf.class);
    public static final VectorPassthruIntf LOADER_PASSTHRU = load(VectorPassthruIntf.class);
    public static VectorClientIntf client()
    {
        if (!PLATFORM.isClient()) throw new IllegalStateException("Developer attempted to use Client-side code on the server! Lole!");
        return load(VectorClientIntf.class);
    }

    // Config file
    public static VectorLibConfig CONFIG = new VectorLibConfig();

    public static <T> T load(Class<T> classy)
    {
        final T service = ServiceLoader.load(classy, VectorLib.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Couldn't load the VectorLib service for " + classy.getName() + "! Is the META-INF broken?"));
        LOGGER.debug("Successfully loaded VectorLib: {} -> service of {}.", service, classy);
        return service;
    }

    public static void bootstrap()
    {
        LOGGER.info(
                "\n =====> [[VectorLib]] - a multiloader porting library for mods by Artyrian" +
                "\n =====> Special thanks to Yirmiri (RunicLib), Hecco (NexusLib), & Zeus (RefractionAPI)" +
                "\n =====> Protip: " + VectorSystems.doAGamerMoment()
        );

        CONFIG = VectorLibConfig.load_config();

        VectorCapeMan.init();
        VectorJoinMsgMan.init();

        VectorLib.strapLibContent();
    }

    private static void strapLibContent()
    {
        VectorLib.REGISTRY.registerCommand(VLEventCommand::register);
    }

    public static ResourceLocation id(String string) { return VectorLib.id(ID, string); }
    public static ResourceLocation id(String id, String string) { return ResourceLocation.fromNamespaceAndPath(id, string); }
}