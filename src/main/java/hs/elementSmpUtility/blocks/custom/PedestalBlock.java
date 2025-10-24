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

public class PedestalBlock {

    private static final double HOVER_HEIGHT = 0.25;
    private static final String METADATA_KEY = "pedestal_display";

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

    private static ArmorStand createDisplay(Location pedestalLocation) {
        Location spawnLoc = pedestalLocation.clone().add(0.5, HOVER_HEIGHT, 0.5);
        spawnLoc.setYaw(0);
        spawnLoc.setPitch(0);

        Plugin plugin = Bukkit.getPluginManager().getPlugin("ElementSmpUtility");

        if (plugin == null) {
            return null;
        }

        ArmorStand stand = (ArmorStand) pedestalLocation.getWorld()
                .spawnEntity(spawnLoc, EntityType.ARMOR_STAND);

        stand.setVisible(false);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        stand.setBasePlate(false);
        stand.setArms(false);
        stand.setSmall(true);
        stand.setMarker(true);
        stand.setCustomNameVisible(false);
        stand.setPersistent(true);
        stand.setCanPickupItems(false);
        stand.setCollidable(false);

        stand.setHeadPose(new EulerAngle(0, 0, 0));

        stand.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, true));

        return stand;
    }

    public static ArmorStand getExistingDisplay(Location pedestalLocation) {
        Location checkLoc = pedestalLocation.clone().add(0.5, HOVER_HEIGHT, 0.5);

        return pedestalLocation.getWorld().getNearbyEntities(checkLoc, 0.5, 0.5, 0.5).stream()
                .filter(entity -> entity instanceof ArmorStand)
                .map(entity -> (ArmorStand) entity)
                .filter(stand -> stand.hasMetadata(METADATA_KEY))
                .findFirst()
                .orElse(null);
    }

    public static void removeDisplay(Location pedestalLocation) {
        ArmorStand stand = getExistingDisplay(pedestalLocation);
        if (stand != null) {
            stand.remove();
        }
    }

    public static ItemStack getDisplayedItem(Location pedestalLocation) {
        ArmorStand stand = getExistingDisplay(pedestalLocation);
        if (stand != null) {
            ItemStack helmet = stand.getEquipment().getHelmet();
            return (helmet != null && helmet.getType() != Material.AIR) ? helmet : null;
        }
        return null;
    }

    public static void rotateDisplay(ArmorStand stand, float rotationSpeed) {
        if (stand != null && stand.isValid()) {
            Location loc = stand.getLocation();
            loc.setYaw(loc.getYaw() + rotationSpeed);
            loc.setX(Math.floor(loc.getX()) + 0.5);
            loc.setZ(Math.floor(loc.getZ()) + 0.5);
            stand.teleport(loc);
        }
    }
}