package hs.elementSmpUtility.structure;

import java.util.ArrayList;
import java.util.List;

public class TempleStructure {

    private final List<StructureBlock> blocks;
    private final int width;
    private final int height;
    private final int depth;

    public TempleStructure() {
        this.blocks = new ArrayList<>();
        this.width = 25;
        this.height = 30;
        this.depth = 25;
        generateTempleLayout();
    }

    private void generateTempleLayout() {
        buildGrandFoundation();
        buildEntranceStairs();
        buildDecorativeWalls();
        buildCornerTowers();
        buildGrandPillars();
        buildInnerSanctum();
        buildUpperGallery();
        buildTieredRoof();
        buildCentralSpire();
    }

    private void buildGrandFoundation() {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                blocks.add(new StructureBlock(x, 0, z, "reinforced_deepslate_tiles", false));
                blocks.add(new StructureBlock(x, 1, z, "reinforced_deepslate_bricks", false));

                if (x > 0 && x < width - 1 && z > 0 && z < depth - 1) {
                    String pattern = ((x + z) % 3 == 0) ? "chiseled_deepslate" : "polished_deepslate";
                    blocks.add(new StructureBlock(x, 2, z, pattern, false));
                } else {
                    blocks.add(new StructureBlock(x, 2, z, "reinforced_deepslate_tiles", false));
                }
            }
        }
    }

    private void buildEntranceStairs() {
        // Removed entrance stairs - keeping ground level entrance
        int centerX = width / 2;

        // Just add decorative pillars at entrance
        for (int y = 3; y <= 10; y++) {
            blocks.add(new StructureBlock(centerX - 4, y, 0, "chiseled_deepslate", false));
            blocks.add(new StructureBlock(centerX + 4, y, 0, "chiseled_deepslate", false));
        }
    }

    private void buildDecorativeWalls() {
        for (int y = 3; y <= 12; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    boolean isWall = x == 0 || x == width - 1 || z == 0 || z == depth - 1;
                    boolean isDoorway = (x >= width / 2 - 2 && x <= width / 2 + 2) && z == 0 && y >= 3 && y <= 7;
                    boolean isWindow = !isDoorway && isWall && y >= 6 && y <= 8 &&
                            ((x % 6 == 0 && (z == 0 || z == depth - 1)) ||
                                    (z % 6 == 0 && (x == 0 || x == width - 1)));

                    if (isWall && !isDoorway && !isWindow) {
                        String blockType;
                        if (y % 3 == 0 || (x + z) % 4 == 0) {
                            blockType = "chiseled_deepslate";
                        } else if (y % 2 == 0) {
                            blockType = "reinforced_deepslate_bricks";
                        } else {
                            blockType = "reinforced_deepslate_tiles";
                        }
                        blocks.add(new StructureBlock(x, y, z, blockType, false));
                    }
                }
            }
        }
    }

    private void buildCornerTowers() {
        int[][] corners = {
                {1, 1}, {1, depth - 2}, {width - 2, 1}, {width - 2, depth - 2}
        };

        for (int[] corner : corners) {
            int cx = corner[0];
            int cz = corner[1];

            for (int y = 3; y <= 5; y++) {
                for (int x = cx - 1; x <= cx + 1; x++) {
                    for (int z = cz - 1; z <= cz + 1; z++) {
                        blocks.add(new StructureBlock(x, y, z, "reinforced_deepslate_bricks", false));
                    }
                }
            }

            for (int y = 6; y <= 18; y++) {
                blocks.add(new StructureBlock(cx, y, cz, "reinforced_deepslate_bricks", false));

                if (y % 3 == 0) {
                    blocks.add(new StructureBlock(cx - 1, y, cz, "chiseled_deepslate", false));
                    blocks.add(new StructureBlock(cx + 1, y, cz, "chiseled_deepslate", false));
                    blocks.add(new StructureBlock(cx, y, cz - 1, "chiseled_deepslate", false));
                    blocks.add(new StructureBlock(cx, y, cz + 1, "chiseled_deepslate", false));
                }
            }

            for (int y = 19; y <= 20; y++) {
                for (int x = cx - 1; x <= cx + 1; x++) {
                    for (int z = cz - 1; z <= cz + 1; z++) {
                        blocks.add(new StructureBlock(x, y, z, "reinforced_deepslate_tiles", false));
                    }
                }
            }

            blocks.add(new StructureBlock(cx, 21, cz, "reinforced_deepslate_altar", true));
        }
    }

    private void buildGrandPillars() {
        int[][] pillarPositions = {
                {5, 5}, {5, depth - 6}, {width - 6, 5}, {width - 6, depth - 6},
                {width / 2, 5}, {width / 2, depth - 6},
                {5, depth / 2}, {width - 6, depth / 2}
        };

        for (int[] pos : pillarPositions) {
            for (int y = 3; y <= 16; y++) {
                blocks.add(new StructureBlock(pos[0], y, pos[1], "reinforced_deepslate_bricks", false));

                if (y % 4 == 0) {
                    blocks.add(new StructureBlock(pos[0] - 1, y, pos[1], "chiseled_deepslate", false));
                    blocks.add(new StructureBlock(pos[0] + 1, y, pos[1], "chiseled_deepslate", false));
                    blocks.add(new StructureBlock(pos[0], y, pos[1] - 1, "chiseled_deepslate", false));
                    blocks.add(new StructureBlock(pos[0], y, pos[1] + 1, "chiseled_deepslate", false));
                }

                if (y >= 15) {
                    for (int x = pos[0] - 1; x <= pos[0] + 1; x++) {
                        for (int z = pos[1] - 1; z <= pos[1] + 1; z++) {
                            blocks.add(new StructureBlock(x, y, z, "chiseled_deepslate", false));
                        }
                    }
                }
            }
        }
    }

    private void buildInnerSanctum() {
        int centerX = width / 2;
        int centerZ = depth / 2;

        // Simple raised platform in center
        for (int x = centerX - 3; x <= centerX + 3; x++) {
            for (int z = centerZ - 3; z <= centerZ + 3; z++) {
                blocks.add(new StructureBlock(x, 3, z, "polished_deepslate", false));
            }
        }

        // Central altar pedestal
        for (int x = centerX - 1; x <= centerX + 1; x++) {
            for (int z = centerZ - 1; z <= centerZ + 1; z++) {
                blocks.add(new StructureBlock(x, 4, z, "reinforced_deepslate_bricks", false));
            }
        }

        // Main altar block
        blocks.add(new StructureBlock(centerX, 5, centerZ, "reinforced_deepslate_altar", true));
    }

    private void buildSideChamber(int cx, int cz, int y) {
        // Removed - keeping interior open and clean
    }

    private void buildUpperGallery() {
        // Removed - keeping single floor design for cleaner interior
    }

    private void buildTieredRoof() {
        int centerX = width / 2;
        int centerZ = depth / 2;

        // Solid base ceiling at y=13
        for (int x = 1; x < width - 1; x++) {
            for (int z = 1; z < depth - 1; z++) {
                blocks.add(new StructureBlock(x, 13, z, "reinforced_deepslate_tiles", false));
            }
        }

        // Tiered pyramid roof - fully solid
        for (int tier = 0; tier < 5; tier++) {
            int baseY = 14 + tier;
            int inset = 2 + (tier * 2);

            // Fill entire tier solidly
            for (int x = inset; x < width - inset; x++) {
                for (int z = inset; z < depth - inset; z++) {
                    String blockType = (tier % 2 == 0) ? "reinforced_deepslate_bricks" : "reinforced_deepslate_tiles";
                    blocks.add(new StructureBlock(x, baseY, z, blockType, false));
                }
            }

            // Decorative corners
            if (tier < 4) {
                blocks.add(new StructureBlock(inset, baseY, inset, "chiseled_deepslate", false));
                blocks.add(new StructureBlock(width - inset - 1, baseY, inset, "chiseled_deepslate", false));
                blocks.add(new StructureBlock(inset, baseY, depth - inset - 1, "chiseled_deepslate", false));
                blocks.add(new StructureBlock(width - inset - 1, baseY, depth - inset - 1, "chiseled_deepslate", false));
            }
        }

        // Top cap
        for (int x = centerX - 3; x <= centerX + 3; x++) {
            for (int z = centerZ - 3; z <= centerZ + 3; z++) {
                blocks.add(new StructureBlock(x, 19, z, "reinforced_deepslate_bricks", false));
            }
        }
    }

    private void buildCentralSpire() {
        int centerX = width / 2;
        int centerZ = depth / 2;

        // Solid spire base
        for (int x = centerX - 2; x <= centerX + 2; x++) {
            for (int z = centerZ - 2; z <= centerZ + 2; z++) {
                blocks.add(new StructureBlock(x, 20, z, "reinforced_deepslate_bricks", false));
                blocks.add(new StructureBlock(x, 21, z, "reinforced_deepslate_bricks", false));
            }
        }

        // Tapering spire - solid layers
        for (int y = 22; y <= 26; y++) {
            int size = 27 - y;
            for (int x = centerX - size; x <= centerX + size; x++) {
                for (int z = centerZ - size; z <= centerZ + size; z++) {
                    blocks.add(new StructureBlock(x, y, z, "chiseled_deepslate", false));
                }
            }
        }

        // Spire cap
        blocks.add(new StructureBlock(centerX, 27, centerZ, "reinforced_deepslate_altar", true));
        blocks.add(new StructureBlock(centerX - 1, 27, centerZ, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX + 1, 27, centerZ, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX, 27, centerZ - 1, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX, 27, centerZ + 1, "chiseled_deepslate", false));
    }

    public List<StructureBlock> getBlocks() {
        return new ArrayList<>(blocks);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

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

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public String getBlockType() {
            return blockType;
        }

        public boolean isUnbreakableCore() {
            return isUnbreakableCore;
        }
    }
}