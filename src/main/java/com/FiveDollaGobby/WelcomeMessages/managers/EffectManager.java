package com.FiveDollaGobby.WelcomeMessages.managers;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import com.FiveDollaGobby.WelcomeMessages.utils.MessageUtils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class EffectManager implements Listener {

    private final WelcomePlugin plugin;
    private final Set<Firework> safeFireworks = new HashSet<>();
    // Using ThreadLocalRandom for better performance

    public EffectManager(WelcomePlugin plugin) {
        this.plugin = plugin;
        // Register this class as an event listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void reload() {
        // nothing to reload yet
    }
    
    /**
     * Add a firework to the safe fireworks set to prevent it from causing damage
     */
    public void addSafeFirework(Firework firework) {
        safeFireworks.add(firework);
        
        // Remove the firework from the set after 10 seconds to prevent memory leaks
        new BukkitRunnable() {
            @Override
            public void run() {
                safeFireworks.remove(firework);
            }
        }.runTaskLater(plugin, 200L); // 10 seconds = 200 ticks
    }
    
    /**
     * Event handler to prevent damage from safe fireworks
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            Firework firework = (Firework) event.getDamager();
            if (safeFireworks.contains(firework)) {
                event.setCancelled(true);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void sendTitle(Player player, boolean isFirstJoin) {
        String title, subtitle;
        int fadeIn = plugin.getConfig().getInt("effects.title.fade-in", 10);
        int stay = plugin.getConfig().getInt("effects.title.stay", 70);
        int fadeOut = plugin.getConfig().getInt("effects.title.fade-out", 20);

        // Get current theme
        String currentTheme = plugin.getMessageManager().getThemeManager().getCurrentTheme();
        
        // Check if theme-specific titles exist in config
        String themeTitlePath = "effects.title.themes." + currentTheme + "." + (isFirstJoin ? "first-join" : "regular");
        String configTitle = plugin.getConfig().getString(themeTitlePath + ".title");
        String configSubtitle = plugin.getConfig().getString(themeTitlePath + ".subtitle");
        
        if (configTitle != null && !configTitle.isEmpty()) {
            // Use theme-specific titles from config
            title = MessageUtils.colorize(configTitle.replace("{player}", player.getName()));
            subtitle = MessageUtils.colorize(configSubtitle.replace("{player}", player.getName()));
        } else {
            // Use default titles
            if (isFirstJoin) {
                title = plugin.getConfig().getString("effects.title.first-join.title", "&6&lWELCOME!");
                subtitle = plugin.getConfig().getString("effects.title.first-join.subtitle", "&eEnjoy your stay, {player}!");
            } else {
                title = plugin.getConfig().getString("effects.title.regular.title", "&a&lWelcome Back!");
                subtitle = plugin.getConfig().getString("effects.title.regular.subtitle", "&7Good to see you again, {player}!");
            }
            
            // Replace placeholders
            title = MessageUtils.colorize(title.replace("{player}", player.getName()));
            subtitle = MessageUtils.colorize(subtitle.replace("{player}", player.getName()));
        }

        // Send the title
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
    
    @SuppressWarnings("deprecation")
    public void sendTitle(Player player, boolean isFirstJoin, String theme) {
        String title, subtitle;
        // Use smaller, faster title effects for theme testing
        int fadeIn = 5;   // Faster fade in
        int stay = 40;    // Shorter display time
        int fadeOut = 10; // Faster fade out

        // Check if theme-specific titles exist in config (same as normal join)
        String themeTitlePath = "effects.title.themes." + theme + "." + (isFirstJoin ? "first-join" : "regular");
        String configTitle = plugin.getConfig().getString(themeTitlePath + ".title");
        String configSubtitle = plugin.getConfig().getString(themeTitlePath + ".subtitle");
        
        if (configTitle != null && !configTitle.isEmpty()) {
            // Use theme-specific titles from config (same as normal join)
            title = MessageUtils.colorize(configTitle.replace("{player}", player.getName()));
            subtitle = MessageUtils.colorize(configSubtitle.replace("{player}", player.getName()));
        } else {
            // Fallback to default titles if no theme messages exist
            if (isFirstJoin) {
                title = plugin.getConfig().getString("effects.title.first-join.title", "&6&lWELCOME!");
                subtitle = plugin.getConfig().getString("effects.title.first-join.subtitle", "&eEnjoy your stay, {player}!");
            } else {
                title = plugin.getConfig().getString("effects.title.regular.title", "&a&lWelcome Back!");
                subtitle = plugin.getConfig().getString("effects.title.regular.subtitle", "&7Good to see you again, {player}!");
            }
            
            // Replace placeholders
            title = MessageUtils.colorize(title.replace("{player}", player.getName()));
            subtitle = MessageUtils.colorize(subtitle.replace("{player}", player.getName()));
        }

        // Using deprecated method for compatibility
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public void playJoinSound(Player player, boolean isFirstJoin) {
        Sound sound;
        float volume = (float) plugin.getConfig().getDouble("effects.sound.volume", 1.0);
        float pitch = (float) plugin.getConfig().getDouble("effects.sound.pitch", 1.0);

        try {
            if (isFirstJoin) {
                @SuppressWarnings("deprecation")
                Sound firstJoinSound = Sound.valueOf(plugin.getConfig().getString("effects.sound.first-join", "UI_TOAST_CHALLENGE_COMPLETE"));
                sound = firstJoinSound;
            } else {
                @SuppressWarnings("deprecation")
                Sound regularSound = Sound.valueOf(plugin.getConfig().getString("effects.sound.regular", "ENTITY_PLAYER_LEVELUP"));
                sound = regularSound;
            }

            // play to player
            if (plugin.getConfig().getBoolean("effects.sound.to-player", true)) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }

            // play to nearby players
            if (plugin.getConfig().getBoolean("effects.sound.to-others", false)) {
                int radius = plugin.getConfig().getInt("effects.sound.radius", 10);
                player.getNearbyEntities(radius, radius, radius).stream()
                        .filter(entity -> entity instanceof Player)
                        .map(entity -> (Player) entity)
                        .forEach(p -> p.playSound(player.getLocation(), sound, volume * 0.5f, pitch));
            }
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound in config: " + e.getMessage());
        }
    }

    public void playJoinParticles(Player player, boolean isFirstJoin) {
        Particle particle;
        int count = plugin.getConfig().getInt("effects.particles.count", 50);
        double offsetX = plugin.getConfig().getDouble("effects.particles.offset.x", 0.5);
        double offsetY = plugin.getConfig().getDouble("effects.particles.offset.y", 1.0);
        double offsetZ = plugin.getConfig().getDouble("effects.particles.offset.z", 0.5);
        double speed = plugin.getConfig().getDouble("effects.particles.speed", 0.1);

        try {
            if (isFirstJoin) {
                particle = Particle.valueOf(plugin.getConfig().getString("effects.particles.first-join", "TOTEM"));
            } else {
                particle = Particle.valueOf(plugin.getConfig().getString("effects.particles.regular", "VILLAGER_HAPPY"));
            }

            Location loc = player.getLocation();

            if (plugin.getConfig().getBoolean("effects.particles.animated", true)) {
                // animated spiral
                new BukkitRunnable() {
                    int iteration = 0;
                    final int maxIterations = 20;

                    @Override
                    public void run() {
                        if (iteration >= maxIterations || !player.isOnline()) {
                            cancel();
                            return;
                        }

                        double angle = (2 * Math.PI * iteration) / maxIterations;
                        double x = Math.cos(angle) * 1.5;
                        double z = Math.sin(angle) * 1.5;
                        double y = iteration * 0.15;

                        Location particleLoc = loc.clone().add(x, y, z);
                        player.getWorld().spawnParticle(particle, particleLoc, 5, 0.1, 0.1, 0.1, speed);

                        iteration++;
                    }
                }.runTaskTimer(plugin, 0L, 2L);
            } else {
                // static burst
                player.getWorld().spawnParticle(particle, loc.add(0, 1, 0), count, offsetX, offsetY, offsetZ, speed);
            }

        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid particle in config: " + e.getMessage());
        }
    }

    public void launchFireworks(Player player) {
        int amount = plugin.getConfig().getInt("effects.fireworks.amount", 3);
        int delayBetween = plugin.getConfig().getInt("effects.fireworks.delay-between", 20);

        new BukkitRunnable() {
            int launched = 0;

            @Override
            public void run() {
                if (launched >= amount || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location loc = player.getLocation();
                // randomize location a bit
                double offsetX = (ThreadLocalRandom.current().nextDouble() - 0.5) * 4;
                double offsetZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * 4;
                loc.add(offsetX, 0, offsetZ);

                try {
                    Firework fw = (Firework) player.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
                FireworkMeta fwMeta = fw.getFireworkMeta();

                // random firework stuff
                FireworkEffect.Type type = FireworkEffect.Type.values()[ThreadLocalRandom.current().nextInt(FireworkEffect.Type.values().length)];
                Color color = getRandomColor();
                Color fadeColor = getRandomColor();

                FireworkEffect effect = FireworkEffect.builder()
                        .withColor(color)
                        .withFade(fadeColor)
                        .with(type)
                        .trail(ThreadLocalRandom.current().nextBoolean())
                        .flicker(ThreadLocalRandom.current().nextBoolean())
                        .build();

                fwMeta.addEffect(effect);
                fwMeta.setPower(ThreadLocalRandom.current().nextInt(2) + 1);
                fw.setFireworkMeta(fwMeta);
                
                // Prevent firework from causing damage to players
                fw.setShotAtAngle(false);
                
                // Store the firework in a set to track it for damage prevention
                EffectManager.this.addSafeFirework(fw);

                launched++;
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to spawn firework for player " + player.getName() + ": " + e.getMessage());
                    // Continue with next firework attempt
                }
            }
        }.runTaskTimer(plugin, 0L, delayBetween);
    }

    private Color getRandomColor() {
        Color[] colors = {
                Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
                Color.PURPLE, Color.ORANGE, Color.WHITE, Color.AQUA,
                Color.FUCHSIA, Color.LIME, Color.TEAL, Color.SILVER
        };
        return colors[ThreadLocalRandom.current().nextInt(colors.length)];
    }
    
    
}