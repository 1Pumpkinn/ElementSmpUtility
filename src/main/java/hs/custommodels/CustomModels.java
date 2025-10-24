package hs.custommodels;

import hs.custommodels.commands.CustomModelsCommand;
import hs.custommodels.listeners.BlockListener;
import hs.custommodels.listeners.ItemListener;
import hs.custommodels.managers.ModelManager;
import hs.custommodels.managers.ResourcePackManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * CustomModels - A flexible custom model system for Paper 1.21.8
 * Similar to ModelEngine and Oraxen
 */
public final class CustomModels extends JavaPlugin {

    private static CustomModels instance;
    private ModelManager modelManager;
    private ResourcePackManager resourcePackManager;

    @Override
    public void onEnable() {
        instance = this;

        // Create directories
        createDirectories();

        // Save default config
        saveDefaultConfig();

        // Initialize managers
        this.modelManager = new ModelManager(this);
        this.resourcePackManager = new ResourcePackManager(this);

        // Load models from config
        modelManager.loadModels();

        // Generate resource pack
        if (getConfig().getBoolean("auto-generate-pack", true)) {
            resourcePackManager.generateResourcePack();
        }

        // Register listeners
        getServer().getPluginManager().registerEvents(new BlockListener(modelManager), this);
        getServer().getPluginManager().registerEvents(new ItemListener(modelManager), this);

        // Register commands
        CustomModelsCommand cmd = new CustomModelsCommand(this);
        getCommand("custommodels").setExecutor(cmd);
        getCommand("custommodels").setTabCompleter(cmd);

        getLogger().info("CustomModels enabled!");
        getLogger().info("Loaded " + modelManager.getModelCount() + " custom models");

        if (getConfig().getBoolean("send-pack-on-join", false)) {
            getLogger().info("Resource pack will be sent to players on join");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("CustomModels disabled!");
    }

    private void createDirectories() {
        new File(getDataFolder(), "models").mkdirs();
        new File(getDataFolder(), "textures").mkdirs();
        new File(getDataFolder(), "pack").mkdirs();
    }

    public static CustomModels getInstance() {
        return instance;
    }

    public ModelManager getModelManager() {
        return modelManager;
    }

    public ResourcePackManager getResourcePackManager() {
        return resourcePackManager;
    }

    public void sendMessage(org.bukkit.command.CommandSender sender, String message) {
        sender.sendMessage(Component.text("[CustomModels] ", NamedTextColor.GOLD)
                .append(Component.text(message, NamedTextColor.WHITE)));
    }

    public void sendError(org.bukkit.command.CommandSender sender, String message) {
        sender.sendMessage(Component.text("[CustomModels] ", NamedTextColor.GOLD)
                .append(Component.text(message, NamedTextColor.RED)));
    }

    public void sendSuccess(org.bukkit.command.CommandSender sender, String message) {
        sender.sendMessage(Component.text("[CustomModels] ", NamedTextColor.GOLD)
                .append(Component.text(message, NamedTextColor.GREEN)));
    }
}