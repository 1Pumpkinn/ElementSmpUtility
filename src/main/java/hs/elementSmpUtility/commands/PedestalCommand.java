package hs.elementSmpUtility.commands;

import hs.elementSmpUtility.storage.PedestalDataStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
 * Admin command for managing pedestals
 */
public class PedestalCommand implements CommandExecutor, TabCompleter {

    private final PedestalDataStorage storage;

    public PedestalCommand(PedestalDataStorage storage) {
        this.storage = storage;
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
    }

    private void handleReload(CommandSender sender) {
        storage.getYmlStorage().reload();

        sender.sendMessage(Component.text("Pedestal data reloaded from YAML!")
                .color(TextColor.color(0x55FF55)));
        sender.sendMessage(Component.text("Found " + storage.getYmlStorage().getPedestalCount() + " pedestals")
                .color(TextColor.color(0xFFFFFF)));
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("=== Pedestal Admin Commands ===")
                .color(TextColor.color(0x55FFFF)));
        sender.sendMessage(Component.text("/pedestal info - View storage information")
                .color(TextColor.color(0xFFFFFF)));
        sender.sendMessage(Component.text("/pedestal reload - Reload from YAML backup")
                .color(TextColor.color(0xFFFFFF)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("info");
            completions.add("reload");
        }

        return completions;
    }
}