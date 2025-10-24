package hs.elementSmpUtility.structure.component;

import hs.elementSmpUtility.structure.TempleStructure.StructureBlock;
import java.util.List;

public class LightingBuilder extends StructureComponent {

    public LightingBuilder(int width, int height, int depth) {
        super(width, height, depth);
    }

    @Override
    public void build(List<StructureBlock> blocks) {
        // Add lanterns throughout the temple for ambient lighting

        // Main hall lighting - ceiling lanterns
        for (int x = 10; x < width - 10; x += 6) {
            for (int z = 10; z < depth - 10; z += 6) {
                // Hang lanterns from ceiling with chains
                for (int y = 14; y >= 12; y--) {
                    blocks.add(new StructureBlock(x, y, z, "chain", false));
                }
                blocks.add(new StructureBlock(x, 11, z, "lantern", false));
            }
        }

        // Wall-mounted lanterns along corridors
        // North wall
        for (int x = 8; x < width - 8; x += 8) {
            blocks.add(new StructureBlock(x, 7, 2, "lantern", false));
        }

        // South wall
        for (int x = 8; x < width - 8; x += 8) {
            blocks.add(new StructureBlock(x, 7, depth - 3, "lantern", false));
        }

        // East wall
        for (int z = 8; z < depth - 8; z += 8) {
            blocks.add(new StructureBlock(width - 3, 7, z, "lantern", false));
        }

        // West wall
        for (int z = 8; z < depth - 8; z += 8) {
            blocks.add(new StructureBlock(2, 7, z, "lantern", false));
        }

        // Corner accent lighting
        int[][] cornerPositions = {
                {5, 5},
                {width - 6, 5},
                {5, depth - 6},
                {width - 6, depth - 6}
        };

        for (int[] pos : cornerPositions) {
            // Create decorative light posts in corners
            for (int y = 4; y <= 9; y++) {
                blocks.add(new StructureBlock(pos[0], y, pos[1], "chain", false));
            }
            blocks.add(new StructureBlock(pos[0], 10, pos[1], "lantern", false));
        }

        // Add floor-level lighting along main pathways
        int centerX = width / 2;
        int centerZ = depth / 2;

        // Create lit pathway from entrance to center
        for (int z = 5; z < centerZ - 5; z += 4) {
            blocks.add(new StructureBlock(centerX - 3, 4, z, "polished_deepslate", false));
            blocks.add(new StructureBlock(centerX - 3, 5, z, "lantern", false));

            blocks.add(new StructureBlock(centerX + 3, 4, z, "polished_deepslate", false));
            blocks.add(new StructureBlock(centerX + 3, 5, z, "lantern", false));
        }
    }
}