package hs.elementSmpUtility.structure.component;

import hs.elementSmpUtility.structure.TempleStructure.StructureBlock;
import java.util.List;

public class EntranceBuilder extends StructureComponent {

    public EntranceBuilder(int width, int height, int depth) {
        super(width, height, depth);
    }

    @Override
    public void build(List<StructureBlock> blocks) {
        int centerX = width / 2;
        int entranceWidth = 5;
        int entranceHeight = 8;

        // Create main entrance opening on the front (z = 0)
        for (int y = 4; y <= 4 + entranceHeight; y++) {
            for (int x = centerX - entranceWidth / 2; x <= centerX + entranceWidth / 2; x++) {
                blocks.add(new StructureBlock(x, y, 0, "air", false));
            }
        }

        // Create grand entrance pillars
        for (int y = 4; y <= 12; y++) {
            // Left pillar
            blocks.add(new StructureBlock(centerX - entranceWidth / 2 - 1, y, 0, "chiseled_deepslate", true));
            blocks.add(new StructureBlock(centerX - entranceWidth / 2 - 1, y, 1, "chiseled_deepslate", true));

            // Right pillar
            blocks.add(new StructureBlock(centerX + entranceWidth / 2 + 1, y, 0, "chiseled_deepslate", true));
            blocks.add(new StructureBlock(centerX + entranceWidth / 2 + 1, y, 1, "chiseled_deepslate", true));
        }

        // Add decorative arch over entrance
        int archY = 4 + entranceHeight;
        for (int x = centerX - entranceWidth / 2; x <= centerX + entranceWidth / 2; x++) {
            blocks.add(new StructureBlock(x, archY + 1, 0, "reinforced_deepslate_tiles", true));
            blocks.add(new StructureBlock(x, archY + 2, 0, "chiseled_deepslate", true));
        }

        // Add lanterns at entrance
        blocks.add(new StructureBlock(centerX - entranceWidth / 2 - 1, archY, 1, "lantern", false));
        blocks.add(new StructureBlock(centerX + entranceWidth / 2 + 1, archY, 1, "lantern", false));

        // Add chains from arch
        for (int y = archY - 1; y >= 6; y--) {
            blocks.add(new StructureBlock(centerX - 2, y, 1, "chain", false));
            blocks.add(new StructureBlock(centerX + 2, y, 1, "chain", false));
        }

        // Add entrance steps
        for (int z = 0; z <= 2; z++) {
            int stepY = 3 - z;
            if (stepY >= 1) {
                for (int x = centerX - entranceWidth / 2 - 2; x <= centerX + entranceWidth / 2 + 2; x++) {
                    blocks.add(new StructureBlock(x, stepY, z, "polished_deepslate", false));
                }
            }
        }
    }
}