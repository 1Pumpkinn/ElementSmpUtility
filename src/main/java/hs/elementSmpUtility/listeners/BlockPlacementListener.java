package hs.elementSmpUtility.listeners;

import hs.elementSmpUtility.blocks.CustomBlockManager;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Handles placing custom blocks and storing their data
 */
public class BlockPlacementListener implements Listener {

    private final CustomBlockManager blockManager;

    public BlockPlacementListener(CustomBlockManager blockManager) {
        this.blockManager = blockManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        String blockId = blockManager.getCustomBlockId(event.getItemInHand());
        if (blockId == null) {
            return;
        }

        Block block = event.getBlockPlaced();

        // Store custom block data in the block's chunk
        block.getChunk().getPersistentDataContainer().set(
                blockManager.getCustomBlockKey(),
                PersistentDataType.STRING,
                block.getX() + "," + block.getY() + "," + block.getZ() + ":" + blockId
        );
    }
}