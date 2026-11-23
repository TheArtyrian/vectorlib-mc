package net.vertisoft.vectorlib;

import net.minecraft.ReportType;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.resources.ResourceLocation;
import net.vertisoft.vectorlib.agnostic.VectorSystems;
import net.vertisoft.vectorlib.agnostic.commands.VLEventCommand;
import net.vertisoft.vectorlib.agnostic.cosmetic.capes.VectorCapeMan;
import net.vertisoft.vectorlib.agnostic.cosmetic.joinmsg.VectorJoinMsgMan;
import net.vertisoft.vectorlib.agnostic.splash.VectorSplash;
import net.vertisoft.vectorlib.agnostic.util.VectorDateEvents;
import net.vertisoft.vectorlib.platform.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        VectorDateEvents.getDates();

        VectorLib.strapLibContent();
        VectorLib.setNewReportErrors();
    }

    private static void strapLibContent()
    {
        VectorLib.REGISTRY.registerCommand(VLEventCommand::register);
    }

    private static void setNewReportErrors()
    {
        // Crash
        ReportType.CRASH.nuggets().addAll(List.of(
                "Guess you could say...that was your final Frontier. *snicker*",
                "Maybe I shouldn't have suppressed experimental warnings...",
                "Proooobably shouldn't report this log to Mojang",
                "Did you mod his answer?",
                "Will this mean no more Java mods or custom skins?",
                "...did you think I wasn't capable of using mixins here? lol",
                "USE ALL THE MIXINS, JERRY",
                "I thought...I thought I was [title card].",
                "Oh, good. Game engine guts.",
                "LWJGL my beloved",
                "I tried, okay? Cut me a little slack.",
                "That's probably going to leave a mark",
                "Hey, on the bright side, this is an excuse to go outside now!",
                "Did you know mitochondria is the powerhouse of the cell?",
                "Ten thousand?! MY PC",
                "The consequences of the fill command with TNT",
                "Your crash WILL be reported, but first I must QUICKLY tell y'all that...",
                "Sorry, no free cake for you!",
                "Whoops! You have to put the CD in your computer!",
                "Famous VectorLib mod developer Artyrian was subsequently taken out back and put down",
                "Reality is often disappointing, isn't it?",
                "Today's crash is brought to you by VectorLib",
                "Enjoying the crashes? Also try Realm of the Mad God!"
        ));

        // Chunk
        ReportType.CHUNK_IO_ERROR.nuggets().addAll(List.of(
                "Dang it, I thought we actually removed Herobrine this time!",
                "The .mca format is perfect and flawless",
                "Sorry, I was hungry",
                "Creepypasta moment",
                "I bet it was one of those worldgen overhaul mods you probably have installed. Tsk.",
                "You think there's an anthropomorphic goat down there?",
                "...I don't suppose a hug will make up for this, will it?",
                "Cripes.",
                "Maybe if you stare at it for long enough it'll reappear or something",
                "In my defense, that build was pretty awful",
                "Ok, maybe that missing blockstate wasn't recoverable after all",
                "Genuinely what"
        ));

        // Network
        ReportType.NETWORK_PROTOCOL_ERROR.nuggets().addAll(List.of(
                "There's an axe in the server!",
                "Great, now we gotta contact tech support",
                "Could this be the work of Skynet?!",
                "You made sure your modem is hooked up, right?",
                "Is someone using the phone line right now? That might be the issue.",
                "MOM, GET OFF THE PHONE!!!",
                "Where are you, a house made of lead?",
                "Have your checked your VPN?",
                "You DID make sure you forwarded that port, right?",
                "The only thing that could make this worse is your IP address getting leaked!",
                "Steve Breaks the Internet",
                "Server owners hate this one simple trick",
                "Let's turn this into an excuse to beat up Sam Altman"
        ));

        // Profiler
        ReportType.PROFILE.nuggets().addAll(List.of(
                "Because you can never have enough optimization in Java",
                "If you have OptiFine installed, then this won't even make a difference",
                "C# better.",
                "At least this is easier than profiling C++. Probably",
                "Whoa, and a profiler? This thing's deluxe!",
                "Because writing more performant code isn't an option!",
                "We pushin', we poppin', and there ain't no signs of stoppin'",
                "5 billion Erythmites later..."
        ));

        // Test
        ReportType.TEST.nuggets().addAll(List.of(
                "Hey ma, I'm in a test log!",
                "A widdle baby crash!",
                "You have not crashed! Click Here to crash!",
                "Bet I scared you there, huh?",
                "Did I jumpscare you? Yeah, I bet I did. You coward. You make me sick.",
                "Oh hey, I'm still alive!"
        ));
    }

    public static ResourceLocation id(String string) { return VectorLib.id(ID, string); }
    public static ResourceLocation id(String id, String string) { return ResourceLocation.fromNamespaceAndPath(id, string); }
}