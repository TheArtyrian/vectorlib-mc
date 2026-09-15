package net.vertisoft.vectorlib.agnostic.networking.eventsync;

import net.vertisoft.vectorlib.VectorLib;
import org.jetbrains.annotations.Nullable;

import java.nio.file.AccessDeniedException;

public class EventSyncHolder
{
    private final VectorEventSync.EventType type;
    private boolean frozen = false;
    private final String path;
    @Nullable public IVecEvent event;

    public EventSyncHolder(VectorEventSync.EventType type, String path)
    {
        this.type = type;
        this.path = path;
        // Freezes this holder if on server; not necessary to set it.
        if (!VectorLib.PLATFORM.isClient()) this.frozen = true;
    }

    public void setEvent(IVecEvent event)
    {
        if (this.event != null || this.frozen) throw new IllegalArgumentException("This EventSyncHolder is already frozen!");
        this.event = event;
        this.frozen = true;
    }

    public boolean isFrozen() { return this.event != null && this.frozen; }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof EventSyncHolder sync)) return false;
        return (sync.type == this.type && sync.path.equals(this.path));
    }
}
