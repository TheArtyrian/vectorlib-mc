package net.vertisoft.vectorlib.agnostic;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.cosmetic.capes.VectorCapeMan;
import net.vertisoft.vectorlib.agnostic.cosmetic.joinmsg.VectorJoinMsg;
import net.vertisoft.vectorlib.agnostic.splash.VectorSplash;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class VectorSystems
{
    // Important contributor IDs
    public static final Map<String, UUID> CONTRIBUTORS = Map.ofEntries(
        // NEOFORGE DEV //////////////////////////////
        Map.entry("Dev", UUID.fromString("380df991-f603-344c-a090-369bad2a924a")),
        //////////////////////////////////////////////
        Map.entry("Artyrian", UUID.fromString("774e37fc-1ca4-4156-827e-661afa24cb56")),
        Map.entry("Yurjezich", UUID.fromString("2a9c377e-26cc-4d48-a62a-05ce3ac2f405")),
        Map.entry("KirbyTG", UUID.fromString("651fefc2-fae9-46ea-b383-8e45798fc1b2")),
        Map.entry("LucarioDeath", UUID.fromString("2f213cea-2443-4313-8aa4-0f4c72687ddd")),
        Map.entry("EmeraldEiscue", UUID.fromString("3ab1a668-b818-4d44-b81c-ac1b105c7692")),
        Map.entry("Rednalokin", UUID.fromString("da448516-03c1-4695-b659-37daa441bd7f")),
        Map.entry("Hecco", UUID.fromString("bc56b2c8-9ef8-4532-b045-00f44804bca4")),
        Map.entry("Diemant", UUID.fromString("32290fa8-77ed-4794-9cba-25c09e7f4e1d")),
        Map.entry("Yirmiri", UUID.fromString("1cedf927-5c8f-4650-95e9-808fc8f94d00")),
        Map.entry("Courtjjester", UUID.fromString("95e928ac-0cc8-4bf9-8451-d33da7933fd3")),
        Map.entry("SlimeSlabs", UUID.fromString("54701376-b19a-4fc1-b107-74626b0d1bfb"))
    );

    private static final RandomSource RANDOM = RandomSource.createNewThreadLocalInstance();     // Randomsource

    // Special join/leave messages
    public final Map<UUID, VectorJoinMsg> JOIN_MSGS = new HashMap<>();

    // I'm so funny
    private static final List<String> GAMER_MOMENTS = List.of(
            "Frontiers is the worst mod ever made",
            "Frontiers is the best mod ever made",
            "Oggy <3<3<3<3<3<3<3<3<3<3<3",
            "Dig straight down right now",
            "Wash your hands, you filthy animal",
            "Download Secure Craft Protect (Zeus made me write this)",
            "You could be programming in literally any other language than Java",
            "Don't be Notch and make games in JavaScript",
            "xD",
            "Please for the love of god don't pick up Deadlock",
            "java.lang.NullPointerException",
            "Failed to parse splash.txt",
            "I hate you",
            "I love you",
            "Soon Wyoming will be ours!",
            "Also try Terraria",
            "Also try Core Keeper",
            "Also try Stardew Valley",
            "Also try Hypertrig",
            "Stream Metaroom on Spotify",
            "Stream Aphex Twin on Spotify",
            "Stream Kawai Sprite on Spotify",
            "Hiiiiiiii :3",
            "Heccology mods are cool and based",
            "Toby Fox can you please give me Chapter 7 early thanks",
            "Gabe Newell would make billions of dollars if he hired Artyrian (fact checked)",
            "I don't even have a protip today, just enjoy the broken mods",
            "This is our...final frontiers",
            "Don't name your Tome of Fangs \"Florida Man\"",
            "For just 30 levels, you can remove all of your curses at a Curse Altar! This is true and real information",
            "Seven Deadlock",
            "It has two modes: full auto, and fuller auto",
            "You must construct additional pylons",
            "Sleep in a bed to skip to day",
            "Everybody knows that Hatsune Miku made this game",
            "Don't eat the gas station bagels, they'll give you a tummyache",
            "Remember to like and subscribe!",
            "Mending is fair and balanced",
            "I'm watching you.",
            "I am inside your skin",
            "Remember to clean your bathroom at least once every 2 weeks",
            "Insert cash, or select payment type",
            "Press F to zipline boost",
            "You have not crashed! Click here to crash",
            "Dandruff is a good substitute for shredded parmesean",
            "Attack while it's tail's up!",
            "OwOLib's wisdoms are far funnier than this poor excuse of a stupid sentence generator, also please don't kill me owo devs",
            "The world's in a pretty tough spot right now. I'm just glad you're here, though. Stay strong. :)",
            "Join me, and I will make your face the greatest in Koridai, or else you will DIE",
            "You can make a lot of good things with just some bread and eggs",
            "The End Update is never coming out and that makes me sad",
            "Roblox has like 2 or 3 good games and then the rest are trash just like the platform itself",
            "Don't eat citrus and then drink milk afterwards, trust me on this one",
            "Yoshis or Chocobos? It's a hard decision for sure",
            "Protip: Protip: Protip: Protip: Protip:",
            "Don't ever give up, my son",
            "Remember to get at least 8 hours of sleep (says the guy who only sleeps 4 hours)",
            "If you see Artyrian, tell him that I said die",
            "I miss my wife",
            "I miss my husband",
            "Shut up",
            "Why are you actually taking time out of your day to read these",
            "Without mucous secretions, your stomach would digest itself",
            "Remember your mortality",
            "Being \"on time\" usually is padded with 15 minutes before and after the marked time",
            "Don't write multiloader code or you will hate yourself for eternity",
            "Eat a good breakfast, and you will feel like taking the world head-on",
            "Be like Dani, drink your milk and abuse Unity's particle system",
            "Take a shower, I can smell you from the east coast",
            "you are dead",
            "Remember to take a break approximately every 30 minutes!",
            "Can you shut up about the direction and magnitude",
            "Never tell the world your dreams.",
            "Frontiers is dead, I'm not sure why so many of my protips reference it",
            "My blood is a controlled substance in 42 states",
            "Har har har har har har har har har haaar",
            "VectorLib killed my grandma",
            "You can make a library mod without using vibe coding! Just like VectorLib!"
    );

    // Suppression warning message
    public static final String SUPPRESSION_WARNING =
            "\n     Suppressing experimental warnings due to VectorLib config settings - as this is a modded installation, you should know the risks already." +
            "\n     You can disable this suppression in the VectorLib config file - set suppressExperimentalWarn to false." +
            "\n     Remember, make backups of your world whenever possible and/or convenient!";

    // Here for making my life easier
    public static final String JEI_PREFIX = "jei.item.desc.";

    public static String doAGamerMoment() { return GAMER_MOMENTS.get(RANDOM.nextInt(GAMER_MOMENTS.size())); }
}
