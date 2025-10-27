package hs.elementSmpUtility;

import hs.elementSmpUtility.blocks.CustomBlockManager;
import hs.elementSmpUtility.commands.CustomBlockCommand;
import hs.elementSmpUtility.listeners.BlockBreakListener;
import hs.elementSmpUtility.listeners.BlockPlacementListener;
import hs.elementSmpUtility.listeners.ChunkListener;
import hs.elementSmpUtility.listeners.PedestalInteractionListener;
import hs.elementSmpUtility.storage.BlockDataStorage;
import hs.elementSmpUtility.storage.PedestalDataStorage;
import hs.elementSmpUtility.structure.components.StructureCommand;
import hs.elementSmpUtility.structure.components.StructureManagerImpl;
import org.bukkit.plugin.java.JavaPlugin;

public final class ElementSmpUtility extends JavaPlugin {

    private CustomBlockManager blockManager;
    private BlockDataStorage storage;
    private PedestalDataStorage pedestalStorage;
    private StructureManagerImpl structureManager;

    @Override
    public void onEnable() {
        // Initialize managers
        blockManager = new CustomBlockManager(this);
        storage = new BlockDataStorage(this, blockManager);
        pedestalStorage = new PedestalDataStorage(this);
        structureManager = new StructureManagerImpl(this);

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

        StructureCommand structureCommand = new StructureCommand(structureManager);
        getCommand("structure").setExecutor(structureCommand);
        getCommand("structure").setTabCompleter(structureCommand);

        getLogger().info("ElementSmpUtility has been enabled!");
        getLogger().info("Registered " + blockManager.getAllBlockTypes().size() + " custom blocks");
        getLogger().info("Temple system initialized");
        getLogger().info("Pedestal system initialized (static display)");
        getLogger().info("Structure generation system initialized");
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

    public PedestalDataStorage getPedestalStorage() {
        return pedestalStorage;
    }

    public StructureManagerImpl getStructureManager() {
        return structureManager;
    }
}