package net.vertisoft.vectorlib.agnostic.networking.netsync;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.vertisoft.vectorlib.VectorLib;
import net.vertisoft.vectorlib.agnostic.networking.data.payloads.NetSyncPayload;

import java.util.UUID;
import java.util.function.Consumer;

/** A loader-independent replacement for data attachments for vanilla entities.
 * Takes care of most of the heavy lifting when it comes to syncing data between the server and clients.
 * <p>
 * Since this a purely in-house solution, there's no concerns about other mods clashing with any data stored.
 * <p>
 * Any entity using this MUST implement {@link VectorSyncable VectorSyncable}
 * in order to properly sync.
 * */
public class VectorNetSync
{
    public static final String LEGACY_FABRIC = "fabric:attachments";
    public static final String NBT_NAME = "VectorNetSyncData";
    public static final String ID_TAG_NAME = "VECTOR_ID";

    private final Entity TRACKING;
    private final String ID;
    private final boolean sync_to_client;
    private CompoundTag TAG;

    public VectorNetSync(Entity trackable, String identifier, boolean sync_to_client, Consumer<CompoundTag> impl)
    {
        this.TRACKING = trackable;
        this.ID = identifier;
        this.sync_to_client = sync_to_client;
        this.TAG = new CompoundTag();
        this.TAG.putString(ID_TAG_NAME, ID);
        impl.accept(this.TAG);
    }

    /** Sets the tag for this sync. */
    public void setTag(CompoundTag tag) { this.TAG = tag; }
    /** Gets the CompoundTag attached to this sync. */
    public CompoundTag getTag() { return this.TAG; }

    /** Determines if the identity for the provided compound tag exists. */
    public boolean identityMatches(CompoundTag tag)
    {
        if (this.TAG.contains(ID_TAG_NAME, ByteTag.TAG_STRING) && tag.contains(ID_TAG_NAME, ByteTag.TAG_STRING))
        {
            String x1 = this.TAG.getString(ID_TAG_NAME);
            String x2 = tag.getString(ID_TAG_NAME);
            return x1.equals(x2);
        }
        return false;
    }

    /** Syncs an integer. */
    public void syncInt(String key, int value, boolean force_if_absent)
    {
        if (this.TAG.contains(key, ByteTag.TAG_INT) || force_if_absent)
        {
            this.TAG.putInt(key, value);
            this.sendToAllTracking();
        }
        else
        {
            VectorLib.LOGGER.warn("Attempted to add an Int value {} to a NetSync tag for {} but it doesn't exist.", key, this.TRACKING.toString());
        }
    }
    /** Gets an integer, or defaults to another. */
    public int getInt(String key, int fallback)
    {
        if (this.TAG.contains(key, ByteTag.TAG_INT))
        {
            return this.TAG.getInt(key);
        }
        else
        {
            VectorLib.LOGGER.warn("Cannot find Int value for {}, falling back", key);
            return fallback;
        }
    }

    /** Syncs a String. */
    public void syncString(String key, String value, boolean force_if_absent)
    {
        if (this.TAG.contains(key, ByteTag.TAG_STRING) || force_if_absent)
        {
            this.TAG.putString(key, value);
            this.sendToAllTracking();
        }
        else
        {
            VectorLib.LOGGER.warn("Attempted to add a String value {} to a NetSync tag for {} but it doesn't exist.", key, this.TRACKING.toString());
        }
    }
    /** Gets a String, or defaults to another. */
    public String getString(String key, String fallback)
    {
        if (this.TAG.contains(key, ByteTag.TAG_STRING))
        {
            return this.TAG.getString(key);
        }
        else
        {
            VectorLib.LOGGER.warn("Cannot find String value for {}, falling back", key);
            return fallback;
        }
    }

