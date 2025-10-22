package hs.elementSmpUtility;

import hs.elementSmpUtility.blocks.CustomBlockManager;
import hs.elementSmpUtility.commands.CustomBlockCommand;
import hs.elementSmpUtility.commands.TempleCommand;
import hs.elementSmpUtility.listeners.BlockBreakListener;
import hs.elementSmpUtility.listeners.BlockPlacementListener;
import hs.elementSmpUtility.listeners.ChunkListener;
import hs.elementSmpUtility.storage.BlockDataStorage;
import hs.elementSmpUtility.structure.StructurePlacement;
import hs.elementSmpUtility.structure.TempleRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public final class ElementSmpUtility extends JavaPlugin {

    private CustomBlockManager blockManager;
    private BlockDataStorage storage;
    private TempleRegistry templeRegistry;
    private StructurePlacement structurePlacement;

    @Override
    public void onEnable() {
        // Initialize managers
        blockManager = new CustomBlockManager(this);
        storage = new BlockDataStorage(this, blockManager);
        templeRegistry = new TempleRegistry(this);
        structurePlacement = new StructurePlacement(blockManager, storage);

        // Register listeners
        getServer().getPluginManager().registerEvents(
                new BlockPlacementListener(blockManager, storage), this);
        getServer().getPluginManager().registerEvents(
                new BlockBreakListener(blockManager, storage), this);
        getServer().getPluginManager().registerEvents(
                new ChunkListener(storage), this);

        // Register commands
        CustomBlockCommand blockCommand = new CustomBlockCommand(blockManager);
        getCommand("customblock").setExecutor(blockCommand);
        getCommand("customblock").setTabCompleter(blockCommand);

        TempleCommand templeCommand = new TempleCommand(structurePlacement, templeRegistry);
        getCommand("temple").setExecutor(templeCommand);
        getCommand("temple").setTabCompleter(templeCommand);

        getLogger().info("ElementSmpUtility has been enabled!");
        getLogger().info("Registered " + blockManager.getAllBlockTypes().size() + " custom blocks");
        getLogger().info("Temple system initialized");
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

    public TempleRegistry getTempleRegistry() {
        return templeRegistry;
    }

    public StructurePlacement getStructurePlacement() {
        return structurePlacement;
    }
}