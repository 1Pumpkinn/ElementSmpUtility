package hs.elementSmpUtility.structure.component;

import hs.elementSmpUtility.structure.TempleStructure.StructureBlock;
import java.util.List;

public class InnerSanctumBuilder extends StructureComponent {

    public InnerSanctumBuilder(int width, int height, int depth) {
        super(width, height, depth);
    }

    @Override
    public void build(List<StructureBlock> blocks) {
        int centerX = width / 2;
        int centerZ = depth / 2;
        int sanctumSize = 10;

        // Build inner sanctum walls
        for (int y = 4; y <= 12; y++) {
            for (int x = centerX - sanctumSize / 2; x <= centerX + sanctumSize / 2; x++) {
                for (int z = centerZ - sanctumSize / 2; z <= centerZ + sanctumSize / 2; z++) {
                    boolean isWall = (x == centerX - sanctumSize / 2 || x == centerX + sanctumSize / 2 ||
                            z == centerZ - sanctumSize / 2 || z == centerZ + sanctumSize / 2);

                    if (isWall) {
                        if (y % 2 == 0) {
                            blocks.add(new StructureBlock(x, y, z, "chiseled_deepslate", true));
                        } else {
                            blocks.add(new StructureBlock(x, y, z, "reinforced_deepslate_altar", true));
                        }
                    }
                }
            }
        }

        // Create entrance to sanctum (south side)
        int entranceZ = centerZ + sanctumSize / 2;
        for (int y = 4; y <= 8; y++) {
            for (int x = centerX - 2; x <= centerX + 2; x++) {
                blocks.add(new StructureBlock(x, y, entranceZ, "air", false));
            }
        }

        // Build raised platform in center
        int platformRadius = 3;
        for (int x = centerX - platformRadius; x <= centerX + platformRadius; x++) {
            for (int z = centerZ - platformRadius; z <= centerZ + platformRadius; z++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));

                if (distance <= platformRadius) {
                    // Raised platform base
                    blocks.add(new StructureBlock(x, 4, z, "reinforced_deepslate_tiles", false));

                    // Platform surface
                    if (distance <= platformRadius - 1) {
                        blocks.add(new StructureBlock(x, 5, z, "chiseled_deepslate", false));
                    }
                }
            }
        }

        // Place PEDESTAL in the very center (replacing chiseled deepslate)
        blocks.add(new StructureBlock(centerX, 6, centerZ, "pedestal", false));

        // Add corner pillars inside sanctum
        int[][] corners = {
                {centerX - 3, centerZ - 3},
                {centerX + 3, centerZ - 3},
                {centerX - 3, centerZ + 3},
                {centerX + 3, centerZ + 3}
        };

        for (int[] corner : corners) {
            for (int y = 4; y <= 11; y++) {
                blocks.add(new StructureBlock(corner[0], y, corner[1], "reinforced_deepslate_bricks", true));
            }
            // Add lantern on top of corner pillar
            blocks.add(new StructureBlock(corner[0], 12, corner[1], "lantern", false));
        }

        // Add decorative chains hanging from sanctum ceiling
        for (int y = 11; y >= 7; y--) {
            // Chain circle around central pedestal
            blocks.add(new StructureBlock(centerX - 2, y, centerZ - 2, "chain", false));
            blocks.add(new StructureBlock(centerX + 2, y, centerZ - 2, "chain", false));
            blocks.add(new StructureBlock(centerX - 2, y, centerZ + 2, "chain", false));
            blocks.add(new StructureBlock(centerX + 2, y, centerZ + 2, "chain", false));
        }

        // Add bookshelves along sanctum walls
        for (int x = centerX - sanctumSize / 2 + 1; x < centerX + sanctumSize / 2; x += 2) {
            if (Math.abs(x - centerX) > 2) {
                blocks.add(new StructureBlock(x, 6, centerZ - sanctumSize / 2 + 1, "chiseled_bookshelf", false));
                blocks.add(new StructureBlock(x, 8, centerZ - sanctumSize / 2 + 1, "chiseled_bookshelf", false));
            }
        }

        // Add sanctum ceiling
        for (int x = centerX - sanctumSize / 2; x <= centerX + sanctumSize / 2; x++) {
            for (int z = centerZ - sanctumSize / 2; z <= centerZ + sanctumSize / 2; z++) {
                blocks.add(new StructureBlock(x, 12, z, "reinforced_deepslate_tiles", true));
            }
        }
    }
}