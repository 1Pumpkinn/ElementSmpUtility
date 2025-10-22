package hs.elementSmpUtility.structure;

import hs.elementSmpUtility.blocks.CustomBlockManager;
import hs.elementSmpUtility.storage.BlockDataStorage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class StructurePlacement {

    private final CustomBlockManager blockManager;
    private final BlockDataStorage storage;

    public StructurePlacement(CustomBlockManager blockManager, BlockDataStorage storage) {
        this.blockManager = blockManager;
        this.storage = storage;
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

        clearArea(world, baseX, baseY, baseZ, temple.getWidth(), temple.getHeight(), temple.getDepth());

        for (TempleStructure.StructureBlock structureBlock : temple.getBlocks()) {
            int x = baseX + structureBlock.getX();
            int y = baseY + structureBlock.getY();
            int z = baseZ + structureBlock.getZ();

            Block block = world.getBlockAt(x, y, z);
            String blockType = structureBlock.getBlockType();

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