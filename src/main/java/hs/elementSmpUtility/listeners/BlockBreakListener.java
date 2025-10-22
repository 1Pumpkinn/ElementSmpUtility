package hs.elementSmpUtility.listeners;

import hs.elementSmpUtility.blocks.CustomBlockManager;
import hs.elementSmpUtility.blocks.CustomBlockType;
import hs.elementSmpUtility.storage.BlockDataStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Handles breaking custom blocks and preventing unbreakable blocks from being broken
 */
public class BlockBreakListener implements Listener {

    private final CustomBlockManager blockManager;
    private final BlockDataStorage storage;

    public BlockBreakListener(CustomBlockManager blockManager, BlockDataStorage storage) {
        this.blockManager = blockManager;
        this.storage = storage;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        String blockId = storage.getCustomBlockId(event.getBlock());
        if (blockId == null) {
            return;
        }

        CustomBlockType blockType = blockManager.getBlockType(blockId);
        if (blockType == null) {
            return;
        }

        Player player = event.getPlayer();

        // Check if block is unbreakable
        if (blockType.isUnbreakable()) {
            // Allow creative mode players to break
            if (player.getGameMode() == GameMode.CREATIVE) {
                storage.removeCustomBlock(event.getBlock());
                return;
            }

            // Cancel break for survival players
            event.setCancelled(true);
            player.sendActionBar(
                    Component.text("This block is unbreakable!")
                            .color(TextColor.color(0xFF5555))
            );
            return;
        }

        // If breakable, remove from storage
        storage.removeCustomBlock(event.getBlock());
    }
}