package hs.elementSmpUtility.tasks;

import hs.elementSmpUtility.blocks.custom.PedestalBlock;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles rotating items displayed on pedestals with performance optimizations
 * - Runs every 2 ticks for smooth rotation (10 rotations/sec)
 * - Caches armor stands to avoid repeated metadata checks
 * - Only processes stands in loaded chunks
 * - Auto-cleanup of invalid stands
 */
public class PedestalRotationTask extends BukkitRunnable {

    private final JavaPlugin plugin;
    private static final float ROTATION_SPEED = 2.0f; // Degrees per execution (2 ticks)
    private static final long UPDATE_INTERVAL = 2L; // Ticks between rotations

    // Cache to avoid repeated entity lookups
    private final List<ArmorStand> cachedStands = new ArrayList<>();
    private int cacheUpdateCounter = 0;
    private static final int CACHE_UPDATE_FREQUENCY = 100; // Update cache every 100 executions (~10 seconds)

    public PedestalRotationTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Periodically refresh the cache to pick up new pedestals and remove old ones
        if (++cacheUpdateCounter >= CACHE_UPDATE_FREQUENCY) {
            updateCache();
            cacheUpdateCounter = 0;
        }

        // Rotate all cached pedestal displays
        cachedStands.removeIf(stand -> {
            if (!stand.isValid() || stand.isDead()) {
                return true; // Remove invalid stands from cache
            }

            // Only rotate if chunk is loaded
            if (stand.getLocation().getChunk().isLoaded()) {
                PedestalBlock.rotateDisplay(stand, ROTATION_SPEED);
            }

            return false;
        });
    }

    /**
     * Update the cache of pedestal armor stands
     */
    private void updateCache() {
        cachedStands.clear();

        for (World world : Bukkit.getWorlds()) {
            world.getEntitiesByClass(ArmorStand.class).forEach(stand -> {
                if (stand.hasMetadata("pedestal_display") && stand.isValid()) {
                    cachedStands.add(stand);
                }
            });
        }
    }

    /**
     * Start the rotation task with optimized timing
     */
    public void start() {
        // Initial cache population
        updateCache();

        // Run every 2 ticks (10 times per second) for smooth rotation
        this.runTaskTimer(plugin, 0L, UPDATE_INTERVAL);

        plugin.getLogger().info("Pedestal rotation task started (2-tick interval, " +
                cachedStands.size() + " displays found)");
    }

    /**
     * Force cache update (useful after placing/breaking pedestals)
     */
    public void refreshCache() {
        updateCache();
    }

    /**
     * Get count of currently tracked displays
     */
    public int getDisplayCount() {
        return cachedStands.size();
    }
}