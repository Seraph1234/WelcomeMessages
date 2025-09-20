package com.FiveDollaGobby.WelcomeMessages.utils;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Performance monitoring utility for tracking plugin performance metrics
 */
public class PerformanceMonitor {
    
    private final WelcomePlugin plugin;
    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> timers = new ConcurrentHashMap<>();
    private final Map<String, Long> startTimes = new ConcurrentHashMap<>();
    
    // Performance metrics
    private final AtomicLong totalMessages = new AtomicLong(0);
    private final AtomicLong totalAnimations = new AtomicLong(0);
    private final AtomicLong totalEffects = new AtomicLong(0);
    private final AtomicLong totalMilestones = new AtomicLong(0);
    private final AtomicLong totalThemes = new AtomicLong(0);
    private final AtomicLong totalPlaceholders = new AtomicLong(0);
    
    // Timing metrics
    private final AtomicLong totalMessageTime = new AtomicLong(0);
    private final AtomicLong totalAnimationTime = new AtomicLong(0);
    private final AtomicLong totalEffectTime = new AtomicLong(0);
    private final AtomicLong totalMilestoneTime = new AtomicLong(0);
    private final AtomicLong totalThemeTime = new AtomicLong(0);
    private final AtomicLong totalPlaceholderTime = new AtomicLong(0);
    
    // Memory metrics
    private final AtomicLong peakMemoryUsage = new AtomicLong(0);
    private final AtomicLong totalMemoryAllocated = new AtomicLong(0);
    
    // Error metrics
    private final AtomicLong totalErrors = new AtomicLong(0);
    private final AtomicLong totalWarnings = new AtomicLong(0);
    
    // Performance thresholds
    private static final long MESSAGE_TIME_THRESHOLD = 50; // 50ms
    private static final long ANIMATION_TIME_THRESHOLD = 100; // 100ms
    private static final long EFFECT_TIME_THRESHOLD = 200; // 200ms
    private static final long MILESTONE_TIME_THRESHOLD = 30; // 30ms
    private static final long THEME_TIME_THRESHOLD = 20; // 20ms
    private static final long PLACEHOLDER_TIME_THRESHOLD = 10; // 10ms
    
    public PerformanceMonitor(WelcomePlugin plugin) {
        this.plugin = plugin;
        startMonitoring();
    }
    
    /**
     * Start performance monitoring
     */
    private void startMonitoring() {
        // Monitor memory usage every 5 minutes
        new BukkitRunnable() {
            @Override
            public void run() {
                updateMemoryMetrics();
            }
        }.runTaskTimerAsynchronously(plugin, 20 * 60 * 5, 20 * 60 * 5); // Every 5 minutes
        
        // Log performance report every hour
        new BukkitRunnable() {
            @Override
            public void run() {
                logPerformanceReport();
            }
        }.runTaskTimerAsynchronously(plugin, 20 * 60 * 60, 20 * 60 * 60); // Every hour
    }
    
    /**
     * Start timing a specific operation
     */
    public void startTimer(String operation) {
        startTimes.put(operation, System.nanoTime());
    }
    
    /**
     * End timing a specific operation
     */
    public void endTimer(String operation) {
        Long startTime = startTimes.remove(operation);
        if (startTime != null) {
            long duration = System.nanoTime() - startTime;
            long durationMs = TimeUnit.NANOSECONDS.toMillis(duration);
            
            // Update timing metrics
            timers.computeIfAbsent(operation, k -> new AtomicLong(0)).addAndGet(durationMs);
            
            // Check for performance issues
            checkPerformanceThreshold(operation, durationMs);
        }
    }
    
