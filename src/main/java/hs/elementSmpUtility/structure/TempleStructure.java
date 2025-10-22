
package hs.elementSmpUtility.structure;

import hs.elementSmpUtility.structure.StructurePlacement;
import hs.elementSmpUtility.structure.TempleRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the temple structure layout and blocks
 */
public class TempleStructure {

    private final List<StructureBlock> blocks;
    private final int width;
    private final int height;
    private final int depth;

    public TempleStructure() {
        this.blocks = new ArrayList<>();
        this.width = 15;
        this.height = 20;
        this.depth = 15;

        generateTempleLayout();
    }

    /**
     * Generate the temple structure layout
     */
    private void generateTempleLayout() {
        // Foundation and floor (Layer 0-1)
        buildFoundation();

        // Walls (Layer 2-10)
        buildWalls();

        // Pillars (Layer 2-15)
        buildPillars();

        // Inner chamber (Layer 5-8)
        buildInnerChamber();

        // Roof layers (Layer 11-16)
        buildRoof();

        // Spire (Layer 17-19)
        buildSpire();
    }

    private void buildFoundation() {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                // Base layer - reinforced deepslate tiles
                blocks.add(new StructureBlock(x, 0, z, "reinforced_deepslate_tiles", false));

                // Floor layer
                if (x > 0 && x < width - 1 && z > 0 && z < depth - 1) {
                    blocks.add(new StructureBlock(x, 1, z, "polished_deepslate", false));
                } else {
                    blocks.add(new StructureBlock(x, 1, z, "reinforced_deepslate_tiles", false));
                }
            }
        }
    }

    private void buildWalls() {
        for (int y = 2; y <= 10; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    boolean isWall = x == 0 || x == width - 1 || z == 0 || z == depth - 1;
                    boolean isDoorway = (x == width / 2 || x == width / 2 + 1) && z == 0 && y >= 2 && y <= 5;

                    if (isWall && !isDoorway) {
                        String blockType = (y % 2 == 0) ? "reinforced_deepslate_bricks" : "reinforced_deepslate_tiles";
                        blocks.add(new StructureBlock(x, y, z, blockType, false));
                    }
                }
            }
        }
    }

    private void buildPillars() {
        int[][] pillarPositions = {
                {2, 2}, {2, depth - 3}, {width - 3, 2}, {width - 3, depth - 3},
                {width / 2, depth / 2}  // Center pillar
        };

        for (int[] pos : pillarPositions) {
            for (int y = 2; y <= 15; y++) {
                String blockType = (y % 3 == 0) ? "chiseled_deepslate" : "reinforced_deepslate_bricks";
                blocks.add(new StructureBlock(pos[0], y, pos[1], blockType, false));
            }
        }
    }

    private void buildInnerChamber() {
        int chamberX = width / 2;
        int chamberZ = depth / 2;

        // Altar pedestal
        for (int y = 5; y <= 7; y++) {
            int size = 8 - y;
            for (int x = chamberX - size; x <= chamberX + size; x++) {
                for (int z = chamberZ - size; z <= chamberZ + size; z++) {
                    if (x >= 0 && x < width && z >= 0 && z < depth) {
                        blocks.add(new StructureBlock(x, y, z, "reinforced_deepslate_bricks", false));
                    }
                }
            }
        }

        // Altar top (unbreakable)
        blocks.add(new StructureBlock(chamberX, 8, chamberZ, "reinforced_deepslate_altar", true));
    }

    private void buildRoof() {
        for (int y = 11; y <= 16; y++) {
            int inset = (y - 11);

            for (int x = inset; x < width - inset; x++) {
                for (int z = inset; z < depth - inset; z++) {
                    if (x == inset || x == width - inset - 1 || z == inset || z == depth - inset - 1) {
                        String blockType = (y % 2 == 0) ? "reinforced_deepslate_tiles" : "reinforced_deepslate_bricks";
                        blocks.add(new StructureBlock(x, y, z, blockType, false));
                    }
                }
            }
        }
    }

    private void buildSpire() {
        int centerX = width / 2;
        int centerZ = depth / 2;

        for (int y = 17; y <= 19; y++) {
            blocks.add(new StructureBlock(centerX, y, centerZ, "chiseled_deepslate", false));
        }

        // Spire top
        blocks.add(new StructureBlock(centerX, 20, centerZ, "reinforced_deepslate_altar", true));
    }

    public List<StructureBlock> getBlocks() {
        return new ArrayList<>(blocks);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getDepth() { return depth; }

    /**
     * Represents a single block in the structure
     */
    public static class StructureBlock {
        private final int x, y, z;
        private final String blockType;
        private final boolean isUnbreakableCore;

        public StructureBlock(int x, int y, int z, String blockType, boolean isUnbreakableCore) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockType = blockType;
            this.isUnbreakableCore = isUnbreakableCore;
        }

        public int getX() { return x; }
        public int getY() { return y; }
        public int getZ() { return z; }
        public String getBlockType() { return blockType; }
        public boolean isUnbreakableCore() { return isUnbreakableCore; }
    }
}
