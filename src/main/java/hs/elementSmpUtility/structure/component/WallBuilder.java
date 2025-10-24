package hs.elementSmpUtility.structure.component;

import hs.elementSmpUtility.structure.TempleStructure.StructureBlock;
import java.util.List;

public class WallBuilder extends StructureComponent {

    public WallBuilder(int width, int height, int depth) {
        super(width, height, depth);
    }

    @Override
    public void build(List<StructureBlock> blocks) {
        // Build outer walls from y=4 to y=15
        for (int y = 4; y <= 15; y++) {
            // North and South walls (z = 0 and z = depth-1)
            for (int x = 0; x < width; x++) {
                // North wall (z = 0)
                blocks.add(new StructureBlock(x, y, 0, getWallBlockType(x, y), true));

                // South wall (z = depth-1)
                blocks.add(new StructureBlock(x, y, depth - 1, getWallBlockType(x, y), true));
            }

            // East and West walls (x = 0 and x = width-1)
            for (int z = 1; z < depth - 1; z++) {
                // West wall (x = 0)
                blocks.add(new StructureBlock(0, y, z, getWallBlockType(z, y), true));

                // East wall (x = width-1)
                blocks.add(new StructureBlock(width - 1, y, z, getWallBlockType(z, y), true));
            }
        }

        // Add decorative elements to walls
        addWallDecorations(blocks);
    }

    private String getWallBlockType(int pos, int y) {
        // Create vertical striped pattern
        if (y % 4 == 0) {
            return "chiseled_deepslate";
        } else if (pos % 3 == 0) {
            return "reinforced_deepslate_tiles";
        } else {
            return "reinforced_deepslate_bricks";
        }
    }

    private void addWallDecorations(List<StructureBlock> blocks) {
        // Add chains hanging from ceiling at regular intervals
        // North wall chains
        for (int x = 5; x < width - 5; x += 8) {
            for (int y = 13; y >= 6; y--) {
                blocks.add(new StructureBlock(x, y, 1, "chain", false));
            }
        }

        // South wall chains
        for (int x = 5; x < width - 5; x += 8) {
            for (int y = 13; y >= 6; y--) {
                blocks.add(new StructureBlock(x, y, depth - 2, "chain", false));
            }
        }

        // West wall chains
        for (int z = 5; z < depth - 5; z += 8) {
            for (int y = 13; y >= 6; y--) {
                blocks.add(new StructureBlock(1, y, z, "chain", false));
            }
        }

        // East wall chains
        for (int z = 5; z < depth - 5; z += 8) {
            for (int y = 13; y >= 6; y--) {
                blocks.add(new StructureBlock(width - 2, y, z, "chain", false));
            }
        }
    }
}