    /**
     * Increment a counter
     */
    public void incrementCounter(String counter) {
        counters.computeIfAbsent(counter, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * Record a message processing operation
     */
    public void recordMessage(long durationMs) {
        totalMessages.incrementAndGet();
        totalMessageTime.addAndGet(durationMs);
        
        if (durationMs > MESSAGE_TIME_THRESHOLD) {
            plugin.getLogger().warning("Slow message processing detected: " + durationMs + "ms");
        }
    }
    
    /**
     * Record an animation operation
     */
    public void recordAnimation(long durationMs) {
        totalAnimations.incrementAndGet();
        totalAnimationTime.addAndGet(durationMs);
        
        if (durationMs > ANIMATION_TIME_THRESHOLD) {
            plugin.getLogger().warning("Slow animation detected: " + durationMs + "ms");
        }
    }
    
    /**
     * Record an effect operation
     */
    public void recordEffect(long durationMs) {
        totalEffects.incrementAndGet();
        totalEffectTime.addAndGet(durationMs);
        
        if (durationMs > EFFECT_TIME_THRESHOLD) {
            plugin.getLogger().warning("Slow effect processing detected: " + durationMs + "ms");
        }
    }
    
    /**
     * Record a milestone operation
     */
    public void recordMilestone(long durationMs) {
        totalMilestones.incrementAndGet();
        totalMilestoneTime.addAndGet(durationMs);
        
        if (durationMs > MILESTONE_TIME_THRESHOLD) {
            plugin.getLogger().warning("Slow milestone processing detected: " + durationMs + "ms");
        }
    }
    
    /**
     * Record a theme operation
     */
    public void recordTheme(long durationMs) {
        totalThemes.incrementAndGet();
        totalThemeTime.addAndGet(durationMs);
        
        if (durationMs > THEME_TIME_THRESHOLD) {
            plugin.getLogger().warning("Slow theme processing detected: " + durationMs + "ms");
        }
    }
    
    /**
     * Record a placeholder operation
     */
    public void recordPlaceholder(long durationMs) {
        totalPlaceholders.incrementAndGet();
        totalPlaceholderTime.addAndGet(durationMs);
        
        if (durationMs > PLACEHOLDER_TIME_THRESHOLD) {
            plugin.getLogger().warning("Slow placeholder processing detected: " + durationMs + "ms");
        }
    }
    
    /**
     * Record an error
     */
    public void recordError() {
        totalErrors.incrementAndGet();
    }
    
    /**
     * Record a warning
     */
    public void recordWarning() {
        totalWarnings.incrementAndGet();
    }
    
    /**
     * Check performance threshold for an operation
     */
    private void checkPerformanceThreshold(String operation, long durationMs) {
        long threshold = getThresholdForOperation(operation);
        if (durationMs > threshold) {
            plugin.getLogger().warning("Slow " + operation + " detected: " + durationMs + "ms (threshold: " + threshold + "ms)");
        }
    }
    
    /**
     * Get performance threshold for an operation
     */
    private long getThresholdForOperation(String operation) {
        switch (operation.toLowerCase()) {
            case "message":
                return MESSAGE_TIME_THRESHOLD;
            case "animation":
                return ANIMATION_TIME_THRESHOLD;
            case "effect":
                return EFFECT_TIME_THRESHOLD;
            case "milestone":
                return MILESTONE_TIME_THRESHOLD;
            case "theme":
                return THEME_TIME_THRESHOLD;
            case "placeholder":
                return PLACEHOLDER_TIME_THRESHOLD;
            default:
                return 100; // Default threshold
        }
    }
    
    /**
     * Update memory metrics
     */
    private void updateMemoryMetrics() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        
        // Update peak memory usage
        long currentPeak = peakMemoryUsage.get();
        if (usedMemory > currentPeak) {
            peakMemoryUsage.set(usedMemory);
        }
        
        // Update total memory allocated
        totalMemoryAllocated.addAndGet(usedMemory);
        
        // Check for memory issues
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        if (memoryUsagePercent > 80) {
            plugin.getLogger().warning("High memory usage detected: " + String.format("%.2f", memoryUsagePercent) + "%");
        }
    }
    
    /**
     * Log performance report
     */
    private void logPerformanceReport() {
        if (!plugin.getConfig().getBoolean("performance.monitoring.enabled", true)) {
            return;
        }
        
        plugin.getLogger().info("=== Performance Report ===");
        plugin.getLogger().info("Messages processed: " + totalMessages.get());
        plugin.getLogger().info("Animations executed: " + totalAnimations.get());
        plugin.getLogger().info("Effects triggered: " + totalEffects.get());
        plugin.getLogger().info("Milestones detected: " + totalMilestones.get());
        plugin.getLogger().info("Themes applied: " + totalThemes.get());
        plugin.getLogger().info("Placeholders resolved: " + totalPlaceholders.get());
        plugin.getLogger().info("Errors encountered: " + totalErrors.get());
        plugin.getLogger().info("Warnings generated: " + totalWarnings.get());
        
        // Average processing times
        if (totalMessages.get() > 0) {
            plugin.getLogger().info("Average message time: " + (totalMessageTime.get() / totalMessages.get()) + "ms");
        }
        if (totalAnimations.get() > 0) {
            plugin.getLogger().info("Average animation time: " + (totalAnimationTime.get() / totalAnimations.get()) + "ms");
        }
        if (totalEffects.get() > 0) {
            plugin.getLogger().info("Average effect time: " + (totalEffectTime.get() / totalEffects.get()) + "ms");
        }
        if (totalMilestones.get() > 0) {
            plugin.getLogger().info("Average milestone time: " + (totalMilestoneTime.get() / totalMilestones.get()) + "ms");
        }
        if (totalThemes.get() > 0) {
            plugin.getLogger().info("Average theme time: " + (totalThemeTime.get() / totalThemes.get()) + "ms");
        }
        if (totalPlaceholders.get() > 0) {
            plugin.getLogger().info("Average placeholder time: " + (totalPlaceholderTime.get() / totalPlaceholders.get()) + "ms");
        }
        
        // Memory usage
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        plugin.getLogger().info("Current memory usage: " + formatBytes(usedMemory) + " / " + formatBytes(maxMemory));
        plugin.getLogger().info("Peak memory usage: " + formatBytes(peakMemoryUsage.get()));
        
        // Custom counters
        for (Map.Entry<String, AtomicLong> entry : counters.entrySet()) {
            plugin.getLogger().info(entry.getKey() + ": " + entry.getValue().get());
        }
        
        plugin.getLogger().info("========================");
    }
    
    /**
     * Get performance statistics
     */
    public Map<String, Object> getPerformanceStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        
        // Basic metrics
        stats.put("totalMessages", totalMessages.get());
        stats.put("totalAnimations", totalAnimations.get());
        stats.put("totalEffects", totalEffects.get());
        stats.put("totalMilestones", totalMilestones.get());
        stats.put("totalThemes", totalThemes.get());
        stats.put("totalPlaceholders", totalPlaceholders.get());
        stats.put("totalErrors", totalErrors.get());
        stats.put("totalWarnings", totalWarnings.get());
        
        // Average times
        if (totalMessages.get() > 0) {
            stats.put("avgMessageTime", totalMessageTime.get() / totalMessages.get());
        }
        if (totalAnimations.get() > 0) {
            stats.put("avgAnimationTime", totalAnimationTime.get() / totalAnimations.get());
        }
        if (totalEffects.get() > 0) {
            stats.put("avgEffectTime", totalEffectTime.get() / totalEffects.get());
        }
        if (totalMilestones.get() > 0) {
            stats.put("avgMilestoneTime", totalMilestoneTime.get() / totalMilestones.get());
        }
        if (totalThemes.get() > 0) {
            stats.put("avgThemeTime", totalThemeTime.get() / totalThemes.get());
        }
        if (totalPlaceholders.get() > 0) {
            stats.put("avgPlaceholderTime", totalPlaceholderTime.get() / totalPlaceholders.get());
        }
        
        // Memory metrics
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        stats.put("currentMemoryUsage", usedMemory);
        stats.put("maxMemory", maxMemory);
        stats.put("peakMemoryUsage", peakMemoryUsage.get());
        stats.put("memoryUsagePercent", (double) usedMemory / maxMemory * 100);
        
        // Custom counters
        for (Map.Entry<String, AtomicLong> entry : counters.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().get());
        }
        