    /** Syncs a Double. */
    public void syncDouble(String key, double value, boolean force_if_absent)
    {
        if (this.TAG.contains(key, ByteTag.TAG_DOUBLE) || force_if_absent)
        {
            this.TAG.putDouble(key, value);
            this.sendToAllTracking();
        }
        else
        {
            VectorLib.LOGGER.warn("Attempted to add a Double value {} to a NetSync tag for {} but it doesn't exist.", key, this.TRACKING.toString());
        }
    }
    /** Gets a double, or defaults to another. */
    public double getDouble(String key, double fallback)
    {
        if (this.TAG.contains(key, ByteTag.TAG_DOUBLE))
        {
            return this.TAG.getDouble(key);
        }
        else
        {
            VectorLib.LOGGER.warn("Cannot find Double value for {}, falling back", key);
            return fallback;
        }
    }

    /** Syncs a Float. */
    public void syncFloat(String key, float value, boolean force_if_absent)
    {
        if (this.TAG.contains(key, ByteTag.TAG_FLOAT) || force_if_absent)
        {
            this.TAG.putFloat(key, value);
            this.sendToAllTracking();
        }
        else
        {
            VectorLib.LOGGER.warn("Attempted to add a Float value {} to a NetSync tag for {} but it doesn't exist.", key, this.TRACKING.toString());
        }
    }
    /** Gets a float, or defaults to another. */
    public float getFloat(String key, float fallback)
    {
        if (this.TAG.contains(key, ByteTag.TAG_FLOAT))
        {
            return this.TAG.getFloat(key);
        }
        else
        {
            VectorLib.LOGGER.warn("Cannot find Float value for {}, falling back", key);
            return fallback;
        }
    }

    /** Syncs a Byte. */
    public void syncByte(String key, byte value, boolean force_if_absent)
    {
        if (this.TAG.contains(key, ByteTag.TAG_BYTE) || force_if_absent)
        {
            this.TAG.putByte(key, value);
            this.sendToAllTracking();
        }
        else
        {
            VectorLib.LOGGER.warn("Attempted to add a Byte value {} to a NetSync tag for {} but it doesn't exist.", key, this.TRACKING.toString());
        }
    }
    /** Gets a byte, or defaults to another. */
    public byte getByte(String key, byte fallback)
    {
        if (this.TAG.contains(key, ByteTag.TAG_BYTE))
        {
            return this.TAG.getByte(key);
        }
        else
        {
            VectorLib.LOGGER.warn("Cannot find Byte value for {}, falling back", key);
            return fallback;
        }
    }

    /** Syncs a boolean. */
    public void syncBool(String key, boolean value, boolean force_if_absent)
    {
        if (this.TAG.contains(key, ByteTag.TAG_BYTE) || force_if_absent)
        {
            this.TAG.putBoolean(key, value);
            this.sendToAllTracking();
        }
        else
        {
            VectorLib.LOGGER.warn("Attempted to add a Boolean value {} to a NetSync tag for {} but it doesn't exist.", key, this.TRACKING.toString());
        }
    }
    /** Gets a boolean, or defaults to another. */
    public boolean getBool(String key, boolean fallback)
    {
        if (this.TAG.contains(key, ByteTag.TAG_BYTE))
        {
            return this.TAG.getBoolean(key);
        }
        else
        {
            VectorLib.LOGGER.warn("Cannot find Boolean value for {}, falling back", key);
            return fallback;
        }
    }

    /** Syncs an ItemStack. */
    public void syncItemStack(String key, ItemStack stack, boolean force_if_absent)
    {
        if (stack.isEmpty())
        {
            VectorLib.LOGGER.warn("Cannot encode an empty ItemStack for {}!", this.TRACKING.toString());
            return;
        }

        if (this.TAG.contains(key, ByteTag.TAG_COMPOUND) || force_if_absent)
        {
            this.TAG.put(key, stack.save(this.TRACKING.registryAccess()));
            this.sendToAllTracking();
        }
        else
        {
            VectorLib.LOGGER.warn("Attempted to add an ItemStack Compound value {} to a NetSync tag for {} but it doesn't exist.", key, this.TRACKING.toString());
        }
    }
    /** Gets an ItemStack, or defaults to another. */
    public ItemStack getItemStack(String key, ItemStack fallback)
    {
        if (this.TAG.contains(key, ByteTag.TAG_COMPOUND))
        {
            CompoundTag tag = (CompoundTag)this.TAG.get(key);
            return ItemStack.parse(this.TRACKING.registryAccess(), tag).orElse(fallback);
        }
        else
        {
            VectorLib.LOGGER.warn("Cannot find ItemStack value for {}, falling back", key);
            return fallback;
        }
    }

