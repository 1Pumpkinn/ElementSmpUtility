package hs.elementSmpUtility.structure;

import hs.elementSmpUtility.structure.component.*;
import java.util.ArrayList;
import java.util.List;

public class TempleStructure {

    private final List<StructureBlock> blocks;
    private final int width;
    private final int height;
    private final int depth;

    public TempleStructure() {
        this.width = 45;
        this.height = 25;
        this.depth = 45;
        this.blocks = new ArrayList<>();

        generateTempleLayout();
    }

    private void generateTempleLayout() {
        List<StructureComponent> components = List.of(
                new FoundationBuilder(width, height, depth),
                new WallBuilder(width, height, depth),
                new EntranceBuilder(width, height, depth),
                new MainHallBuilder(width, height, depth),
                new HallwayBuilder(width, height, depth, 5, depth/2 - 2, 12, depth/2 + 2, "horizontal"),
                new HallwayBuilder(width, height, depth, width - 17, depth/2 - 2, width - 5, depth/2 + 2, "horizontal"),
                new TreasureRoomBuilder(width, height, depth, 6, depth/2 - 6, 8, 8, "left"),
                new InnerSanctumBuilder(width, height, depth),
                new CeilingBuilder(width, height, depth),
                new RoofBuilder(width, height, depth),
                new LightingBuilder(width, height, depth)
        );

        for (StructureComponent comp : components) {
            comp.build(blocks);
        }
    }

    public List<StructureBlock> getBlocks() {
        return new ArrayList<>(blocks);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getDepth() { return depth; }

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

        public int getX() { return x; }
        public int getY() { return y; }
        public int getZ() { return z; }
        public String getBlockType() { return blockType; }
        public boolean isUnbreakableCore() { return isUnbreakableCore; }
    }
}
