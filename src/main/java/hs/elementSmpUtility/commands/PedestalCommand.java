package hs.elementSmpUtility.commands;

import hs.elementSmpUtility.storage.pedestal.PedestalDataStorage;
import hs.elementSmpUtility.storage.pedestal.PedestalOwnerStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Admin command for managing pedestals
 */
public class PedestalCommand implements CommandExecutor, TabCompleter {

    private final PedestalDataStorage storage;
    private final PedestalOwnerStorage ownerStorage;

    public PedestalCommand(PedestalDataStorage storage, PedestalOwnerStorage ownerStorage) {
        this.storage = storage;
        this.ownerStorage = ownerStorage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("elementsmp.pedestal.admin")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!")
                    .color(TextColor.color(0xFF5555)));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "info":
                handleInfo(sender);
                break;

            case "reload":
                handleReload(sender);
                break;

            case "check":
                handleCheck(sender);
                break;

            case "transfer":
                handleTransfer(sender, args);
                break;

            default:
                sendUsage(sender);
                break;
        }

        return true;
    }

    private void handleInfo(CommandSender sender) {
        int count = storage.getYmlStorage().getPedestalCount();

        sender.sendMessage(Component.text("=== Pedestal Storage Info ===")
                .color(TextColor.color(0x55FFFF)));
        sender.sendMessage(Component.text("Total pedestals in YAML: " + count)
                .color(TextColor.color(0xFFFFFF)));
        sender.sendMessage(Component.text("Storage: Dual (Chunk PDC + YAML backup)")
                .color(TextColor.color(0x55FF55)));
        sender.sendMessage(Component.text("YAML file: plugins/ElementSmpUtility/pedestals.yml")
                .color(TextColor.color(0xAAAAAA)));
        sender.sendMessage(Component.text("Owner storage: Dual (Chunk PDC + YAML backup)")
                .color(TextColor.color(0x55FF55)));
    }

    private void handleReload(CommandSender sender) {
        storage.getYmlStorage().reload();

        sender.sendMessage(Component.text("Pedestal data reloaded from YAML!")
                .color(TextColor.color(0x55FF55)));
        sender.sendMessage(Component.text("Found " + storage.getYmlStorage().getPedestalCount() + " pedestals")
                .color(TextColor.color(0xFFFFFF)));
    }

    private void handleCheck(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!")
                    .color(TextColor.color(0xFF5555)));
            return;
        }

        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            player.sendMessage(Component.text("You must be looking at a pedestal!")
                    .color(TextColor.color(0xFF5555)));
            return;
        }

        Location loc = targetBlock.getLocation();
        UUID owner = ownerStorage.getOwner(loc);

        if (owner == null) {
            player.sendMessage(Component.text("This block is not a pedestal!")
                    .color(TextColor.color(0xFF5555)));
            return;
        }

        String ownerName = ownerStorage.getOwnerName(loc);
        player.sendMessage(Component.text("Pedestal Owner: " + ownerName)
                .color(TextColor.color(0x55FFFF)));
        player.sendMessage(Component.text("UUID: " + owner.toString())
                .color(TextColor.color(0xAAAAAA)));
    }

    private void handleTransfer(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!")
                    .color(TextColor.color(0xFF5555)));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /pedestal transfer <player>")
                    .color(TextColor.color(0xFF5555)));
            return;
        }

        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            player.sendMessage(Component.text("You must be looking at a pedestal!")
                    .color(TextColor.color(0xFF5555)));
            return;
        }

        Location loc = targetBlock.getLocation();
        UUID currentOwner = ownerStorage.getOwner(loc);

        if (currentOwner == null) {
            player.sendMessage(Component.text("This block is not a pedestal!")
                    .color(TextColor.color(0xFF5555)));
            return;
        }

        // Check if player is owner or has bypass permission
        if (!currentOwner.equals(player.getUniqueId()) && !player.hasPermission("elementsmp.pedestal.bypass")) {
            player.sendMessage(Component.text("You don't own this pedestal!")
                    .color(TextColor.color(0xFF5555)));
            return;
        }

        OfflinePlayer newOwner = Bukkit.getOfflinePlayer(args[1]);
        if (!newOwner.hasPlayedBefore() && !newOwner.isOnline()) {
            player.sendMessage(Component.text("Player not found!")
                    .color(TextColor.color(0xFF5555)));
            return;
        }

        ownerStorage.setOwner(loc, newOwner.getUniqueId());
        player.sendMessage(Component.text("Pedestal transferred to " + newOwner.getName())
                .color(TextColor.color(0x55FF55)));
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("=== Pedestal Admin Commands ===")
                .color(TextColor.color(0x55FFFF)));
        sender.sendMessage(Component.text("/pedestal info - View storage information")
                .color(TextColor.color(0xFFFFFF)));
        sender.sendMessage(Component.text("/pedestal reload - Reload from YAML backup")
                .color(TextColor.color(0xFFFFFF)));
        sender.sendMessage(Component.text("/pedestal check - Check pedestal owner")
                .color(TextColor.color(0xFFFFFF)));
        sender.sendMessage(Component.text("/pedestal transfer <player> - Transfer ownership")
                .color(TextColor.color(0xFFFFFF)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("info");
            completions.add("reload");
            completions.add("check");
            completions.add("transfer");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("transfer")) {
            // Add online player names
            Bukkit.getOnlinePlayers().forEach(p -> completions.add(p.getName()));
        }

        return completions;
    }
}