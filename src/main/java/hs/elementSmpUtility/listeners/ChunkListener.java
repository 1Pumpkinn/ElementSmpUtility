package hs.elementSmpUtility.listeners;

import hs.elementSmpUtility.storage.BlockDataStorage;
import hs.elementSmpUtility.storage.PedestalDataStorage;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * Manages loading and unloading chunk data cache
 * NO block scanning - pedestals restore on interaction
 */
public class ChunkListener implements Listener {

    private final BlockDataStorage storage;
    private final PedestalDataStorage pedestalStorage;

    public ChunkListener(BlockDataStorage storage, PedestalDataStorage pedestalStorage) {
        this.storage = storage;
        this.pedestalStorage = pedestalStorage;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        // Load data into cache (instant, no lag)
        storage.loadChunk(chunk);
        pedestalStorage.loadChunk(chunk);

        // That's it! Displays restore when players interact with pedestals
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();

        // Unload data from cache to free memory
        storage.unloadChunk(chunk);
        pedestalStorage.unloadChunk(chunk);
    }
}