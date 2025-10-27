package hs.elementSmpUtility;

import hs.elementSmpUtility.blocks.CustomBlockManager;
import hs.elementSmpUtility.blocks.custom.PedestalBlock;
import hs.elementSmpUtility.commands.CustomBlockCommand;
import hs.elementSmpUtility.listeners.BlockBreakListener;
import hs.elementSmpUtility.listeners.BlockPlacementListener;
import hs.elementSmpUtility.listeners.ChunkListener;
import hs.elementSmpUtility.listeners.PedestalInteractionListener;
import hs.elementSmpUtility.recipes.PedestalRecipe;
import hs.elementSmpUtility.storage.BlockDataStorage;
import hs.elementSmpUtility.storage.PedestalDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public final class ElementSmpUtility extends JavaPlugin {

    private CustomBlockManager blockManager;
    private BlockDataStorage storage;
    private PedestalDataStorage pedestalStorage;

    @Override
    public void onEnable() {
        // Initialize managers
        blockManager = new CustomBlockManager(this);
        storage = new BlockDataStorage(this, blockManager);
        pedestalStorage = new PedestalDataStorage(this);

        // Register recipes
        PedestalRecipe pedestalRecipe = new PedestalRecipe(this, blockManager);
        pedestalRecipe.register();

        // Register listeners
        getServer().getPluginManager().registerEvents(
                new BlockPlacementListener(blockManager, storage), this);
        getServer().getPluginManager().registerEvents(
                new BlockBreakListener(blockManager, storage, pedestalStorage), this);
        getServer().getPluginManager().registerEvents(
                new ChunkListener(storage, pedestalStorage), this);
        getServer().getPluginManager().registerEvents(
                new PedestalInteractionListener(blockManager, storage, pedestalStorage), this);

        // Register commands
        CustomBlockCommand blockCommand = new CustomBlockCommand(blockManager);
        getCommand("customblock").setExecutor(blockCommand);
        getCommand("customblock").setTabCompleter(blockCommand);

        getLogger().info("ElementSmpUtility has been enabled!");
        getLogger().info("Registered " + blockManager.getAllBlockTypes().size() + " custom blocks");

        // Clean up any orphaned armor stands from previous sessions (async to prevent startup lag)
        Bukkit.getScheduler().runTaskLater(this, this::cleanupOrphanedArmorStands, 40L); // 2 seconds after startup
    }

    @Override
    public void onDisable() {
        // Clean up all pedestal displays on shutdown
        getLogger().info("Cleaning up pedestal displays...");
        cleanupAllPedestalDisplays();
        getLogger().info("ElementSmpUtility has been disabled!");
    }

    /**
     * Remove all pedestal display armor stands on server shutdown
     * This ensures a clean slate on restart
     */
    private void cleanupAllPedestalDisplays() {
        int removed = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand stand) {
                    if (stand.hasMetadata("pedestal_display")) {
                        stand.remove();
                        removed++;
                    } else if (!stand.isVisible() && stand.isSmall() && stand.isMarker()) {
                        // Also remove invisible small marker stands (likely orphaned pedestal displays)
                        stand.remove();
                        removed++;
                    }
                }
            }
        }
        if (removed > 0) {
            getLogger().info("Removed " + removed + " pedestal displays on shutdown");
        }
    }

    /**
     * Clean up orphaned armor stands that might exist from previous crashes/improper shutdowns
     */
    private void cleanupOrphanedArmorStands() {
        getLogger().info("Scanning for orphaned pedestal displays...");
        int removed = 0;

        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                // Load chunk data
                storage.loadChunk(chunk);

                // Scan for lodestones
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
                            Block block = chunk.getBlock(x, y, z);

                            if (block.getType() == Material.LODESTONE) {
                                Location loc = block.getLocation();
                                String blockId = storage.getCustomBlockIdCached(loc);

                                // If it's NOT a pedestal, remove any nearby armor stands
                                if (!"pedestal".equals(blockId)) {
                                    for (Entity entity : loc.getWorld().getNearbyEntities(loc.clone().add(0.5, 0.5, 0.5), 1.0, 1.5, 1.0)) {
                                        if (entity instanceof ArmorStand stand) {
                                            if (!stand.isVisible() && stand.isSmall() && stand.isMarker()) {
                                                stand.remove();
                                                removed++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (removed > 0) {
            getLogger().info("Removed " + removed + " orphaned armor stands");
        } else {
            getLogger().info("No orphaned armor stands found");
        }
    }

    public CustomBlockManager getBlockManager() {
        return blockManager;
    }

    public BlockDataStorage getStorage() {
        return storage;
    }

    public PedestalDataStorage getPedestalStorage() {
        return pedestalStorage;
    }
}