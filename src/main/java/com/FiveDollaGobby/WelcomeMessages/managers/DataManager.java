package com.FiveDollaGobby.WelcomeMessages.managers;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager {

    private final WelcomePlugin plugin;
    private final Map<UUID, PlayerData> playerCache = new ConcurrentHashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;
    private int totalUniqueJoins = 0;

    public DataManager(WelcomePlugin plugin) {
        this.plugin = plugin;
        loadDataFile();
        startAutoSave();
    }

    private void loadDataFile() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml: " + e.getMessage());
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        // load total joins
        totalUniqueJoins = dataConfig.getInt("total-unique-joins", 0);

        // load player cache
        if (dataConfig.contains("players")) {
            ConfigurationSection playersSection = dataConfig.getConfigurationSection("players");
            if (playersSection != null) {
                for (String uuidStr : playersSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    PlayerData data = new PlayerData();
                    data.joinCount = dataConfig.getInt("players." + uuidStr + ".join-count", 0);
                    data.firstJoinTime = dataConfig.getLong("players." + uuidStr + ".first-join", 0);
                    data.lastSeenTime = dataConfig.getLong("players." + uuidStr + ".last-seen", 0);
                    data.messagesDisabled = dataConfig.getBoolean("players." + uuidStr + ".messages-disabled", false);
                    data.totalPlaytime = dataConfig.getLong("players." + uuidStr + ".total-playtime", 0);
                    data.sessionStartTime = dataConfig.getLong("players." + uuidStr + ".session-start", 0);
                    playerCache.put(uuid, data);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in playerdata: " + uuidStr);
                }
                }
            }
        }
    }

    private void startAutoSave() {
        int saveInterval = plugin.getConfig().getInt("general.save-interval", 10);
        
        // Bounds check to prevent integer overflow
        if (saveInterval < 1) {
            saveInterval = 1;
            plugin.getLogger().warning("Save interval too low, setting to 1 minute");
        } else if (saveInterval > 1440) { // 24 hours max
            saveInterval = 1440;
            plugin.getLogger().warning("Save interval too high, setting to 1440 minutes (24 hours)");
        }
        
        long interval = (long) saveInterval * 20L * 60L; // minutes to ticks, using long to prevent overflow
        
        // Additional safety check
        if (interval > Integer.MAX_VALUE) {
            interval = Integer.MAX_VALUE;
            plugin.getLogger().warning("Save interval would cause overflow, capping at maximum value");
        }
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveData, (int) interval, (int) interval);
        
        // Start playtime update task (every 5 minutes)
        Bukkit.getScheduler().runTaskTimer(plugin, this::updateAllPlaytime, 20 * 60 * 5, 20 * 60 * 5);
        
        // Start cleanup task (every hour)
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::performCleanup, 20 * 60 * 60, 20 * 60 * 60);
    }
    
    /**
     * Update playtime for all online players
     */
    private void updateAllPlaytime() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlaytime(player);
        }
    }

    public synchronized void saveData() {
        // Create a snapshot of current data to avoid concurrent modification
        Map<UUID, PlayerData> dataSnapshot = new HashMap<>(playerCache);
        int totalJoinsSnapshot = totalUniqueJoins;
        
        // save all cached data
        for (Map.Entry<UUID, PlayerData> entry : dataSnapshot.entrySet()) {
            String path = "players." + entry.getKey().toString();
            PlayerData data = entry.getValue();
            dataConfig.set(path + ".join-count", data.joinCount);
            dataConfig.set(path + ".first-join", data.firstJoinTime);
            dataConfig.set(path + ".last-seen", data.lastSeenTime);
            dataConfig.set(path + ".messages-disabled", data.messagesDisabled);
            dataConfig.set(path + ".total-playtime", data.totalPlaytime);
            dataConfig.set(path + ".session-start", data.sessionStartTime);
        }

        dataConfig.set("total-unique-joins", totalJoinsSnapshot);

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml: " + e.getMessage());
        }
    }

    public boolean isFirstJoin(Player player) {
        PlayerData data = getPlayerData(player);
        return data.firstJoinTime == 0;
    }

    public void updatePlayerData(Player player) {
        PlayerData data = getPlayerData(player);

        if (data.firstJoinTime == 0) {
            data.firstJoinTime = System.currentTimeMillis();
            totalUniqueJoins++;
        }

        data.joinCount++;
        data.lastSeenTime = System.currentTimeMillis();
        
        // Start new session for playtime tracking
        data.sessionStartTime = System.currentTimeMillis();
    }

    public void setLastSeen(Player player) {
        PlayerData data = getPlayerData(player);
        
        // Update playtime before setting last seen
        updatePlaytime(player);
        
        data.lastSeenTime = System.currentTimeMillis();
    }
    
    /**
     * Update player's total playtime based on current session
     */
    public void updatePlaytime(Player player) {
        PlayerData data = getPlayerData(player);
        
        if (data.sessionStartTime > 0) {
            long sessionTime = System.currentTimeMillis() - data.sessionStartTime;
            data.totalPlaytime += sessionTime;
            data.sessionStartTime = System.currentTimeMillis(); // Reset session start
        }
    }
    
    /**
     * Get player's total playtime in hours
     */
    public long getTotalPlaytime(Player player) {
        PlayerData data = getPlayerData(player);
        
        // Add current session time to total
        long currentSessionTime = 0;
        if (data.sessionStartTime > 0) {
            currentSessionTime = System.currentTimeMillis() - data.sessionStartTime;
        }
        
        return (data.totalPlaytime + currentSessionTime) / (1000 * 60 * 60); // Convert to hours
    }
    
    /**
     * Get player's current session time in minutes
     */
    public long getCurrentSessionTime(Player player) {
        PlayerData data = getPlayerData(player);
        
        if (data.sessionStartTime > 0) {
            return (System.currentTimeMillis() - data.sessionStartTime) / (1000 * 60); // Convert to minutes
        }
        
        return 0;
    }

    public int getJoinCount(Player player) {
        return getPlayerData(player).joinCount;
    }

    public int getTotalUniqueJoins() {
        return totalUniqueJoins;
    }

    public boolean hasMessagesDisabled(Player player) {
        return getPlayerData(player).messagesDisabled;
    }

    public boolean toggleMessages(Player player) {
        PlayerData data = getPlayerData(player);
        data.messagesDisabled = !data.messagesDisabled;
        return data.messagesDisabled;
    }

    public void resetPlayerData(String playerName) {
        // try online player first
        Player onlinePlayer = Bukkit.getPlayer(playerName);
        if (onlinePlayer != null) {
            playerCache.remove(onlinePlayer.getUniqueId());
            dataConfig.set("players." + onlinePlayer.getUniqueId().toString(), null);
            
            // Also reset milestone data in SmartRecognitionManager
            plugin.getMessageManager().getSmartRecognitionManager().resetPlayerData(onlinePlayer);
        } else {
            // search offline data
            ConfigurationSection playersSection = dataConfig.getConfigurationSection("players");
            if (playersSection != null) {
                for (String uuidStr : playersSection.getKeys(false)) {
                String storedName = dataConfig.getString("players." + uuidStr + ".name");
                if (playerName.equalsIgnoreCase(storedName)) {
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        playerCache.remove(uuid);
                        dataConfig.set("players." + uuidStr, null);
                        
                        // Also reset milestone data in SmartRecognitionManager
                        plugin.getMessageManager().getSmartRecognitionManager().resetPlayerData(uuid);
                        break;
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid UUID format when resetting player data: " + uuidStr + " - " + e.getMessage());
                    }
                }
                }
            }
        }

        saveData();
    }
    
    /**
     * Reset playtime data for a specific player
     */
    public void resetPlaytimeData(Player player) {
        PlayerData data = getPlayerData(player);
        data.totalPlaytime = 0;
        data.sessionStartTime = System.currentTimeMillis();
        
        // Also reset milestone data in SmartRecognitionManager
        plugin.getMessageManager().getSmartRecognitionManager().resetPlayerData(player);
    }

    private PlayerData getPlayerData(Player player) {
        return playerCache.computeIfAbsent(player.getUniqueId(), k -> new PlayerData());
    }

    // Public method for PlaceholderAPI access
    public PlayerData getPlayerDataPublic(Player player) {
        return getPlayerData(player);
    }

    // clean old cache entries
    public void cleanCache() {
        long maxCacheTime = plugin.getConfig().getInt("performance.cache-time", 5) * 60 * 1000L;
        long currentTime = System.currentTimeMillis();

        playerCache.entrySet().removeIf(entry -> {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || !player.isOnline()) {
                return currentTime - entry.getValue().lastSeenTime > maxCacheTime;
            }
            return false;
        });
    }
    
    /**
     * Perform comprehensive cleanup of old data
     */
    private void performCleanup() {
        // Clean up DataManager cache
        cleanCache();
        
        // Clean up SmartRecognitionManager data
        if (plugin.getMessageManager() != null && 
            plugin.getMessageManager().getSmartRecognitionManager() != null) {
            plugin.getMessageManager().getSmartRecognitionManager().cleanupOldData();
        }
        
        plugin.getLogger().info("Performed data cleanup - removed old entries");
    }

    public static class PlayerData {
        public int joinCount = 0;
        public long firstJoinTime = 0;
        public long lastSeenTime = 0;
        public boolean messagesDisabled = false;
        public long totalPlaytime = 0; // Total playtime in milliseconds
        public long sessionStartTime = 0; // When current session started
    }
}