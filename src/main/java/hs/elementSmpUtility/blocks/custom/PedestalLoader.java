package hs.elementSmpUtility.blocks.custom;

import hs.elementSmpUtility.storage.BlockDataStorage;
import hs.elementSmpUtility.storage.PedestalDataStorage;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Handles loading pedestal displays efficiently without blocking the main thread
 * Uses a queue system to restore pedestals gradually over time
 */
public class PedestalLoader {

    private final JavaPlugin plugin;
    private final BlockDataStorage blockStorage;
    private final PedestalDataStorage pedestalStorage;
    private final Queue<ChunkToScan> scanQueue;
    private BukkitRunnable scanTask;

    public PedestalLoader(JavaPlugin plugin, BlockDataStorage blockStorage, PedestalDataStorage pedestalStorage) {
        this.plugin = plugin;
        this.blockStorage = blockStorage;
        this.pedestalStorage = pedestalStorage;
        this.scanQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * Start the pedestal loader task
     * Processes chunks gradually to avoid lag
     */
    public void start() {
        if (scanTask != null) {
            return;
        }

        scanTask = new BukkitRunnable() {
            @Override
            public void run() {
                // Process up to 5 chunks per tick to avoid lag
                for (int i = 0; i < 5; i++) {
                    ChunkToScan chunkData = scanQueue.poll();
                    if (chunkData == null) {
                        break;
                    }

                    if (chunkData.chunk.isLoaded()) {
                        scanChunkForPedestals(chunkData.chunk);
                    }
                }
            }
        };

        // Run every 5 ticks (4 times per second)
        scanTask.runTaskTimer(plugin, 20L, 5L);
    }

    /**
     * Stop the pedestal loader task
     */
    public void stop() {
        if (scanTask != null) {
            scanTask.cancel();
            scanTask = null;
        }
        scanQueue.clear();
    }

    /**
     * Queue a chunk to have its pedestals restored
     */
    public void queueChunk(Chunk chunk) {
        scanQueue.offer(new ChunkToScan(chunk));
    }

    /**
     * Scan a chunk for pedestals and restore their displays
     * Only scans blocks that are actually stored in the custom block data
     */
    private void scanChunkForPedestals(Chunk chunk) {
        // Get all stored block locations from cache for this chunk
        String chunkKey = getChunkKey(chunk);

        // Only scan blocks we know exist from the cache
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Only scan likely pedestal heights (Y 50-100 to reduce scan)
                for (int y = Math.max(chunk.getWorld().getMinHeight(), -64);
                     y < Math.min(chunk.getWorld().getMaxHeight(), 320); y++) {

                    Block block = chunk.getBlock(x, y, z);
                    Location loc = block.getLocation();

                    // Use cached check which is very fast
                    String blockId = blockStorage.getCustomBlockIdCached(loc);

                    if ("pedestal".equals(blockId)) {
                        ItemStack item = pedestalStorage.getPedestalItem(loc);
                        if (item != null) {
                            // Schedule display creation on main thread
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (block.getChunk().isLoaded()) {
                                        PedestalBlock.createOrUpdateDisplay(loc, item);
                                    }
                                }
                            }.runTask(plugin);
                        }
                    }
                }
            }
        }
    }

    /**
     * Force reload a single pedestal display immediately
     */
    public void reloadPedestal(Location location) {
        ItemStack item = pedestalStorage.getPedestalItem(location);
        PedestalBlock.createOrUpdateDisplay(location, item);
    }

    /**
     * Clean up orphaned armor stands (displays without pedestals)
     */
    public void cleanupOrphanedDisplays(Chunk chunk) {
        chunk.getWorld().getEntitiesByClass(org.bukkit.entity.ArmorStand.class).forEach(stand -> {
            if (!stand.hasMetadata("pedestal_display")) {
                return;
            }

            Location standLoc = stand.getLocation();
            Location pedestalLoc = standLoc.clone().subtract(0.5, 1.2, 0.5);
            Block block = pedestalLoc.getBlock();

            String blockId = blockStorage.getCustomBlockIdCached(pedestalLoc);
            if (blockId == null || !blockId.equals("pedestal")) {
                // Pedestal is gone, remove the display
                stand.remove();
            }
        });
    }

    private String getChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
    }

    private static class ChunkToScan {
        final Chunk chunk;

        ChunkToScan(Chunk chunk) {
            this.chunk = chunk;
        }
    }
}