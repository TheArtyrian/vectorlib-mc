package net.vertisoft.vectorlib.agnostic.cosmetic.capes;

import net.minecraft.resources.ResourceLocation;
import net.vertisoft.vectorlib.VectorLib;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An instance of a cape in VectorLib.
 * Contains several bits of data, such as its name, texture, transparency, and a list of UUIDs that can use it.
 */
public class VecCape
{
    private final List<UUID> allowedUsers;
    private final ResourceLocation texture;
    private final String translatable;
    private final String rawName;
    private final boolean transparent;

    public VecCape(String name, boolean transparent, List<UUID> users)
    {
        this.rawName = name;
        this.transparent = transparent;
        this.texture = VectorLib.id("textures/entity/capes/" + name + ".png");
        this.translatable = "cape.vectorlib.type." + name;
        this.allowedUsers = users;
    }

    public boolean isTransparent() { return this.transparent; }
    public ResourceLocation getTexture() { return this.texture; }
    public String getComponentID() { return this.translatable; }
    public String getCapeName() { return this.rawName; }
    public boolean userIsWorthy(UUID uuid) { return this.allowedUsers.contains(uuid); }
}
