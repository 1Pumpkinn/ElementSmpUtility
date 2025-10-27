package hs.elementSmpUtility.listeners.block;

import hs.elementSmpUtility.blocks.CustomBlockManager;
import hs.elementSmpUtility.blocks.CustomBlockType;
import hs.elementSmpUtility.blocks.custom.PedestalBlock;
import hs.elementSmpUtility.storage.BlockDataStorage;
import hs.elementSmpUtility.storage.pedestal.PedestalDataStorage;
import hs.elementSmpUtility.storage.pedestal.PedestalOwnerStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles breaking custom blocks and preventing unbreakable blocks from being broken
 */
public class BlockBreakListener implements Listener {

    private final CustomBlockManager blockManager;
    private final BlockDataStorage storage;
    private final PedestalDataStorage pedestalStorage;
    private final PedestalOwnerStorage ownerStorage;

    public BlockBreakListener(CustomBlockManager blockManager, BlockDataStorage storage,
                              PedestalDataStorage pedestalStorage, PedestalOwnerStorage ownerStorage) {
        this.blockManager = blockManager;
        this.storage = storage;
        this.pedestalStorage = pedestalStorage;
        this.ownerStorage = ownerStorage;
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
        Location location = event.getBlock().getLocation();

        // Handle pedestal breaking
        if ("pedestal".equals(blockId)) {
            // Check ownership - only owner can break (unless creative mode with bypass permission)
            if (!ownerStorage.isOwner(location, player.getUniqueId())) {
                if (player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("elementsmp.pedestal.bypass")) {
                    event.setCancelled(true);
                    String ownerName = ownerStorage.getOwnerName(location);
                    player.sendActionBar(
                            Component.text("This pedestal belongs to " + ownerName + "!")
                                    .color(TextColor.color(0xFF5555))
                    );
                    return;
                }
            }

            handlePedestalBreak(event);

            // Check if unbreakable
            if (blockType.isUnbreakable() && player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
                player.sendActionBar(
                        Component.text("This pedestal is unbreakable!")
                                .color(TextColor.color(0xFF5555))
                );
                return;
            }

            storage.removeCustomBlock(event.getBlock());
            return;
        }

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

    /**
     * Handle breaking a pedestal block - properly cleans up ALL armor stands
     */
    private void handlePedestalBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();

        // Get displayed item from storage
        ItemStack displayedItem = pedestalStorage.getPedestalItem(loc);

        // Log for debugging
        if (displayedItem != null) {
            blockManager.getPlugin().getLogger().info(
                    "Breaking pedestal at " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() +
                            " with item: " + displayedItem.getType()
            );
        }

        // CRITICAL: Remove ALL nearby armor stands with pedestal metadata
        PedestalBlock.removeAllDisplays(loc);

        // Verify removal
        ArmorStand remaining = PedestalBlock.getExistingDisplay(loc);
        if (remaining != null) {
            blockManager.getPlugin().getLogger().warning(
                    "Armor stand still exists after removal attempt! Force removing..."
            );
            remaining.remove();
        }

        // Drop displayed item if present
        if (displayedItem != null && displayedItem.getType() != Material.AIR) {
            event.getBlock().getWorld().dropItemNaturally(loc, displayedItem);
        }

        // Remove from pedestal storage
        pedestalStorage.savePedestalItem(loc, null);

        // Remove owner data
        ownerStorage.removeOwner(loc);

        blockManager.getPlugin().getLogger().info("Pedestal break cleanup complete");
    }
}