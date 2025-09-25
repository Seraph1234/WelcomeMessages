package com.FiveDollaGobby.WelcomeMessages.commands;

import com.FiveDollaGobby.WelcomeMessages.WelcomePlugin;
import com.FiveDollaGobby.WelcomeMessages.utils.MessageUtils;
import com.FiveDollaGobby.WelcomeMessages.utils.SecurityUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class WelcomeCommand implements CommandExecutor, TabCompleter {

    private final WelcomePlugin plugin;
    private final Map<UUID, Long> commandCooldowns = new HashMap<>();
    private final Map<UUID, Map<String, Long>> commandRateLimits = new HashMap<>();
    private final Map<String, Integer> commandCooldownTimes = new HashMap<>();

    public WelcomeCommand(WelcomePlugin plugin) {
        this.plugin = plugin;
        initializeRateLimits();
    }
    
    private void initializeRateLimits() {
        // Set cooldown times for different commands (in seconds)
        commandCooldownTimes.put("test", 2);
        commandCooldownTimes.put("testall", 5);
        commandCooldownTimes.put("testanim", 3);
        commandCooldownTimes.put("testtheme", 3);
        commandCooldownTimes.put("testmilestone", 2);
        commandCooldownTimes.put("testplaytime", 2);
        commandCooldownTimes.put("stats", 1);
        commandCooldownTimes.put("reset", 10);
        commandCooldownTimes.put("toggle", 5);
        commandCooldownTimes.put("reload", 30);
    }
    
    private boolean checkRateLimit(CommandSender sender, String command) {
        if (!(sender instanceof Player)) {
            return true; // Console can bypass rate limits
        }
        
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        
        // Check if player has bypass permission
        if (player.hasPermission("welcome.effects.bypass")) {
            return true;
        }
        
        int cooldownTime = commandCooldownTimes.getOrDefault(command, 1);
        long currentTime = System.currentTimeMillis();
        
        // Get or create rate limit map for player
        Map<String, Long> playerLimits = commandRateLimits.computeIfAbsent(uuid, k -> new HashMap<>());
        
        // Check if command is on cooldown
        Long lastUsed = playerLimits.get(command);
        if (lastUsed != null && currentTime - lastUsed < (cooldownTime * 1000L)) {
            long remaining = (cooldownTime * 1000L) - (currentTime - lastUsed);
            MessageUtils.sendMessage(sender, "&cPlease wait &e" + (remaining / 1000 + 1) + " &cseconds before using this command again!");
            return false;
        }
        
        // Update last used time
        playerLimits.put(command, currentTime);
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args == null || args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                sendHelp(sender);
                break;

            case "reload":
                handleReload(sender);
                break;

            case "test":
                handleTest(sender, args);
                break;

            case "testall":
                handleTestAll(sender, args);
                break;

            case "testanim":
                handleTestAnim(sender, args);
                break;

            case "testtheme":
                handleTestTheme(sender, args);
                break;

            case "testmilestone":
                handleTestMilestone(sender, args);
                break;

            case "testplaytime":
                handleTestPlaytime(sender, args);
                break;

            case "stats":
                handleStats(sender, args);
                break;

            case "reset":
                handleReset(sender, args);
                break;

            case "toggle":
                handleToggle(sender);
                break;

            case "version":
                handleVersion(sender);
                break;

            case "performance":
            case "metrics":
                handlePerformance(sender);
                break;

            default:
                MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.unknown-command"));
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        if (!sender.hasPermission("welcome.use")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.help.header"));

        List<String> commands = plugin.getMessagesConfig().getStringList("commands.help.commands");
        for (String cmd : commands) {
            // only show commands player has permission for
            if (cmd.contains("reload") && !sender.hasPermission("welcome.reload")) continue;
            if (cmd.contains("test") && !sender.hasPermission("welcome.test")) continue;
            if (cmd.contains("stats") && !sender.hasPermission("welcome.stats")) continue;
            if (cmd.contains("reset") && !sender.hasPermission("welcome.reset")) continue;

            MessageUtils.sendMessage(sender, cmd);
        }

        MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.help.footer"));
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("welcome.reload")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }
        
        if (!checkRateLimit(sender, "reload")) {
            return;
        }

        try {
            plugin.reload();
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.reload-success"));
        } catch (Exception e) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.reload-error"));
            plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
        }
    }

    private void handleTest(CommandSender sender, String[] args) {
        if (!sender.hasPermission("welcome.test")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }
        
        if (!checkRateLimit(sender, "test")) {
            return;
        }

        Player target;
        if (args.length > 1) {
            String playerName = SecurityUtils.sanitizePlayerName(args[1]);
            if (playerName == null) {
                MessageUtils.sendMessage(sender, "&cInvalid player name format!");
                return;
            }
            
            target = Bukkit.getPlayer(playerName);
            if (target == null) {
                String msg = plugin.getMessagesConfig().getString("commands.player-not-found");
                MessageUtils.sendMessage(sender, msg.replace("{player}", SecurityUtils.sanitizeText(playerName)));
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            MessageUtils.sendMessage(sender, "&cPlease specify a player name!");
            return;
        }

        // test messages
        boolean isFirstJoin = plugin.getDataManager().isFirstJoin(target);
        String joinMessage = plugin.getMessageManager().getJoinMessage(target, isFirstJoin);

        MessageUtils.sendMessage(sender, "&6Testing join message for &e" + target.getName() + "&6:");
        MessageUtils.sendMessage(sender, joinMessage);

        String quitMessage = plugin.getMessageManager().getQuitMessage(target);
        MessageUtils.sendMessage(sender, "&6Quit message:");
        MessageUtils.sendMessage(sender, quitMessage);

        // test effects if sender is target (but skip title to avoid flashing)
        if (sender.equals(target)) {
            MessageUtils.sendMessage(sender, "&6Testing effects in 2 seconds...");
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // Skip title effect to prevent flashing, only test sound and particles
                plugin.getEffectManager().playJoinSound(target, isFirstJoin);
                plugin.getEffectManager().playJoinParticles(target, isFirstJoin);
            }, 40L);
        }
    }

    private void handleTestAll(CommandSender sender, String[] args) {
        if (!sender.hasPermission("welcome.test")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                String msg = plugin.getMessagesConfig().getString("commands.player-not-found");
                MessageUtils.sendMessage(sender, msg.replace("{player}", args[1]));
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            MessageUtils.sendMessage(sender, "&cPlease specify a player name!");
            return;
        }

        MessageUtils.sendMessage(sender, "&6&l=== WelcomeMessages Feature Showcase ===");
        MessageUtils.sendMessage(sender, "&7Testing all features for: &e" + target.getName());
        MessageUtils.sendMessage(sender, "");

        // Test 1: Basic Messages
        MessageUtils.sendMessage(sender, "&a&l1. Basic Messages:");
        boolean isFirstJoin = plugin.getDataManager().isFirstJoin(target);
        String joinMessage = plugin.getMessageManager().getJoinMessage(target, isFirstJoin);
        String quitMessage = plugin.getMessageManager().getQuitMessage(target);
        
        MessageUtils.sendMessage(sender, "&7Join: " + (joinMessage != null ? joinMessage : "No message"));
        MessageUtils.sendMessage(sender, "&7Quit: " + (quitMessage != null ? quitMessage : "No message"));
        MessageUtils.sendMessage(sender, "");

        // Test 2: RGB and Gradient Text
        MessageUtils.sendMessage(sender, "&a&l2. RGB & Gradient Text:");
        MessageUtils.sendMessage(sender, "&7Basic RGB: &#FF6B6BHello &#4ECDC4World &#45B7D1Test");
        MessageUtils.sendMessage(sender, "&7Gradient: <gradient:#FF6B6B:#4ECDC4>Welcome to our server!</gradient>");
        MessageUtils.sendMessage(sender, "&7Rainbow: <rainbow>This is rainbow text!</rainbow>");
        MessageUtils.sendMessage(sender, "");

        // Test 2.5: Animation System
        MessageUtils.sendMessage(sender, "&a&l2.5. Animation System:");
        if (plugin.getConfig().getBoolean("animations.enabled", true)) {
            MessageUtils.sendMessage(sender, "&7Animations enabled! Available types:");
            MessageUtils.sendMessage(sender, "&7- &etyping &7- Character by character reveal");
            MessageUtils.sendMessage(sender, "&7- &efade &7- Fade in effect");
            MessageUtils.sendMessage(sender, "&7- &eslide &7- Slide in from side");
            MessageUtils.sendMessage(sender, "&7- &ewave &7- Wave up and down");
            MessageUtils.sendMessage(sender, "&7- &erainbow &7- Rainbow color cycling");
            MessageUtils.sendMessage(sender, "&7- &eglitch &7- Glitch effect");
            MessageUtils.sendMessage(sender, "&7- &etypewriter &7- Typewriter with cursor");
            MessageUtils.sendMessage(sender, "&7- &ebounce &7- Bounce up and down");
            MessageUtils.sendMessage(sender, "&7- &eshake &7- Shake left and right");
            MessageUtils.sendMessage(sender, "&7- &epulse &7- Pulse brightness");
            MessageUtils.sendMessage(sender, "&7- &ematrix &7- Matrix-style falling characters");
            MessageUtils.sendMessage(sender, "&7- &escramble &7- Scramble and reveal");
            
            // Multi-layer animations
            if (plugin.getConfig().getBoolean("animations.multi-layer.enabled", true)) {
                MessageUtils.sendMessage(sender, "");
                MessageUtils.sendMessage(sender, "&e&lMulti-Layer Animations:");
                MessageUtils.sendMessage(sender, "&7- &eepic_welcome &7- Rainbow + Wave + Bounce");
                MessageUtils.sendMessage(sender, "&7- &emysterious_join &7- Glitch + Fade + Matrix");
                MessageUtils.sendMessage(sender, "&7- &efestive_celebration &7- Rainbow + Pulse + Shake");
                MessageUtils.sendMessage(sender, "&7- &esmooth_entrance &7- Fade + Slide + Typing");
                MessageUtils.sendMessage(sender, "&7- &eparty_time &7- Bounce + Pulse + Rainbow + Wave");
                MessageUtils.sendMessage(sender, "&7- &emilestone_celebration &7- All effects combined!");
            }
        } else {
            MessageUtils.sendMessage(sender, "&7Animations disabled in config");
        }
        MessageUtils.sendMessage(sender, "");

        // Test 2.6: Dynamic Themes System
        MessageUtils.sendMessage(sender, "&a&l2.6. Dynamic Themes System:");
        if (plugin.getConfig().getBoolean("themes.enabled", true)) {
            String currentTheme = plugin.getMessageManager().getThemeManager().getCurrentTheme();
            String themeInfo = plugin.getMessageManager().getThemeManager().getThemeInfo();
            MessageUtils.sendMessage(sender, "&7Current Theme: &e" + currentTheme.toUpperCase());
            MessageUtils.sendMessage(sender, "&7Theme Info: &f" + themeInfo);
            MessageUtils.sendMessage(sender, "&7Available Themes:");
            MessageUtils.sendMessage(sender, "&7- &eSeasonal: &fHalloween, Christmas, Valentine, Easter, Summer, Winter");
            MessageUtils.sendMessage(sender, "&7- &eTime-based: &fMorning, Afternoon, Evening, Night");
            MessageUtils.sendMessage(sender, "&7- &eAuto-detection: &f" + (plugin.getConfig().getBoolean("themes.auto-detect", true) ? "Enabled" : "Disabled"));
        } else {
            MessageUtils.sendMessage(sender, "&7Themes disabled in config");
        }
        MessageUtils.sendMessage(sender, "");

        // Test 2.7: Smart Player Recognition
        MessageUtils.sendMessage(sender, "&a&l2.7. Smart Player Recognition:");
        if (plugin.getConfig().getBoolean("smart-recognition.enabled", true)) {
            MessageUtils.sendMessage(sender, "&7Smart recognition enabled! Features:");
            MessageUtils.sendMessage(sender, "&7- &eMilestone Detection: &fJoin count, playtime, streak milestones");
            MessageUtils.sendMessage(sender, "&7- &eReturning Players: &fDifferent messages based on absence duration");
            MessageUtils.sendMessage(sender, "&7- &eBehavior Analysis: &fPeak activity, favorite world, join patterns");
            
            // Show player's milestone info
            String milestoneInfo = plugin.getMessageManager().getSmartRecognitionManager().getMilestoneInfo(target);
            MessageUtils.sendMessage(sender, "&7Player Data: &f" + milestoneInfo);
        } else {
            MessageUtils.sendMessage(sender, "&7Smart recognition disabled in config");
        }
        MessageUtils.sendMessage(sender, "");

        // Test 3: PlaceholderAPI Examples
        MessageUtils.sendMessage(sender, "&a&l3. PlaceholderAPI Examples:");
        MessageUtils.sendMessage(sender, "&7Join Count: &e" + plugin.getDataManager().getJoinCount(target));
        MessageUtils.sendMessage(sender, "&7First Join: &e" + (isFirstJoin ? "Yes" : "No"));
        MessageUtils.sendMessage(sender, "&7Messages Disabled: &e" + plugin.getDataManager().hasMessagesDisabled(target));
        MessageUtils.sendMessage(sender, "&7Player Status: &e" + (isFirstJoin ? "New Player" : "Returning Player"));
        MessageUtils.sendMessage(sender, "");

        // Test 4: Rank Messages
        MessageUtils.sendMessage(sender, "&a&l4. Rank System:");
        
        // Check if custom ranks are enabled
        if (plugin.getConfig().getBoolean("custom-ranks.enabled", true)) {
            List<String> customRanks = plugin.getConfig().getStringList("custom-ranks.ranks");
            
            if (!customRanks.isEmpty()) {
                MessageUtils.sendMessage(sender, "&7Custom ranks enabled. Checking permissions:");
                boolean foundRank = false;
                for (String rank : customRanks) {
                    if (target.hasPermission("welcome.rank." + rank)) {
                        MessageUtils.sendMessage(sender, "&7You have &e" + rank.toUpperCase() + " &7rank permissions");
                        foundRank = true;
                        break;
                    }
                }
                if (!foundRank) {
                    MessageUtils.sendMessage(sender, "&7No custom rank permissions found");
                }
            } else {
                MessageUtils.sendMessage(sender, "&7Custom ranks enabled but no ranks configured");
            }
        } else {
            MessageUtils.sendMessage(sender, "&7Custom ranks disabled, using default system:");
            String[] defaultRanks = {"owner", "admin", "mvp", "vip"};
            boolean foundRank = false;
            for (String rank : defaultRanks) {
                if (target.hasPermission("welcome.rank." + rank)) {
                    MessageUtils.sendMessage(sender, "&7You have &e" + rank.toUpperCase() + " &7rank permissions");
                    foundRank = true;
                    break;
                }
            }
            if (!foundRank) {
                MessageUtils.sendMessage(sender, "&7No default rank permissions found");
            }
        }
        MessageUtils.sendMessage(sender, "");

        // Test 5: Effects (if sender is target)
        if (sender.equals(target)) {
            MessageUtils.sendMessage(sender, "&a&l5. Visual Effects (starting in 3 seconds):");
            MessageUtils.sendMessage(sender, "&7- Sound effects");
            MessageUtils.sendMessage(sender, "&7- Particle effects");
            MessageUtils.sendMessage(sender, "&7- Firework effects");
            MessageUtils.sendMessage(sender, "");

            // Schedule effects with delays for better showcase
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                MessageUtils.sendMessage(target, "&6&l>>> Sound Effect <<<");
                plugin.getEffectManager().playJoinSound(target, isFirstJoin);
            }, 60L);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                MessageUtils.sendMessage(target, "&6&l>>> Particle Effect <<<");
                plugin.getEffectManager().playJoinParticles(target, isFirstJoin);
            }, 80L);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                MessageUtils.sendMessage(target, "&6&l>>> Firework Effect <<<");
                plugin.getEffectManager().launchFireworks(target);
            }, 100L);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                MessageUtils.sendMessage(target, "&6&l=== Showcase Complete! ===");
                MessageUtils.sendMessage(target, "&7All features have been demonstrated.");
                MessageUtils.sendMessage(target, "&7Perfect for screenshots! 📸");
            }, 120L);
        } else {
            MessageUtils.sendMessage(sender, "&a&l5. Visual Effects:");
            MessageUtils.sendMessage(sender, "&7Effects can only be shown to the target player");
            MessageUtils.sendMessage(sender, "&7Run &e/welcome testall &7on yourself to see effects");
            MessageUtils.sendMessage(sender, "");
            MessageUtils.sendMessage(sender, "&6&l=== Showcase Complete! ===");
        }
    }

    private void handleTestAnim(CommandSender sender, String[] args) {
        if (!sender.hasPermission("welcome.test")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        if (args.length < 2) {
            MessageUtils.sendMessage(sender, "&cUsage: /welcome testanim <animation_type> [player]");
            MessageUtils.sendMessage(sender, "&7Available animations: typing, fade, slide, wave, rainbow, glitch, typewriter, bounce, shake, pulse, matrix, scramble");
            return;
        }

        String animationType = SecurityUtils.sanitizeAnimationType(args[1]);
        if (animationType == null) {
            MessageUtils.sendMessage(sender, "&cInvalid animation type format!");
            return;
        }
        
        Player target;
        
        if (args.length > 2) {
            String playerName = SecurityUtils.sanitizePlayerName(args[2]);
            if (playerName == null) {
                MessageUtils.sendMessage(sender, "&cInvalid player name format!");
                return;
            }
            
            target = Bukkit.getPlayer(playerName);
            if (target == null) {
                String msg = plugin.getMessagesConfig().getString("commands.player-not-found");
                MessageUtils.sendMessage(sender, msg.replace("{player}", SecurityUtils.sanitizeText(playerName)));
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            MessageUtils.sendMessage(sender, "&cPlease specify a player name!");
            return;
        }

        // Test message
        String testMessage = "&6&lWelcome to our server, &e" + target.getName() + "&6&l!";
        int duration = 60; // 3 seconds

        MessageUtils.sendMessage(sender, "&6Testing &e" + animationType + " &6animation for &e" + target.getName() + "&6...");
        
        // Use AnimationUtils directly
        com.FiveDollaGobby.WelcomeMessages.utils.AnimationUtils animationUtils = 
            new com.FiveDollaGobby.WelcomeMessages.utils.AnimationUtils(plugin);
        animationUtils.animateMessage(target, testMessage, animationType, duration);
    }

    private void handleTestTheme(CommandSender sender, String[] args) {
        if (!sender.hasPermission("welcome.test")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        if (args.length < 2) {
            MessageUtils.sendMessage(sender, "&cUsage: /welcome testtheme <theme> [player]");
            MessageUtils.sendMessage(sender, "&7Available themes: " + String.join(", ", plugin.getMessageManager().getThemeManager().getAvailableThemes()));
            return;
        }

        String theme = SecurityUtils.sanitizeThemeName(args[1]);
        if (theme == null) {
            MessageUtils.sendMessage(sender, "&cInvalid theme name format!");
            return;
        }
        
        Player target;
        
        if (args.length > 2) {
            String playerName = SecurityUtils.sanitizePlayerName(args[2]);
            if (playerName == null) {
                MessageUtils.sendMessage(sender, "&cInvalid player name format!");
                return;
            }
            
            target = Bukkit.getPlayer(playerName);
            if (target == null) {
                String msg = plugin.getMessagesConfig().getString("commands.player-not-found");
                MessageUtils.sendMessage(sender, msg.replace("{player}", SecurityUtils.sanitizeText(playerName)));
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            MessageUtils.sendMessage(sender, "&cPlease specify a player name!");
            return;
        }

        // Set theme temporarily
        plugin.getMessageManager().getThemeManager().setTheme(theme);
        
        // Test messages
        boolean isFirstJoin = plugin.getDataManager().isFirstJoin(target);
        List<String> themeMessages = plugin.getMessageManager().getThemeManager().getThemeMessages("join", isFirstJoin);
        
        MessageUtils.sendMessage(sender, "&6Testing &e" + theme + " &6theme for &e" + target.getName() + "&6...");
        
        if (themeMessages != null && !themeMessages.isEmpty()) {
            String message = themeMessages.get(ThreadLocalRandom.current().nextInt(themeMessages.size()));
            String finalMessage = message.replace("{player}", target.getName())
                                       .replace("{displayname}", target.getName()) // Using getName() instead of deprecated getDisplayName()
                                       .replace("{world}", target.getWorld() != null ? target.getWorld().getName() : "unknown")
                                       .replace("{online}", String.valueOf(plugin.getServer().getOnlinePlayers().size()))
                                       .replace("{max}", String.valueOf(plugin.getServer().getMaxPlayers()))
                                       .replace("{joincount}", String.valueOf(plugin.getDataManager().getJoinCount(target)));
            
            MessageUtils.sendMessage(sender, "&7Theme message: " + finalMessage);
            
            // Show title effect for theme testing
            if (sender.equals(target)) {
                MessageUtils.sendMessage(sender, "&6Testing title effect in 2 seconds...");
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    plugin.getEffectManager().sendTitle(target, isFirstJoin, theme);
                }, 40L);
            }
        } else {
            MessageUtils.sendMessage(sender, "&7No theme messages found for " + theme);
        }
        
        // Reset theme
        plugin.getMessageManager().getThemeManager().setTheme("auto");
    }

    private void handleTestMilestone(CommandSender sender, String[] args) {
        if (!sender.hasPermission("welcome.test")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                String msg = plugin.getMessagesConfig().getString("commands.player-not-found");
                MessageUtils.sendMessage(sender, msg.replace("{player}", args[1]));
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            MessageUtils.sendMessage(sender, "&cPlease specify a player name!");
            return;
        }

        MessageUtils.sendMessage(sender, "&6Testing milestone detection for &e" + target.getName() + "&6...");
        
        // Show current milestone info
        String milestoneInfo = plugin.getMessageManager().getSmartRecognitionManager().getMilestoneInfo(target);
        MessageUtils.sendMessage(sender, "&7Current data: &f" + milestoneInfo);
        
        // Test milestone messages
        String milestoneMessage = plugin.getMessageManager().getSmartRecognitionManager().checkMilestones(target, false);
        if (milestoneMessage != null) {
            MessageUtils.sendMessage(sender, "&7Milestone message: " + milestoneMessage);
        } else {
            MessageUtils.sendMessage(sender, "&7No milestone reached");
        }
        
        // Test returning player message
        String returningMessage = plugin.getMessageManager().getSmartRecognitionManager().getReturningPlayerMessage(target);
        if (returningMessage != null) {
            MessageUtils.sendMessage(sender, "&7Returning player message: " + returningMessage);
        } else {
            MessageUtils.sendMessage(sender, "&7No returning player message");
        }
    }

    private void handleTestPlaytime(CommandSender sender, String[] args) {
        if (!sender.hasPermission("welcome.test")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                String msg = plugin.getMessagesConfig().getString("commands.player-not-found");
                MessageUtils.sendMessage(sender, msg.replace("{player}", args[1]));
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            MessageUtils.sendMessage(sender, "&cPlease specify a player name!");
            return;
        }

        MessageUtils.sendMessage(sender, "&6&l=== Playtime Testing for " + target.getName() + " ===");
        
        // Get playtime data
        long totalPlaytime = plugin.getDataManager().getTotalPlaytime(target);
        long sessionTime = plugin.getDataManager().getCurrentSessionTime(target);
        int joinCount = plugin.getDataManager().getJoinCount(target);
        
        MessageUtils.sendMessage(sender, "&7Total Playtime: &e" + totalPlaytime + " hours");
        MessageUtils.sendMessage(sender, "&7Current Session: &e" + sessionTime + " minutes");
        MessageUtils.sendMessage(sender, "&7Join Count: &e" + joinCount);
        MessageUtils.sendMessage(sender, "");
        
        // Test playtime milestones
        MessageUtils.sendMessage(sender, "&a&lTesting Playtime Milestones:");
        List<Integer> milestones = plugin.getConfig().getIntegerList("smart-recognition.milestones.playtime-milestones");
        MessageUtils.sendMessage(sender, "&7Configured milestones: &e" + milestones.toString());
        
        boolean reachedMilestone = false;
        for (int milestone : milestones) {
            if (totalPlaytime >= milestone) {
                MessageUtils.sendMessage(sender, "&a✓ Reached " + milestone + " hour milestone!");
                reachedMilestone = true;
            }
        }
        
        if (!reachedMilestone) {
            MessageUtils.sendMessage(sender, "&7No playtime milestones reached yet");
        }
        
        MessageUtils.sendMessage(sender, "");
        MessageUtils.sendMessage(sender, "&6&l=== Playtime Test Complete ===");
        MessageUtils.sendMessage(sender, "&7Playtime is now tracked accurately!");
        MessageUtils.sendMessage(sender, "&7Session time updates every 5 minutes automatically.");
    }

    private void handleStats(CommandSender sender, String[] args) {
        if (!sender.hasPermission("welcome.stats")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtils.sendMessage(sender, "&cPlayer must be online to view stats!");
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            MessageUtils.sendMessage(sender, "&cPlease specify a player name!");
            return;
        }

        MessageUtils.sendMessage(sender, "&6&l--- Player Statistics ---");
        MessageUtils.sendMessage(sender, "&ePlayer: &f" + target.getName());
        MessageUtils.sendMessage(sender, "&eJoin Count: &f" + plugin.getDataManager().getJoinCount(target));
        MessageUtils.sendMessage(sender, "&eFirst Join: &f" + (plugin.getDataManager().isFirstJoin(target) ? "Never" : "Yes"));
        MessageUtils.sendMessage(sender, "&eMessages Enabled: &f" + !plugin.getDataManager().hasMessagesDisabled(target));
        MessageUtils.sendMessage(sender, "&eTotal Playtime: &f" + plugin.getDataManager().getTotalPlaytime(target) + " hours");
        MessageUtils.sendMessage(sender, "&eCurrent Session: &f" + plugin.getDataManager().getCurrentSessionTime(target) + " minutes");
        MessageUtils.sendMessage(sender, "&6&l----------------------");
    }

    private void handleReset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("welcome.reset")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }
        
        if (!checkRateLimit(sender, "reset")) {
            return;
        }

        if (args.length < 2) {
            MessageUtils.sendMessage(sender, "&cUsage: /welcome reset <player>");
            return;
        }

        String playerName = SecurityUtils.sanitizePlayerName(args[1]);
        if (playerName == null) {
            MessageUtils.sendMessage(sender, "&cInvalid player name format!");
            return;
        }
        
        plugin.getDataManager().resetPlayerData(playerName);
        MessageUtils.sendMessage(sender, "&aPlayer data reset for &e" + SecurityUtils.sanitizeText(playerName));
    }

    private void handleToggle(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, "&cOnly players can toggle their messages!");
            return;
        }

        if (!sender.hasPermission("welcome.toggle")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        Player player = (Player) sender;

        // cooldown check (5 seconds)
        UUID uuid = player.getUniqueId();
        long lastUse = commandCooldowns.getOrDefault(uuid, 0L);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUse < 5000 && !player.hasPermission("welcome.effects.bypass")) {
            MessageUtils.sendMessage(player, "&cPlease wait before using this command again!");
            return;
        }

        commandCooldowns.put(uuid, currentTime);

        boolean disabled = plugin.getDataManager().toggleMessages(player);
        if (disabled) {
            MessageUtils.sendMessage(player, "&cYour join/quit messages have been disabled!");
        } else {
            MessageUtils.sendMessage(player, "&aYour join/quit messages have been enabled!");
        }
    }

    private void handleVersion(CommandSender sender) {
        @SuppressWarnings("deprecation")
        var version = plugin.getDescription().getVersion();
        MessageUtils.sendMessage(sender, "&6WelcomeMessages &ev" + version);
        MessageUtils.sendMessage(sender, "&7Created by &eFiveDollaGobby");
    }

    private void handlePerformance(CommandSender sender) {
        if (!sender.hasPermission("welcome.admin")) {
            MessageUtils.sendMessage(sender, plugin.getMessagesConfig().getString("commands.no-permission"));
            return;
        }

        if (!checkRateLimit(sender, "performance")) {
            return;
        }

        MessageUtils.sendMessage(sender, "&6&l=== Performance Monitor ===");
        String summary = plugin.getPerformanceMonitor().getPerformanceSummary();
        MessageUtils.sendMessage(sender, summary);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args == null || args.length == 0) {
            return completions;
        }

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "reload", "test", "testall", "testanim", "testtheme", "testmilestone", "testplaytime", "stats", "reset", "toggle", "version", "performance");
            StringUtil.copyPartialMatches(args[0], subCommands, completions);
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("test") || subCommand.equals("testall") || subCommand.equals("stats") || subCommand.equals("reset") || subCommand.equals("testmilestone") || subCommand.equals("testplaytime")) {
                List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList());
                StringUtil.copyPartialMatches(args[1], playerNames, completions);
            } else if (subCommand.equals("testanim")) {
                List<String> animations = Arrays.asList("typing", "fade", "slide", "wave", "rainbow", "glitch", "typewriter", "bounce", "shake", "pulse", "matrix", "scramble", "epic_welcome", "mysterious_join", "festive_celebration", "smooth_entrance", "party_time", "milestone_celebration");
                StringUtil.copyPartialMatches(args[1], animations, completions);
            } else if (subCommand.equals("testtheme")) {
                List<String> themes = plugin.getMessageManager().getThemeManager().getAvailableThemes();
                StringUtil.copyPartialMatches(args[1], themes, completions);
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("testanim") || subCommand.equals("testtheme")) {
                List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList());
                StringUtil.copyPartialMatches(args[2], playerNames, completions);
            }
        }

        Collections.sort(completions);
        return completions;
    }
}