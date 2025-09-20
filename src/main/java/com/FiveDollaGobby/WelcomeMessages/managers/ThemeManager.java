package com.FiveDollaGobby.WelcomeMessages.managers;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ThemeManager {

    private final WelcomePlugin plugin;
    private String currentTheme;
    private long lastThemeCheck;

    public ThemeManager(WelcomePlugin plugin) {
        this.plugin = plugin;
        this.currentTheme = "default";
        this.lastThemeCheck = System.currentTimeMillis();
        updateTheme();
    }

    /**
     * Get the current active theme
     */
    public String getCurrentTheme() {
        // Check if we need to update theme
        long checkInterval = plugin.getConfig().getLong("themes.check-interval", 60) * 60 * 1000; // Convert minutes to milliseconds
        if (System.currentTimeMillis() - lastThemeCheck > checkInterval) {
            updateTheme();
            lastThemeCheck = System.currentTimeMillis();
        }
        return currentTheme;
    }

    /**
     * Update the current theme based on configuration
     */
    private void updateTheme() {
        if (!plugin.getConfig().getBoolean("themes.enabled", true)) {
            currentTheme = "default";
            return;
        }

        String configTheme = plugin.getConfig().getString("themes.current", "auto");
        if (!configTheme.equals("auto")) {
            currentTheme = configTheme;
            return;
        }

        if (!plugin.getConfig().getBoolean("themes.auto-detect", true)) {
            currentTheme = "default";
            return;
        }

        // Find the highest priority theme that matches current conditions
        String detectedTheme = detectTheme();
        currentTheme = detectedTheme != null ? detectedTheme : "default";
    }

    /**
     * Detect the appropriate theme based on date and time
     */
    private String detectTheme() {
        Map<String, Integer> activeThemes = new HashMap<>();

        // Check seasonal themes
        if (plugin.getConfig().getBoolean("themes.seasonal", true)) {
            String seasonalTheme = detectSeasonalTheme();
            if (seasonalTheme != null) {
                int priority = plugin.getConfig().getInt("themes.seasonal." + seasonalTheme + ".priority", 0);
                activeThemes.put(seasonalTheme, priority);
            }
        }

        // Check time-based themes
        if (plugin.getConfig().getBoolean("themes.time-based.enabled", true)) {
            String timeTheme = detectTimeBasedTheme();
            if (timeTheme != null) {
                int priority = plugin.getConfig().getInt("themes.time-based." + timeTheme + ".priority", 0);
                activeThemes.put(timeTheme, priority);
            }
        }

        // Return the theme with highest priority
        return activeThemes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Detect seasonal theme based on current date
     */
    private String detectSeasonalTheme() {
        LocalDate now = LocalDate.now();
        String currentDate = now.format(DateTimeFormatter.ofPattern("MM-dd"));

        ConfigurationSection seasonalThemes = plugin.getConfig().getConfigurationSection("themes.seasonal");
        if (seasonalThemes == null) return null;

        for (String theme : seasonalThemes.getKeys(false)) {
            String start = plugin.getConfig().getString("themes.seasonal." + theme + ".start");
            String end = plugin.getConfig().getString("themes.seasonal." + theme + ".end");
            
            if (start != null && end != null && isDateInRange(currentDate, start, end)) {
                return theme;
            }
        }

        return null;
    }

    /**
     * Detect time-based theme based on current time
     */
    private String detectTimeBasedTheme() {
        LocalTime now = LocalTime.now();
        String currentTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));

        ConfigurationSection timeThemes = plugin.getConfig().getConfigurationSection("themes.time-based");
        if (timeThemes == null) return null;

        for (String theme : timeThemes.getKeys(false)) {
            if (theme.equals("enabled")) continue;
            
            String start = plugin.getConfig().getString("themes.time-based." + theme + ".start");
            String end = plugin.getConfig().getString("themes.time-based." + theme + ".end");
            
            if (start != null && end != null && isTimeInRange(currentTime, start, end)) {
                return theme;
            }
        }

        return null;
    }

    /**
     * Check if a date is within a range
     */
    private boolean isDateInRange(String currentDate, String startDate, String endDate) {
        try {
            String[] currentParts = currentDate.split("-");
            String[] startParts = startDate.split("-");
            String[] endParts = endDate.split("-");

            if (currentParts.length != 2 || startParts.length != 2 || endParts.length != 2) {
                plugin.getLogger().warning("Invalid date format: " + currentDate + " or " + startDate + " or " + endDate);
                return false;
            }

            int currentMonth = Integer.parseInt(currentParts[0]);
            int currentDay = Integer.parseInt(currentParts[1]);
            int startMonth = Integer.parseInt(startParts[0]);
            int startDay = Integer.parseInt(startParts[1]);
            int endMonth = Integer.parseInt(endParts[0]);
            int endDay = Integer.parseInt(endParts[1]);

            // Validate month and day ranges
            if (currentMonth < 1 || currentMonth > 12 || startMonth < 1 || startMonth > 12 || endMonth < 1 || endMonth > 12) {
                plugin.getLogger().warning("Invalid month in date: " + currentDate + " or " + startDate + " or " + endDate);
                return false;
            }

            // Handle year rollover (e.g., winter theme from Dec to Feb)
            if (startMonth > endMonth) {
                return (currentMonth > startMonth || (currentMonth == startMonth && currentDay >= startDay)) ||
                       (currentMonth < endMonth || (currentMonth == endMonth && currentDay <= endDay));
            } else {
                return (currentMonth > startMonth || (currentMonth == startMonth && currentDay >= startDay)) &&
                       (currentMonth < endMonth || (currentMonth == endMonth && currentDay <= endDay));
            }
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Error parsing date range (invalid number): " + e.getMessage());
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Error parsing date range: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if a time is within a range
     */
    private boolean isTimeInRange(String currentTime, String startTime, String endTime) {
        try {
            String[] currentParts = currentTime.split(":");
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");

            int currentHour = Integer.parseInt(currentParts[0]);
            int currentMinute = Integer.parseInt(currentParts[1]);
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            int currentMinutes = currentHour * 60 + currentMinute;
            int startMinutes = startHour * 60 + startMinute;
            int endMinutes = endHour * 60 + endMinute;

            // Handle day rollover (e.g., night theme from 22:00 to 06:00)
            if (startMinutes > endMinutes) {
                return currentMinutes >= startMinutes || currentMinutes <= endMinutes;
            } else {
                return currentMinutes >= startMinutes && currentMinutes <= endMinutes;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error parsing time range: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get theme-specific messages for a player
     */
    public List<String> getThemeMessages(String messageType, boolean isFirstJoin) {
        String theme = getCurrentTheme();
        if (theme.equals("default")) {
            return null; // Use default messages
        }

        String path = "messages.join.themes." + theme + "." + (isFirstJoin ? "first-time" : "default");
        List<String> messages = plugin.getMessagesConfig().getStringList(path);
        
        return messages.isEmpty() ? null : messages;
    }

    /**
     * Get all available themes
     */
    public List<String> getAvailableThemes() {
        List<String> themes = new ArrayList<>();
        themes.add("default");
        
        ConfigurationSection seasonalThemes = plugin.getConfig().getConfigurationSection("themes.seasonal");
        if (seasonalThemes != null) {
            themes.addAll(seasonalThemes.getKeys(false));
        }
        
        ConfigurationSection timeThemes = plugin.getConfig().getConfigurationSection("themes.time-based");
        if (timeThemes != null) {
            for (String theme : timeThemes.getKeys(false)) {
                if (!theme.equals("enabled")) {
                    themes.add(theme);
                }
            }
        }
        
        return themes;
    }

    /**
     * Force set a theme (for testing)
     */
    public void setTheme(String theme) {
        this.currentTheme = theme;
    }

    /**
     * Get theme information
     */
    public String getThemeInfo() {
        String theme = getCurrentTheme();
        if (theme.equals("default")) {
            return "Default theme (no special styling)";
        }
        
        String description = plugin.getConfig().getString("themes.seasonal." + theme + ".description", 
                plugin.getConfig().getString("themes.time-based." + theme + ".description", "Custom theme"));
        
        return theme.toUpperCase() + " theme: " + description;
    }
}
