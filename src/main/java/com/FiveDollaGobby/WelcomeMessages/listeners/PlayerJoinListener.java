package com.FiveDollaGobby.WelcomeMessages.listeners;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import com.FiveDollaGobby.WelcomeMessages.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {

    private final WelcomePlugin plugin;

    public PlayerJoinListener(WelcomePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Check if join messages are enabled
        if (!plugin.getConfig().getBoolean("messages.join.enabled", true)) {
            return;
        }

        // Cancel vanilla message if configured
        if (plugin.getConfig().getBoolean("messages.join.disable-vanilla", true)) {
            event.setJoinMessage(null);
        }

        // Run join tasks asynchronously for better performance
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean isFirstJoin = plugin.getDataManager().isFirstJoin(player);

            // Process join message
            String message = plugin.getMessageManager().getJoinMessage(player, isFirstJoin);

            // Broadcast message on main thread
            if (message != null && !message.isEmpty()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    // Send to all players with permission
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (onlinePlayer.hasPermission("welcome.see.join")) {
                            MessageUtils.sendMessage(onlinePlayer, message);
                        }
                    }

                    // Console message
                    if (plugin.getConfig().getBoolean("messages.join.console", true)) {
                        MessageUtils.sendConsole(message);
                    }
                });
            }

            // Handle welcome title
            if (plugin.getConfig().getBoolean("effects.title.enabled", true)) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    plugin.getEffectManager().sendTitle(player, isFirstJoin);
                });
            }

            // Handle effects with delay
            int effectDelay = plugin.getConfig().getInt("effects.delay-ticks", 20);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // Sound effects
                if (plugin.getConfig().getBoolean("effects.sound.enabled", true)) {
                    plugin.getEffectManager().playJoinSound(player, isFirstJoin);
                }

                // Particle effects
                if (plugin.getConfig().getBoolean("effects.particles.enabled", true)) {
                    plugin.getEffectManager().playJoinParticles(player, isFirstJoin);
                }

                // Fireworks
                if (plugin.getConfig().getBoolean("effects.fireworks.enabled", true)) {
                    if (isFirstJoin || plugin.getConfig().getBoolean("effects.fireworks.all-joins", false)) {
                        plugin.getEffectManager().launchFireworks(player);
                    }
                }
            }, effectDelay);

            // Update player data
            plugin.getDataManager().updatePlayerData(player);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Check if quit messages are enabled
        if (!plugin.getConfig().getBoolean("messages.quit.enabled", true)) {
            return;
        }

        // Cancel vanilla message if configured
        if (plugin.getConfig().getBoolean("messages.quit.disable-vanilla", true)) {
            event.setQuitMessage(null);
        }

        // Get quit message
        String message = plugin.getMessageManager().getQuitMessage(player);

        if (message != null && !message.isEmpty()) {
            // Send to all players with permission
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("welcome.see.quit")) {
                    MessageUtils.sendMessage(onlinePlayer, message);
                }
            }

            // Console message
            if (plugin.getConfig().getBoolean("messages.quit.console", true)) {
                MessageUtils.sendConsole(message);
            }
        }

        // Save last seen time
        plugin.getDataManager().setLastSeen(player);
    }
}