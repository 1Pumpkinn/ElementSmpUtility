package hs.elementSmpUtility.structure.component;

import hs.elementSmpUtility.structure.TempleStructure.StructureBlock;
import java.util.List;

public class HallwayBuilder extends StructureComponent {

    private final int startX, startZ, endX, endZ;
    private final String direction;

    public HallwayBuilder(int width, int height, int depth,
                          int startX, int startZ, int endX, int endZ, String direction) {
        super(width, height, depth);
        this.startX = startX;
        this.startZ = startZ;
        this.endX = endX;
        this.endZ = endZ;
        this.direction = direction;
    }

    @Override
    public void build(List<StructureBlock> blocks) {
        for (int y = 3; y <= 9; y++) {
            for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
                for (int z = Math.min(startZ, endZ); z <= Math.max(startZ, endZ); z++) {
                    if (y == 3) {
                        blocks.add(new StructureBlock(x, y, z, "polished_deepslate", false));
                    } else if (y == 9) {
                        blocks.add(new StructureBlock(x, y, z, "reinforced_deepslate_tiles", false));
                    } else {
                        boolean isWall = (direction.equals("horizontal") && (z == Math.min(startZ, endZ) || z == Math.max(startZ, endZ))) ||
                                (direction.equals("vertical") && (x == Math.min(startX, endX) || x == Math.max(startX, endX)));

                        if (isWall) {
                            blocks.add(new StructureBlock(x, y, z, "reinforced_deepslate_bricks", false));
                        }
                    }
                }
            }
        }

        // Add lighting
        if (direction.equals("horizontal")) {
            for (int x = startX + 3; x <= endX - 3; x += 4) {
                blocks.add(new StructureBlock(x, 7, startZ, "lantern", false));
                blocks.add(new StructureBlock(x, 7, endZ, "lantern", false));
            }
        } else {
            for (int z = startZ + 3; z <= endZ - 3; z += 4) {
                blocks.add(new StructureBlock(startX, 7, z, "lantern", false));
                blocks.add(new StructureBlock(endX, 7, z, "lantern", false));
            }
        }
    }
}
