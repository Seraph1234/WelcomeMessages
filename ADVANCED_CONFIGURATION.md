# WelcomeMessages - Advanced Configuration Guide

## Table of Contents
- [Performance Optimization](#performance-optimization)
- [Security Configuration](#security-configuration)
- [Custom Integration](#custom-integration)
- [Troubleshooting Advanced Issues](#troubleshooting-advanced-issues)
- [API Usage](#api-usage)

## Performance Optimization

### Memory Management
```yaml
# config.yml - Optimize memory usage
performance:
  # Use async processing for better performance
  async-messages: true
  
  # Cache player data for 5 minutes (reduce I/O)
  cache-time: 5
  
  # Limit cache size to prevent memory leaks
  max-cache-size: 100

general:
  # Save data every 5 minutes (more frequent = more I/O)
  save-interval: 5
```

### Animation Performance
```yaml
# config.yml - Optimize animations
animations:
  # Disable animations for better performance
  enabled: false
  
  # Or optimize animation settings
  enabled: true
  default-duration: 30  # Shorter animations
  use-action-bar: true  # More efficient than chat
  show-final-in-chat: false  # Reduce chat spam
```

### Effect Optimization
```yaml
# config.yml - Optimize effects
effects:
  # Disable expensive effects
  particles:
    enabled: false
    animated: false  # Static particles are faster
  
  fireworks:
    enabled: false  # Fireworks are expensive
  
  # Keep lightweight effects
  title:
    enabled: true
    stay: 40  # Shorter display time
  
  sound:
    enabled: true
    to-others: false  # Only play to joining player
```

## Security Configuration

### Input Validation
```yaml
# config.yml - Security settings
general:
  # Enable debug for security monitoring
  debug: false
  
  # Check for updates (security patches)
  check-updates: true

# Rate limiting is built-in for commands
# No additional configuration needed
```

### Permission Security
```yaml
# Example permission configuration
permissions:
  # Restrict admin commands
  welcome.admin:
    default: false
    children:
      welcome.reload: true
      welcome.test: true
      welcome.stats: true
      welcome.reset: true
  
  # Allow basic usage
  welcome.use:
    default: true
  
  # Restrict testing commands
  welcome.test:
    default: false
```

### Data Security
```yaml
# config.yml - Data protection
storage:
  # Use YAML for simple setups
  type: 'yaml'
  
  # Or SQLite for better performance and security
  type: 'sqlite'
  database: 'welcomedata.db'

general:
  # Save more frequently for data safety
  save-interval: 5
```

## Custom Integration

### Custom Rank System
```yaml
# config.yml - Advanced rank system
custom-ranks:
  enabled: true
  ranks:
    - "owner"
    - "admin"
    - "moderator"
    - "mvp"
    - "vip"
    - "premium"
    - "donator"
    - "member"
    - "trial"
    - "guest"

# messages.yml - Custom rank messages
messages:
  join:
    ranks:
      trial:
        - "&8[&a+&8] <gradient:#00FF00:#32CD32>[TRIAL] {player}</gradient> &7joined for a trial!"
      guest:
        - "&8[&a+&8] <gradient:#87CEEB:#4169E1>[GUEST] {player}</gradient> &7is visiting!"
```

### Custom Themes
```yaml
# config.yml - Custom theme system
themes:
  enabled: true
  current: "auto"
  auto-detect: true
  
  # Custom seasonal themes
  seasonal:
    newyear:
      start: "01-01"
      end: "01-07"
      priority: 15
    blackfriday:
      start: "11-24"
      end: "11-30"
      priority: 12
    backtoschool:
      start: "09-01"
      end: "09-30"
      priority: 8
  
  # Custom time-based themes
  time-based:
    enabled: true
    dawn:
      start: "05:00"
      end: "07:00"
      priority: 2
    midnight:
      start: "00:00"
      end: "05:00"
      priority: 2

# messages.yml - Custom theme messages
messages:
  join:
    themes:
      newyear:
        first-time:
          - "&8[&a+&8] &e🎊 <gradient:#FFD700:#FF6B6B>Happy New Year {player}!</gradient> &e🎊"
        default:
          - "&8[&a+&8] &e🎉 {player} &7joined the new year celebration! &e🎉"
      
      blackfriday:
        first-time:
          - "&8[&a+&8] &c🛍️ <gradient:#000000:#FF0000>Black Friday Deal!</gradient> &c{player} &7joined! &c🛍️"
        default:
          - "&8[&a+&8] &c💳 {player} &7is here for the deals! &c💳"
```

### Custom Milestones
```yaml
# config.yml - Custom milestone system
smart-recognition:
  enabled: true
  milestones:
    enabled: true
    # Custom join milestones
    join-milestones: [5, 15, 30, 75, 150, 300, 750, 1500, 3000, 7500, 15000]
    # Custom playtime milestones (in hours)
    playtime-milestones: [0.5, 2, 5, 12, 24, 48, 120, 240, 480, 1000, 2000]
    # Custom streak milestones (consecutive days)
    streak-milestones: [2, 5, 10, 20, 50, 100, 200, 500, 1000]

# messages.yml - Custom milestone messages
messages:
  join:
    milestones:
      join-milestones:
        5:
          - "&8[&a+&8] &a🎯 <gradient:#00FF00:#32CD32>First Steps!</gradient> &a{player} &7has joined &e5 times! &a🎯"
        15:
          - "&8[&a+&8] &b⭐ <gradient:#00BFFF:#4169E1>Getting Started!</gradient> &b{player} &7has joined &e15 times! &b⭐"
        30:
          - "&8[&a+&8] &e🔥 <gradient:#FFD700:#FFA500>On Fire!</gradient> &e{player} &7has joined &e30 times! &e🔥"
      
      playtime-milestones:
        0.5:
          - "&8[&a+&8] &a⏰ <gradient:#00FF00:#32CD32>Quick Start!</gradient> &a{player} &7has played for &e30 minutes! &a⏰"
        2:
          - "&8[&a+&8] &b⏱️ <gradient:#00BFFF:#4169E1>Getting Into It!</gradient> &b{player} &7has played for &e2 hours! &b⏱️"
        12:
          - "&8[&a+&8] &e🕐 <gradient:#FFD700:#FFA500>Half Day!</gradient> &e{player} &7has played for &e12 hours! &e🕐"
```

## Troubleshooting Advanced Issues

### Performance Issues

#### High Memory Usage
```yaml
# Solution: Reduce cache and save more frequently
performance:
  cache-time: 2  # Reduce from 5 to 2 minutes
  max-cache-size: 50  # Reduce from 100 to 50

general:
  save-interval: 3  # Reduce from 10 to 3 minutes
```

#### Slow Message Processing
```yaml
# Solution: Disable expensive features
animations:
  enabled: false  # Disable animations

effects:
  particles:
    enabled: false  # Disable particles
  fireworks:
    enabled: false  # Disable fireworks
  title:
    stay: 20  # Reduce title display time
```

#### Database Issues
```yaml
# Solution: Switch to YAML for simplicity
storage:
  type: 'yaml'  # Use YAML instead of SQLite
```

### Animation Issues

#### Animations Not Showing
```yaml
# Check animation configuration
animations:
  enabled: true
  use-action-bar: true  # Ensure action bar is enabled
  show-final-in-chat: true  # Show final message in chat
```

#### Animation Performance Issues
```yaml
# Optimize animation settings
animations:
  default-duration: 30  # Reduce duration
  use-action-bar: true  # More efficient than chat
  show-final-in-chat: false  # Reduce chat spam
```

### Theme Issues

#### Themes Not Changing
```yaml
# Check theme configuration
themes:
  enabled: true
  auto-detect: true
  check-interval: 30  # Check more frequently
```

#### Custom Themes Not Working
```yaml
# Ensure theme messages are configured
# Check messages.yml for theme-specific messages
# Verify theme priority settings
```

## API Usage

### Java API Examples

#### Basic Plugin Integration
```java
// Get the plugin instance
WelcomePlugin plugin = (WelcomePlugin) Bukkit.getPluginManager().getPlugin("WelcomeMessages");

// Get managers
MessageManager messageManager = plugin.getMessageManager();
DataManager dataManager = plugin.getDataManager();
EffectManager effectManager = plugin.getEffectManager();

// Check if player is first join
boolean isFirstJoin = dataManager.isFirstJoin(player);

// Get join message
String joinMessage = messageManager.getJoinMessage(player, isFirstJoin);

// Send effects
effectManager.sendTitle(player, isFirstJoin);
effectManager.playJoinSound(player, isFirstJoin);
effectManager.playJoinParticles(player, isFirstJoin);
```

#### Custom Message Integration
```java
// Create custom message
String customMessage = "&6&lCustom Welcome &e{player}&6&l!";

// Replace placeholders
customMessage = customMessage.replace("{player}", player.getName());

// Send with animation
AnimationUtils animationUtils = new AnimationUtils(plugin);
animationUtils.animateMessage(player, customMessage, "typing", 60);
```

#### Data Access
```java
// Get player data
DataManager.PlayerData playerData = dataManager.getPlayerDataPublic(player);

// Access specific data
int joinCount = playerData.joinCount;
long totalPlaytime = playerData.totalPlaytime;
boolean messagesDisabled = playerData.messagesDisabled;

// Update data
dataManager.updatePlayerData(player);
dataManager.setLastSeen(player);
```

#### Theme Management
```java
// Get theme manager
ThemeManager themeManager = messageManager.getThemeManager();

// Get current theme
String currentTheme = themeManager.getCurrentTheme();

// Set custom theme
themeManager.setTheme("halloween");

// Get theme messages
List<String> themeMessages = themeManager.getThemeMessages("join", true);
```

#### Smart Recognition
```java
// Get smart recognition manager
SmartRecognitionManager smartManager = messageManager.getSmartRecognitionManager();

// Check milestones
String milestoneMessage = smartManager.checkMilestones(player, false);

// Get returning player message
String returningMessage = smartManager.getReturningPlayerMessage(player);

// Get player behavior
Map<String, Object> behavior = smartManager.getPlayerBehavior(player);
```

### Event Integration

#### Custom Event Handling
```java
@EventHandler
public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    
    // Get WelcomeMessages data
    WelcomePlugin plugin = (WelcomePlugin) Bukkit.getPluginManager().getPlugin("WelcomeMessages");
    DataManager dataManager = plugin.getDataManager();
    
    // Check if first join
    boolean isFirstJoin = dataManager.isFirstJoin(player);
    
    // Custom logic based on join status
    if (isFirstJoin) {
        // Handle first join
        player.sendMessage("Welcome to our server!");
    } else {
        // Handle returning player
        int joinCount = dataManager.getJoinCount(player);
        player.sendMessage("Welcome back! This is your " + joinCount + "th join.");
    }
}
```

#### Custom Animation Integration
```java
// Create custom animation
AnimationUtils animationUtils = new AnimationUtils(plugin);

// Animate custom message
String message = "&6&lWelcome to our custom server!";
animationUtils.animateMessage(player, message, "rainbow", 80);

// Clean up animations when needed
animationUtils.cleanupPlayerAnimations(player.getUniqueId());
```

### Configuration API

#### Dynamic Configuration Updates
```java
// Reload configuration
plugin.reload();

// Update specific settings
plugin.getConfig().set("messages.join.enabled", true);
plugin.saveConfig();

// Reload messages
plugin.reloadMessagesConfig();
```

#### Custom Configuration Validation
```java
// Create custom validator
ConfigValidator validator = new ConfigValidator(plugin);

// Validate configuration
boolean isValid = validator.validateConfig();

// Get validation results
List<String> errors = validator.getErrors();
List<String> warnings = validator.getWarnings();
```

This advanced configuration guide provides comprehensive examples for optimizing performance, implementing security measures, creating custom integrations, and troubleshooting complex issues with the WelcomeMessages plugin.
