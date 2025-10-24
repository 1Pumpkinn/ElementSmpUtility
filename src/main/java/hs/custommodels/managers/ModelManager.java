package hs.custommodels.managers;

import hs.custommodels.CustomModels;
import hs.custommodels.model.CustomModel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

/**
 * Manages all custom models - loading, creating items, and identification
 */
public class ModelManager {

    private final CustomModels plugin;
    private final Map<String, CustomModel> models;
    private final NamespacedKey modelKey;

    public ModelManager(CustomModels plugin) {
        this.plugin = plugin;
        this.models = new HashMap<>();
        this.modelKey = new NamespacedKey(plugin, "custom_model");
    }

    /**
     * Load all models from configuration files
     */
    public void loadModels() {
        models.clear();

        // Load from main config
        loadFromConfig(plugin.getConfig().getConfigurationSection("models"));

        // Load from individual model files
        File modelsDir = new File(plugin.getDataFolder(), "models");
        if (modelsDir.exists() && modelsDir.isDirectory()) {
            File[] files = modelsDir.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null) {
                for (File file : files) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    loadFromConfig(config.getConfigurationSection("models"));
                }
            }
        }

        plugin.getLogger().info("Loaded " + models.size() + " custom models");
    }

    /**
     * Load models from a configuration section
     */
    private void loadFromConfig(ConfigurationSection section) {
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection modelSection = section.getConfigurationSection(key);
            if (modelSection == null) continue;

            try {
                CustomModel model = parseModel(key, modelSection);
                models.put(key, model);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load model '" + key + "': " + e.getMessage());
            }
        }
    }

    /**
     * Parse a model from configuration
     */
    private CustomModel parseModel(String id, ConfigurationSection section) {
        CustomModel.Builder builder = new CustomModel.Builder(id);

        // Type
        String typeStr = section.getString("type", "ITEM").toUpperCase();
        try {
            builder.type(CustomModel.ModelType.valueOf(typeStr));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid model type '" + typeStr + "' for " + id);
        }

        // Base material
        String materialStr = section.getString("material", "PAPER").toUpperCase();
        try {
            builder.baseMaterial(Material.valueOf(materialStr));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material '" + materialStr + "' for " + id);
        }

        // Custom model data
        builder.customModelData(section.getInt("custom-model-data", 1));

        // Display name
        if (section.contains("display-name")) {
            String name = section.getString("display-name");
            Component displayName = LegacyComponentSerializer.legacyAmpersand().deserialize(name)
                    .decoration(TextDecoration.ITALIC, false);
            builder.displayName(displayName);
        }

        // Lore
        if (section.contains("lore")) {
            List<String> loreLines = section.getStringList("lore");
            for (String line : loreLines) {
                Component loreLine = LegacyComponentSerializer.legacyAmpersand().deserialize(line)
                        .decoration(TextDecoration.ITALIC, false);
                builder.addLore(loreLine);
            }
        }

        // Paths
        builder.modelPath(section.getString("model-path", ""));
        builder.texturePath(section.getString("texture-path", ""));

        // Properties
        builder.unbreakable(section.getBoolean("unbreakable", false));
        builder.glowing(section.getBoolean("glowing", false));
        builder.stackSize(section.getInt("stack-size", 64));

        return builder.build();
    }

    /**
     * Create an ItemStack for a custom model
     */
    public ItemStack createItem(String modelId, int amount) {
        CustomModel model = models.get(modelId);
        if (model == null) {
            return null;
        }

        ItemStack item = new ItemStack(model.getBaseMaterial(), amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Set display name
            meta.displayName(model.getDisplayName());

            // Set lore
            if (!model.getLore().isEmpty()) {
                meta.lore(model.getLore());
            }

            // Set custom model data
            meta.setCustomModelData(model.getCustomModelData());

            // Mark as custom model
            meta.getPersistentDataContainer().set(
                    modelKey,
                    PersistentDataType.STRING,
                    modelId
            );

            // Set unbreakable
            if (model.isUnbreakable()) {
                meta.setUnbreakable(true);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            }

            // Set glowing
            if (model.isGlowing()) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            // Set max stack size (1.21+ feature)
            meta.setMaxStackSize(model.getStackSize());

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Check if an ItemStack is a custom model
     */
    public boolean isCustomModel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        return item.getItemMeta().getPersistentDataContainer()
                .has(modelKey, PersistentDataType.STRING);
    }

    /**
     * Get the model ID from an ItemStack
     */
    public String getModelId(ItemStack item) {
        if (!isCustomModel(item)) {
            return null;
        }

        return item.getItemMeta().getPersistentDataContainer()
                .get(modelKey, PersistentDataType.STRING);
    }

    /**
     * Get a custom model by ID
     */
    public CustomModel getModel(String id) {
        return models.get(id);
    }

    /**
     * Get all registered models
     */
    public Map<String, CustomModel> getAllModels() {
        return new HashMap<>(models);
    }

    /**
     * Get count of registered models
     */
    public int getModelCount() {
        return models.size();
    }

    /**
     * Register a model programmatically
     */
    public void registerModel(CustomModel model) {
        models.put(model.getId(), model);
    }

    /**
     * Unregister a model
     */
    public void unregisterModel(String id) {
        models.remove(id);
    }

    /**
     * Get all model IDs
     */
    public Set<String> getModelIds() {
        return new HashSet<>(models.keySet());
    }
}