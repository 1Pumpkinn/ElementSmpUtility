package hs.elementSmpUtility.blocks;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages all custom unbreakable blocks including custom models
 */
public class CustomBlockManager {

    private final JavaPlugin plugin;
    private final NamespacedKey customBlockKey;
    private final Map<String, CustomBlockType> blockTypes;

    public CustomBlockManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.customBlockKey = new NamespacedKey(plugin, "custom_block");
        this.blockTypes = new HashMap<>();

        registerDefaultBlocks();
    }

    /**
     * Register default custom block types
     */
    private void registerDefaultBlocks() {
        // Original reinforced blocks
        registerBlock(new CustomBlockType(
                "reinforced_deepslate_bricks",
                Material.DEEPSLATE_BRICKS,
                Component.text("Reinforced Deepslate Bricks")
                        .color(TextColor.color(0x5A5A5A))
                        .decoration(TextDecoration.ITALIC, false),
                true
        ));

        registerBlock(new CustomBlockType(
                "reinforced_stone_bricks",
                Material.STONE_BRICKS,
                Component.text("Reinforced Stone Bricks")
                        .color(TextColor.color(0x7F7F7F))
                        .decoration(TextDecoration.ITALIC, false),
                true
        ));

        registerBlock(new CustomBlockType(
                "reinforced_obsidian",
                Material.OBSIDIAN,
                Component.text("Reinforced Obsidian")
                        .color(TextColor.color(0x3C0A5A))
                        .decoration(TextDecoration.ITALIC, false),
                true
        ));

        registerBlock(new CustomBlockType(
                "reinforced_prismarine",
                Material.PRISMARINE_BRICKS,
                Component.text("Reinforced Prismarine")
                        .color(TextColor.color(0x63A295))
                        .decoration(TextDecoration.ITALIC, false),
                true
        ));

        // Temple-specific blocks
        registerBlock(new CustomBlockType(
                "reinforced_deepslate_tiles",
                Material.DEEPSLATE_TILES,
                Component.text("Reinforced Deepslate Tiles")
                        .color(TextColor.color(0x494949))
                        .decoration(TextDecoration.ITALIC, false),
                true
        ));

        registerBlock(new CustomBlockType(
                "reinforced_deepslate_altar",
                Material.CHISELED_DEEPSLATE,
                Component.text("Reinforced Deepslate Altar")
                        .color(TextColor.color(0x3A3A3A))
                        .decoration(TextDecoration.ITALIC, false),
                true
        ));

        registerBlock(new CustomBlockType(
                "chiseled_deepslate",
                Material.CHISELED_DEEPSLATE,
                Component.text("Reinforced Chiseled Deepslate")
                        .color(TextColor.color(0x434343))
                        .decoration(TextDecoration.ITALIC, false),
                true
        ));

        registerBlock(new CustomBlockType(
                "polished_deepslate",
                Material.POLISHED_DEEPSLATE,
                Component.text("Reinforced Polished Deepslate")
                        .color(TextColor.color(0x505050))
                        .decoration(TextDecoration.ITALIC, false),
                true
        ));

        registerBlock(new CustomModelBlock(
                "pedestal",
                Material.STONE,
                Component.text("Ancient Pedestal")
                        .color(TextColor.color(0xD4AF37)) // Gold color
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true),
                true, // Unbreakable
                1 // Custom model data ID
        ));
    }

    /**
     * Register a custom block type
     */
    public void registerBlock(CustomBlockType blockType) {
        blockTypes.put(blockType.getId(), blockType);
    }

    /**
     * Create a custom block item
     */
    public ItemStack createCustomBlock(String blockId, int amount) {
        CustomBlockType blockType = blockTypes.get(blockId);
        if (blockType == null) {
            return null;
        }

        ItemStack item = new ItemStack(blockType.getMaterial(), amount);
        item.editMeta(meta -> {
            meta.displayName(blockType.getDisplayName());
            meta.getPersistentDataContainer().set(
                    customBlockKey,
                    PersistentDataType.STRING,
                    blockType.getId()
            );

            // Set custom model data if available
            if (blockType instanceof CustomModelBlock modelBlock && modelBlock.hasCustomModel()) {
                meta.setCustomModelData(modelBlock.getCustomModelData());
            }

            if (blockType.isUnbreakable()) {
                meta.lore(java.util.List.of(
                        Component.text("Unbreakable")
                                .color(TextColor.color(0xFF5555))
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Ancient relic of power")
                                .color(TextColor.color(0x888888))
                                .decoration(TextDecoration.ITALIC, true)
                ));
            }
        });

        return item;
    }

    /**
     * Check if an ItemStack is a custom block
     */
    public boolean isCustomBlock(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        return item.getItemMeta().getPersistentDataContainer()
                .has(customBlockKey, PersistentDataType.STRING);
    }

    /**
     * Get the custom block ID from an ItemStack
     */
    public String getCustomBlockId(ItemStack item) {
        if (!isCustomBlock(item)) {
            return null;
        }

        return item.getItemMeta().getPersistentDataContainer()
                .get(customBlockKey, PersistentDataType.STRING);
    }

    /**
     * Get a custom block type by ID
     */
    public CustomBlockType getBlockType(String blockId) {
        return blockTypes.get(blockId);
    }

    /**
     * Get all registered block types
     */
    public Map<String, CustomBlockType> getAllBlockTypes() {
        return new HashMap<>(blockTypes);
    }

    public NamespacedKey getCustomBlockKey() {
        return customBlockKey;
    }
}