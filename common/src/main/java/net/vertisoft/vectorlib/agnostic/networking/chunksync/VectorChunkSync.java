package net.vertisoft.vectorlib.agnostic.networking.chunksync;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.vertisoft.vectorlib.VectorLib;

import java.util.HashMap;
import java.util.Map;

public class VectorChunkSync
{
    public static final String NBT_NAME = "VectorChunkSyncData";
    private static final Map<String, ChunkRead> READ_ACTIONS = new HashMap<>();
    private static final Map<String, ChunkWrite> WRITE_ACTIONS = new HashMap<>();

    public static void registerAction(String modID, ChunkRead read, ChunkWrite write)
    {
        try
        {
            if (READ_ACTIONS.containsKey(modID)) throw new IllegalArgumentException(String.format("ReadAction map already contains a key for %s", modID));
            if (WRITE_ACTIONS.containsKey(modID)) throw new IllegalArgumentException(String.format("WriteAction map already contains a key for %s", modID));

            READ_ACTIONS.put(modID, read);
            WRITE_ACTIONS.put(modID, write);
        }
        catch (IllegalArgumentException exc)
        {
            VectorLib.LOGGER.error("Couldn't add read/write actions to ChunkSync, see below", exc);
        }
    }

    public static void read(ChunkAccess chunk, CompoundTag tag)
    {
        if (READ_ACTIONS.isEmpty()) return;

        if (tag.contains(VectorChunkSync.NBT_NAME, ByteTag.TAG_COMPOUND))
        {
            CompoundTag reader = tag.getCompound(VectorChunkSync.NBT_NAME);
            if (!reader.isEmpty())
            {
                for (String mod : WRITE_ACTIONS.keySet())
                {
                    if (reader.contains(mod, ByteTag.TAG_COMPOUND))
                    {
                        CompoundTag tag1 = reader.getCompound(mod);
                        ChunkRead readAction = READ_ACTIONS.get(mod);
                        readAction.read(chunk, tag1);
                    }
                }
            }
            //else VectorLib.LOGGER.error("VectorChunkSync tag was found on the chunk, but nothing was in it");
        }
    }

    public static void write(ChunkAccess chunk, CompoundTag tag)
    {
        if (WRITE_ACTIONS.isEmpty()) return;

        CompoundTag writer = new CompoundTag();
        for (String mod : WRITE_ACTIONS.keySet())
        {
            CompoundTag write2 = new CompoundTag();
            ChunkWrite writeAction = WRITE_ACTIONS.get(mod);
            writeAction.write(chunk, write2);

            if (!write2.isEmpty()) writer.put(mod, write2);
        }

        if (!writer.isEmpty()) tag.put(VectorChunkSync.NBT_NAME, writer);
        //else VectorLib.LOGGER.error("WriteActions map isn't empty, but nothing was written to the master tag");
    }

    @FunctionalInterface
    public interface ChunkRead
    {
        void read(ChunkAccess chunk, CompoundTag tag);
    }

    @FunctionalInterface
    public interface ChunkWrite
    {
        void write(ChunkAccess chunk, CompoundTag tag);
    }
}
