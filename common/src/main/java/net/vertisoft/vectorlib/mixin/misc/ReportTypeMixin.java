package net.vertisoft.vectorlib.mixin.misc;

import com.google.common.collect.Lists;
import net.minecraft.ReportType;
import net.vertisoft.vectorlib.VectorLib;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Debug(export = true)
@Mixin(ReportType.class)
public abstract class ReportTypeMixin
{
    @Shadow @Final @Mutable public static ReportType TEST;
    @Shadow @Final @Mutable public static ReportType CRASH;
    @Shadow @Final @Mutable public static ReportType CHUNK_IO_ERROR;
    @Shadow @Final @Mutable public static ReportType NETWORK_PROTOCOL_ERROR;
    @Shadow @Final @Mutable public static ReportType PROFILE;

    @Shadow @Final private String header;
    @Shadow @Final private List<String> nuggets;

    static {
        // Crash
        String headerCrash = CRASH.header();
        List<String> nuggetsCrash = Lists.newLinkedList();
        nuggetsCrash.addAll(CRASH.nuggets());
        nuggetsCrash.addAll(List.of(
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
                "Famous Frontiers & VectorLib mod developer Artyrian was subsequently taken out back and put down",
                "Reality is often disappointing, isn't it?",
                "Today's crash is brought to you by VectorLib",
                "Enjoying the crashes? Also try Realm of the Mad God!"
        ));
        CRASH = new ReportType(headerCrash, nuggetsCrash);

        // Chunk
        String headerChunk = CHUNK_IO_ERROR.header();
        List<String> nuggetsChunk = Lists.newLinkedList();
        nuggetsChunk.addAll(CHUNK_IO_ERROR.nuggets());
        nuggetsChunk.addAll(List.of(
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
                "Ok, maybe that missing blockstate wasn't recoverable after all"
        ));
        CHUNK_IO_ERROR = new ReportType(headerChunk, nuggetsChunk);

        // Network
        String headerNet = NETWORK_PROTOCOL_ERROR.header();
        List<String> nuggetsNet = Lists.newLinkedList();
        nuggetsNet.addAll(NETWORK_PROTOCOL_ERROR.nuggets());
        nuggetsNet.addAll(List.of(
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
                "Steve Breaks the Internet"
        ));
        NETWORK_PROTOCOL_ERROR = new ReportType(headerNet, nuggetsNet);

        // Profile
        String headerProfile = PROFILE.header();
        List<String> nuggetsProfile = Lists.newLinkedList();
        nuggetsProfile.addAll(PROFILE.nuggets());
        nuggetsProfile.addAll(List.of(
                "Because you can never have enough optimization in Java",
                "If you have OptiFine installed, then this won't even make a difference",
                "C# better.",
                "At least this is easier than profiling C++. Probably",
                "Whoa, and a profiler? This thing's deluxe!",
                "Because writing more performant code isn't an option!",
                "We pushin', we poppin', and there ain't no signs of stoppin'"
        ));
        PROFILE = new ReportType(headerProfile, nuggetsProfile);

        // Profile
        String headerTest = TEST.header();
        List<String> nuggetsTest = Lists.newLinkedList();
        nuggetsTest.addAll(TEST.nuggets());
        nuggetsTest.addAll(List.of(
                "Hey ma, I'm in a test log!",
                "A widdle baby crash!",
                "You have not crashed! Click Here to crash!",
                "Bet I scared you there, huh?",
                "Did I jumpscare you? Yeah, I bet I did. You coward. You make me sick.",
                "Oh hey, I'm still alive!"
        ));
        TEST = new ReportType(headerTest, nuggetsTest);
    }
}