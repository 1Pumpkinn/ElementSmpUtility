package hs.elementSmpUtility.structure.component;

import hs.elementSmpUtility.structure.TempleStructure.StructureBlock;
import java.util.List;

public class RoofBuilder extends StructureComponent {

    public RoofBuilder(int width, int height, int depth) {
        super(width, height, depth);
    }

    @Override
    public void build(List<StructureBlock> blocks) {
        // Build a stepped pyramid roof
        int currentWidth = width;
        int currentDepth = depth;
        int offsetX = 0;
        int offsetZ = 0;

        // Build layers from y=17 to y=24
        for (int y = 17; y <= 24; y++) {
            // Calculate stepped inset
            int inset = (y - 17) * 2;
            offsetX = inset;
            offsetZ = inset;
            currentWidth = width - (inset * 2);
            currentDepth = depth - (inset * 2);

            if (currentWidth <= 0 || currentDepth <= 0) {
                break;
            }

            for (int x = offsetX; x < offsetX + currentWidth; x++) {
                for (int z = offsetZ; z < offsetZ + currentDepth; z++) {
                    String blockType;

                    // Create pattern on roof
                    boolean isEdge = (x == offsetX || x == offsetX + currentWidth - 1 ||
                            z == offsetZ || z == offsetZ + currentDepth - 1);

                    if (isEdge) {
                        blockType = "reinforced_deepslate_bricks";
                    } else if ((x + z + y) % 5 == 0) {
                        blockType = "chiseled_deepslate";
                    } else {
                        blockType = "reinforced_deepslate_tiles";
                    }

                    blocks.add(new StructureBlock(x, y, z, blockType, false));
                }
            }

            // Add corner decorations at each level
            if (y % 2 == 1 && currentWidth > 4 && currentDepth > 4) {
                // Add lanterns at corners
                blocks.add(new StructureBlock(offsetX, y, offsetZ, "lantern", false));
                blocks.add(new StructureBlock(offsetX + currentWidth - 1, y, offsetZ, "lantern", false));
                blocks.add(new StructureBlock(offsetX, y, offsetZ + currentDepth - 1, "lantern", false));
                blocks.add(new StructureBlock(offsetX + currentWidth - 1, y, offsetZ + currentDepth - 1, "lantern", false));
            }
        }

        // Add peak ornament
        int peakX = width / 2;
        int peakZ = depth / 2;
        int peakY = 25;

        // Create small pyramid peak
        for (int dy = 0; dy < 3; dy++) {
            int size = 3 - dy;
            for (int dx = -size; dx <= size; dx++) {
                for (int dz = -size; dz <= size; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) <= size) {
                        String blockType = dy == 2 ? "chiseled_deepslate" : "reinforced_deepslate_altar";
                        blocks.add(new StructureBlock(peakX + dx, peakY + dy, peakZ + dz, blockType, false));
                    }
                }
            }
        }

        // Add final capstone
        blocks.add(new StructureBlock(peakX, peakY + 3, peakZ, "reinforced_deepslate_altar", false));
    }
}