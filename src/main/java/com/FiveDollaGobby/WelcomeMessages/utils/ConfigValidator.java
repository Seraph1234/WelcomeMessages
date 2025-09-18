package com.FiveDollaGobby.WelcomeMessages.utils;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigValidator {

    private final WelcomePlugin plugin;
    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();

    public ConfigValidator(WelcomePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean validateConfig() {
        errors.clear();
        warnings.clear();

        FileConfiguration config = plugin.getConfig();
        FileConfiguration messagesConfig = plugin.getMessagesConfig();

        // Validate general settings
        validateGeneralSettings(config);

        // Validate message settings
        validateMessageSettings(config, messagesConfig);

        // Validate effects
        validateEffects(config);

        // Validate performance settings
        validatePerformanceSettings(config);

        // Log results
        logValidationResults();

        return errors.isEmpty();
    }

    private void validateGeneralSettings(FileConfiguration config) {
        // Check save interval
        int saveInterval = config.getInt("general.save-interval", 10);
        if (saveInterval < 1) {
            errors.add("general.save-interval must be at least 1 minute");
        } else if (saveInterval > 60) {
            warnings.add("general.save-interval is very high (" + saveInterval + " minutes) - consider reducing for better data safety");
        }
    }

    private void validateMessageSettings(FileConfiguration config, FileConfiguration messagesConfig) {
        // Check if messages are enabled but no messages exist
        if (config.getBoolean("messages.join.enabled", true)) {
            List<String> defaultJoinMessages = messagesConfig.getStringList("messages.join.default");
            if (defaultJoinMessages.isEmpty()) {
                errors.add("messages.join.enabled is true but no default join messages found");
            }
        }

        if (config.getBoolean("messages.quit.enabled", true)) {
            List<String> defaultQuitMessages = messagesConfig.getStringList("messages.quit.default");
            if (defaultQuitMessages.isEmpty()) {
                errors.add("messages.quit.enabled is true but no default quit messages found");
            }
        }

        // Validate RGB settings
        boolean rgbEnabled = config.getBoolean("messages.rgb.enabled", true);
        if (rgbEnabled) {
            // Check if server supports RGB (basic check)
            try {
                net.md_5.bungee.api.ChatColor.of("#FF0000");
            } catch (Exception e) {
                warnings.add("RGB colors enabled but server may not support them (requires 1.16+)");
            }
        }
    }

    private void validateEffects(FileConfiguration config) {
        // Validate sound settings
        if (config.getBoolean("effects.sound.enabled", true)) {
            validateSound(config, "effects.sound.first-join", "UI_TOAST_CHALLENGE_COMPLETE");
            validateSound(config, "effects.sound.regular", "ENTITY_PLAYER_LEVELUP");
        }

        // Validate particle settings
        if (config.getBoolean("effects.particles.enabled", true)) {
            validateParticle(config, "effects.particles.first-join", "TOTEM");
            validateParticle(config, "effects.particles.regular", "VILLAGER_HAPPY");
        }

        // Validate title settings
        if (config.getBoolean("effects.title.enabled", true)) {
            int fadeIn = config.getInt("effects.title.fade-in", 10);
            int stay = config.getInt("effects.title.stay", 70);
            int fadeOut = config.getInt("effects.title.fade-out", 20);

            if (fadeIn < 0 || fadeIn > 100) {
                errors.add("effects.title.fade-in must be between 0 and 100");
            }
            if (stay < 0 || stay > 200) {
                errors.add("effects.title.stay must be between 0 and 200");
            }
            if (fadeOut < 0 || fadeOut > 100) {
                errors.add("effects.title.fade-out must be between 0 and 100");
            }
        }

        // Validate firework settings
        if (config.getBoolean("effects.fireworks.enabled", true)) {
            int amount = config.getInt("effects.fireworks.amount", 3);
            int delay = config.getInt("effects.fireworks.delay-between", 20);

            if (amount < 1 || amount > 20) {
                errors.add("effects.fireworks.amount must be between 1 and 20");
            }
            if (delay < 1 || delay > 200) {
                errors.add("effects.fireworks.delay-between must be between 1 and 200");
            }
        }
    }

    private void validatePerformanceSettings(FileConfiguration config) {
        int cacheTime = config.getInt("performance.cache-time", 5);
        int maxCacheSize = config.getInt("performance.max-cache-size", 100);

        if (cacheTime < 1 || cacheTime > 60) {
            errors.add("performance.cache-time must be between 1 and 60 minutes");
        }
        if (maxCacheSize < 10 || maxCacheSize > 1000) {
            errors.add("performance.max-cache-size must be between 10 and 1000");
        }
    }

    private void validateSound(FileConfiguration config, String path, String defaultValue) {
        String soundName = config.getString(path, defaultValue);
        try {
            // Just check if it exists, don't use the deprecated method
            @SuppressWarnings({"deprecation", "unused"})
            Sound sound = Sound.valueOf(soundName);
        } catch (IllegalArgumentException e) {
            errors.add("Invalid sound '" + soundName + "' in " + path + " - using default");
            config.set(path, defaultValue);
        }
    }

    private void validateParticle(FileConfiguration config, String path, String defaultValue) {
        String particleName = config.getString(path, defaultValue);
        try {
            Particle.valueOf(particleName);
        } catch (IllegalArgumentException e) {
            errors.add("Invalid particle '" + particleName + "' in " + path + " - using default");
            config.set(path, defaultValue);
        }
    }

    private void logValidationResults() {
        if (!errors.isEmpty()) {
            MessageUtils.sendConsole("&cConfiguration validation failed:");
            for (String error : errors) {
                MessageUtils.sendConsole("&c- " + error);
            }
            MessageUtils.sendConsole("&cPlease fix these errors and reload the plugin!");
        }

        if (!warnings.isEmpty()) {
            MessageUtils.sendConsole("&eConfiguration warnings:");
            for (String warning : warnings) {
                MessageUtils.sendConsole("&e- " + warning);
            }
        }

        if (errors.isEmpty() && warnings.isEmpty()) {
            MessageUtils.sendConsole("&aConfiguration validation passed!");
        }
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }
}
