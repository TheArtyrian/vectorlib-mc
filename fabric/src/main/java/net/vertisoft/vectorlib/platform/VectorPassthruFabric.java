package net.vertisoft.vectorlib.platform;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.vertisoft.vectorlib.VectorLib;

public class VectorPassthruFabric implements VectorPassthruIntf
{
    @Override
    public void setLootTableID(LootTable table, ResourceLocation id)
    {
        try
        {
            throw new IllegalArgumentException("VectorLootIDIntf is reserved for NeoForge ONLY - should not be used on other platforms");
        }
        catch (IllegalArgumentException exc)
        {
            VectorLib.LOGGER.error("Cannot invoke setLootTableID, see below:", exc);
        }
    }
}
