package com.FiveDollaGobby.WelcomeMessages.placeholders;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import com.FiveDollaGobby.WelcomeMessages.managers.DataManager;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WelcomePlaceholders {

    private final WelcomePlugin plugin;
    private Object expansion;

    public WelcomePlaceholders(WelcomePlugin plugin) {
        this.plugin = plugin;
        try {
            // Try to create PlaceholderAPI expansion using reflection
            Class.forName("me.clip.placeholderapi.expansion.PlaceholderExpansion");
            this.expansion = new PlaceholderAPIExpansion(plugin);
        } catch (ClassNotFoundException e) {
            // PlaceholderAPI not available
            this.expansion = null;
        }
    }

    public boolean register() {
        if (expansion == null) {
            return false;
        }
        try {
            // Use reflection to call register method
            expansion.getClass().getMethod("register").invoke(expansion);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getPlaceholder(Player player, String params) {
        if (player == null) {
            return "";
        }

        DataManager dataManager = plugin.getDataManager();
        if (dataManager == null) {
            return "";
        }

        // Player data placeholders
        switch (params.toLowerCase()) {
            case "joincount":
            case "joins":
                return String.valueOf(dataManager.getJoinCount(player));

            case "firstjoin":
            case "isfirstjoin":
                return dataManager.isFirstJoin(player) ? "true" : "false";

            case "messagesdisabled":
            case "messages_off":
                return dataManager.hasMessagesDisabled(player) ? "true" : "false";

            case "lastseen":
                long lastSeen = dataManager.getPlayerDataPublic(player).lastSeenTime;
                if (lastSeen == 0) {
                    return "Never";
                }
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(lastSeen));

            case "firstjointime":
                long firstJoin = dataManager.getPlayerDataPublic(player).firstJoinTime;
                if (firstJoin == 0) {
                    return "Never";
                }
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(firstJoin));

            case "time_since_last_seen":
                long lastSeenTime = dataManager.getPlayerDataPublic(player).lastSeenTime;
                if (lastSeenTime == 0) {
                    return "Never";
                }
                return formatTimeAgo(System.currentTimeMillis() - lastSeenTime);

            case "time_since_first_join":
                long firstJoinTime = dataManager.getPlayerDataPublic(player).firstJoinTime;
                if (firstJoinTime == 0) {
                    return "Never";
                }
                return formatTimeAgo(System.currentTimeMillis() - firstJoinTime);

            case "total_unique_joins":
            case "server_joins":
                return String.valueOf(dataManager.getTotalUniqueJoins());

            case "join_ordinal":
                int totalJoins = dataManager.getTotalUniqueJoins();
                return getOrdinal(totalJoins);

            case "player_ordinal":
                // This would need to track when each player first joined
                // For now, just return their join count
                return getOrdinal(dataManager.getJoinCount(player));

            // Status placeholders
            case "status":
                if (dataManager.isFirstJoin(player)) {
                    return "New Player";
                } else if (dataManager.hasMessagesDisabled(player)) {
                    return "Messages Disabled";
                } else {
                    return "Regular Player";
                }

            case "rank":
                return getPlayerRank(player);

            // Time-based placeholders
            case "time_greeting":
                return getTimeGreeting();

            case "server_uptime":
                // Simple uptime calculation - could be improved with proper server start time
                return "Unknown";

            default:
                return null; // Placeholder is unknown by the Expansion
        }
    }

    private String formatTimeAgo(long timeDiff) {
        long seconds = timeDiff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else {
            return seconds + " second" + (seconds == 1 ? "" : "s") + " ago";
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

    private String getTimeGreeting() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            return "morning";
        } else if (hour < 18) {
            return "afternoon";
        } else {
            return "evening";
        }
    }

    private String getPlayerRank(Player player) {
        // check if custom ranks are enabled
        if (plugin.getConfig().getBoolean("custom-ranks.enabled", true)) {
            List<String> customRanks = plugin.getConfig().getStringList("custom-ranks.ranks");
            
            // if custom ranks are configured, use them
            if (!customRanks.isEmpty()) {
                for (String rank : customRanks) {
                    if (player.hasPermission("welcome.rank." + rank)) {
                        return rank.toUpperCase();
                    }
                }
            }
        }
        
        // fallback to default rank priority if custom ranks are disabled or empty
        String[] defaultRankPriority = {"owner", "admin", "mvp", "vip"};
        for (String rank : defaultRankPriority) {
            if (player.hasPermission("welcome.rank." + rank)) {
                return rank.toUpperCase();
            }
        }
        
        return "DEFAULT";
    }

    // Helper method to access PlayerData (currently unused but kept for future use)
    @SuppressWarnings("unused")
    private DataManager.PlayerData getPlayerData(Player player) {
        return plugin.getDataManager().getPlayerDataPublic(player);
    }
}