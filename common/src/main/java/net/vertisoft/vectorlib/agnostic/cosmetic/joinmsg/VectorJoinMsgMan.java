package net.vertisoft.vectorlib.agnostic.cosmetic.joinmsg;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.vertisoft.vectorlib.agnostic.VectorSystems;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * The custom join/leave msg manager for VectorLib.
 */
public class VectorJoinMsgMan
{
    public static final Map<UUID, VectorJoinMsg> JOIN_MSGS = new HashMap<>();

    private static boolean INITED = false;

    public static void init()
    {
        if (INITED) throw new IllegalArgumentException("VectorJoinMsgMan was already initialized");
        INITED = true;

        // Artyrian
        addJoinLeaveMsg(
                VectorSystems.CONTRIBUTORS.get("Artyrian"),
                "multiplayer.vectorlib.player.joined_bad",
                "multiplayer.vectorlib.player.joined_bad.renamed",
                "multiplayer.vectorlib.player.left_bad",
                ChatFormatting.GOLD.getColor()
        );

        // Rednalokin
        addJoinLeaveMsg(
                VectorSystems.CONTRIBUTORS.get("Rednalokin"),
                "multiplayer.vectorlib.player.joined_redna",
                "multiplayer.vectorlib.player.joined_redna.renamed",
                "multiplayer.vectorlib.player.left_redna",
                0xE44239,
                new VectorJoinMsg.CustomComponentSet()
                {
                    @Override public MutableComponent join(String id, ServerPlayer player)
                    {
                        LocalDate localDate = LocalDate.now();
                        String month = localDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK);
                        String day = String.valueOf(localDate.getDayOfMonth());
                        String yr = String.valueOf(localDate.getYear());
                        return Component.translatable(id, month, day, yr, player.getDisplayName());
                    }
                    @Override public MutableComponent joinRenamed(String id, ServerPlayer player, String oldName)
                    {
                        LocalDate localDate = LocalDate.now();
                        String month = localDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK);
                        String day = String.valueOf(localDate.getDayOfMonth());
                        String yr = String.valueOf(localDate.getYear());
                        return Component.translatable(id, month, day, yr, player.getDisplayName(), oldName);
                    }
                }
        );

        // Color only
        addJoinLeaveColor(VectorSystems.CONTRIBUTORS.get("Yurjezich"), 0x49FFCE);
        addJoinLeaveColor(VectorSystems.CONTRIBUTORS.get("EmeraldEiscue"), 0x326AE9);
        addJoinLeaveColor(VectorSystems.CONTRIBUTORS.get("Courtjjester"), 0xFF4DDF);
    }

    /////////////////////////////////////////////////////////////

    public static VectorJoinMsg getMsg(UUID uuid)
    {
        return JOIN_MSGS.getOrDefault(uuid, null);
    }

    /////////////////////////////////////////////////////////////

    private static void addJoinLeaveMsg(UUID ID, String join, String rename, String leave, Integer color)
    {
        addJoinLeaveMsg(ID, join, rename, leave, color, new VectorJoinMsg.CustomComponentSet(){});
    }

    private static void addJoinLeaveMsg(UUID ID, String join, String rename, String leave, Integer color, VectorJoinMsg.CustomComponentSet set)
    {
        JOIN_MSGS.put(ID, new VectorJoinMsg(join, rename, leave, set, color));
    }

    private static void addJoinLeaveColor(UUID ID, Integer color)
    {
        JOIN_MSGS.put(ID, new VectorJoinMsg(color));
    }
}
