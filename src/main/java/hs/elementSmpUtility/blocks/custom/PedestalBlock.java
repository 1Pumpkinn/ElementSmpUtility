package hs.elementSmpUtility.blocks.custom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;

public class PedestalBlock {

    // Different hover heights for items vs blocks
    private static final double ITEM_HOVER_HEIGHT = 0.5;
    private static final double BLOCK_HOVER_HEIGHT = 0.6;

    // Different centering for items vs blocks
    private static final double ITEM_CENTER_X = 0.5;
    private static final double ITEM_CENTER_Y = -0.4;
    private static final double ITEM_CENTER_Z = 0.7;

    private static final double BLOCK_CENTER_X = 0.5;
    private static final double BLOCK_CENTER_Y = -0.2;
    private static final double BLOCK_CENTER_Z = 0.5;

    private static final String METADATA_KEY = "pedestal_display";

    /**
     * Creates or updates the ArmorStand display for the pedestal.
     */
    public static ArmorStand createOrUpdateDisplay(Location pedestalLocation, ItemStack displayItem) {
        ArmorStand stand = getExistingDisplay(pedestalLocation);

        if (stand == null) {
            stand = createDisplay(pedestalLocation, displayItem);
        } else if (displayItem != null && displayItem.getType() != Material.AIR) {
            // Update position if item type changed (block vs item)
            boolean isBlock = displayItem.getType().isBlock();
            Location newLoc = getCenteredLocation(pedestalLocation, isBlock);
            stand.teleport(newLoc);
        }

        if (stand != null) {
            if (displayItem != null && displayItem.getType() != Material.AIR) {
                stand.getEquipment().setHelmet(displayItem);
                stand.setGlowing(true);
                // Add light source at pedestal
                addGlowEffect(pedestalLocation, true);
            } else {
                stand.getEquipment().setHelmet(null);
                stand.setGlowing(false);
                // Remove light source
                addGlowEffect(pedestalLocation, false);
            }
        }

        return stand;
    }

    /**
     * Creates the armor stand used to display the item.
     */
    private static ArmorStand createDisplay(Location pedestalLocation, ItemStack displayItem) {
        boolean isBlock = displayItem != null && displayItem.getType().isBlock();
        Location spawnLoc = getCenteredLocation(pedestalLocation, isBlock);
        spawnLoc.setYaw(0);
        spawnLoc.setPitch(0);

        Plugin plugin = Bukkit.getPluginManager().getPlugin("ElementSmpUtility");
        if (plugin == null) {
            return null;
        }

        ArmorStand stand = (ArmorStand) pedestalLocation.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);

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

        // Make the armor stand glow
        stand.setGlowing(true);

        // Center the head pose
        stand.setHeadPose(new EulerAngle(0, 0, 0));

        stand.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, true));

        return stand;
    }

    /**
     * Returns an existing armor stand for the pedestal if one exists.
     */
    public static ArmorStand getExistingDisplay(Location pedestalLocation) {
        Location checkLoc = getCenteredLocation(pedestalLocation, false);

        return pedestalLocation.getWorld().getNearbyEntities(checkLoc, 0.8, 0.8, 0.8).stream()
                .filter(entity -> entity instanceof ArmorStand)
                .map(entity -> (ArmorStand) entity)
                .filter(s -> s.hasMetadata(METADATA_KEY))
                .findFirst()
                .orElse(null);
    }

    /**
     * Removes the armor stand display from the pedestal.
     */
    public static void removeDisplay(Location pedestalLocation) {
        ArmorStand stand = getExistingDisplay(pedestalLocation);
        if (stand != null) {
            stand.remove();
        }
        addGlowEffect(pedestalLocation, false);
    }

    /**
     * Gets the item currently displayed on the pedestal.
     */
    public static ItemStack getDisplayedItem(Location pedestalLocation) {
        ArmorStand stand = getExistingDisplay(pedestalLocation);
        if (stand != null && stand.getEquipment() != null) {
            ItemStack helmet = stand.getEquipment().getHelmet();
            return (helmet != null && helmet.getType() != Material.AIR) ? helmet : null;
        }
        return null;
    }

    /**
     * Get the centered location for the armor stand display.
     * Uses different positioning for blocks vs items.
     */
    private static Location getCenteredLocation(Location pedestalLocation, boolean isBlock) {
        if (isBlock) {
            return pedestalLocation.clone().add(
                    BLOCK_CENTER_X,
                    BLOCK_HOVER_HEIGHT + BLOCK_CENTER_Y,
                    BLOCK_CENTER_Z
            );
        } else {
            return pedestalLocation.clone().add(
                    ITEM_CENTER_X,
                    ITEM_HOVER_HEIGHT + ITEM_CENTER_Y,
                    ITEM_CENTER_Z
            );
        }
    }

    /**
     * Adds or removes a light block beneath the pedestal for a glowing effect.
     */
    private static void addGlowEffect(Location pedestalLocation, boolean add) {
        // Place light source one block ABOVE the pedestal
        Location lightLocation = pedestalLocation.clone().add(0, 1, 0);
        Block lightBlock = lightLocation.getBlock();

        if (add) {
            if (lightBlock.getType() != Material.LIGHT) {
                lightBlock.setType(Material.LIGHT);

                if (lightBlock.getBlockData() instanceof org.bukkit.block.data.type.Light light) {
                    light.setLevel(15);
                    lightBlock.setBlockData(light);
                }
            }
        } else {
            if (lightBlock.getType() == Material.LIGHT) {
                lightBlock.setType(Material.AIR);
            }
        }
    }
}
