package hs.elementSmpUtility.listeners;

import hs.elementSmpUtility.blocks.custom.PedestalBlock;
import hs.elementSmpUtility.storage.BlockDataStorage;
import hs.elementSmpUtility.storage.PedestalDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Manages loading and unloading chunk data with lazy pedestal restoration
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

        // Restore pedestal displays asynchronously to avoid lag
        // Only scan blocks that are actually stored in the cache
        Bukkit.getScheduler().runTaskAsynchronously(
                Bukkit.getPluginManager().getPlugin("ElementSmpUtility"),
                () -> restorePedestalsInChunk(chunk)
        );
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();

        // Unload block data from cache
        storage.unloadChunk(chunk);

        // Unload pedestal data from cache
        pedestalStorage.unloadChunk(chunk);
    }

    /**
     * Restore pedestal displays in a chunk (runs async to find locations, sync to create displays)
     */
    private void restorePedestalsInChunk(Chunk chunk) {
        if (!chunk.isLoaded()) {
            return;
        }

        // Only scan blocks we know exist from the cache (very fast)
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getWorld().getMinHeight(); y < chunk.getWorld().getMaxHeight(); y++) {
                    Block block = chunk.getBlock(x, y, z);
                    Location loc = block.getLocation();

                    // Use cached check (very fast, no PDC access)
                    String blockId = storage.getCustomBlockIdCached(loc);

                    if ("pedestal".equals(blockId)) {
                        ItemStack item = pedestalStorage.getPedestalItem(loc);

                        // Schedule display creation on main thread
                        Bukkit.getScheduler().runTask(
                                Bukkit.getPluginManager().getPlugin("ElementSmpUtility"),
                                () -> {
                                    if (block.getChunk().isLoaded()) {
                                        PedestalBlock.createOrUpdateDisplay(loc, item);
                                    }
                                }
                        );
                    }
                }
            }
        }
    }
}