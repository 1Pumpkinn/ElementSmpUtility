package hs.elementSmpUtility.structure.component;

import hs.elementSmpUtility.structure.TempleStructure.StructureBlock;
import java.util.List;

public class MainHallBuilder extends StructureComponent {

    public MainHallBuilder(int width, int height, int depth) {
        super(width, height, depth);
    }

    @Override
    public void build(List<StructureBlock> blocks) {
        int centerX = width / 2;
        int centerZ = depth / 2;

        // Build main hall pillars (4 large pillars in main area)
        int[][] pillarPositions = {
                {centerX - 8, centerZ - 8},
                {centerX + 8, centerZ - 8},
                {centerX - 8, centerZ + 8},
                {centerX + 8, centerZ + 8}
        };

        for (int[] pos : pillarPositions) {
            buildPillar(blocks, pos[0], pos[1]);
        }

        // Add decorative floor pattern in main hall
        for (int x = centerX - 12; x <= centerX + 12; x++) {
            for (int z = centerZ - 12; z <= centerZ + 12; z++) {
                // Skip pillar locations
                boolean isPillar = false;
                for (int[] pos : pillarPositions) {
                    if (Math.abs(x - pos[0]) <= 1 && Math.abs(z - pos[1]) <= 1) {
                        isPillar = true;
                        break;
                    }
                }

                if (!isPillar && (x + z) % 6 == 0) {
                    blocks.add(new StructureBlock(x, 3, z, "chiseled_deepslate", false));
                }
            }
        }

        // Add bookshelves along walls for ancient library feel
        addBookshelves(blocks, centerX, centerZ);
    }

    private void buildPillar(List<StructureBlock> blocks, int x, int z) {
        // Build a 3x3 pillar from floor to near ceiling
        for (int y = 4; y <= 14; y++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    String blockType;

                    if (dx == 0 && dz == 0) {
                        // Center of pillar
                        blockType = "reinforced_deepslate_altar";
                    } else if ((dx == 0 || dz == 0) && y % 3 == 0) {
                        blockType = "chiseled_deepslate";
                    } else {
                        blockType = "reinforced_deepslate_bricks";
                    }

                    blocks.add(new StructureBlock(x + dx, y, z + dz, blockType, true));
                }
            }
        }

        // Add pillar caps
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                blocks.add(new StructureBlock(x + dx, 15, z + dz, "reinforced_deepslate_tiles", true));
            }
        }

        // Add lantern on top of pillar
        blocks.add(new StructureBlock(x, 15, z, "lantern", false));

        // Add chains hanging from pillar
        for (int y = 13; y >= 6; y--) {
            blocks.add(new StructureBlock(x - 2, y, z, "chain", false));
            blocks.add(new StructureBlock(x + 2, y, z, "chain", false));
            blocks.add(new StructureBlock(x, y, z - 2, "chain", false));
            blocks.add(new StructureBlock(x, y, z + 2, "chain", false));
        }
    }

    private void addBookshelves(List<StructureBlock> blocks, int centerX, int centerZ) {
        // Add chiseled bookshelves along the walls
        // North side
        for (int x = 8; x < width - 8; x += 3) {
            if (Math.abs(x - centerX) > 10) {
                blocks.add(new StructureBlock(x, 5, 2, "chiseled_bookshelf", false));
                blocks.add(new StructureBlock(x, 7, 2, "chiseled_bookshelf", false));
            }
        }

        // South side
        for (int x = 8; x < width - 8; x += 3) {
            if (Math.abs(x - centerX) > 10) {
                blocks.add(new StructureBlock(x, 5, depth - 3, "chiseled_bookshelf", false));
                blocks.add(new StructureBlock(x, 7, depth - 3, "chiseled_bookshelf", false));
            }
        }
    }
}