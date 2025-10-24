package hs.elementSmpUtility.structure.component;

import hs.elementSmpUtility.structure.TempleStructure.StructureBlock;
import java.util.List;

public class TreasureRoomBuilder extends StructureComponent {

    private final int startX, startZ, roomWidth, roomDepth;
    private final String side;

    public TreasureRoomBuilder(int width, int height, int depth,
                               int startX, int startZ, int roomWidth, int roomDepth, String side) {
        super(width, height, depth);
        this.startX = startX;
        this.startZ = startZ;
        this.roomWidth = roomWidth;
        this.roomDepth = roomDepth;
        this.side = side;
    }

    @Override
    public void build(List<StructureBlock> blocks) {
        // Build treasure room walls
        for (int y = 4; y <= 10; y++) {
            for (int x = startX; x < startX + roomWidth; x++) {
                for (int z = startZ; z < startZ + roomDepth; z++) {
                    boolean isWall = (x == startX || x == startX + roomWidth - 1 ||
                            z == startZ || z == startZ + roomDepth - 1);

                    if (isWall) {
                        blocks.add(new StructureBlock(x, y, z, "reinforced_deepslate_bricks", true));
                    } else if (y == 4) {
                        // Floor
                        blocks.add(new StructureBlock(x, y, z, "polished_deepslate", false));
                    }
                }
            }
        }

        // Add doorway
        int doorX = side.equals("left") ? startX + roomWidth - 1 : startX;
        int doorZ = startZ + roomDepth / 2;

        for (int y = 4; y <= 7; y++) {
            blocks.add(new StructureBlock(doorX, y, doorZ, "air", false));
            blocks.add(new StructureBlock(doorX, y, doorZ - 1, "air", false));
        }

        // Add treasure chest in center
        int chestX = startX + roomWidth / 2;
        int chestZ = startZ + roomDepth / 2;
        blocks.add(new StructureBlock(chestX, 4, chestZ, "chest", false));

        // Add decorative pedestals around chest
        blocks.add(new StructureBlock(chestX - 2, 4, chestZ, "polished_deepslate", false));
        blocks.add(new StructureBlock(chestX + 2, 4, chestZ, "polished_deepslate", false));
        blocks.add(new StructureBlock(chestX, 4, chestZ - 2, "polished_deepslate", false));
        blocks.add(new StructureBlock(chestX, 4, chestZ + 2, "polished_deepslate", false));

        // Add lanterns for lighting
        blocks.add(new StructureBlock(startX + 1, 8, startZ + 1, "lantern", false));
        blocks.add(new StructureBlock(startX + roomWidth - 2, 8, startZ + 1, "lantern", false));
        blocks.add(new StructureBlock(startX + 1, 8, startZ + roomDepth - 2, "lantern", false));
        blocks.add(new StructureBlock(startX + roomWidth - 2, 8, startZ + roomDepth - 2, "lantern", false));

        // Add ceiling
        for (int x = startX; x < startX + roomWidth; x++) {
            for (int z = startZ; z < startZ + roomDepth; z++) {
                blocks.add(new StructureBlock(x, 10, z, "reinforced_deepslate_tiles", true));
            }
        }

        // Add chains from ceiling
        for (int y = 9; y >= 5; y--) {
            blocks.add(new StructureBlock(chestX - 1, y, chestZ - 1, "chain", false));
            blocks.add(new StructureBlock(chestX + 1, y, chestZ - 1, "chain", false));
            blocks.add(new StructureBlock(chestX - 1, y, chestZ + 1, "chain", false));
            blocks.add(new StructureBlock(chestX + 1, y, chestZ + 1, "chain", false));
        }
    }
}