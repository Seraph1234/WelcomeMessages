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
    @SuppressWarnings("deprecation")
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // check if join messages are enabled
        if (!plugin.getConfig().getBoolean("messages.join.enabled", true)) {
            return;
        }

        // check if player has disabled their messages
        if (plugin.getDataManager().hasMessagesDisabled(player)) {
            return;
        }

        // cancel vanilla message if needed
        if (plugin.getConfig().getBoolean("messages.join.disable-vanilla", true)) {
            event.setJoinMessage(null);
        }

        // process join async for performance
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean isFirstJoin = plugin.getDataManager().isFirstJoin(player);

            // get the join message
            String message = plugin.getMessageManager().getJoinMessage(player, isFirstJoin);

            // broadcast message on main thread
            if (message != null && !message.isEmpty()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    // send to players with permission
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (onlinePlayer.hasPermission("welcome.see.join")) {
                            MessageUtils.sendMessage(onlinePlayer, message);
                        }
                    }

                    // console message
                    if (plugin.getConfig().getBoolean("messages.join.console", true)) {
                        MessageUtils.sendConsole(message);
                    }
                });
            }

            // title (with small delay to ensure player is fully loaded)
            if (plugin.getConfig().getBoolean("effects.title.enabled", true)) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        plugin.getEffectManager().sendTitle(player, isFirstJoin);
                    }
                }, 5L); // 5 ticks delay (0.25 seconds)
            }

            // effects with delay
            int effectDelay = plugin.getConfig().getInt("effects.delay-ticks", 20);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // sounds
                if (plugin.getConfig().getBoolean("effects.sound.enabled", true)) {
                    plugin.getEffectManager().playJoinSound(player, isFirstJoin);
                }

                // particles
                if (plugin.getConfig().getBoolean("effects.particles.enabled", true)) {
                    plugin.getEffectManager().playJoinParticles(player, isFirstJoin);
                }

                // fireworks
                if (plugin.getConfig().getBoolean("effects.fireworks.enabled", true)) {
                    if (isFirstJoin || plugin.getConfig().getBoolean("effects.fireworks.all-joins", false)) {
                        plugin.getEffectManager().launchFireworks(player);
                    }
                }
            }, effectDelay);

            // update player data
            plugin.getDataManager().updatePlayerData(player);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("deprecation")
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // check if quit messages enabled
        if (!plugin.getConfig().getBoolean("messages.quit.enabled", true)) {
            return;
        }

        // check if player has disabled their messages
        if (plugin.getDataManager().hasMessagesDisabled(player)) {
            return;
        }

        // cancel vanilla message
        if (plugin.getConfig().getBoolean("messages.quit.disable-vanilla", true)) {
            event.setQuitMessage(null);
        }

        // get quit message
        String message = plugin.getMessageManager().getQuitMessage(player);

        if (message != null && !message.isEmpty()) {
            // send to players with permission
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("welcome.see.quit")) {
                    MessageUtils.sendMessage(onlinePlayer, message);
                }
            }

            // console
            if (plugin.getConfig().getBoolean("messages.quit.console", true)) {
                MessageUtils.sendConsole(message);
            }
        }

        // save last seen
        plugin.getDataManager().setLastSeen(player);
        
        // cleanup animations for this player
        plugin.getAnimationUtils().cleanupPlayerAnimations(player.getUniqueId());
    }
}