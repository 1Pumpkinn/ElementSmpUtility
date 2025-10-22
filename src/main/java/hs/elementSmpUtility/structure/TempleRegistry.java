package hs.elementSmpUtility.structure;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks temple locations and enforces world limit
 */
public class TempleRegistry {

    private final JavaPlugin plugin;
    private final File dataFile;
    private FileConfiguration config;
    private final List<Location> templeLocations;
    private final int maxTemplesPerWorld;

    public TempleRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "temples.yml");
        this.templeLocations = new ArrayList<>();
        this.maxTemplesPerWorld = 1;

        loadData();
    }

    /**
     * Load temple locations from file
     */
    private void loadData() {
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create temples.yml: " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(dataFile);

        if (config.contains("temples")) {
            List<String> locations = config.getStringList("temples");
            for (String locStr : locations) {
                Location loc = parseLocation(locStr);
                if (loc != null) {
                    templeLocations.add(loc);
                }
            }
        }
    }

    /**
     * Save temple locations to file
     */
    private void saveData() {
        List<String> locations = new ArrayList<>();
        for (Location loc : templeLocations) {
            locations.add(serializeLocation(loc));
        }

        config.set("temples", locations);

        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save temples.yml: " + e.getMessage());
        }
    }

    /**
     * Check if a temple can be placed in this world
     */
    public boolean canPlaceTemple(String worldName) {
        long count = templeLocations.stream()
                .filter(loc -> loc.getWorld() != null && loc.getWorld().getName().equals(worldName))
                .count();

        return count < maxTemplesPerWorld;
    }

    /**
     * Register a new temple location
     */
    public void registerTemple(Location location) {
        templeLocations.add(location);
        saveData();
    }

    /**
     * Get all temples in a world
     */
    public List<Location> getTemplesInWorld(String worldName) {
        return templeLocations.stream()
                .filter(loc -> loc.getWorld() != null && loc.getWorld().getName().equals(worldName))
                .toList();
    }

    /**
     * Get count of temples in a world
     */
    public int getTempleCount(String worldName) {
        return (int) templeLocations.stream()
                .filter(loc -> loc.getWorld() != null && loc.getWorld().getName().equals(worldName))
                .count();
    }

    private String serializeLocation(Location loc) {
        return loc.getWorld().getName() + "," +
                loc.getBlockX() + "," +
                loc.getBlockY() + "," +
                loc.getBlockZ();
    }

    private Location parseLocation(String str) {
        try {
            String[] parts = str.split(",");
            if (parts.length == 4) {
                return new Location(
                        plugin.getServer().getWorld(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])
                );
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not parse location: " + str);
        }
        return null;
    }
}
