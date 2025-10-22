package hs.elementSmpUtility.listeners;

import hs.elementSmpUtility.storage.BlockDataStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * Manages loading and unloading chunk data for performance
 */
public class ChunkListener implements Listener {

    private final BlockDataStorage storage;

    public ChunkListener(BlockDataStorage storage) {
        this.storage = storage;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        storage.loadChunk(event.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        storage.unloadChunk(event.getChunk());
    }
}