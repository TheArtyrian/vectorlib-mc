package net.vertisoft.vectorlib.platform;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public interface VectorPassthruIntf
{
    void setLootTableID(LootTable table, ResourceLocation id);
}
