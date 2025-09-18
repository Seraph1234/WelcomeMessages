package com.FiveDollaGobby.WelcomeMessages.commands;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import com.FiveDollaGobby.WelcomeMessages.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class WelcomeCommand implements CommandExecutor, TabCompleter {

    private final WelcomePlugin plugin;
    private final Map<UUID, Long> commandCooldowns = new HashMap<>();

    public WelcomeCommand(WelcomePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                sendHelp(sender);
                break;

            case "reload":
                handleReload(sender);
                break;

            case "test":
                handleTest(sender, args);
                break;

            case "stats":
                handleStats(sender, args);
                break;

            case "reset":
                handleReset(sender, args);
                break;

            case "toggle":
                handleToggle(sender);
                break;

            case "version":
                handleVersion(sender);
                break;

            default:
                MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.unknown-command"));
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        if (!sender.hasPermission("welcome.use")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.help.header"));

        List<String> commands = plugin.getMessagesConfig().getStringList("commands.help.commands");
        for (String cmd : commands) {
            // only show commands player has permission for
            if (cmd.contains("reload") && !sender.hasPermission("welcome.reload")) continue;
            if (cmd.contains("test") && !sender.hasPermission("welcome.test")) continue;
            if (cmd.contains("stats") && !sender.hasPermission("welcome.stats")) continue;
            if (cmd.contains("reset") && !sender.hasPermission("welcome.reset")) continue;

            MessageUtils.sendMessage(sender, cmd);
        }

        MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.help.footer"));
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("welcome.reload")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        try {
            plugin.reload();
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.reload-success"));
        } catch (Exception e) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.reload-error"));
            plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
        }
    }

    private void handleTest(CommandSender sender, String[] args) {
        if (!sender.hasPermission("welcome.test")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                String msg = plugin.getMessagesConfig().getString("commands.player-not-found");
                MessageUtils.sendMessage(sender, msg.replace("{player}", args[1]));
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            MessageUtils.sendMessage(sender, "&cPlease specify a player name!");
            return;
        }

        // test messages
        boolean isFirstJoin = plugin.getDataManager().isFirstJoin(target);
        String joinMessage = plugin.getMessageManager().getJoinMessage(target, isFirstJoin);

        MessageUtils.sendMessage(sender, "&6Testing join message for &e" + target.getName() + "&6:");
        MessageUtils.sendMessage(sender, joinMessage);

        String quitMessage = plugin.getMessageManager().getQuitMessage(target);
        MessageUtils.sendMessage(sender, "&6Quit message:");
        MessageUtils.sendMessage(sender, quitMessage);

        // test effects if sender is target
        if (sender.equals(target)) {
            MessageUtils.sendMessage(sender, "&6Testing effects in 2 seconds...");
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                plugin.getEffectManager().sendTitle(target, isFirstJoin);
                plugin.getEffectManager().playJoinSound(target, isFirstJoin);
                plugin.getEffectManager().playJoinParticles(target, isFirstJoin);
            }, 40L);
        }
    }

    private void handleStats(CommandSender sender, String[] args) {
        if (!sender.hasPermission("welcome.stats")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtils.sendMessage(sender, "&cPlayer must be online to view stats!");
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            MessageUtils.sendMessage(sender, "&cPlease specify a player name!");
            return;
        }

        MessageUtils.sendMessage(sender, "&6&l--- Player Statistics ---");
        MessageUtils.sendMessage(sender, "&ePlayer: &f" + target.getName());
        MessageUtils.sendMessage(sender, "&eJoin Count: &f" + plugin.getDataManager().getJoinCount(target));
        MessageUtils.sendMessage(sender, "&eFirst Join: &f" + (plugin.getDataManager().isFirstJoin(target) ? "Never" : "Yes"));
        MessageUtils.sendMessage(sender, "&eMessages Enabled: &f" + !plugin.getDataManager().hasMessagesDisabled(target));
        MessageUtils.sendMessage(sender, "&6&l----------------------");
    }

    private void handleReset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("welcome.reset")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        if (args.length < 2) {
            MessageUtils.sendMessage(sender, "&cUsage: /welcome reset <player>");
            return;
        }

        String playerName = args[1];
        plugin.getDataManager().resetPlayerData(playerName);
        MessageUtils.sendMessage(sender, "&aPlayer data reset for &e" + playerName);
    }

    private void handleToggle(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, "&cOnly players can toggle their messages!");
            return;
        }

        if (!sender.hasPermission("welcome.toggle")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        Player player = (Player) sender;

        // cooldown check (5 seconds)
        UUID uuid = player.getUniqueId();
        long lastUse = commandCooldowns.getOrDefault(uuid, 0L);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUse < 5000 && !player.hasPermission("welcome.effects.bypass")) {
            MessageUtils.sendMessage(player, "&cPlease wait before using this command again!");
            return;
        }

        commandCooldowns.put(uuid, currentTime);

        boolean disabled = plugin.getDataManager().toggleMessages(player);
        if (disabled) {
            MessageUtils.sendMessage(player, "&cYour join/quit messages have been disabled!");
        } else {
            MessageUtils.sendMessage(player, "&aYour join/quit messages have been enabled!");
        }
    }

    private void handleVersion(CommandSender sender) {
        MessageUtils.sendMessage(sender, "&6WelcomeMessages &ev" + plugin.getDescription().getVersion());
        MessageUtils.sendMessage(sender, "&7Created by &eFiveDollaGobby");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "reload", "test", "stats", "reset", "toggle", "version");
            StringUtil.copyPartialMatches(args[0], subCommands, completions);
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("test") || subCommand.equals("stats") || subCommand.equals("reset")) {
                List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList());
                StringUtil.copyPartialMatches(args[1], playerNames, completions);
            }
        }

        Collections.sort(completions);
        return completions;
    }
}