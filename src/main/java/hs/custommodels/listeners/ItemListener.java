package hs.custommodels.listeners;

import hs.custommodels.managers.ModelManager;
import hs.custommodels.model.CustomModel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles item interactions for custom models
 */
public class ItemListener implements Listener {

    private final ModelManager modelManager;

    public ItemListener(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        String modelId = modelManager.getModelId(item);
        if (modelId == null) {
            return;
        }

        CustomModel model = modelManager.getModel(modelId);
        if (model == null) {
            return;
        }

        // Add custom interaction logic here based on model type
        // For example, special abilities for TOOL type models
    }
}