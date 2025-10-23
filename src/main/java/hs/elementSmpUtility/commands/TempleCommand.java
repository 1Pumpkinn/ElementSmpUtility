package hs.elementSmpUtility.commands;

import hs.elementSmpUtility.structure.StructurePlacement;
import hs.elementSmpUtility.structure.TempleRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to spawn temple structures
 * Allows unlimited temple spawning via command
 */
public class TempleCommand implements CommandExecutor, TabCompleter {

    private final StructurePlacement placement;
    private final TempleRegistry registry;

    public TempleCommand(StructurePlacement placement, TempleRegistry registry) {
        this.placement = placement;
        this.registry = registry;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!")
                    .color(TextColor.color(0xFF5555)));
            return true;
        }

        if (!player.hasPermission("elementsmp.temple")) {
            player.sendMessage(Component.text("You don't have permission to use this command!")
                    .color(TextColor.color(0xFF5555)));
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "spawn":
                handleSpawn(player, args);
                break;

            case "info":
                handleInfo(player);
                break;

            default:
                sendUsage(player);
                break;
        }

        return true;
    }

    private void handleSpawn(Player player, String[] args) {
        Location spawnLoc = player.getLocation();
        player.sendMessage(Component.text("Spawning temple...")
                .color(TextColor.color(0x55FF55)));

        // Spawn temple (no restrictions for command usage)
        if (placement.placeTemple(spawnLoc)) {
            registry.registerTemple(spawnLoc);
            player.sendMessage(Component.text("✔ Temple spawned successfully!")
                    .color(TextColor.color(0x55FF55)));
            player.sendMessage(Component.text("Location: X:" + spawnLoc.getBlockX() +
                            " Y:" + spawnLoc.getBlockY() + " Z:" + spawnLoc.getBlockZ())
                    .color(TextColor.color(0xAAAAAA)));
        } else {
            player.sendMessage(Component.text("✘ Failed to spawn temple!")
                    .color(TextColor.color(0xFF5555)));
        }
    }

    private void handleInfo(Player player) {
        String worldName = player.getWorld().getName();
        List<Location> temples = registry.getTemplesInWorld(worldName);

        player.sendMessage(Component.text("=== Temple Information ===")
                .color(TextColor.color(0x55FFFF)));

        player.sendMessage(Component.text("Temples in " + worldName + ": " + temples.size())
                .color(TextColor.color(0xFFFFFF)));

        if (!temples.isEmpty()) {
            player.sendMessage(Component.text("Locations:")
                    .color(TextColor.color(0xFFAA00)));

            for (int i = 0; i < temples.size(); i++) {
                Location loc = temples.get(i);
                player.sendMessage(Component.text(String.format("  %d. X:%d Y:%d Z:%d",
                                i + 1, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
                        .color(TextColor.color(0xAAAAAA)));
            }
        }
    }

    private void sendUsage(Player player) {
        player.sendMessage(Component.text("=== Temple Commands ===")
                .color(TextColor.color(0x55FFFF)));
        player.sendMessage(Component.text("/temple spawn - Spawn a temple at your location")
                .color(TextColor.color(0xFFFFFF)));
        player.sendMessage(Component.text("/temple info - View temple information")
                .color(TextColor.color(0xFFFFFF)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("spawn");
            completions.add("info");
        }

        return completions;
    }
}