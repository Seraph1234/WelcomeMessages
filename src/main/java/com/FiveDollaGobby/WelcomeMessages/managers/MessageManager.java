package com.FiveDollaGobby.WelcomeMessages.managers;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import com.FiveDollaGobby.WelcomeMessages.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MessageManager {

    private final WelcomePlugin plugin;
    private final Map<String, List<String>> rankJoinMessages = new HashMap<>();
    private final Map<String, List<String>> rankQuitMessages = new HashMap<>();
    private List<String> defaultJoinMessages;
    private List<String> defaultQuitMessages;
    private List<String> firstJoinMessages;

    public MessageManager(WelcomePlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        rankJoinMessages.clear();
        rankQuitMessages.clear();

        // Load default messages
        defaultJoinMessages = plugin.getMessagesConfig().getStringList("messages.join.default");
        defaultQuitMessages = plugin.getMessagesConfig().getStringList("messages.quit.default");
        firstJoinMessages = plugin.getMessagesConfig().getStringList("messages.join.first-time");

        // Load rank-based messages
        ConfigurationSection joinRanks = plugin.getMessagesConfig().getConfigurationSection("messages.join.ranks");
        if (joinRanks != null) {
            for (String rank : joinRanks.getKeys(false)) {
                List<String> messages = plugin.getMessagesConfig().getStringList("messages.join.ranks." + rank);
                rankJoinMessages.put(rank.toLowerCase(), messages);
            }
        }

        ConfigurationSection quitRanks = plugin.getMessagesConfig().getConfigurationSection("messages.quit.ranks");
        if (quitRanks != null) {
            for (String rank : quitRanks.getKeys(false)) {
                List<String> messages = plugin.getMessagesConfig().getStringList("messages.quit.ranks." + rank);
                rankQuitMessages.put(rank.toLowerCase(), messages);
            }
        }
    }

    public String getJoinMessage(Player player, boolean isFirstJoin) {
        List<String> possibleMessages = new ArrayList<>();

        // First join takes priority
        if (isFirstJoin && !firstJoinMessages.isEmpty()) {
            possibleMessages = new ArrayList<>(firstJoinMessages);
        } else {
            // Check for rank-based messages
            for (Map.Entry<String, List<String>> entry : rankJoinMessages.entrySet()) {
                if (player.hasPermission("welcome.rank." + entry.getKey())) {
                    possibleMessages.addAll(entry.getValue());
                }
            }

            // Add default messages if no rank messages or if configured to combine
            if (possibleMessages.isEmpty() || plugin.getConfig().getBoolean("messages.combine-rank-default", false)) {
                possibleMessages.addAll(defaultJoinMessages);
            }
        }

        // Select random message
        if (!possibleMessages.isEmpty()) {
            String message = possibleMessages.get(ThreadLocalRandom.current().nextInt(possibleMessages.size()));
            return replacePlaceholders(message, player, isFirstJoin);
        }

        return null;
    }

    public String getQuitMessage(Player player) {
        List<String> possibleMessages = new ArrayList<>();

        // Check for rank-based messages
        for (Map.Entry<String, List<String>> entry : rankQuitMessages.entrySet()) {
            if (player.hasPermission("welcome.rank." + entry.getKey())) {
                possibleMessages.addAll(entry.getValue());
            }
        }

        // Add default messages if no rank messages or if configured to combine
        if (possibleMessages.isEmpty() || plugin.getConfig().getBoolean("messages.combine-rank-default", false)) {
            possibleMessages.addAll(defaultQuitMessages);
        }

        // Select random message
        if (!possibleMessages.isEmpty()) {
            String message = possibleMessages.get(ThreadLocalRandom.current().nextInt(possibleMessages.size()));
            return replacePlaceholders(message, player, false);
        }

        return null;
    }

    private String replacePlaceholders(String message, Player player, boolean isFirstJoin) {
        message = message.replace("{player}", player.getName())
                .replace("{displayname}", player.getDisplayName())
                .replace("{world}", player.getWorld().getName())
                .replace("{online}", String.valueOf(plugin.getServer().getOnlinePlayers().size()))
                .replace("{max}", String.valueOf(plugin.getServer().getMaxPlayers()))
                .replace("{joincount}", String.valueOf(plugin.getDataManager().getJoinCount(player)));

        // Time-based placeholders
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String timeGreeting = hour < 12 ? "morning" : hour < 18 ? "afternoon" : "evening";
        message = message.replace("{time}", timeGreeting);

        // First join specific
        if (isFirstJoin) {
            message = message.replace("{ordinal}", getOrdinal(plugin.getDataManager().getTotalUniqueJoins()));
        }

        // Apply color codes (including gradients and rainbow if enabled)
        if (plugin.getConfig().getBoolean("messages.rgb.enabled", true)) {
            return MessageUtils.colorize(message);
        } else {
            // Fall back to basic color codes only
            return ChatColor.translateAlternateColorCodes('&', message);
        }
    }

    private String getOrdinal(int number) {
        String[] suffixes = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (number % 100) {
            case 11:
            case 12:
            case 13:
                return number + "th";
            default:
                return number + suffixes[number % 10];
        }
    }
}