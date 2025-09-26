package com.FiveDollaGobby.WelcomeMessages.utils;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.ConfigurationSection;
import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class AnimationUtils {

    private final WelcomePlugin plugin;
    private final Map<UUID, List<BukkitRunnable>> activeAnimations = new ConcurrentHashMap<>();

    public AnimationUtils(WelcomePlugin plugin) {
        this.plugin = plugin;
        startPeriodicCleanup();
    }
    
    /**
     * Start periodic cleanup of old animations
     */
    private void startPeriodicCleanup() {
        // Clean up every 5 minutes
        new BukkitRunnable() {
            @Override
            public void run() {
                cleanupOldAnimations();
            }
        }.runTaskTimerAsynchronously(plugin, 20 * 60 * 5, 20 * 60 * 5); // Every 5 minutes
    }
    
    /**
     * Clean up old animations that may have been left running
     */
    private void cleanupOldAnimations() {
        activeAnimations.entrySet().removeIf(entry -> {
            List<BukkitRunnable> tasks = entry.getValue();
            boolean allCancelled = true;
            for (BukkitRunnable task : tasks) {
                if (!task.isCancelled()) {
                    allCancelled = false;
                    break;
                }
            }
            return allCancelled;
        });
    }

    /**
     * Animate a message with various effects
     */
    public void animateMessage(Player player, String message, String animationType, int duration) {
        if (message == null || message.isEmpty()) {
            return;
        }
        
        // Ensure minimum duration to prevent division by zero
        if (duration <= 0) {
            duration = 60; // Default to 3 seconds
        }

        // Check if this is a multi-layer animation
        if (isMultiLayerAnimation(animationType)) {
            animateMultiLayer(player, message, animationType, duration);
            return;
        }

        switch (animationType.toLowerCase()) {
            case "typing":
                animateTyping(player, message, duration);
                break;
            case "fade":
                animateFade(player, message, duration);
                break;
            case "slide":
                animateSlide(player, message, duration);
                break;
            case "wave":
                animateWave(player, message, duration);
                break;
            case "rainbow":
                animateRainbow(player, message, duration);
                break;
            case "glitch":
                animateGlitch(player, message, duration);
                break;
            case "typewriter":
                animateTypewriter(player, message, duration);
                break;
            case "bounce":
                animateBounce(player, message, duration);
                break;
            case "shake":
                animateShake(player, message, duration);
                break;
            case "pulse":
                animatePulse(player, message, duration);
                break;
            case "matrix":
                animateMatrix(player, message, duration);
                break;
            case "scramble":
                animateScramble(player, message, duration);
                break;
            default:
                // Default to typing if unknown animation
                animateTyping(player, message, duration);
                break;
        }
    }

    /**
     * Check if the animation type is a multi-layer animation
     */
    private boolean isMultiLayerAnimation(String animationType) {
        if (!plugin.getConfig().getBoolean("animations.multi-layer.enabled", true)) {
            return false;
        }
        
        ConfigurationSection combinations = plugin.getConfig().getConfigurationSection("animations.multi-layer.combinations");
        if (combinations == null) {
            return false;
        }
        
        return combinations.getKeys(false).contains(animationType.toLowerCase());
    }

    /**
     * Animate a message with multiple effects combined
     */
    private void animateMultiLayer(Player player, String message, String animationType, int duration) {
        String path = "animations.multi-layer.combinations." + animationType.toLowerCase();
        List<String> effects = plugin.getConfig().getStringList(path + ".effects");
        int configDuration = plugin.getConfig().getInt(path + ".duration", duration);
        
        if (effects.isEmpty()) {
            // Fallback to single animation
            animateTyping(player, message, duration);
            return;
        }

        // Use configured duration if available
        if (configDuration > 0) {
            duration = configDuration;
        }

        // Calculate duration per effect with safety check
        int effectDuration = effects.size() > 0 ? duration / effects.size() : duration;
        
        // Run effects in sequence with proper cleanup
        List<BukkitRunnable> runningTasks = new ArrayList<>();
        UUID playerId = player.getUniqueId();
        
        // Clean up any existing animations for this player
        cleanupPlayerAnimations(playerId);
        
        for (int i = 0; i < effects.size(); i++) {
            final String effect = effects.get(i);
            final int delay = i * effectDuration;
            
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    animateSingleEffect(player, message, effect, effectDuration);
                }
            };
            
            runningTasks.add(task);
            task.runTaskLater(plugin, delay);
        }
        
        // Store tasks for cleanup
        activeAnimations.put(playerId, runningTasks);
    }

    /**
     * Animate a single effect (used by multi-layer)
     */
    private void animateSingleEffect(Player player, String message, String effect, int duration) {
        switch (effect.toLowerCase()) {
            case "typing":
                animateTyping(player, message, duration);
                break;
            case "fade":
                animateFade(player, message, duration);
                break;
            case "slide":
                animateSlide(player, message, duration);
                break;
            case "wave":
                animateWave(player, message, duration);
                break;
            case "rainbow":
                animateRainbow(player, message, duration);
                break;
            case "glitch":
                animateGlitch(player, message, duration);
                break;
            case "typewriter":
                animateTypewriter(player, message, duration);
                break;
            case "bounce":
                animateBounce(player, message, duration);
                break;
            case "shake":
                animateShake(player, message, duration);
                break;
            case "pulse":
                animatePulse(player, message, duration);
                break;
            case "matrix":
                animateMatrix(player, message, duration);
                break;
            case "scramble":
                animateScramble(player, message, duration);
                break;
            default:
                // Default to typing if unknown effect
                animateTyping(player, message, duration);
                break;
        }
    }

    /**
     * Typing animation - reveals text character by character
     */
    private void animateTyping(Player player, String message, int duration) {
        String cleanMessage = MessageUtils.stripColors(message);
        int totalChars = cleanMessage.length();
        int delay = Math.max(1, duration / totalChars);

        new BukkitRunnable() {
            int currentChar = 0;

            @Override
            public void run() {
                if (currentChar >= totalChars) {
                    // Show final message
                    sendActionBar(player, message);
                    this.cancel();
                    return;
                }

                // Build the partial message with proper color formatting
                StringBuilder partialMessage = new StringBuilder();
                int charCount = 0;
                boolean inColorCode = false;
                
                for (int i = 0; i < message.length() && charCount <= currentChar; i++) {
                    char c = message.charAt(i);
                    
                    if (c == '&' && i + 1 < message.length()) {
                        partialMessage.append(c).append(message.charAt(i + 1));
                        inColorCode = true;
                        i++; // Skip next character
                    } else if (inColorCode) {
                        inColorCode = false;
                    } else {
                        if (charCount <= currentChar) {
                            partialMessage.append(c);
                        }
                        charCount++;
                    }
                }
                
                sendActionBar(player, partialMessage.toString());
                currentChar++;
            }
        }.runTaskTimer(plugin, 0L, delay);
    }

    /**
     * Fade animation - text fades in with opacity
     */
    private void animateFade(Player player, String message, int duration) {
        int steps = 10;
        int delay = Math.max(1, duration / steps);

        new BukkitRunnable() {
            int currentStep = 0;

            @Override
            public void run() {
                if (currentStep >= steps) {
                    this.cancel();
                    return;
                }

                float opacity = (float) currentStep / (steps - 1);
                String animatedMessage = applyOpacity(message, opacity);
                
                sendActionBar(player, animatedMessage);
                currentStep++;
            }
        }.runTaskTimer(plugin, 0L, delay);
    }

    /**
     * Slide animation - text slides in from the side
     */
    private void animateSlide(Player player, String message, int duration) {
        int maxSpaces = 20;
        int steps = maxSpaces;
        int delay = Math.max(1, duration / steps);

        new BukkitRunnable() {
            int currentStep = 0;

            @Override
            public void run() {
                if (currentStep >= steps) {
                    this.cancel();
                    return;
                }

                int spaces = maxSpaces - currentStep;
                String spacesStr = " ".repeat(Math.max(0, spaces));
                String animatedMessage = spacesStr + message;
                
                sendActionBar(player, animatedMessage);
                currentStep++;
            }
        }.runTaskTimer(plugin, 0L, delay);
    }

    /**
     * Wave animation - text waves up and down
     */
    private void animateWave(Player player, String message, int duration) {
        String cleanMessage = MessageUtils.stripColors(message);
        int steps = 20;
        int delay = Math.max(1, duration / steps);

        new BukkitRunnable() {
            int currentStep = 0;

            @Override
            public void run() {
                if (currentStep >= steps) {
                    this.cancel();
                    return;
                }

                StringBuilder animatedMessage = new StringBuilder();
                for (int i = 0; i < cleanMessage.length(); i++) {
                    char c = cleanMessage.charAt(i);
                    if (c == ' ') {
                        animatedMessage.append(' ');
                    } else {
                        double wave = Math.sin((currentStep + i) * 0.5) * 2;
                        int spaces = (int) Math.abs(wave);
                        animatedMessage.append(" ".repeat(spaces)).append(c);
                    }
                }
                
                sendActionBar(player, MessageUtils.colorize(animatedMessage.toString()));
                currentStep++;
            }
        }.runTaskTimer(plugin, 0L, delay);
    }

    /**
     * Rainbow animation - colors cycle through rainbow
     */
    private void animateRainbow(Player player, String message, int duration) {
        String cleanMessage = MessageUtils.stripColors(message);
        int steps = 20;
        int delay = Math.max(1, duration / steps);

        new BukkitRunnable() {
            int currentStep = 0;

            @Override
            public void run() {
                if (currentStep >= steps) {
                    this.cancel();
                    return;
                }

                StringBuilder animatedMessage = new StringBuilder();
                for (int i = 0; i < cleanMessage.length(); i++) {
                    char c = cleanMessage.charAt(i);
                    if (c == ' ') {
                        animatedMessage.append(' ');
                    } else {
                        String color = getRainbowColor((currentStep + i) % 6);
                        animatedMessage.append("&").append(color).append(c);
                    }
                }
                
                sendActionBar(player, MessageUtils.colorize(animatedMessage.toString()));
                currentStep++;
            }
        }.runTaskTimer(plugin, 0L, delay);
    }

    /**
     * Glitch animation - random characters appear and disappear
     */
    private void animateGlitch(Player player, String message, int duration) {
        String cleanMessage = MessageUtils.stripColors(message);
        int steps = 15;
        int delay = Math.max(1, duration / steps);

        new BukkitRunnable() {
            int currentStep = 0;

            @Override
            public void run() {
                if (currentStep >= steps) {
                    // Show final message in action bar
                    sendActionBar(player, message);
                    this.cancel();
                    return;
                }

                StringBuilder animatedMessage = new StringBuilder();
                for (int i = 0; i < cleanMessage.length(); i++) {
                    char c = cleanMessage.charAt(i);
                    if (c == ' ') {
                        animatedMessage.append(' ');
                    } else {
                        // Random chance to show glitch character
                        if (ThreadLocalRandom.current().nextDouble() < 0.3) {
                            char glitchChar = (char) (ThreadLocalRandom.current().nextInt(33, 127));
                            animatedMessage.append("&c").append(glitchChar);
                        } else {
                            animatedMessage.append(c);
                        }
                    }
                }
                
                sendActionBar(player, MessageUtils.colorize(animatedMessage.toString()));
                currentStep++;
            }
        }.runTaskTimer(plugin, 0L, delay);
    }

    /**
     * Typewriter animation - text appears with blinking cursor
     */
    private void animateTypewriter(Player player, String message, int duration) {
        String cleanMessage = MessageUtils.stripColors(message);
        int totalChars = cleanMessage.length();
        int delay = Math.max(1, duration / totalChars);

        new BukkitRunnable() {
            int currentChar = 0;
            boolean showCursor = true;

            @Override
            public void run() {
                if (currentChar >= totalChars) {
                    // Show final message without cursor
                    sendActionBar(player, message);
                    this.cancel();
                    return;
                }

                // Build the partial message with proper color formatting
                StringBuilder partialMessage = new StringBuilder();
                int charCount = 0;
                boolean inColorCode = false;
                
                for (int i = 0; i < message.length() && charCount <= currentChar; i++) {
                    char c = message.charAt(i);
                    
                    if (c == '&' && i + 1 < message.length()) {
                        partialMessage.append(c).append(message.charAt(i + 1));
                        inColorCode = true;
                        i++; // Skip next character
                    } else if (inColorCode) {
                        inColorCode = false;
                    } else {
                        if (charCount <= currentChar) {
                            partialMessage.append(c);
                        }
                        charCount++;
                    }
                }
                
                // Add blinking cursor
                if (showCursor) {
                    partialMessage.append("&7_");
                } else {
                    partialMessage.append("&8_");
                }
                
                sendActionBar(player, partialMessage.toString());
                currentChar++;
                showCursor = !showCursor; // Blink cursor
            }
        }.runTaskTimer(plugin, 0L, delay);
    }

    /**
     * Bounce animation - text bounces up and down
     */
    private void animateBounce(Player player, String message, int duration) {
        String cleanMessage = MessageUtils.stripColors(message);
        int steps = 20;
        int delay = Math.max(1, duration / steps);

        new BukkitRunnable() {
            int currentStep = 0;

            @Override
            public void run() {
                if (currentStep >= steps) {
                    sendActionBar(player, message);
                    this.cancel();
                    return;
                }

                StringBuilder animatedMessage = new StringBuilder();
                for (int i = 0; i < cleanMessage.length(); i++) {
                    char c = cleanMessage.charAt(i);
                    if (c == ' ') {
                        animatedMessage.append(' ');
                    } else {
                        double bounce = Math.abs(Math.sin((currentStep + i) * 0.8)) * 3;
                        int spaces = (int) bounce;
                        animatedMessage.append(" ".repeat(spaces)).append(c);
                    }
                }
                
                sendActionBar(player, MessageUtils.colorize(animatedMessage.toString()));
                currentStep++;
            }
        }.runTaskTimer(plugin, 0L, delay);
    }

    /**
     * Shake animation - text shakes left and right
     */
    private void animateShake(Player player, String message, int duration) {
        int steps = 15;
        int delay = Math.max(1, duration / steps);

        new BukkitRunnable() {
            int currentStep = 0;

            @Override
            public void run() {
                if (currentStep >= steps) {
                    sendActionBar(player, message);
                    this.cancel();
                    return;
                }

                int shakeAmount = ThreadLocalRandom.current().nextInt(-2, 3);
                String spaces = " ".repeat(Math.max(0, shakeAmount));
                String animatedMessage = spaces + message;
                
                sendActionBar(player, animatedMessage);
                currentStep++;
            }
        }.runTaskTimer(plugin, 0L, delay);
    }

    /**
     * Pulse animation - text pulses in size
     */
    private void animatePulse(Player player, String message, int duration) {
        int steps = 20;
        int delay = Math.max(1, duration / steps);

        new BukkitRunnable() {
            int currentStep = 0;

            @Override
            public void run() {
                if (currentStep >= steps) {
                    sendActionBar(player, message);
                    this.cancel();
                    return;
                }

                double pulse = Math.sin(currentStep * 0.5) * 0.5 + 0.5;
                String animatedMessage;
                
                if (pulse > 0.7) {
                    animatedMessage = message.replace("&", "&f&"); // Bright
                } else if (pulse > 0.4) {
                    animatedMessage = message.replace("&", "&e&"); // Yellow
                } else {
                    animatedMessage = message.replace("&", "&7&"); // Gray
                }
                
                sendActionBar(player, animatedMessage);
                currentStep++;
            }
        }.runTaskTimer(plugin, 0L, delay);
    }

    /**
     * Matrix animation - characters fall down like in The Matrix
     */
    private void animateMatrix(Player player, String message, int duration) {
        String cleanMessage = MessageUtils.stripColors(message);
        int steps = 25;
        int delay = Math.max(1, duration / steps);

        new BukkitRunnable() {
            int currentStep = 0;

            @Override
            public void run() {
                if (currentStep >= steps) {
                    sendActionBar(player, message);
                    this.cancel();
                    return;
                }

                StringBuilder animatedMessage = new StringBuilder();
                for (int i = 0; i < cleanMessage.length(); i++) {
                    char c = cleanMessage.charAt(i);
                    if (c == ' ') {
                        animatedMessage.append(' ');
                    } else {
                        // Random chance to show matrix character
                        if (ThreadLocalRandom.current().nextDouble() < 0.4) {
                            char matrixChar = (char) (ThreadLocalRandom.current().nextInt(33, 127));
                            animatedMessage.append("&a").append(matrixChar); // Green matrix color
                        } else {
                            animatedMessage.append(c);
                        }
                    }
                }
                
                sendActionBar(player, MessageUtils.colorize(animatedMessage.toString()));
                currentStep++;
            }
        }.runTaskTimer(plugin, 0L, delay);
    }

    /**
     * Scramble animation - text scrambles and then reveals correctly
     */
    private void animateScramble(Player player, String message, int duration) {
        String cleanMessage = MessageUtils.stripColors(message);
        int steps = 20;
        int delay = Math.max(1, duration / steps);

        new BukkitRunnable() {
            int currentStep = 0;

            @Override
            public void run() {
                if (currentStep >= steps) {
                    sendActionBar(player, message);
                    this.cancel();
                    return;
                }

                StringBuilder animatedMessage = new StringBuilder();
                for (int i = 0; i < cleanMessage.length(); i++) {
                    char c = cleanMessage.charAt(i);
                    if (c == ' ') {
                        animatedMessage.append(' ');
                    } else {
                        // Show correct character in the last few steps
                        if (currentStep >= steps - 3) {
                            animatedMessage.append(c);
                        } else {
                            // Show random character
                            char randomChar = (char) (ThreadLocalRandom.current().nextInt(33, 127));
                            animatedMessage.append("&c").append(randomChar); // Red for scrambled
                        }
                    }
                }
                
                sendActionBar(player, MessageUtils.colorize(animatedMessage.toString()));
                currentStep++;
            }
        }.runTaskTimer(plugin, 0L, delay);
    }

    /**
     * Apply opacity effect to message (simulated with color codes)
     */
    private String applyOpacity(String message, float opacity) {
        if (opacity >= 1.0f) {
            return message;
        } else if (opacity >= 0.7f) {
            return message.replace("&", "&f&"); // Bright white
        } else if (opacity >= 0.4f) {
            return message.replace("&", "&7&"); // Gray
        } else {
            return message.replace("&", "&8&"); // Dark gray
        }
    }

    /**
     * Get rainbow color based on index
     */
    private String getRainbowColor(int index) {
        String[] colors = {"c", "6", "e", "a", "b", "d"}; // Red, Gold, Yellow, Green, Aqua, Light Purple
        return colors[index % colors.length];
    }

    /**
     * Send message to action bar (only visible to the player)
     */
    @SuppressWarnings("deprecation")
    private void sendActionBar(Player player, String message) {
        boolean useActionBar = plugin.getConfig().getBoolean("animations.use-action-bar", true);
        
        if (useActionBar) {
            try {
                // Try to use modern action bar method with String (newer Spigot versions)
                player.getClass().getMethod("sendActionBar", String.class).invoke(player, MessageUtils.colorize(message));
            } catch (Exception e) {
                // Fallback to deprecated title method (widely compatible)
                try {
                    player.sendTitle("", MessageUtils.colorize(message), 0, 20, 0);
                } catch (Exception e2) {
                    // Last resort: send to chat (but this should be avoided)
                    MessageUtils.sendMessage(player, message);
                }
            }
        } else {
            // Send to chat if action bar is disabled
            MessageUtils.sendMessage(player, message);
        }
    }

    /**
     * Get available animation types
     */
    public static List<String> getAvailableAnimations() {
        List<String> animations = new ArrayList<>();
        animations.add("typing");
        animations.add("fade");
        animations.add("slide");
        animations.add("wave");
        animations.add("rainbow");
        animations.add("glitch");
        animations.add("typewriter");
        animations.add("bounce");
        animations.add("shake");
        animations.add("pulse");
        animations.add("matrix");
        animations.add("scramble");
        return animations;
    }
    
    /**
     * Clean up animations for a specific player
     */
    public void cleanupPlayerAnimations(UUID playerId) {
        List<BukkitRunnable> tasks = activeAnimations.remove(playerId);
        if (tasks != null) {
            for (BukkitRunnable task : tasks) {
                if (!task.isCancelled()) {
                    task.cancel();
                }
            }
        }
    }
    
    /**
     * Clean up all active animations
     */
    public void cleanupAllAnimations() {
        for (List<BukkitRunnable> tasks : activeAnimations.values()) {
            for (BukkitRunnable task : tasks) {
                if (!task.isCancelled()) {
                    task.cancel();
                }
            }
        }
        activeAnimations.clear();
    }
}
