package hs.custommodels.listeners;

import hs.custommodels.managers.ModelManager;
import hs.custommodels.model.CustomModel;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles block placement and breaking for custom models
 */
public class BlockListener implements Listener {

    private final ModelManager modelManager;

    public BlockListener(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        ItemStack item = event.getItemInHand();
        String modelId = modelManager.getModelId(item);

        if (modelId == null) {
            return;
        }

        CustomModel model = modelManager.getModel(modelId);
        if (model == null) {
            return;
        }

        // Allow placement for BLOCK type models
        if (model.getType() == CustomModel.ModelType.BLOCK) {
            // Block can be placed normally
            // Additional custom logic can be added here
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Block block = event.getBlock();
        Player player = event.getPlayer();

        // Check if the block has custom model data stored
        // This is a basic implementation - you can enhance with PDC storage
        // similar to your ElementSmpUtility plugin
    }
}