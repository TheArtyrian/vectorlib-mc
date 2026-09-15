package net.vertisoft.vectorlib.agnostic.lolololol;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class VectorJoinMsg
{
    @Nullable private final String join;
    @Nullable private final String leave;
    @Nullable private final String renamed;
    private final Integer color;
    @Nullable private final CustomComponentSet componentSet;

    public VectorJoinMsg(Integer color)
    {
        this(null, null, null, null, color);
    }

    public VectorJoinMsg(@Nullable String join, @Nullable String renamed, @Nullable String leave, @Nullable CustomComponentSet set, Integer color)
    {
        this.join = join;
        this.renamed = renamed;
        this.leave = leave;
        this.componentSet = set;
        this.color = color;
    }

    public Component changeJoin(Component original, ServerPlayer player, boolean changed, String oldName)
    {
        Component returnable = original;
        Style ogStyle = original.getStyle();

        if (changed && this.renamed != null && this.componentSet != null) returnable = this.componentSet.joinRenamed(this.renamed, player, oldName).withStyle(ogStyle);
        else if (this.join != null && this.componentSet != null) returnable = this.componentSet.join(this.join, player).withStyle(ogStyle);

        returnable = returnable.copy().withColor(this.color);

        return returnable;
    }

    public Component changeLeave(Component original, ServerPlayer player)
    {
        Component returnable = original;
        Style ogStyle = original.getStyle();

        if (this.leave != null && this.componentSet != null) returnable = this.componentSet.leave(this.leave, player).withStyle(ogStyle);

        returnable = returnable.copy().withColor(this.color);

        return returnable;
    }

    public interface CustomComponentSet
    {
        default MutableComponent join(String id, ServerPlayer player) { return Component.translatable(id, player.getDisplayName()); }
        default MutableComponent joinRenamed(String id, ServerPlayer player, String oldName) { return Component.translatable(id, player.getDisplayName(), oldName); }
        default MutableComponent leave(String id, ServerPlayer player) { return Component.translatable(id, player.getDisplayName()); };
    }
}