        return stats;
    }
    
    /**
     * Reset all metrics
     */
    public void resetMetrics() {
        totalMessages.set(0);
        totalAnimations.set(0);
        totalEffects.set(0);
        totalMilestones.set(0);
        totalThemes.set(0);
        totalPlaceholders.set(0);
        
        totalMessageTime.set(0);
        totalAnimationTime.set(0);
        totalEffectTime.set(0);
        totalMilestoneTime.set(0);
        totalThemeTime.set(0);
        totalPlaceholderTime.set(0);
        
        totalErrors.set(0);
        totalWarnings.set(0);
        
        peakMemoryUsage.set(0);
        totalMemoryAllocated.set(0);
        
        counters.clear();
        timers.clear();
        startTimes.clear();
    }
    
    /**
     * Format bytes to human readable format
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * Get performance summary for commands
     */
    public String getPerformanceSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("&6&l=== Performance Summary ===\n");
        summary.append("&7Messages: &e").append(totalMessages.get()).append("\n");
        summary.append("&7Animations: &e").append(totalAnimations.get()).append("\n");
        summary.append("&7Effects: &e").append(totalEffects.get()).append("\n");
        summary.append("&7Milestones: &e").append(totalMilestones.get()).append("\n");
        summary.append("&7Themes: &e").append(totalThemes.get()).append("\n");
        summary.append("&7Placeholders: &e").append(totalPlaceholders.get()).append("\n");
        summary.append("&7Errors: &c").append(totalErrors.get()).append("\n");
        summary.append("&7Warnings: &e").append(totalWarnings.get()).append("\n");
        
        // Memory usage
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        double memoryPercent = (double) usedMemory / maxMemory * 100;
        summary.append("&7Memory: &e").append(String.format("%.1f", memoryPercent)).append("%\n");
        summary.append("&6&l========================");
        
        return summary.toString();
    }
}
