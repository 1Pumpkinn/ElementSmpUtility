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
        int centerX = width / 2;

        for (int step = 0; step < 5; step++) {
            int z = step;
            int y = 3 + step;
            for (int x = centerX - 3; x <= centerX + 3; x++) {
                if (z < depth) {
                    blocks.add(new StructureBlock(x, y, z, "reinforced_deepslate_tiles", false));
                }
            }
        }

        for (int y = 3; y <= 10; y++) {
            blocks.add(new StructureBlock(centerX - 4, y, 2, "chiseled_deepslate", false));
            blocks.add(new StructureBlock(centerX + 4, y, 2, "chiseled_deepslate", false));
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

        for (int x = centerX - 5; x <= centerX + 5; x++) {
            for (int z = centerZ - 5; z <= centerZ + 5; z++) {
                blocks.add(new StructureBlock(x, 3, z, "polished_deepslate", false));
            }
        }

        for (int y = 4; y <= 8; y++) {
            int size = 9 - y;
            for (int x = centerX - size; x <= centerX + size; x++) {
                for (int z = centerZ - size; z <= centerZ + size; z++) {
                    String blockType = (y == 8) ? "reinforced_deepslate_tiles" : "reinforced_deepslate_bricks";
                    blocks.add(new StructureBlock(x, y, z, blockType, false));
                }
            }
        }

        blocks.add(new StructureBlock(centerX, 9, centerZ, "reinforced_deepslate_altar", true));
        blocks.add(new StructureBlock(centerX - 1, 9, centerZ, "reinforced_deepslate_altar", true));
        blocks.add(new StructureBlock(centerX + 1, 9, centerZ, "reinforced_deepslate_altar", true));
        blocks.add(new StructureBlock(centerX, 9, centerZ - 1, "reinforced_deepslate_altar", true));
        blocks.add(new StructureBlock(centerX, 9, centerZ + 1, "reinforced_deepslate_altar", true));

        int[][] circleOffsets = {
                {-4, 0}, {4, 0}, {0, -4}, {0, 4},
                {-3, -3}, {-3, 3}, {3, -3}, {3, 3}
        };

        for (int[] offset : circleOffsets) {
            int x = centerX + offset[0];
            int z = centerZ + offset[1];
            blocks.add(new StructureBlock(x, 3, z, "chiseled_deepslate", false));
            blocks.add(new StructureBlock(x, 4, z, "chiseled_deepslate", false));
        }

        buildSideChamber(centerX - 8, centerZ, 5);
        buildSideChamber(centerX + 8, centerZ, 5);
    }

    private void buildSideChamber(int cx, int cz, int y) {
        for (int x = cx - 2; x <= cx + 2; x++) {
            for (int z = cz - 2; z <= cz + 2; z++) {
                blocks.add(new StructureBlock(x, y, z, "polished_deepslate", false));

                if (Math.abs(x - cx) == 1 && Math.abs(z - cz) == 1) {
                    blocks.add(new StructureBlock(x, y + 1, z, "chiseled_deepslate", false));
                }
            }
        }
    }

    private void buildUpperGallery() {
        int centerX = width / 2;
        int centerZ = depth / 2;

        for (int y = 13; y <= 15; y++) {
            for (int x = 4; x < width - 4; x++) {
                for (int z = 4; z < depth - 4; z++) {
                    boolean isWalkway = (x == 4 || x == width - 5 || z == 4 || z == depth - 5);
                    boolean isNearCenter = Math.abs(x - centerX) < 4 && Math.abs(z - centerZ) < 4;

                    if (isWalkway && !isNearCenter) {
                        blocks.add(new StructureBlock(x, y, z, "reinforced_deepslate_tiles", false));
                    }
                }
            }

            if (y == 13) {
                for (int x = 4; x < width - 4; x++) {
                    for (int z = 4; z < depth - 4; z++) {
                        boolean isRailing = (x == 3 || x == width - 4 || z == 3 || z == depth - 4);
                        if (isRailing && (x + z) % 2 == 0) {
                            blocks.add(new StructureBlock(x, y + 1, z, "chiseled_deepslate", false));
                        }
                    }
                }
            }
        }
    }

    private void buildTieredRoof() {
        for (int tier = 0; tier < 4; tier++) {
            int baseY = 16 + (tier * 2);
            int inset = 3 + (tier * 2);

            for (int x = inset; x < width - inset; x++) {
                for (int z = inset; z < depth - inset; z++) {
                    boolean isEdge = x == inset || x == width - inset - 1 ||
                            z == inset || z == depth - inset - 1;

                    if (isEdge) {
                        String blockType = (tier % 2 == 0) ? "reinforced_deepslate_tiles" : "reinforced_deepslate_bricks";
                        blocks.add(new StructureBlock(x, baseY, z, blockType, false));

                        if ((x == inset || x == width - inset - 1) &&
                                (z == inset || z == depth - inset - 1)) {
                            blocks.add(new StructureBlock(x, baseY + 1, z, "chiseled_deepslate", false));
                        }
                    }
                }
            }
        }
    }

    private void buildCentralSpire() {
        int centerX = width / 2;
        int centerZ = depth / 2;

        for (int x = centerX - 2; x <= centerX + 2; x++) {
            for (int z = centerZ - 2; z <= centerZ + 2; z++) {
                blocks.add(new StructureBlock(x, 24, z, "reinforced_deepslate_bricks", false));
            }
        }

        for (int y = 25; y <= 28; y++) {
            int size = 29 - y;
            for (int x = centerX - size; x <= centerX + size; x++) {
                for (int z = centerZ - size; z <= centerZ + size; z++) {
                    if (x == centerX - size || x == centerX + size ||
                            z == centerZ - size || z == centerZ + size ||
                            (x == centerX && z == centerZ)) {
                        blocks.add(new StructureBlock(x, y, z, "chiseled_deepslate", false));
                    }
                }
            }
        }

        blocks.add(new StructureBlock(centerX, 29, centerZ, "reinforced_deepslate_altar", true));
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