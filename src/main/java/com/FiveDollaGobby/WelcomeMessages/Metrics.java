package com.FiveDollaGobby.WelcomeMessages;

/**
 * Metrics wrapper for optional bStats integration
 * This class handles metrics without requiring bStats at compile time
 */
public class Metrics {

    private final WelcomePlugin plugin;
    private final int pluginId;
    private Object bStatsMetrics;

    public Metrics(WelcomePlugin plugin, int pluginId) {
        this.plugin = plugin;
        this.pluginId = pluginId;

        // Try to initialize bStats if available
        initializeBStats();
    }

    private void initializeBStats() {
        try {
            // Use reflection to avoid compile-time dependency
            Class<?> metricsClass = Class.forName("org.bstats.bukkit.Metrics");
            bStatsMetrics = metricsClass.getConstructor(org.bukkit.plugin.java.JavaPlugin.class, int.class)
                    .newInstance(plugin, pluginId);

            // Add custom charts if bStats is available
            addCustomCharts();

            plugin.getLogger().info("bStats metrics enabled successfully!");
        } catch (Exception e) {
            // bStats not available or failed to initialize
            plugin.getLogger().info("bStats metrics not available - metrics disabled");
        }
    }

    private void addCustomCharts() {
        if (bStatsMetrics == null) return;

        try {
            // Add storage type chart
            addSimplePieChart("storage_type",
                    () -> plugin.getConfig().getString("storage.type", "yaml"));

            // Add join messages chart
            addSimplePieChart("join_messages_enabled",
                    () -> plugin.getConfig().getBoolean("messages.join.enabled", true) ? "Enabled" : "Disabled");

            // Add quit messages chart
            addSimplePieChart("quit_messages_enabled",
                    () -> plugin.getConfig().getBoolean("messages.quit.enabled", true) ? "Enabled" : "Disabled");

            // Add effects usage chart
            addSimplePieChart("effects_usage", () -> {
                boolean title = plugin.getConfig().getBoolean("effects.title.enabled", true);
                boolean sound = plugin.getConfig().getBoolean("effects.sound.enabled", true);
                boolean particles = plugin.getConfig().getBoolean("effects.particles.enabled", true);
                boolean fireworks = plugin.getConfig().getBoolean("effects.fireworks.enabled", true);

                if (title && sound && particles && fireworks) {
                    return "All Effects";
                } else if (!title && !sound && !particles && !fireworks) {
                    return "No Effects";
                } else {
                    return "Some Effects";
                }
            });

            // Add unique players chart
            addSingleLineChart("unique_players",
                    () -> plugin.getDataManager().getTotalUniqueJoins());

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to add custom bStats charts: " + e.getMessage());
        }
    }

    private void addSimplePieChart(String chartId, java.util.concurrent.Callable<String> callable) {
        try {
            Class<?> simplePieClass = Class.forName("org.bstats.charts.SimplePie");
            Object chart = simplePieClass.getConstructor(String.class, java.util.concurrent.Callable.class)
                    .newInstance(chartId, callable);

            bStatsMetrics.getClass().getMethod("addCustomChart",
                            Class.forName("org.bstats.charts.CustomChart"))
                    .invoke(bStatsMetrics, chart);
        } catch (Exception ignored) {
            // Chart creation failed, ignore silently
        }
    }

    private void addSingleLineChart(String chartId, java.util.concurrent.Callable<Integer> callable) {
        try {
            Class<?> singleLineClass = Class.forName("org.bstats.charts.SingleLineChart");
            Object chart = singleLineClass.getConstructor(String.class, java.util.concurrent.Callable.class)
                    .newInstance(chartId, callable);

            bStatsMetrics.getClass().getMethod("addCustomChart",
                            Class.forName("org.bstats.charts.CustomChart"))
                    .invoke(bStatsMetrics, chart);
        } catch (Exception ignored) {
            // Chart creation failed, ignore silently
        }
    }
}