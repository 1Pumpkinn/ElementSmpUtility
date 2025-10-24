package hs.elementSmpUtility.structure.component;

import hs.elementSmpUtility.structure.TempleStructure.StructureBlock;
import java.util.List;

public class CeilingBuilder extends StructureComponent {

    public CeilingBuilder(int width, int height, int depth) {
        super(width, height, depth);
    }

    @Override
    public void build(List<StructureBlock> blocks) {
        // Build main ceiling at y=16
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                String blockType;

                // Create decorative ceiling pattern
                if (x < 2 || x >= width - 2 || z < 2 || z >= depth - 2) {
                    // Outer edge
                    blockType = "reinforced_deepslate_bricks";
                } else if ((x + z) % 8 == 0) {
                    blockType = "chiseled_deepslate";
                } else if ((x % 4 == 0 && z % 4 == 0)) {
                    blockType = "reinforced_deepslate_altar";
                } else {
                    blockType = "reinforced_deepslate_tiles";
                }

                blocks.add(new StructureBlock(x, 16, z, blockType, true));
            }
        }

        // Add supporting beams across ceiling
        int centerX = width / 2;
        int centerZ = depth / 2;

        // North-South beam
        for (int z = 5; z < depth - 5; z++) {
            for (int dx = -1; dx <= 1; dx++) {
                blocks.add(new StructureBlock(centerX + dx, 15, z, "reinforced_deepslate_bricks", true));
            }
        }

        // East-West beam
        for (int x = 5; x < width - 5; x++) {
            for (int dz = -1; dz <= 1; dz++) {
                blocks.add(new StructureBlock(x, 15, centerZ + dz, "reinforced_deepslate_bricks", true));
            }
        }

        // Add decorative elements at beam intersections
        for (int dy = -1; dy <= 0; dy++) {
            blocks.add(new StructureBlock(centerX, 15 + dy, centerZ, "chiseled_deepslate", true));
        }
    }
}