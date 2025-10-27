package hs.elementSmpUtility.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * OPTIONAL: Caches pedestal locations in a YAML file for faster chunk loading.
 * This is a performance optimization - the PDC system already persists data.
 *
 * Benefits:
 * - No need to scan every block in chunk
 * - Instant lookup of pedestal locations
 * - Reduces lag on chunk load
 *
 * The PDC system remains the source of truth for data integrity.
 */
public class PedestalCacheManager {

    private final JavaPlugin plugin;
    private final File cacheFile;
    private FileConfiguration cacheConfig;

    // In-memory cache: "world,chunkX,chunkZ" -> List of pedestal locations in that chunk
    private final Map<String, Set<String>> chunkToPedestals;

    public PedestalCacheManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.cacheFile = new File(plugin.getDataFolder(), "pedestal_cache.yml");
        this.chunkToPedestals = new HashMap<>();

        loadCache();
    }

    /**
     * Load the cache from disk
     */
    private void loadCache() {
        if (!cacheFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                cacheFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to create pedestal cache file: " + e.getMessage());
            }
        }

        cacheConfig = YamlConfiguration.loadConfiguration(cacheFile);

        // Load all cached locations into memory
        for (String chunkKey : cacheConfig.getKeys(false)) {
            List<String> locations = cacheConfig.getStringList(chunkKey);
            chunkToPedestals.put(chunkKey, new HashSet<>(locations));
        }

        plugin.getLogger().info("Loaded pedestal cache with " +
                chunkToPedestals.values().stream().mapToInt(Set::size).sum() + " pedestals");
    }

    /**
     * Save the cache to disk (async to prevent lag)
     */
    public void saveCache() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Clear and rebuild config
                for (String key : cacheConfig.getKeys(false)) {
                    cacheConfig.set(key, null);
                }

                for (Map.Entry<String, Set<String>> entry : chunkToPedestals.entrySet()) {
                    cacheConfig.set(entry.getKey(), new ArrayList<>(entry.getValue()));
                }

                cacheConfig.save(cacheFile);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to save pedestal cache: " + e.getMessage());
            }
        });
    }

    /**
     * Register a pedestal at the given location
     */
    public void addPedestal(Location location) {
        String chunkKey = getChunkKey(location);
        String locationKey = getLocationKey(location);

        chunkToPedestals.computeIfAbsent(chunkKey, k -> new HashSet<>()).add(locationKey);
    }

    /**
     * Remove a pedestal at the given location
     */
    public void removePedestal(Location location) {
        String chunkKey = getChunkKey(location);
        String locationKey = getLocationKey(location);

        Set<String> pedestals = chunkToPedestals.get(chunkKey);
        if (pedestals != null) {
            pedestals.remove(locationKey);
            if (pedestals.isEmpty()) {
                chunkToPedestals.remove(chunkKey);
            }
        }
    }

    /**
     * Get all pedestal locations in a chunk
     */
    public List<Location> getPedestalsInChunk(String worldName, int chunkX, int chunkZ) {
        String chunkKey = worldName + "," + chunkX + "," + chunkZ;
        Set<String> locationKeys = chunkToPedestals.get(chunkKey);

        if (locationKeys == null || locationKeys.isEmpty()) {
            return Collections.emptyList();
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return Collections.emptyList();
        }

        List<Location> locations = new ArrayList<>();
        for (String locKey : locationKeys) {
            Location loc = parseLocation(locKey, world);
            if (loc != null) {
                locations.add(loc);
            }
        }

        return locations;
    }

    /**
     * Get chunk key from location
     */
    private String getChunkKey(Location loc) {
        return loc.getWorld().getName() + "," +
                (loc.getBlockX() >> 4) + "," +
                (loc.getBlockZ() >> 4);
    }

    /**
     * Get location key (same format as BlockDataStorage)
     */
    private String getLocationKey(Location loc) {
        return loc.getWorld().getName() + "," +
                loc.getBlockX() + "," +
                loc.getBlockY() + "," +
                loc.getBlockZ();
    }

    /**
     * Parse location from string key
     */
    private Location parseLocation(String key, World world) {
        try {
            String[] parts = key.split(",");
            if (parts.length == 4) {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int z = Integer.parseInt(parts[3]);
                return new Location(world, x, y, z);
            }
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Invalid location key in cache: " + key);
        }
        return null;
    }

    /**
     * Check if a location is cached as a pedestal
     */
    public boolean isPedestalCached(Location location) {
        String chunkKey = getChunkKey(location);
        String locationKey = getLocationKey(location);

        Set<String> pedestals = chunkToPedestals.get(chunkKey);
        return pedestals != null && pedestals.contains(locationKey);
    }
}