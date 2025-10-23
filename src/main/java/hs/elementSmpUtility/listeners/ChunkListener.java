package hs.elementSmpUtility.listeners;

import hs.elementSmpUtility.blocks.custom.PedestalBlock;
import hs.elementSmpUtility.storage.BlockDataStorage;
import hs.elementSmpUtility.storage.PedestalDataStorage;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * Manages loading and unloading chunk data for performance
 * Pedestal restoration is now handled lazily to avoid performance issues
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

        // Load block data into cache
        storage.loadChunk(chunk);

        // Load pedestal data into cache
        pedestalStorage.loadChunk(chunk);

        // Note: Pedestal displays are NOT restored here to prevent lag
        // They will be restored when players interact with them or we can
        // restore them asynchronously over time if needed
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();

        // Unload block data from cache
        storage.unloadChunk(chunk);

        // Unload pedestal data from cache
        pedestalStorage.unloadChunk(chunk);
    }
}