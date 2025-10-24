package hs.elementSmpUtility.structure;

import hs.elementSmpUtility.blocks.CustomBlockManager;
import hs.elementSmpUtility.storage.BlockDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class StructurePlacement {

    private final CustomBlockManager blockManager;
    private final BlockDataStorage storage;
    private final Random random;

    public StructurePlacement(CustomBlockManager blockManager, BlockDataStorage storage) {
        this.blockManager = blockManager;
        this.storage = storage;
        this.random = new Random();
    }

    public boolean placeTemple(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        TempleStructure temple = new TempleStructure();
        World world = location.getWorld();

        int baseX = location.getBlockX();
        int baseY = location.getBlockY();
        int baseZ = location.getBlockZ();

        clearArea(world, baseX, baseY, baseZ, temple.getWidth(), temple.getHeight() + 5, temple.getDepth());

        for (TempleStructure.StructureBlock structureBlock : temple.getBlocks()) {
            int x = baseX + structureBlock.getX();
            int y = baseY + structureBlock.getY();
            int z = baseZ + structureBlock.getZ();

            Block block = world.getBlockAt(x, y, z);
            String blockType = structureBlock.getBlockType();

            // Handle special block types
            if ("air".equals(blockType)) {
                block.setType(Material.AIR);
                continue;
            }

            if ("chest".equals(blockType)) {
                block.setType(Material.CHEST);
                Location chestLoc = block.getLocation();
                Bukkit.getScheduler().runTaskLater(
                        blockManager.getPlugin(),
                        () -> populateChest(chestLoc),
                        2L
                );
                continue;
            }

            if ("lantern".equals(blockType)) {
                block.setType(Material.LANTERN);
                continue;
            }

            if ("chain".equals(blockType)) {
                block.setType(Material.CHAIN);
                continue;
            }

            if ("chiseled_bookshelf".equals(blockType)) {
                block.setType(Material.CHISELED_BOOKSHELF);
                continue;
            }

            if ("pedestal".equals(blockType)) {
                block.setType(Material.LODESTONE);
                storage.saveCustomBlock(block, "pedestal");
                continue;
            }

            Material material = getMaterialFromBlockType(blockType);
            if (material != null) {
                block.setType(material);

                if (blockType.startsWith("reinforced_") || blockType.startsWith("chiseled_") || blockType.startsWith("polished_")) {
                    storage.saveCustomBlock(block, blockType);
                }
            }
        }

        return true;
    }

    private void clearArea(World world, int baseX, int baseY, int baseZ, int width, int height, int depth) {
        for (int x = baseX; x < baseX + width; x++) {
            for (int y = baseY; y < baseY + height; y++) {
                for (int z = baseZ; z < baseZ + depth; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() != Material.AIR) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    private void populateChest(Location location) {
        Block block = location.getBlock();
        if (!(block.getState() instanceof Chest chest)) {
            return;
        }

        Inventory inv = chest.getInventory();
        inv.clear();

        // Add guaranteed loot
        inv.setItem(random.nextInt(27), new ItemStack(Material.DIAMOND, random.nextInt(3) + 2));
        inv.setItem(random.nextInt(27), new ItemStack(Material.EMERALD, random.nextInt(5) + 3));
        inv.setItem(random.nextInt(27), new ItemStack(Material.GOLD_INGOT, random.nextInt(8) + 4));

        // Random additional loot
        if (random.nextDouble() < 0.7) {
            inv.setItem(random.nextInt(27), new ItemStack(Material.IRON_INGOT, random.nextInt(10) + 5));
        }

        if (random.nextDouble() < 0.4) {
            inv.setItem(random.nextInt(27), new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));
        }

        if (random.nextDouble() < 0.6) {
            inv.setItem(random.nextInt(27), new ItemStack(Material.ENDER_PEARL, random.nextInt(4) + 2));
        }

        if (random.nextDouble() < 0.3) {
            inv.setItem(random.nextInt(27), new ItemStack(Material.NETHERITE_SCRAP, random.nextInt(2) + 1));
        }

        if (random.nextDouble() < 0.5) {
            inv.setItem(random.nextInt(27), new ItemStack(Material.GOLDEN_APPLE, random.nextInt(3) + 2));
        }

        if (random.nextDouble() < 0.4) {
            inv.setItem(random.nextInt(27), new ItemStack(Material.EXPERIENCE_BOTTLE, random.nextInt(10) + 5));
        }

        if (random.nextDouble() < 0.5) {
            inv.setItem(random.nextInt(27), new ItemStack(Material.ANCIENT_DEBRIS, 1));
        }

        chest.update();
    }

    private Material getMaterialFromBlockType(String blockType) {
        return switch (blockType) {
            case "reinforced_deepslate_bricks" -> Material.DEEPSLATE_BRICKS;
            case "reinforced_deepslate_tiles" -> Material.DEEPSLATE_TILES;
            case "reinforced_deepslate_altar" -> Material.CHISELED_DEEPSLATE;
            case "chiseled_deepslate" -> Material.CHISELED_DEEPSLATE;
            case "polished_deepslate" -> Material.POLISHED_DEEPSLATE;
            default -> null;
        };
    }
}