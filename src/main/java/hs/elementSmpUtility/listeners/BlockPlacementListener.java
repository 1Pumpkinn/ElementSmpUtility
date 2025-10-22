package hs.elementSmpUtility.listeners;

import hs.elementSmpUtility.blocks.CustomBlockManager;
import hs.elementSmpUtility.storage.BlockDataStorage;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Handles placing custom blocks and storing their data
 */
public class BlockPlacementListener implements Listener {

    private final CustomBlockManager blockManager;
    private final BlockDataStorage storage;

    public BlockPlacementListener(CustomBlockManager blockManager, BlockDataStorage storage) {
        this.blockManager = blockManager;
        this.storage = storage;
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

        // Store custom block data using the storage manager
        storage.saveCustomBlock(block, blockId);
    }
}