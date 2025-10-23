package hs.elementSmpUtility.listeners;

import hs.elementSmpUtility.blocks.CustomBlockManager;
import hs.elementSmpUtility.blocks.custom.PedestalBlock;
import hs.elementSmpUtility.storage.BlockDataStorage;
import hs.elementSmpUtility.storage.PedestalDataStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Handles player interactions with pedestal blocks
 */
public class PedestalInteractionListener implements Listener {

    private final CustomBlockManager blockManager;
    private final BlockDataStorage blockStorage;
    private final PedestalDataStorage pedestalStorage;

    public PedestalInteractionListener(CustomBlockManager blockManager,
                                       BlockDataStorage blockStorage,
                                       PedestalDataStorage pedestalStorage) {
        this.blockManager = blockManager;
        this.blockStorage = blockStorage;
        this.pedestalStorage = pedestalStorage;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPedestalInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        // Check if it's a pedestal block
        String blockId = blockStorage.getCustomBlockId(block);
        if (blockId == null || !blockId.equals("pedestal")) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        // Get current displayed item
        ItemStack currentItem = pedestalStorage.getPedestalItem(block.getLocation());

        // Auto-restore display if it's missing but should exist
        if (currentItem != null) {
            ArmorStand existing = PedestalBlock.getExistingDisplay(block.getLocation());
            if (existing == null || !existing.isValid()) {
                // Display is missing, restore it
                PedestalBlock.createOrUpdateDisplay(block.getLocation(), currentItem);
            }
        }

        if (player.isSneaking()) {
            // Sneaking = Remove item from pedestal
            if (currentItem != null) {
                // Give item back to player
                player.getInventory().addItem(currentItem.clone());

                // Remove from pedestal
                pedestalStorage.savePedestalItem(block.getLocation(), null);
                PedestalBlock.removeDisplay(block.getLocation());

                player.playSound(block.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
                player.sendActionBar(Component.text("Removed item from pedestal")
                        .color(TextColor.color(0x55FF55)));
            } else {
                player.sendActionBar(Component.text("Pedestal is empty")
                        .color(TextColor.color(0xFF5555)));
            }
        } else {
            // Not sneaking = Place item on pedestal
            if (heldItem.getType() == Material.AIR) {
                if (currentItem != null) {
                    player.sendActionBar(Component.text("Sneak + Right Click to remove item")
                            .color(TextColor.color(0xFFAA00)));
                } else {
                    player.sendActionBar(Component.text("Hold an item to place it on the pedestal")
                            .color(TextColor.color(0xFFAA00)));
                }
                return;
            }

            if (currentItem != null) {
                player.sendActionBar(Component.text("Pedestal already has an item! Sneak to remove it first.")
                        .color(TextColor.color(0xFF5555)));
                return;
            }

            // Take one item from player's hand
            ItemStack toPlace = heldItem.clone();
            toPlace.setAmount(1);

            heldItem.setAmount(heldItem.getAmount() - 1);

            // Save to storage and create display
            pedestalStorage.savePedestalItem(block.getLocation(), toPlace);
            PedestalBlock.createOrUpdateDisplay(block.getLocation(), toPlace);

            player.playSound(block.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.5f);
            player.sendActionBar(Component.text("Placed item on pedestal")
                    .color(TextColor.color(0x55FF55)));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPedestalBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        String blockId = blockStorage.getCustomBlockId(block);

        if (blockId == null || !blockId.equals("pedestal")) {
            return;
        }

        // Get displayed item
        ItemStack displayedItem = pedestalStorage.getPedestalItem(block.getLocation());

        // Remove display armor stand
        PedestalBlock.removeDisplay(block.getLocation());

        // Drop displayed item
        if (displayedItem != null) {
            block.getWorld().dropItemNaturally(block.getLocation(), displayedItem);
        }

        // Remove from storage
        pedestalStorage.savePedestalItem(block.getLocation(), null);
    }

    /**
     * Prevent players from damaging pedestal armor stands
     */
    @EventHandler
    public void onArmorStandDamage(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ArmorStand stand)) {
            return;
        }

        if (stand.hasMetadata("pedestal_display")) {
            event.setCancelled(true);
        }
    }
}