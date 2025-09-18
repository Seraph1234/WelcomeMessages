package com.FiveDollaGobby.WelcomeMessages.managers;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import org.bukkit.Bukkit;
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
            for (String uuidStr : dataConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    PlayerData data = new PlayerData();
                    data.joinCount = dataConfig.getInt("players." + uuidStr + ".join-count", 0);
                    data.firstJoinTime = dataConfig.getLong("players." + uuidStr + ".first-join", 0);
                    data.lastSeenTime = dataConfig.getLong("players." + uuidStr + ".last-seen", 0);
                    data.messagesDisabled = dataConfig.getBoolean("players." + uuidStr + ".messages-disabled", false);
                    playerCache.put(uuid, data);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in playerdata: " + uuidStr);
                }
            }
        }
    }

    private void startAutoSave() {
        int interval = plugin.getConfig().getInt("general.save-interval", 10) * 20 * 60; // minutes to ticks
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveData, interval, interval);
    }

    public void saveData() {
        // save all cached data
        for (Map.Entry<UUID, PlayerData> entry : playerCache.entrySet()) {
            String path = "players." + entry.getKey().toString();
            PlayerData data = entry.getValue();
            dataConfig.set(path + ".join-count", data.joinCount);
            dataConfig.set(path + ".first-join", data.firstJoinTime);
            dataConfig.set(path + ".last-seen", data.lastSeenTime);
            dataConfig.set(path + ".messages-disabled", data.messagesDisabled);
        }

        dataConfig.set("total-unique-joins", totalUniqueJoins);

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
    }

    public void setLastSeen(Player player) {
        PlayerData data = getPlayerData(player);
        data.lastSeenTime = System.currentTimeMillis();
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
        } else {
            // search offline data
            for (String uuidStr : dataConfig.getConfigurationSection("players").getKeys(false)) {
                String storedName = dataConfig.getString("players." + uuidStr + ".name");
                if (playerName.equalsIgnoreCase(storedName)) {
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        playerCache.remove(uuid);
                        dataConfig.set("players." + uuidStr, null);
                        break;
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }

        saveData();
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

    public static class PlayerData {
        public int joinCount = 0;
        public long firstJoinTime = 0;
        public long lastSeenTime = 0;
        public boolean messagesDisabled = false;
    }
}