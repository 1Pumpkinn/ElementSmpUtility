package hs.elementSmpUtility.tasks;

import hs.elementSmpUtility.blocks.custom.PedestalBlock;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles rotating items displayed on pedestals
 */
public class PedestalRotationTask extends BukkitRunnable {

    private final JavaPlugin plugin;

    public PedestalRotationTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Rotate all pedestal display armor stands
        Bukkit.getWorlds().forEach(world -> {
            world.getEntitiesByClass(ArmorStand.class).forEach(stand -> {
                if (stand.hasMetadata("pedestal_display") && stand.isValid()) {
                    PedestalBlock.rotateDisplay(stand);
                }
            });
        });
    }

    /**
     * Start the rotation task
     */
    public void start() {
        // Run every tick (20 times per second) for smooth rotation
        this.runTaskTimer(plugin, 0L, 1L);
    }
}