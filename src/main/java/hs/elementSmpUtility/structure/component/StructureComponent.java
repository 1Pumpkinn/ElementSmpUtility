package hs.elementSmpUtility.structure.component;

import hs.elementSmpUtility.structure.TempleStructure.StructureBlock;
import java.util.List;

public abstract class StructureComponent {

    protected final int width;
    protected final int height;
    protected final int depth;

    public StructureComponent(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    /**
     * Generate structure blocks for this component.
     * @param blocks The master list of structure blocks to append to.
     */
    public abstract void build(List<StructureBlock> blocks);
}
