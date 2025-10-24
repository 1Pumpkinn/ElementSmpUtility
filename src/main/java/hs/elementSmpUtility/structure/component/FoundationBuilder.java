package hs.elementSmpUtility.structure.component;

import hs.elementSmpUtility.structure.TempleStructure.StructureBlock;
import java.util.List;

public class FoundationBuilder extends StructureComponent {

    public FoundationBuilder(int width, int height, int depth) {
        super(width, height, depth);
    }

    @Override
    public void build(List<StructureBlock> blocks) {
        // Build foundation layers (y=0 to y=2)
        for (int y = 0; y <= 2; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    String blockType;

                    if (y == 0) {
                        // Solid base layer
                        blockType = "reinforced_deepslate_bricks";
                    } else if (y == 1) {
                        // Second layer with some variation
                        if ((x + z) % 3 == 0) {
                            blockType = "reinforced_deepslate_tiles";
                        } else {
                            blockType = "reinforced_deepslate_bricks";
                        }
                    } else {
                        // Top floor layer (y=2)
                        // Create decorative pattern
                        if (x < 3 || x >= width - 3 || z < 3 || z >= depth - 3) {
                            // Outer border
                            blockType = "chiseled_deepslate";
                        } else if ((x + z) % 4 == 0) {
                            blockType = "reinforced_deepslate_tiles";
                        } else {
                            blockType = "polished_deepslate";
                        }
                    }

                    blocks.add(new StructureBlock(x, y, z, blockType, y <= 1));
                }
            }
        }

        // Add floor layer (y=3) - this is the walkable surface
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                String blockType;

                // Create a decorative floor pattern
                boolean isEdge = x < 2 || x >= width - 2 || z < 2 || z >= depth - 2;

                if (isEdge) {
                    blockType = "reinforced_deepslate_bricks";
                } else if ((x + z) % 5 == 0) {
                    blockType = "chiseled_deepslate";
                } else if ((x % 3 == 0 && z % 3 == 0)) {
                    blockType = "reinforced_deepslate_tiles";
                } else {
                    blockType = "polished_deepslate";
                }

                blocks.add(new StructureBlock(x, 3, z, blockType, false));
            }
        }
    }
}