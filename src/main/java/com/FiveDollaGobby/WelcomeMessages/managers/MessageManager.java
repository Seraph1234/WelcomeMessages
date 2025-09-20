package com.FiveDollaGobby.WelcomeMessages.managers;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import com.FiveDollaGobby.WelcomeMessages.utils.MessageUtils;
import com.FiveDollaGobby.WelcomeMessages.utils.AnimationUtils;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import net.md_5.bungee.api.ChatColor;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MessageManager {

    private final WelcomePlugin plugin;
    private final AnimationUtils animationUtils;
    private final ThemeManager themeManager;
    private final SmartRecognitionManager smartRecognitionManager;
    private final Map<String, List<String>> rankJoinMessages = new HashMap<>();
    private final Map<String, List<String>> rankQuitMessages = new HashMap<>();
    private List<String> defaultJoinMessages;
    private List<String> defaultQuitMessages;
    private List<String> firstJoinMessages;

    public MessageManager(WelcomePlugin plugin) {
        this.plugin = plugin;
        this.animationUtils = new AnimationUtils(plugin);
        this.themeManager = new ThemeManager(plugin);
        this.smartRecognitionManager = new SmartRecognitionManager(plugin);
        reload();
    }

    public void reload() {
        rankJoinMessages.clear();
        rankQuitMessages.clear();

        // load messages from config
        defaultJoinMessages = plugin.getMessagesConfig().getStringList("messages.join.default");
        defaultQuitMessages = plugin.getMessagesConfig().getStringList("messages.quit.default");
        firstJoinMessages = plugin.getMessagesConfig().getStringList("messages.join.first-time");

        // load rank messages
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
        // Check for milestone messages first (highest priority)
        String milestoneMessage = smartRecognitionManager.checkMilestones(player, isFirstJoin);
        if (milestoneMessage != null) {
            String finalMessage = replacePlaceholders(milestoneMessage, player, isFirstJoin);
            return handleAnimation(finalMessage, player, isFirstJoin);
        }

        // Check for returning player messages (second priority)
        String returningMessage = smartRecognitionManager.getReturningPlayerMessage(player);
        if (returningMessage != null) {
            String finalMessage = replacePlaceholders(returningMessage, player, isFirstJoin);
            return handleAnimation(finalMessage, player, isFirstJoin);
        }

        // Check for theme-based messages (third priority)
        List<String> themeMessages = themeManager.getThemeMessages("join", isFirstJoin);
        if (themeMessages != null && !themeMessages.isEmpty()) {
            String message = themeMessages.get(ThreadLocalRandom.current().nextInt(themeMessages.size()));
            String finalMessage = replacePlaceholders(message, player, isFirstJoin);
            return handleAnimation(finalMessage, player, isFirstJoin);
        }

        // If no special messages, use normal logic
        List<String> possibleMessages = new ArrayList<>();
        
        // first join gets special message
        if (isFirstJoin && !firstJoinMessages.isEmpty()) {
            possibleMessages = new ArrayList<>(firstJoinMessages);
        } else {
            // check rank with priority
            String highestRank = getHighestRank(player);

            if (highestRank != null && rankJoinMessages.containsKey(highestRank)) {
                possibleMessages.addAll(rankJoinMessages.get(highestRank));
            }

            // add defaults if needed
            if (possibleMessages.isEmpty() || plugin.getConfig().getBoolean("messages.combine-rank-default", false)) {
                possibleMessages.addAll(defaultJoinMessages);
            }
        }

        // pick random message
        if (!possibleMessages.isEmpty()) {
            String message = possibleMessages.get(ThreadLocalRandom.current().nextInt(possibleMessages.size()));
            String finalMessage = replacePlaceholders(message, player, isFirstJoin);
            
            // Apply animation if enabled
            if (plugin.getConfig().getBoolean("animations.enabled", true)) {
                String animationType = getAnimationType("join", isFirstJoin);
                int duration = getAnimationDuration("join", isFirstJoin);
                boolean showFinalInChat = plugin.getConfig().getBoolean("animations.show-final-in-chat", true);
                
                // Check if we should use multi-layer animation
                boolean useMultiLayer = plugin.getConfig().getBoolean("animations.join.use-multi-layer", false);
                if (useMultiLayer) {
                    String multiLayerType = plugin.getConfig().getString("animations.join.multi-layer-type", "smooth_entrance");
                    animationType = multiLayerType;
                }
                
                if (animationType != null && !animationType.equals("none")) {
                    animationUtils.animateMessage(player, finalMessage, animationType, duration);
                    
                    // Show final message in chat after animation if configured
                    if (showFinalInChat) {
                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            MessageUtils.sendMessage(player, finalMessage);
                        }, duration + 20L); // Wait for animation to complete + 1 second
                    }
                    
                    return null; // Don't send the message normally, animation will handle it
                }
            }
            
            return finalMessage;
        }

        return null;
    }

    /**
     * Handle animation for a message
     */
    private String handleAnimation(String message, Player player, boolean isFirstJoin) {
        // Apply animation if enabled
        if (plugin.getConfig().getBoolean("animations.enabled", true)) {
            String animationType = getAnimationType("join", isFirstJoin);
            int duration = getAnimationDuration("join", isFirstJoin);
            boolean showFinalInChat = plugin.getConfig().getBoolean("animations.show-final-in-chat", true);
            
            // Check if we should use multi-layer animation
            boolean useMultiLayer = plugin.getConfig().getBoolean("animations.join.use-multi-layer", false);
            if (useMultiLayer) {
                String multiLayerType = plugin.getConfig().getString("animations.join.multi-layer-type", "smooth_entrance");
                animationType = multiLayerType;
            }
            
            if (animationType != null && !animationType.equals("none")) {
                animationUtils.animateMessage(player, message, animationType, duration);
                
                // Show final message in chat after animation if configured
                if (showFinalInChat) {
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        MessageUtils.sendMessage(player, message);
                    }, duration + 20L); // Wait for animation to complete + 1 second
                }
                
                return null; // Don't send the message normally, animation will handle it
            }
        }
        
        return message;
    }

    public String getQuitMessage(Player player) {
        List<String> possibleMessages = new ArrayList<>();

        // check rank with priority
        String highestRank = getHighestRank(player);

        if (highestRank != null && rankQuitMessages.containsKey(highestRank)) {
            possibleMessages.addAll(rankQuitMessages.get(highestRank));
        }

        // add defaults if needed
        if (possibleMessages.isEmpty() || plugin.getConfig().getBoolean("messages.combine-rank-default", false)) {
            possibleMessages.addAll(defaultQuitMessages);
        }

        // pick random message
        if (!possibleMessages.isEmpty()) {
            String message = possibleMessages.get(ThreadLocalRandom.current().nextInt(possibleMessages.size()));
            String finalMessage = replacePlaceholders(message, player, false);
            
            // Apply animation if enabled
            if (plugin.getConfig().getBoolean("animations.enabled", true)) {
                String animationType = getAnimationType("quit", false);
                int duration = getAnimationDuration("quit", false);
                boolean showFinalInChat = plugin.getConfig().getBoolean("animations.show-final-in-chat", true);
                
                if (animationType != null && !animationType.equals("none")) {
                    animationUtils.animateMessage(player, finalMessage, animationType, duration);
                    
                    // Show final message in chat after animation if configured
                    if (showFinalInChat) {
                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            MessageUtils.sendMessage(player, finalMessage);
                        }, duration + 20L); // Wait for animation to complete + 1 second
                    }
                    
                    return null; // Don't send the message normally, animation will handle it
                }
            }
            
            return finalMessage;
        }

        return null;
    }

    // get highest rank for player using custom rank system
    private String getHighestRank(Player player) {
        // ops use default if configured
        if (player.isOp() && plugin.getConfig().getBoolean("messages.op-uses-default", false)) {
            return null;
        }

        // check if custom ranks are enabled
        if (plugin.getConfig().getBoolean("custom-ranks.enabled", true)) {
            List<String> customRanks = plugin.getConfig().getStringList("custom-ranks.ranks");
            
            // if custom ranks are configured, use them
            if (!customRanks.isEmpty()) {
                for (String rank : customRanks) {
                    if (player.hasPermission("welcome.rank." + rank)) {
                        return rank;
                    }
                }
            }
        }

        // fallback to default rank priority if custom ranks are disabled or empty
        String[] defaultRankPriority = {"owner", "admin", "mvp", "vip"};
        for (String rank : defaultRankPriority) {
            if (player.hasPermission("welcome.rank." + rank)) {
                return rank;
            }
        }

        return null;
    }

    private String replacePlaceholders(String message, Player player, boolean isFirstJoin) {
        message = message.replace("{player}", player.getName())
                .replace("{displayname}", getDisplayName(player))
                .replace("{world}", player.getWorld() != null ? player.getWorld().getName() : "unknown")
                .replace("{online}", String.valueOf(plugin.getServer().getOnlinePlayers().size()))
                .replace("{max}", String.valueOf(plugin.getServer().getMaxPlayers()))
                .replace("{joincount}", String.valueOf(plugin.getDataManager().getJoinCount(player)));

        // time greeting
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String timeGreeting = hour < 12 ? "morning" : hour < 18 ? "afternoon" : "evening";
        message = message.replace("{time}", timeGreeting);

        // first join ordinal
        if (isFirstJoin) {
            message = message.replace("{ordinal}", getOrdinal(plugin.getDataManager().getTotalUniqueJoins()));
        }

        // apply colors
        if (plugin.getConfig().getBoolean("messages.rgb.enabled", true)) {
            return MessageUtils.colorize(message);
        } else {
            // fallback to basic colors
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

    /**
     * Get animation type for message type
     */
    private String getAnimationType(String messageType, boolean isFirstJoin) {
        if (isFirstJoin && messageType.equals("join")) {
            return plugin.getConfig().getString("animations.first-join.type", 
                plugin.getConfig().getString("animations.default-type", "typing"));
        }
        
        return plugin.getConfig().getString("animations." + messageType + ".type", 
            plugin.getConfig().getString("animations.default-type", "typing"));
    }

    /**
     * Get animation duration for message type
     */
    private int getAnimationDuration(String messageType, boolean isFirstJoin) {
        if (isFirstJoin && messageType.equals("join")) {
            return plugin.getConfig().getInt("animations.first-join.duration", 
                plugin.getConfig().getInt("animations.default-duration", 60));
        }
        
        return plugin.getConfig().getInt("animations." + messageType + ".duration", 
            plugin.getConfig().getInt("animations.default-duration", 60));
    }

    @SuppressWarnings("deprecation")
    private String getDisplayName(Player player) {
        return player.getDisplayName();
    }

    /**
     * Get the theme manager
     */
    public ThemeManager getThemeManager() {
        return themeManager;
    }

    /**
     * Get the smart recognition manager
     */
    public SmartRecognitionManager getSmartRecognitionManager() {
        return smartRecognitionManager;
    }
}