    /** Syncs a UUID. */
    public void syncUUID(String key, UUID value, boolean force_if_absent)
    {
        if (this.TAG.contains(key, ByteTag.TAG_INT_ARRAY) || force_if_absent)
        {
            this.TAG.putUUID(key, value);
            this.sendToAllTracking();
        }
        else
        {
            VectorLib.LOGGER.warn("Attempted to add a UUID value {} to a NetSync tag for {} but it doesn't exist.", key, this.TRACKING.toString());
        }
    }
    /** Gets a UUID (or null). */
    public UUID getUUID(String key, boolean suppress_warn)
    {
        if (this.TAG.contains(key, ByteTag.TAG_INT_ARRAY))
        {
            UUID tryer = this.TAG.getUUID(key);
            if (tryer != null) return tryer;
        }
        if (!suppress_warn) VectorLib.LOGGER.warn("Cannot find UUID value for {}, falling back", key);
        return null;
    }

    /** Syncs a BlockPos. */
    public void syncBlockPos(String key, BlockPos value, boolean force_if_absent)
    {
        if (this.TAG.contains(key, ByteTag.TAG_COMPOUND) || force_if_absent)
        {
            CompoundTag tag = new CompoundTag();
            tag.putInt("x", value.getX());
            tag.putInt("y", value.getY());
            tag.putInt("z", value.getZ());
            this.TAG.put(key, tag);
            this.sendToAllTracking();
        }
        else
        {
            VectorLib.LOGGER.warn("Attempted to add a BlockPos compound value {} to a NetSync tag for {} but it doesn't exist.", key, this.TRACKING.toString());
        }
    }
    /** Gets a BlockPos, or defaults to another. */
    public BlockPos getBlockPos(String key, BlockPos fallback)
    {
        if (this.TAG.contains(key, ByteTag.TAG_COMPOUND))
        {
            CompoundTag tag = (CompoundTag)this.TAG.get(key);
            if (tag.contains("x", ByteTag.TAG_INT) && tag.contains("y", ByteTag.TAG_INT) && tag.contains("z", ByteTag.TAG_INT))
            {
                int x = tag.getInt("x");
                int y = tag.getInt("y");
                int z = tag.getInt("z");
                return new BlockPos(x, y, z);
            }
        }
        VectorLib.LOGGER.warn("Cannot find BlockPos compound value for {}, falling back", key);
        return fallback;
    }

    /** Sends the NBT to all tracking. */
    private void sendToAllTracking()
    {
        if (!this.TRACKING.level().isClientSide && this.sync_to_client)
        {
            VectorLib.NETWORK.sendToAllTrackingEntity(this.TRACKING, new NetSyncPayload(this.TRACKING.getId(), this.TAG));
        }
    }

    /** Sends the NBT to one player. */
    public void sendToPlayer(ServerPlayer player)
    {
        if (!this.TRACKING.level().isClientSide && this.sync_to_client)
        {
            VectorLib.NETWORK.sendToPlayer(player, new NetSyncPayload(this.TRACKING.getId(), this.TAG));
        }
    }

    /** Attempts to read NBT from a provided compound. Also has legacy fabric support. */
    public void readNetSyncFromNBT(CompoundTag reader)
    {
        if (reader.contains(NBT_NAME, ByteTag.TAG_COMPOUND))
        {
            this.TAG = reader.getCompound(NBT_NAME);
        }

        if (reader.contains(LEGACY_FABRIC, ByteTag.TAG_COMPOUND))
        {
            CompoundTag legacy = reader.getCompound(LEGACY_FABRIC);
            this.TAG.merge(legacy);
        }

        sendToAllTracking();
    }

    /** Saves this compound to NBT. */
    public void saveToNBT(CompoundTag reader)
    {
        reader.put(NBT_NAME, this.TAG);
        sendToAllTracking();
    }
}
