package com.FiveDollaGobby.WelcomeMessages.managers;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import com.FiveDollaGobby.WelcomeMessages.utils.MessageUtils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class EffectManager {

    private final WelcomePlugin plugin;
    private final Random random = new Random();

    public EffectManager(WelcomePlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        // nothing to reload yet
    }

    public void sendTitle(Player player, boolean isFirstJoin) {
        String title, subtitle;
        int fadeIn = plugin.getConfig().getInt("effects.title.fade-in", 10);
        int stay = plugin.getConfig().getInt("effects.title.stay", 70);
        int fadeOut = plugin.getConfig().getInt("effects.title.fade-out", 20);

        if (isFirstJoin) {
            title = plugin.getConfig().getString("effects.title.first-join.title", "&6&lWELCOME!");
            subtitle = plugin.getConfig().getString("effects.title.first-join.subtitle", "&eEnjoy your stay, {player}!");
        } else {
            title = plugin.getConfig().getString("effects.title.regular.title", "&a&lWelcome Back!");
            subtitle = plugin.getConfig().getString("effects.title.regular.subtitle", "&7Good to see you again, {player}!");
        }

        // replace placeholders
        title = MessageUtils.colorize(title.replace("{player}", player.getName()));
        subtitle = MessageUtils.colorize(subtitle.replace("{player}", player.getName()));

        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public void playJoinSound(Player player, boolean isFirstJoin) {
        Sound sound;
        float volume = (float) plugin.getConfig().getDouble("effects.sound.volume", 1.0);
        float pitch = (float) plugin.getConfig().getDouble("effects.sound.pitch", 1.0);

        try {
            if (isFirstJoin) {
                sound = Sound.valueOf(plugin.getConfig().getString("effects.sound.first-join", "UI_TOAST_CHALLENGE_COMPLETE"));
            } else {
                sound = Sound.valueOf(plugin.getConfig().getString("effects.sound.regular", "ENTITY_PLAYER_LEVELUP"));
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
                double offsetX = (random.nextDouble() - 0.5) * 4;
                double offsetZ = (random.nextDouble() - 0.5) * 4;
                loc.add(offsetX, 0, offsetZ);

                Firework fw = (Firework) player.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
                FireworkMeta fwMeta = fw.getFireworkMeta();

                // random firework stuff
                FireworkEffect.Type type = FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)];
                Color color = getRandomColor();
                Color fadeColor = getRandomColor();

                FireworkEffect effect = FireworkEffect.builder()
                        .withColor(color)
                        .withFade(fadeColor)
                        .with(type)
                        .trail(random.nextBoolean())
                        .flicker(random.nextBoolean())
                        .build();

                fwMeta.addEffect(effect);
                fwMeta.setPower(random.nextInt(2) + 1);
                fw.setFireworkMeta(fwMeta);

                launched++;
            }
        }.runTaskTimer(plugin, 0L, delayBetween);
    }

    private Color getRandomColor() {
        Color[] colors = {
                Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
                Color.PURPLE, Color.ORANGE, Color.WHITE, Color.AQUA,
                Color.FUCHSIA, Color.LIME, Color.TEAL, Color.SILVER
        };
        return colors[random.nextInt(colors.length)];
    }
}