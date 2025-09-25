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
        
        // Validate theme configuration on startup
        validateThemeConfiguration();
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

            // Validate day ranges (basic validation)
            if (currentDay < 1 || currentDay > 31 || startDay < 1 || startDay > 31 || endDay < 1 || endDay > 31) {
                plugin.getLogger().warning("Invalid day in date: " + currentDate + " or " + startDate + " or " + endDate);
                return false;
            }

            // Convert to comparable values (month * 100 + day for easier comparison)
            int currentValue = currentMonth * 100 + currentDay;
            int startValue = startMonth * 100 + startDay;
            int endValue = endMonth * 100 + endDay;

            // Handle year rollover (e.g., winter theme from Dec to Feb)
            if (startValue > endValue) {
                // Range crosses year boundary (e.g., Dec 1 to Feb 28)
                return currentValue >= startValue || currentValue <= endValue;
            } else {
                // Range within same year (e.g., Jun 1 to Aug 31)
                return currentValue >= startValue && currentValue <= endValue;
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

            // Validate time format
            if (currentParts.length != 2 || startParts.length != 2 || endParts.length != 2) {
                plugin.getLogger().warning("Invalid time format: " + currentTime + " or " + startTime + " or " + endTime);
                return false;
            }

            int currentHour = Integer.parseInt(currentParts[0]);
            int currentMinute = Integer.parseInt(currentParts[1]);
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            // Validate hour and minute ranges
            if (currentHour < 0 || currentHour > 23 || startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23) {
                plugin.getLogger().warning("Invalid hour in time: " + currentTime + " or " + startTime + " or " + endTime);
                return false;
            }
            if (currentMinute < 0 || currentMinute > 59 || startMinute < 0 || startMinute > 59 || endMinute < 0 || endMinute > 59) {
                plugin.getLogger().warning("Invalid minute in time: " + currentTime + " or " + startTime + " or " + endTime);
                return false;
            }

            int currentMinutes = currentHour * 60 + currentMinute;
            int startMinutes = startHour * 60 + startMinute;
            int endMinutes = endHour * 60 + endMinute;

            // Handle day rollover (e.g., night theme from 22:00 to 05:59)
            if (startMinutes > endMinutes) {
                return currentMinutes >= startMinutes || currentMinutes <= endMinutes;
            } else {
                return currentMinutes >= startMinutes && currentMinutes <= endMinutes;
            }
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Error parsing time range (invalid number): " + e.getMessage());
            return false;
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

    /**
     * Validate theme configuration for conflicts and issues
     */
    public void validateThemeConfiguration() {
        plugin.getLogger().info("Validating theme configuration...");
        
        // Check for seasonal theme conflicts
        validateSeasonalThemes();
        
        // Check for time-based theme conflicts
        validateTimeBasedThemes();
        
        // Check for overlapping date ranges
        validateDateRangeOverlaps();
        
        plugin.getLogger().info("Theme configuration validation completed.");
    }

    /**
     * Validate seasonal themes for conflicts
     */
    private void validateSeasonalThemes() {
        ConfigurationSection seasonalThemes = plugin.getConfig().getConfigurationSection("themes.seasonal");
        if (seasonalThemes == null) return;

        for (String theme : seasonalThemes.getKeys(false)) {
            String start = plugin.getConfig().getString("themes.seasonal." + theme + ".start");
            String end = plugin.getConfig().getString("themes.seasonal." + theme + ".end");
            int priority = plugin.getConfig().getInt("themes.seasonal." + theme + ".priority", 0);
            
            if (start == null || end == null) {
                plugin.getLogger().warning("Seasonal theme '" + theme + "' is missing start or end date!");
                continue;
            }
            
            // Check for overlapping date ranges with other seasonal themes
            for (String otherTheme : seasonalThemes.getKeys(false)) {
                if (theme.equals(otherTheme)) continue;
                
                String otherStart = plugin.getConfig().getString("themes.seasonal." + otherTheme + ".start");
                String otherEnd = plugin.getConfig().getString("themes.seasonal." + otherTheme + ".end");
                int otherPriority = plugin.getConfig().getInt("themes.seasonal." + otherTheme + ".priority", 0);
                
                if (otherStart == null || otherEnd == null) continue;
                
                if (dateRangesOverlap(start, end, otherStart, otherEnd)) {
                    String winner = priority > otherPriority ? theme : otherTheme;
                    plugin.getLogger().info("Date range overlap detected between '" + theme + "' (" + start + "-" + end + 
                            ", priority " + priority + ") and '" + otherTheme + "' (" + otherStart + "-" + otherEnd + 
                            ", priority " + otherPriority + "). Winner: " + winner);
                }
            }
        }
    }

    /**
     * Validate time-based themes for conflicts
     */
    private void validateTimeBasedThemes() {
        ConfigurationSection timeThemes = plugin.getConfig().getConfigurationSection("themes.time-based");
        if (timeThemes == null) return;

        for (String theme : timeThemes.getKeys(false)) {
            if (theme.equals("enabled")) continue;
            
            String start = plugin.getConfig().getString("themes.time-based." + theme + ".start");
            String end = plugin.getConfig().getString("themes.time-based." + theme + ".end");
            int priority = plugin.getConfig().getInt("themes.time-based." + theme + ".priority", 0);
            
            if (start == null || end == null) {
                plugin.getLogger().warning("Time-based theme '" + theme + "' is missing start or end time!");
                continue;
            }
            
            // Check for overlapping time ranges with other time-based themes
            for (String otherTheme : timeThemes.getKeys(false)) {
                if (theme.equals(otherTheme) || otherTheme.equals("enabled")) continue;
                
                String otherStart = plugin.getConfig().getString("themes.time-based." + otherTheme + ".start");
                String otherEnd = plugin.getConfig().getString("themes.time-based." + otherTheme + ".end");
                int otherPriority = plugin.getConfig().getInt("themes.time-based." + otherTheme + ".priority", 0);
                
                if (otherStart == null || otherEnd == null) continue;
                
                if (timeRangesOverlap(start, end, otherStart, otherEnd)) {
                    String winner = priority > otherPriority ? theme : otherTheme;
                    plugin.getLogger().info("Time range overlap detected between '" + theme + "' (" + start + "-" + end + 
                            ", priority " + priority + ") and '" + otherTheme + "' (" + otherStart + "-" + otherEnd + 
                            ", priority " + otherPriority + "). Winner: " + winner);
                }
            }
        }
    }

    /**
     * Validate for overlapping date ranges
     */
    private void validateDateRangeOverlaps() {
        // This method can be expanded to check for more complex overlaps
        // For now, it's handled in the individual validation methods above
    }

    /**
     * Check if two date ranges overlap
     */
    private boolean dateRangesOverlap(String start1, String end1, String start2, String end2) {
        // Convert dates to comparable values
        int start1Value = parseDateToValue(start1);
        int end1Value = parseDateToValue(end1);
        int start2Value = parseDateToValue(start2);
        int end2Value = parseDateToValue(end2);
        
        // Handle year rollover for both ranges
        boolean range1RollsOver = start1Value > end1Value;
        boolean range2RollsOver = start2Value > end2Value;
        
        if (range1RollsOver && range2RollsOver) {
            // Both ranges roll over - they overlap if either start is in the other range
            return (start1Value <= start2Value && start2Value <= 1231) || (start2Value <= start1Value && start1Value <= 1231) ||
                   (start1Value <= end2Value && end2Value <= 1231) || (start2Value <= end1Value && end1Value <= 1231);
        } else if (range1RollsOver) {
            // Only range1 rolls over
            return (start2Value >= start1Value || start2Value <= end1Value) && (end2Value >= start1Value || end2Value <= end1Value);
        } else if (range2RollsOver) {
            // Only range2 rolls over
            return (start1Value >= start2Value || start1Value <= end2Value) && (end1Value >= start2Value || end1Value <= end2Value);
        } else {
            // Neither range rolls over
            return start1Value <= end2Value && start2Value <= end1Value;
        }
    }

    /**
     * Check if two time ranges overlap
     */
    private boolean timeRangesOverlap(String start1, String end1, String start2, String end2) {
        int start1Minutes = parseTimeToMinutes(start1);
        int end1Minutes = parseTimeToMinutes(end1);
        int start2Minutes = parseTimeToMinutes(start2);
        int end2Minutes = parseTimeToMinutes(end2);
        
        // Handle day rollover for both ranges
        boolean range1RollsOver = start1Minutes > end1Minutes;
        boolean range2RollsOver = start2Minutes > end2Minutes;
        
        if (range1RollsOver && range2RollsOver) {
            // Both ranges roll over - they overlap if either start is in the other range
            return (start1Minutes <= start2Minutes && start2Minutes <= 1439) || (start2Minutes <= start1Minutes && start1Minutes <= 1439) ||
                   (start1Minutes <= end2Minutes && end2Minutes <= 1439) || (start2Minutes <= end1Minutes && end1Minutes <= 1439);
        } else if (range1RollsOver) {
            // Only range1 rolls over
            return (start2Minutes >= start1Minutes || start2Minutes <= end1Minutes) && (end2Minutes >= start1Minutes || end2Minutes <= end1Minutes);
        } else if (range2RollsOver) {
            // Only range2 rolls over
            return (start1Minutes >= start2Minutes || start1Minutes <= end2Minutes) && (end1Minutes >= start2Minutes || end1Minutes <= end2Minutes);
        } else {
            // Neither range rolls over
            return start1Minutes <= end2Minutes && start2Minutes <= end1Minutes;
        }
    }

    /**
     * Parse date string to comparable integer value
     */
    private int parseDateToValue(String date) {
        String[] parts = date.split("-");
        int month = Integer.parseInt(parts[0]);
        int day = Integer.parseInt(parts[1]);
        return month * 100 + day;
    }

    /**
     * Parse time string to minutes since midnight
     */
    private int parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return hour * 60 + minute;
    }
}
