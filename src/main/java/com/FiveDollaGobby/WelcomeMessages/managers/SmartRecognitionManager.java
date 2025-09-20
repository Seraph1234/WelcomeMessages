package com.FiveDollaGobby.WelcomeMessages.managers;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class SmartRecognitionManager {

    private final WelcomePlugin plugin;
    private final Map<UUID, Long> lastSeenTimes = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> streakData = new ConcurrentHashMap<>();
    private final Map<UUID, LocalDateTime> lastLoginDate = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> reachedMilestones = new ConcurrentHashMap<>();

    public SmartRecognitionManager(WelcomePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Check if a player has reached a milestone
     */
    public String checkMilestones(Player player, boolean isFirstJoin) {
        if (!plugin.getConfig().getBoolean("smart-recognition.enabled", true)) {
            return null;
        }

        if (!plugin.getConfig().getBoolean("smart-recognition.milestones.enabled", true)) {
            return null;
        }

        int joinCount = plugin.getDataManager().getJoinCount(player);

        // Check join milestones
        String joinMilestone = checkJoinMilestone(player, joinCount);
        if (joinMilestone != null) {
            return joinMilestone;
        }

        // Check playtime milestones
        String playtimeMilestone = checkPlaytimeMilestone(player);
        if (playtimeMilestone != null) {
            return playtimeMilestone;
        }

        // Check streak milestones
        String streakMilestone = checkStreakMilestone(player);
        if (streakMilestone != null) {
            return streakMilestone;
        }

        return null;
    }

    /**
     * Check join count milestones
     */
    private String checkJoinMilestone(Player player, int joinCount) {
        List<Integer> milestones = plugin.getConfig().getIntegerList("smart-recognition.milestones.join-milestones");
        
        // Find the highest milestone the player has reached but hasn't been notified about
        int highestUnnotifiedMilestone = -1;
        for (int milestone : milestones) {
            if (joinCount >= milestone && !hasReachedMilestone(player, "join", milestone)) {
                if (milestone > highestUnnotifiedMilestone) {
                    highestUnnotifiedMilestone = milestone;
                }
            }
        }
        
        // If we found a milestone to notify about, mark it as reached and return the message
        if (highestUnnotifiedMilestone > 0) {
            markMilestoneReached(player, "join", highestUnnotifiedMilestone);
            List<String> messages = plugin.getMessagesConfig().getStringList("messages.join.milestones.join-milestones." + highestUnnotifiedMilestone);
            if (!messages.isEmpty()) {
                String message = messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
                return message.replace("{player}", player.getName())
                             .replace("{milestone}", String.valueOf(highestUnnotifiedMilestone));
            }
        }
        
        return null;
    }

    /**
     * Check playtime milestones
     */
    private String checkPlaytimeMilestone(Player player) {
        // Get actual playtime from DataManager
        long playtime = plugin.getDataManager().getTotalPlaytime(player);
        
        List<Integer> milestones = plugin.getConfig().getIntegerList("smart-recognition.milestones.playtime-milestones");
        
        // Find the highest milestone the player has reached but hasn't been notified about
        int highestUnnotifiedMilestone = -1;
        for (int milestone : milestones) {
            if (playtime >= milestone && !hasReachedMilestone(player, "playtime", milestone)) {
                if (milestone > highestUnnotifiedMilestone) {
                    highestUnnotifiedMilestone = milestone;
                }
            }
        }
        
        // If we found a milestone to notify about, mark it as reached and return the message
        if (highestUnnotifiedMilestone > 0) {
            markMilestoneReached(player, "playtime", highestUnnotifiedMilestone);
            List<String> messages = plugin.getMessagesConfig().getStringList("messages.join.milestones.playtime-milestones." + highestUnnotifiedMilestone);
            if (!messages.isEmpty()) {
                String message = messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
                return message.replace("{player}", player.getName())
                             .replace("{milestone}", String.valueOf(highestUnnotifiedMilestone));
            }
        }
        
        return null;
    }

    /**
     * Check streak milestones
     */
    private String checkStreakMilestone(Player player) {
        UUID uuid = player.getUniqueId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastLogin = lastLoginDate.get(uuid);
        
        int streak = streakData.getOrDefault(uuid, 0);
        
        if (lastLogin != null) {
            long daysBetween = ChronoUnit.DAYS.between(lastLogin.toLocalDate(), now.toLocalDate());
            if (daysBetween == 1) {
                // Consecutive day
                streak++;
            } else if (daysBetween > 1) {
                // Streak broken
                streak = 1;
            }
        } else {
            // First time
            streak = 1;
        }
        
        streakData.put(uuid, streak);
        lastLoginDate.put(uuid, now);
        
        List<Integer> milestones = plugin.getConfig().getIntegerList("smart-recognition.milestones.streak-milestones");
        
        // Find the highest milestone the player has reached but hasn't been notified about
        int highestUnnotifiedMilestone = -1;
        for (int milestone : milestones) {
            if (streak >= milestone && !hasReachedMilestone(player, "streak", milestone)) {
                if (milestone > highestUnnotifiedMilestone) {
                    highestUnnotifiedMilestone = milestone;
                }
            }
        }
        
        // If we found a milestone to notify about, mark it as reached and return the message
        if (highestUnnotifiedMilestone > 0) {
            markMilestoneReached(player, "streak", highestUnnotifiedMilestone);
            List<String> messages = plugin.getMessagesConfig().getStringList("messages.join.milestones.streak-milestones." + highestUnnotifiedMilestone);
            if (!messages.isEmpty()) {
                String message = messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
                return message.replace("{player}", player.getName())
                             .replace("{milestone}", String.valueOf(highestUnnotifiedMilestone));
            }
        }
        
        return null;
    }

    /**
     * Get returning player message based on absence duration
     */
    public String getReturningPlayerMessage(Player player) {
        if (!plugin.getConfig().getBoolean("smart-recognition.enabled", true)) {
            return null;
        }

        if (!plugin.getConfig().getBoolean("smart-recognition.returning-player.enabled", true)) {
            return null;
        }

        UUID uuid = player.getUniqueId();
        long lastSeen = lastSeenTimes.getOrDefault(uuid, 0L);
        long currentTime = System.currentTimeMillis();
        
        if (lastSeen == 0) {
            // First time seeing this player
            lastSeenTimes.put(uuid, currentTime);
            return null;
        }

        long hoursAbsent = (currentTime - lastSeen) / (1000 * 60 * 60);
        lastSeenTimes.put(uuid, currentTime);

        String absenceType = getAbsenceType(hoursAbsent);
        if (absenceType == null) {
            return null;
        }

        List<String> messages = plugin.getMessagesConfig().getStringList("messages.join.returning." + absenceType);
        if (messages.isEmpty()) {
            return null;
        }

        String message = messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
        return message.replace("{player}", player.getName())
                     .replace("{hours}", String.valueOf(hoursAbsent));
    }

    /**
     * Determine absence type based on hours
     */
    private String getAbsenceType(long hoursAbsent) {
        int shortThreshold = plugin.getConfig().getInt("smart-recognition.returning-player.thresholds.short-absence", 24);
        int mediumThreshold = plugin.getConfig().getInt("smart-recognition.returning-player.thresholds.medium-absence", 168);
        int longThreshold = plugin.getConfig().getInt("smart-recognition.returning-player.thresholds.long-absence", 720);
        int veryLongThreshold = plugin.getConfig().getInt("smart-recognition.returning-player.thresholds.very-long-absence", 2160);

        if (hoursAbsent >= veryLongThreshold) {
            return "very-long-absence";
        } else if (hoursAbsent >= longThreshold) {
            return "long-absence";
        } else if (hoursAbsent >= mediumThreshold) {
            return "medium-absence";
        } else if (hoursAbsent >= shortThreshold) {
            return "short-absence";
        }

        return null;
    }

    /**
     * Get player behavior data
     */
    public Map<String, Object> getPlayerBehavior(Player player) {
        Map<String, Object> behavior = new HashMap<>();
        
        if (!plugin.getConfig().getBoolean("smart-recognition.behavior.enabled", true)) {
            return behavior;
        }

        // Peak activity detection
        if (plugin.getConfig().getBoolean("smart-recognition.behavior.peak-activity", true)) {
            behavior.put("peak_activity", isPeakActivityTime());
        }
        
        // Favorite world detection
        if (plugin.getConfig().getBoolean("smart-recognition.behavior.favorite-world", true)) {
            World world = player.getWorld();
            behavior.put("favorite_world", world != null ? world.getName() : "unknown");
        }
        
        // Join patterns
        if (plugin.getConfig().getBoolean("smart-recognition.behavior.join-patterns", true)) {
            behavior.put("join_count", plugin.getDataManager().getJoinCount(player));
            behavior.put("first_join", plugin.getDataManager().isFirstJoin(player));
        }
        
        return behavior;
    }

    /**
     * Check if current time is peak activity time
     */
    private boolean isPeakActivityTime() {
        // Simple peak activity detection (evening hours)
        int hour = LocalDateTime.now().getHour();
        return hour >= 18 && hour <= 22;
    }

    /**
     * Get milestone information for a player
     */
    public String getMilestoneInfo(Player player) {
        UUID uuid = player.getUniqueId();
        int joinCount = plugin.getDataManager().getJoinCount(player);
        int streak = streakData.getOrDefault(uuid, 0);
        long playtime = plugin.getDataManager().getTotalPlaytime(player);
        long sessionTime = plugin.getDataManager().getCurrentSessionTime(player);
        
        return String.format("Join Count: %d | Streak: %d days | Total Playtime: %d hours | Current Session: %d minutes", 
                           joinCount, streak, playtime, sessionTime);
    }

    /**
     * Check if a player has already reached a specific milestone
     */
    private boolean hasReachedMilestone(Player player, String type, int milestone) {
        UUID uuid = player.getUniqueId();
        Set<String> milestones = reachedMilestones.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet());
        return milestones.contains(type + ":" + milestone);
    }
    
    /**
     * Mark a milestone as reached for a player
     */
    private void markMilestoneReached(Player player, String type, int milestone) {
        UUID uuid = player.getUniqueId();
        Set<String> milestones = reachedMilestones.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet());
        milestones.add(type + ":" + milestone);
    }
    
    /**
     * Reset player data (for testing)
     */
    public void resetPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        lastSeenTimes.remove(uuid);
        streakData.remove(uuid);
        lastLoginDate.remove(uuid);
        reachedMilestones.remove(uuid);
        
        // Reset playtime data in DataManager
        plugin.getDataManager().resetPlaytimeData(player);
    }
    
    /**
     * Reset milestone data for offline player by UUID
     */
    public void resetPlayerData(UUID uuid) {
        lastSeenTimes.remove(uuid);
        streakData.remove(uuid);
        lastLoginDate.remove(uuid);
        reachedMilestones.remove(uuid);
    }
    
    /**
     * Clean up old milestone data to prevent memory leaks
     */
    public void cleanupOldData() {
        long maxAge = 30 * 24 * 60 * 60 * 1000L; // 30 days in milliseconds
        long currentTime = System.currentTimeMillis();
        
        // Clean up old milestone data
        reachedMilestones.entrySet().removeIf(entry -> {
            UUID uuid = entry.getKey();
            Long lastSeen = lastSeenTimes.get(uuid);
            
            // If player hasn't been seen in 30 days, remove their milestone data
            if (lastSeen != null && currentTime - lastSeen > maxAge) {
                lastSeenTimes.remove(uuid);
                streakData.remove(uuid);
                lastLoginDate.remove(uuid);
                return true; // Remove from reachedMilestones
            }
            return false;
        });
        
        // Clean up old streak data
        streakData.entrySet().removeIf(entry -> {
            UUID uuid = entry.getKey();
            Long lastSeen = lastSeenTimes.get(uuid);
            return lastSeen != null && currentTime - lastSeen > maxAge;
        });
        
        // Clean up old last seen data
        lastSeenTimes.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > maxAge);
            
        // Clean up old login date data
        lastLoginDate.entrySet().removeIf(entry -> {
            LocalDateTime loginDate = entry.getValue();
            return loginDate != null && 
                   ChronoUnit.DAYS.between(loginDate, LocalDateTime.now()) > 30;
        });
    }
}
