package com.FiveDollaGobby.WelcomeMessages.placeholders;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class PlaceholderAPIExpansion {
    
    private final WelcomePlaceholders placeholders;
    private Object expansion;

    public PlaceholderAPIExpansion(WelcomePlugin plugin) {
        this.placeholders = new WelcomePlaceholders(plugin);
        
        try {
            // Create the expansion using reflection
            Class.forName("me.clip.placeholderapi.expansion.PlaceholderExpansion");
            this.expansion = new Object() {
                @SuppressWarnings("unused")
                public String getIdentifier() { return "welcome"; }
                @SuppressWarnings("unused")
                public String getAuthor() { return "FiveDollaGobby"; }
                @SuppressWarnings("unused")
                public String getVersion() { 
                    @SuppressWarnings("deprecation")
                    var version = plugin.getDescription().getVersion();
                    return version;
                }
                @SuppressWarnings("unused")
                public boolean persist() { return true; }
                @SuppressWarnings("unused")
                public String onPlaceholderRequest(Player player, String params) {
                    return placeholders.getPlaceholder(player, params);
                }
            };
        } catch (Exception e) {
            this.expansion = null;
        }
    }

    public boolean register() {
        if (expansion == null) {
            return false;
        }
        
        try {
            // Use reflection to register with PlaceholderAPI
            Class<?> placeholderAPI = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            Method registerMethod = placeholderAPI.getMethod("registerExpansion", Object.class);
            registerMethod.invoke(null, expansion);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
