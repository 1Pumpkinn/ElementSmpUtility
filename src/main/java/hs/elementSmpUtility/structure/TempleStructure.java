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
        this.width = 35;  // Increased from 25
        this.height = 35; // Increased from 30
        this.depth = 35;  // Increased from 25
        generateTempleLayout();
    }

    private void generateTempleLayout() {
        buildGrandFoundation();
        buildEntranceStairs();
        buildDecorativeWalls();
        buildCornerTowers();
        buildGrandPillars();
        buildSideRooms();
        buildInnerSanctum();
        buildFloorDetails();
        buildLighting();
        buildUpperLevel();
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

        // Grand entrance pillars
        for (int y = 3; y <= 12; y++) {
            blocks.add(new StructureBlock(centerX - 5, y, 0, "chiseled_deepslate", false));
            blocks.add(new StructureBlock(centerX + 5, y, 0, "chiseled_deepslate", false));

            // Decorative details every 2 blocks
            if (y % 2 == 0) {
                blocks.add(new StructureBlock(centerX - 6, y, 0, "polished_deepslate", false));
                blocks.add(new StructureBlock(centerX + 6, y, 0, "polished_deepslate", false));
            }
        }

        // Entrance arch
        for (int x = centerX - 4; x <= centerX + 4; x++) {
            blocks.add(new StructureBlock(x, 10, 0, "chiseled_deepslate", false));
        }

        // Lanterns at entrance
        blocks.add(new StructureBlock(centerX - 5, 8, 1, "lantern", false));
        blocks.add(new StructureBlock(centerX + 5, 8, 1, "lantern", false));
    }

    private void buildDecorativeWalls() {
        for (int y = 3; y <= 15; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    boolean isWall = x == 0 || x == width - 1 || z == 0 || z == depth - 1;
                    boolean isDoorway = (x >= width / 2 - 3 && x <= width / 2 + 3) && z == 0 && y >= 3 && y <= 9;
                    boolean isWindow = !isDoorway && isWall && y >= 7 && y <= 9 &&
                            ((x % 7 == 0 && (z == 0 || z == depth - 1)) ||
                                    (z % 7 == 0 && (x == 0 || x == width - 1)));

                    // Side room doorways
                    boolean isSideRoomDoor = false;
                    if ((x == 0 || x == width - 1) && y >= 3 && y <= 7) {
                        if ((z >= 8 && z <= 10) || (z >= depth - 10 && z <= depth - 8)) {
                            isSideRoomDoor = true;
                        }
                    }
                    if ((z == 0 || z == depth - 1) && y >= 3 && y <= 7 && !isDoorway) {
                        if ((x >= 8 && x <= 10) || (x >= width - 10 && x <= width - 8)) {
                            isSideRoomDoor = true;
                        }
                    }

                    if (isWall && !isDoorway && !isWindow && !isSideRoomDoor) {
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

                    // Add decorative bands
                    if (isWall && !isDoorway && !isSideRoomDoor && y == 11) {
                        blocks.add(new StructureBlock(x, y, z, "chiseled_deepslate", false));
                    }
                }
            }
        }
    }

    private void buildCornerTowers() {
        int[][] corners = {
                {2, 2}, {2, depth - 3}, {width - 3, 2}, {width - 3, depth - 3}
        };

        for (int[] corner : corners) {
            int cx = corner[0];
            int cz = corner[1];

            // Tower base
            for (int y = 3; y <= 5; y++) {
                for (int x = cx - 2; x <= cx + 2; x++) {
                    for (int z = cz - 2; z <= cz + 2; z++) {
                        blocks.add(new StructureBlock(x, y, z, "reinforced_deepslate_bricks", false));
                    }
                }
            }

            // Tower shaft with decorative bands
            for (int y = 6; y <= 22; y++) {
                blocks.add(new StructureBlock(cx, y, cz, "reinforced_deepslate_bricks", false));

                if (y % 3 == 0) {
                    blocks.add(new StructureBlock(cx - 1, y, cz, "chiseled_deepslate", false));
                    blocks.add(new StructureBlock(cx + 1, y, cz, "chiseled_deepslate", false));
                    blocks.add(new StructureBlock(cx, y, cz - 1, "chiseled_deepslate", false));
                    blocks.add(new StructureBlock(cx, y, cz + 1, "chiseled_deepslate", false));
                }

                // Lanterns on towers
                if (y == 10 || y == 16) {
                    blocks.add(new StructureBlock(cx - 2, y, cz, "lantern", false));
                    blocks.add(new StructureBlock(cx + 2, y, cz, "lantern", false));
                    blocks.add(new StructureBlock(cx, y, cz - 2, "lantern", false));
                    blocks.add(new StructureBlock(cx, y, cz + 2, "lantern", false));
                }
            }

            // Tower cap
            for (int y = 23; y <= 24; y++) {
                for (int x = cx - 2; x <= cx + 2; x++) {
                    for (int z = cz - 2; z <= cz + 2; z++) {
                        blocks.add(new StructureBlock(x, y, z, "reinforced_deepslate_tiles", false));
                    }
                }
            }

            blocks.add(new StructureBlock(cx, 25, cz, "reinforced_deepslate_altar", true));
            blocks.add(new StructureBlock(cx, 26, cz, "lantern", false));
        }
    }

    private void buildGrandPillars() {
        int[][] pillarPositions = {
                {7, 7}, {7, depth - 8}, {width - 8, 7}, {width - 8, depth - 8},
                {width / 2, 7}, {width / 2, depth - 8},
                {7, depth / 2}, {width - 8, depth / 2}
        };

        for (int[] pos : pillarPositions) {
            // Pillar base
            for (int x = pos[0] - 1; x <= pos[0] + 1; x++) {
                for (int z = pos[1] - 1; z <= pos[1] + 1; z++) {
                    blocks.add(new StructureBlock(x, 3, z, "polished_deepslate", false));
                }
            }

            // Main pillar with decorative rings
            for (int y = 4; y <= 18; y++) {
                blocks.add(new StructureBlock(pos[0], y, pos[1], "reinforced_deepslate_bricks", false));

                if (y % 4 == 0) {
                    blocks.add(new StructureBlock(pos[0] - 1, y, pos[1], "chiseled_deepslate", false));
                    blocks.add(new StructureBlock(pos[0] + 1, y, pos[1], "chiseled_deepslate", false));
                    blocks.add(new StructureBlock(pos[0], y, pos[1] - 1, "chiseled_deepslate", false));
                    blocks.add(new StructureBlock(pos[0], y, pos[1] + 1, "chiseled_deepslate", false));
                }

                // Pillar capitals
                if (y >= 17) {
                    for (int x = pos[0] - 1; x <= pos[0] + 1; x++) {
                        for (int z = pos[1] - 1; z <= pos[1] + 1; z++) {
                            blocks.add(new StructureBlock(x, y, z, "chiseled_deepslate", false));
                        }
                    }
                }
            }

            // Lanterns on pillars
            blocks.add(new StructureBlock(pos[0], 12, pos[1] + 2, "lantern", false));
            blocks.add(new StructureBlock(pos[0], 12, pos[1] - 2, "lantern", false));
        }
    }

    private void buildSideRooms() {
        // Four side chambers
        buildSideRoom(3, 7, 5);       // Front left
        buildSideRoom(width - 8, 7, 5);   // Front right
        buildSideRoom(3, depth - 8, 5);   // Back left
        buildSideRoom(width - 8, depth - 8, 5); // Back right
    }

    private void buildSideRoom(int centerX, int centerZ, int size) {
        // Room walls
        for (int y = 3; y <= 12; y++) {
            for (int x = centerX - size; x <= centerX + size; x++) {
                for (int z = centerZ - size; z <= centerZ + size; z++) {
                    boolean isWall = x == centerX - size || x == centerX + size ||
                            z == centerZ - size || z == centerZ + size;

                    // Leave doorways
                    boolean isDoorway = (x == centerX && (z == centerZ - size || z == centerZ + size) && y <= 7) ||
                            (z == centerZ && (x == centerX - size || x == centerX + size) && y <= 7);

                    if (isWall && !isDoorway && y <= 10) {
                        String blockType = (y % 2 == 0) ? "reinforced_deepslate_bricks" : "reinforced_deepslate_tiles";
                        blocks.add(new StructureBlock(x, y, z, blockType, false));
                    }
                }
            }
        }

        // Room ceiling
        for (int x = centerX - size; x <= centerX + size; x++) {
            for (int z = centerZ - size; z <= centerZ + size; z++) {
                blocks.add(new StructureBlock(x, 11, z, "reinforced_deepslate_tiles", false));
            }
        }

        // Decorative altar in room
        blocks.add(new StructureBlock(centerX, 3, centerZ, "polished_deepslate", false));
        blocks.add(new StructureBlock(centerX, 4, centerZ, "chiseled_deepslate", false));

        // Lanterns in corners
        blocks.add(new StructureBlock(centerX - size + 1, 7, centerZ - size + 1, "lantern", false));
        blocks.add(new StructureBlock(centerX + size - 1, 7, centerZ - size + 1, "lantern", false));
        blocks.add(new StructureBlock(centerX - size + 1, 7, centerZ + size - 1, "lantern", false));
        blocks.add(new StructureBlock(centerX + size - 1, 7, centerZ + size - 1, "lantern", false));

        // Decorative pillars in room
        blocks.add(new StructureBlock(centerX - 2, 3, centerZ - 2, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX - 2, 4, centerZ - 2, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX + 2, 3, centerZ - 2, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX + 2, 4, centerZ - 2, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX - 2, 3, centerZ + 2, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX - 2, 4, centerZ + 2, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX + 2, 3, centerZ + 2, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX + 2, 4, centerZ + 2, "chiseled_deepslate", false));
    }

    private void buildInnerSanctum() {
        int centerX = width / 2;
        int centerZ = depth / 2;

        // Ornate circular platform with radiating pattern
        for (int x = centerX - 6; x <= centerX + 6; x++) {
            for (int z = centerZ - 6; z <= centerZ + 6; z++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));
                if (distance <= 6) {
                    if (distance <= 2) {
                        blocks.add(new StructureBlock(x, 3, z, "chiseled_deepslate", false));
                    } else if ((x + z) % 2 == 0) {
                        blocks.add(new StructureBlock(x, 3, z, "polished_deepslate", false));
                    } else {
                        blocks.add(new StructureBlock(x, 3, z, "reinforced_deepslate_tiles", false));
                    }
                }
            }
        }

        // Stepped pyramid base for pedestal
        for (int x = centerX - 2; x <= centerX + 2; x++) {
            for (int z = centerZ - 2; z <= centerZ + 2; z++) {
                blocks.add(new StructureBlock(x, 4, z, "reinforced_deepslate_bricks", false));
            }
        }

        for (int x = centerX - 1; x <= centerX + 1; x++) {
            for (int z = centerZ - 1; z <= centerZ + 1; z++) {
                blocks.add(new StructureBlock(x, 5, z, "chiseled_deepslate", false));
            }
        }

        // PEDESTAL at the center
        blocks.add(new StructureBlock(centerX, 6, centerZ, "pedestal", false));

        // Decorative brazier pillars around the pedestal
        int[][] brazierPositions = {
                {centerX - 5, centerZ - 5}, {centerX + 5, centerZ - 5},
                {centerX - 5, centerZ + 5}, {centerX + 5, centerZ + 5}
        };

        for (int[] pos : brazierPositions) {
            blocks.add(new StructureBlock(pos[0], 3, pos[1], "polished_deepslate", false));
            blocks.add(new StructureBlock(pos[0], 4, pos[1], "chiseled_deepslate", false));
            blocks.add(new StructureBlock(pos[0], 5, pos[1], "lantern", false));
        }

        // Ritual circles in the floor
        int[][] circlePositions = {
                {centerX - 8, centerZ}, {centerX + 8, centerZ},
                {centerX, centerZ - 8}, {centerX, centerZ + 8}
        };

        for (int[] pos : circlePositions) {
            for (int x = pos[0] - 1; x <= pos[0] + 1; x++) {
                for (int z = pos[1] - 1; z <= pos[1] + 1; z++) {
                    double dist = Math.sqrt(Math.pow(x - pos[0], 2) + Math.pow(z - pos[1], 2));
                    if (dist <= 1.5) {
                        blocks.add(new StructureBlock(x, 3, z, "chiseled_deepslate", false));
                    }
                }
            }
        }
    }

    private void buildFloorDetails() {
        int centerX = width / 2;
        int centerZ = depth / 2;

        // Main pathway from entrance to center
        for (int z = 3; z < centerZ - 6; z++) {
            for (int x = centerX - 2; x <= centerX + 2; x++) {
                blocks.add(new StructureBlock(x, 3, z, "polished_deepslate", false));
            }
            // Decorative edges
            blocks.add(new StructureBlock(centerX - 3, 3, z, "chiseled_deepslate", false));
            blocks.add(new StructureBlock(centerX + 3, 3, z, "chiseled_deepslate", false));
        }

        // Cross pathways
        for (int x = 3; x < width - 3; x++) {
            if (Math.abs(x - centerX) > 6) {
                blocks.add(new StructureBlock(x, 3, centerZ, "polished_deepslate", false));
            }
        }

        for (int z = 3; z < depth - 3; z++) {
            if (Math.abs(z - centerZ) > 6) {
                blocks.add(new StructureBlock(centerX, 3, z, "polished_deepslate", false));
            }
        }
    }

    private void buildLighting() {
        int centerX = width / 2;
        int centerZ = depth / 2;

        // Wall lanterns around the perimeter
        for (int x = 4; x < width - 4; x += 6) {
            blocks.add(new StructureBlock(x, 8, 1, "lantern", false));
            blocks.add(new StructureBlock(x, 8, depth - 2, "lantern", false));
        }

        for (int z = 4; z < depth - 4; z += 6) {
            blocks.add(new StructureBlock(1, 8, z, "lantern", false));
            blocks.add(new StructureBlock(width - 2, 8, z, "lantern", false));
        }

        // Hanging lanterns from ceiling
        blocks.add(new StructureBlock(centerX - 6, 12, centerZ - 6, "lantern", false));
        blocks.add(new StructureBlock(centerX + 6, 12, centerZ - 6, "lantern", false));
        blocks.add(new StructureBlock(centerX - 6, 12, centerZ + 6, "lantern", false));
        blocks.add(new StructureBlock(centerX + 6, 12, centerZ + 6, "lantern", false));
    }

    private void buildUpperLevel() {
        int centerX = width / 2;
        int centerZ = depth / 2;

        // Upper walkway around perimeter at y=16-18
        for (int x = 3; x < width - 3; x++) {
            for (int z = 3; z < depth - 3; z++) {
                boolean isPerimeter = (x >= 3 && x <= 5) || (x >= width - 6 && x < width - 3) ||
                        (z >= 3 && z <= 5) || (z >= depth - 6 && z < depth - 3);

                if (isPerimeter) {
                    blocks.add(new StructureBlock(x, 16, z, "reinforced_deepslate_tiles", false));

                    // Railings
                    boolean isOuterEdge = x == 3 || x == width - 4 || z == 3 || z == depth - 4;
                    if (isOuterEdge && (x + z) % 2 == 0) {
                        blocks.add(new StructureBlock(x, 17, z, "polished_deepslate", false));
                    }
                }
            }
        }
    }

    private void buildTieredRoof() {
        int centerX = width / 2;
        int centerZ = depth / 2;

        // Solid base ceiling at y=16
        for (int x = 1; x < width - 1; x++) {
            for (int z = 1; z < depth - 1; z++) {
                blocks.add(new StructureBlock(x, 16, z, "reinforced_deepslate_tiles", false));
            }
        }

        // Tiered pyramid roof
        for (int tier = 0; tier < 7; tier++) {
            int baseY = 17 + tier;
            int inset = 3 + (tier * 2);

            for (int x = inset; x < width - inset; x++) {
                for (int z = inset; z < depth - inset; z++) {
                    String blockType = (tier % 2 == 0) ? "reinforced_deepslate_bricks" : "reinforced_deepslate_tiles";
                    blocks.add(new StructureBlock(x, baseY, z, blockType, false));
                }
            }

            // Decorative corners
            if (tier < 6) {
                blocks.add(new StructureBlock(inset, baseY, inset, "chiseled_deepslate", false));
                blocks.add(new StructureBlock(width - inset - 1, baseY, inset, "chiseled_deepslate", false));
                blocks.add(new StructureBlock(inset, baseY, depth - inset - 1, "chiseled_deepslate", false));
                blocks.add(new StructureBlock(width - inset - 1, baseY, depth - inset - 1, "chiseled_deepslate", false));
            }
        }

        // Top cap
        for (int x = centerX - 4; x <= centerX + 4; x++) {
            for (int z = centerZ - 4; z <= centerZ + 4; z++) {
                blocks.add(new StructureBlock(x, 24, z, "reinforced_deepslate_bricks", false));
            }
        }
    }

    private void buildCentralSpire() {
        int centerX = width / 2;
        int centerZ = depth / 2;

        // Solid spire base
        for (int x = centerX - 3; x <= centerX + 3; x++) {
            for (int z = centerZ - 3; z <= centerZ + 3; z++) {
                blocks.add(new StructureBlock(x, 25, z, "reinforced_deepslate_bricks", false));
                blocks.add(new StructureBlock(x, 26, z, "reinforced_deepslate_bricks", false));
            }
        }

        // Decorative layer
        for (int x = centerX - 3; x <= centerX + 3; x++) {
            for (int z = centerZ - 3; z <= centerZ + 3; z++) {
                if (x == centerX - 3 || x == centerX + 3 || z == centerZ - 3 || z == centerZ + 3) {
                    blocks.add(new StructureBlock(x, 27, z, "chiseled_deepslate", false));
                }
            }
        }

        // Lanterns on spire
        blocks.add(new StructureBlock(centerX - 3, 27, centerZ, "lantern", false));
        blocks.add(new StructureBlock(centerX + 3, 27, centerZ, "lantern", false));
        blocks.add(new StructureBlock(centerX, 27, centerZ - 3, "lantern", false));
        blocks.add(new StructureBlock(centerX, 27, centerZ + 3, "lantern", false));

        // Tapering spire
        for (int y = 28; y <= 32; y++) {
            int size = 33 - y;
            for (int x = centerX - size; x <= centerX + size; x++) {
                for (int z = centerZ - size; z <= centerZ + size; z++) {
                    blocks.add(new StructureBlock(x, y, z, "chiseled_deepslate", false));
                }
            }
        }

        // Spire cap
        blocks.add(new StructureBlock(centerX, 33, centerZ, "reinforced_deepslate_altar", true));
        blocks.add(new StructureBlock(centerX - 1, 33, centerZ, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX + 1, 33, centerZ, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX, 33, centerZ - 1, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX, 33, centerZ + 1, "chiseled_deepslate", false));
        blocks.add(new StructureBlock(centerX, 34, centerZ, "lantern", false));
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