package hs.elementSmpUtility.blocks.custom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;

/**
 * Handles pedestal block functionality - displays items hovering above with rotation
 */
public class PedestalBlock {

    private static final double HOVER_HEIGHT = 0.6;
    private static final String METADATA_KEY = "pedestal_display";

    /**
     * Create or update the display armor stand for a pedestal
     */
    public static ArmorStand createOrUpdateDisplay(Location pedestalLocation, ItemStack displayItem) {
        ArmorStand stand = getExistingDisplay(pedestalLocation);

        if (stand == null) {
            stand = createDisplay(pedestalLocation);
        }

        if (stand != null) {
            if (displayItem != null && displayItem.getType() != Material.AIR) {
                stand.getEquipment().setHelmet(displayItem);
            } else {
                stand.getEquipment().setHelmet(null);
            }
        }

        return stand;
    }

    /**
     * Create a new display armor stand
     */
    private static ArmorStand createDisplay(Location pedestalLocation) {
        // Spawn at EXACT center of block (0.5, 0.5) with no yaw offset
        Location spawnLoc = pedestalLocation.clone().add(0.5, HOVER_HEIGHT, 0.5);
        spawnLoc.setYaw(0); // Start at 0 degrees for consistent rotation
        spawnLoc.setPitch(0);

        Plugin plugin = Bukkit.getPluginManager().getPlugin("ElementSmpUtility");

        if (plugin == null) {
            return null;
        }

        ArmorStand stand = (ArmorStand) pedestalLocation.getWorld()
                .spawnEntity(spawnLoc, EntityType.ARMOR_STAND);

        // Configure armor stand for optimal performance and display
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        stand.setBasePlate(false);
        stand.setArms(false);
        stand.setSmall(false); // Changed to normal size for larger item display
        stand.setMarker(true); // No hitbox, no collisions
        stand.setCustomNameVisible(false);
        stand.setPersistent(true);
        stand.setCanPickupItems(false);
        stand.setCollidable(false);

        // Set head pose - perfectly upright for clean display
        stand.setHeadPose(new EulerAngle(0, 0, 0));

        // Add metadata to identify as pedestal display
        stand.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, true));

        return stand;
    }

    /**
     * Get existing display armor stand at a pedestal location
     */
    public static ArmorStand getExistingDisplay(Location pedestalLocation) {
        Location checkLoc = pedestalLocation.clone().add(0.5, HOVER_HEIGHT, 0.5);

        return pedestalLocation.getWorld().getNearbyEntities(checkLoc, 0.5, 0.5, 0.5).stream()
                .filter(entity -> entity instanceof ArmorStand)
                .map(entity -> (ArmorStand) entity)
                .filter(stand -> stand.hasMetadata(METADATA_KEY))
                .findFirst()
                .orElse(null);
    }

    /**
     * Remove the display armor stand
     */
    public static void removeDisplay(Location pedestalLocation) {
        ArmorStand stand = getExistingDisplay(pedestalLocation);
        if (stand != null) {
            stand.remove();
        }
    }

    /**
     * Get the item currently displayed on the pedestal
     */
    public static ItemStack getDisplayedItem(Location pedestalLocation) {
        ArmorStand stand = getExistingDisplay(pedestalLocation);
        if (stand != null) {
            ItemStack helmet = stand.getEquipment().getHelmet();
            return (helmet != null && helmet.getType() != Material.AIR) ? helmet : null;
        }
        return null;
    }

    /**
     * Rotate the display armor stand smoothly - rotation only around Y axis
     */
    public static void rotateDisplay(ArmorStand stand, float rotationSpeed) {
        if (stand != null && stand.isValid()) {
            Location loc = stand.getLocation();
            // Only modify yaw to keep it spinning in place at exact center
            loc.setYaw(loc.getYaw() + rotationSpeed);
            // Keep X and Z exactly at 0.5 to prevent drift
            loc.setX(Math.floor(loc.getX()) + 0.5);
            loc.setZ(Math.floor(loc.getZ()) + 0.5);
            stand.teleport(loc);
        }
    }
}