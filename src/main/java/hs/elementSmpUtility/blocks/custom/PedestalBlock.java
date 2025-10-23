package hs.elementSmpUtility.blocks.custom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

/**
 * Handles pedestal block functionality - displays items hovering above
 */
public class PedestalBlock {

    private static final double HOVER_HEIGHT = 1.2;
    private static final double ROTATION_SPEED = 0.05; // Radians per tick

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
                stand.setCustomName(null);
                stand.setCustomNameVisible(false);
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
        Location spawnLoc = pedestalLocation.clone().add(0.5, HOVER_HEIGHT, 0.5);

        ArmorStand stand = (ArmorStand) pedestalLocation.getWorld()
                .spawnEntity(spawnLoc, EntityType.ARMOR_STAND);

        // Configure armor stand
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        stand.setBasePlate(false);
        stand.setArms(false);
        stand.setSmall(false);
        stand.setMarker(true); // Makes it not have a hitbox
        stand.setCustomNameVisible(false);
        stand.setPersistent(true);

        // Set head pose for better display
        stand.setHeadPose(new EulerAngle(0, 0, 0));

        // Add metadata to identify it as a pedestal display
        stand.setMetadata("pedestal_display",
                new org.bukkit.metadata.FixedMetadataValue(
                        org.bukkit.Bukkit.getPluginManager().getPlugin("ElementSmpUtility"),
                        true));

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
                .filter(stand -> stand.hasMetadata("pedestal_display"))
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
     * Rotate the display armor stand (call from a repeating task)
     */
    public static void rotateDisplay(ArmorStand stand) {
        if (stand != null && stand.isValid()) {
            Location loc = stand.getLocation();
            float newYaw = loc.getYaw() + (float) Math.toDegrees(ROTATION_SPEED);
            loc.setYaw(newYaw);
            stand.teleport(loc);
        }
    }

    /**
     * Check if a location has a pedestal block
     */
    public static boolean isPedestalBlock(Location location) {
        return location.getBlock().getType() == Material.STONE; // Pedestal base material
        // TODO: Add custom block check through BlockDataStorage
    }
}