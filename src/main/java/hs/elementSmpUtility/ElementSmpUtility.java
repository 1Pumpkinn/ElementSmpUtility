package hs.elementSmpUtility;

import hs.elementSmpUtility.blocks.CustomBlockManager;
import hs.elementSmpUtility.commands.CustomBlockCommand;
import hs.elementSmpUtility.listeners.BlockBreakListener;
import hs.elementSmpUtility.listeners.BlockPlacementListener;
import hs.elementSmpUtility.listeners.ChunkListener;
import hs.elementSmpUtility.storage.BlockDataStorage;
import org.bukkit.plugin.java.JavaPlugin;

public final class ElementSmpUtility extends JavaPlugin {

    private CustomBlockManager blockManager;
    private BlockDataStorage storage;

    @Override
    public void onEnable() {
        // Initialize managers
        blockManager = new CustomBlockManager(this);
        storage = new BlockDataStorage(this, blockManager);

        // Register listeners
        getServer().getPluginManager().registerEvents(
                new BlockPlacementListener(blockManager), this);
        getServer().getPluginManager().registerEvents(
                new BlockBreakListener(blockManager, storage), this);
        getServer().getPluginManager().registerEvents(
                new ChunkListener(storage), this);

        // Register commands
        CustomBlockCommand blockCommand = new CustomBlockCommand(blockManager);
        getCommand("customblock").setExecutor(blockCommand);
        getCommand("customblock").setTabCompleter(blockCommand);

        getLogger().info("ElementSmpUtility has been enabled!");
        getLogger().info("Registered " + blockManager.getAllBlockTypes().size() + " custom blocks");
    }

    @Override
    public void onDisable() {
        getLogger().info("ElementSmpUtility has been disabled!");
    }

    public CustomBlockManager getBlockManager() {
        return blockManager;
    }

    public BlockDataStorage getStorage() {
        return storage;
    }
}