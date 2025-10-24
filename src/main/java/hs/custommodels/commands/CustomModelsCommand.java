package hs.custommodels.commands;

import hs.custommodels.CustomModels;
import hs.custommodels.model.CustomModel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main command handler for CustomModels plugin
 */
public class CustomModelsCommand implements CommandExecutor, TabCompleter {

    private final CustomModels plugin;

    public CustomModelsCommand(CustomModels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "give":
                if (!sender.hasPermission("custommodels.give")) {
                    plugin.sendError(sender, "No permission!");
                    return true;
                }
                handleGive(sender, args);
                break;

            case "list":
                if (!sender.hasPermission("custommodels.list")) {
                    plugin.sendError(sender, "No permission!");
                    return true;
                }
                handleList(sender);
                break;

            case "reload":
                if (!sender.hasPermission("custommodels.reload")) {
                    plugin.sendError(sender, "No permission!");
                    return true;
                }
                handleReload(sender);
                break;

            case "generate":
                if (!sender.hasPermission("custommodels.generate")) {
                    plugin.sendError(sender, "No permission!");
                    return true;
                }
                handleGenerate(sender);
                break;

            case "info":
                if (!sender.hasPermission("custommodels.info")) {
                    plugin.sendError(sender, "No permission!");
                    return true;
                }
                handleInfo(sender, args);
                break;

            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.sendError(sender, "Only players can use this command!");
            return;
        }

        if (args.length < 2) {
            plugin.sendError(sender, "Usage: /cm give <model> [amount]");
            return;
        }

        String modelId = args[1];
        int amount = 1;

        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount < 1 || amount > 64) {
                    plugin.sendError(sender, "Amount must be between 1 and 64!");
                    return;
                }
            } catch (NumberFormatException e) {
                plugin.sendError(sender, "Invalid amount!");
                return;
            }
        }

        ItemStack item = plugin.getModelManager().createItem(modelId, amount);
        if (item == null) {
            plugin.sendError(sender, "Unknown model: " + modelId);
            return;
        }

        player.getInventory().addItem(item);
        plugin.sendSuccess(sender, "Given " + amount + "x " + modelId);
    }

    private void handleList(CommandSender sender) {
        sender.sendMessage(Component.text("=== Custom Models ===")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));

        List<CustomModel> models = new ArrayList<>(plugin.getModelManager().getAllModels().values());
        models.sort(Comparator.comparing(CustomModel::getId));

        if (models.isEmpty()) {
            sender.sendMessage(Component.text("No models loaded!")
                    .color(NamedTextColor.RED));
            return;
        }

        for (CustomModel model : models) {
            Component line = Component.text("• ", NamedTextColor.GRAY)
                    .append(Component.text(model.getId(), NamedTextColor.WHITE))
                    .append(Component.text(" (", NamedTextColor.DARK_GRAY))
                    .append(Component.text(model.getType().name(), NamedTextColor.YELLOW))
                    .append(Component.text(", ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("CMD: " + model.getCustomModelData(), NamedTextColor.AQUA))
                    .append(Component.text(")", NamedTextColor.DARK_GRAY));
            sender.sendMessage(line);
        }

        sender.sendMessage(Component.text("Total: " + models.size() + " models")
                .color(NamedTextColor.GREEN));
    }

    private void handleReload(CommandSender sender) {
        plugin.sendMessage(sender, "Reloading configuration...");

        plugin.reloadConfig();
        plugin.getModelManager().loadModels();

        plugin.sendSuccess(sender, "Reloaded! " + plugin.getModelManager().getModelCount() + " models loaded");
    }

    private void handleGenerate(CommandSender sender) {
        plugin.sendMessage(sender, "Generating resource pack...");

        if (plugin.getResourcePackManager().generateResourcePack()) {
            plugin.sendSuccess(sender, "Resource pack generated successfully!");
            sender.sendMessage(Component.text("Location: ", NamedTextColor.GRAY)
                    .append(Component.text(plugin.getResourcePackManager().getResourcePackFile().getAbsolutePath(),
                            NamedTextColor.WHITE)));
        } else {
            plugin.sendError(sender, "Failed to generate resource pack! Check console for errors.");
        }
    }

    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.sendError(sender, "Usage: /cm info <model>");
            return;
        }

        String modelId = args[1];
        CustomModel model = plugin.getModelManager().getModel(modelId);

        if (model == null) {
            plugin.sendError(sender, "Unknown model: " + modelId);
            return;
        }

        sender.sendMessage(Component.text("=== Model Info: " + modelId + " ===")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));

        sender.sendMessage(Component.text("Type: ", NamedTextColor.GRAY)
                .append(Component.text(model.getType().name(), NamedTextColor.WHITE)));

        sender.sendMessage(Component.text("Base Material: ", NamedTextColor.GRAY)
                .append(Component.text(model.getBaseMaterial().name(), NamedTextColor.WHITE)));

        sender.sendMessage(Component.text("Custom Model Data: ", NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(model.getCustomModelData()), NamedTextColor.AQUA)));

        sender.sendMessage(Component.text("Display Name: ", NamedTextColor.GRAY)
                .append(model.getDisplayName()));

        if (!model.getLore().isEmpty()) {
            sender.sendMessage(Component.text("Lore Lines: " + model.getLore().size(), NamedTextColor.GRAY));
        }

        if (!model.getModelPath().isEmpty()) {
            sender.sendMessage(Component.text("Model Path: ", NamedTextColor.GRAY)
                    .append(Component.text(model.getModelPath(), NamedTextColor.WHITE)));
        }

        if (!model.getTexturePath().isEmpty()) {
            sender.sendMessage(Component.text("Texture Path: ", NamedTextColor.GRAY)
                    .append(Component.text(model.getTexturePath(), NamedTextColor.WHITE)));
        }

        sender.sendMessage(Component.text("Stack Size: ", NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(model.getStackSize()), NamedTextColor.WHITE)));

        if (model.isUnbreakable()) {
            sender.sendMessage(Component.text("✓ Unbreakable", NamedTextColor.GREEN));
        }

        if (model.isGlowing()) {
            sender.sendMessage(Component.text("✓ Glowing", NamedTextColor.GREEN));
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("=== CustomModels Commands ===")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));

        sender.sendMessage(Component.text("/cm give <model> [amount]", NamedTextColor.WHITE)
                .append(Component.text(" - Give a custom model item", NamedTextColor.GRAY)));

        sender.sendMessage(Component.text("/cm list", NamedTextColor.WHITE)
                .append(Component.text(" - List all custom models", NamedTextColor.GRAY)));

        sender.sendMessage(Component.text("/cm info <model>", NamedTextColor.WHITE)
                .append(Component.text(" - View model details", NamedTextColor.GRAY)));

        sender.sendMessage(Component.text("/cm reload", NamedTextColor.WHITE)
                .append(Component.text(" - Reload configuration", NamedTextColor.GRAY)));

        sender.sendMessage(Component.text("/cm generate", NamedTextColor.WHITE)
                .append(Component.text(" - Generate resource pack", NamedTextColor.GRAY)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("give", "list", "info", "reload", "generate"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("info")) {
                completions.addAll(plugin.getModelManager().getModelIds());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            completions.addAll(Arrays.asList("1", "16", "32", "64"));
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}