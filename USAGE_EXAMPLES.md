# WelcomeMessages - Detailed Usage Examples

## Table of Contents
- [Basic Setup](#basic-setup)
- [Command Examples](#command-examples)
- [Configuration Examples](#configuration-examples)
- [Animation Examples](#animation-examples)
- [Theme Examples](#theme-examples)
- [PlaceholderAPI Examples](#placeholderapi-examples)
- [Permission Examples](#permission-examples)
- [Troubleshooting](#troubleshooting)

## Basic Setup

### 1. Installation
```bash
# Download the plugin JAR file
# Place WelcomeMessages-1.3.1.jar in your server's plugins folder
# Restart your server
# Configure the plugin as needed
```

### 2. First Configuration
```yaml
# config.yml - Basic setup
general:
  debug: false
  check-updates: true
  save-interval: 10

messages:
  join:
    enabled: true
    disable-vanilla: true
    console: true
  quit:
    enabled: true
    disable-vanilla: true
    console: true
```

## Command Examples

### Basic Commands

#### Help Command
```bash
/welcome help
# Shows all available commands with descriptions
```

#### Reload Command
```bash
/welcome reload
# Reloads all configuration files
# Requires: welcome.reload permission
```

#### Version Command
```bash
/welcome version
# Shows plugin version and author information
```

### Testing Commands

#### Test Join Message
```bash
# Test for yourself
/welcome test

# Test for another player
/welcome test PlayerName

# Example output:
# Testing join message for PlayerName:
# [WelcomeMessages] Welcome PlayerName to the server!
```

#### Test All Features
```bash
# Comprehensive feature showcase
/welcome testall PlayerName

# Shows:
# - Basic messages
# - RGB and gradient text
# - Animation system
# - Dynamic themes
# - Smart recognition
# - PlaceholderAPI examples
# - Rank system
# - Visual effects
```

#### Test Animations
```bash
# Test specific animation
/welcome testanim typing PlayerName
/welcome testanim rainbow PlayerName
/welcome testanim epic_welcome PlayerName

# Available animations:
# - typing, fade, slide, wave, rainbow, glitch
# - typewriter, bounce, shake, pulse, matrix, scramble
# - epic_welcome, mysterious_join, festive_celebration
# - smooth_entrance, party_time, milestone_celebration
```

#### Test Themes
```bash
# Test specific theme
/welcome testtheme halloween PlayerName
/welcome testtheme christmas PlayerName
/welcome testtheme morning PlayerName

# Available themes:
# - Seasonal: halloween, christmas, valentine, easter, summer, winter
# - Time-based: morning, afternoon, evening, night
```

#### Test Milestones
```bash
# Test milestone detection
/welcome testmilestone PlayerName

# Shows:
# - Current milestone data
# - Milestone messages
# - Returning player messages
```

#### Test Playtime
```bash
# Test playtime tracking
/welcome testplaytime PlayerName

# Shows:
# - Total playtime
# - Current session time
# - Milestone status
```

### Player Management Commands

#### View Statistics
```bash
# View your own stats
/welcome stats

# View another player's stats
/welcome stats PlayerName

# Shows:
# - Join count
# - First join status
# - Messages enabled/disabled
# - Total playtime
# - Current session time
```

#### Reset Player Data
```bash
# Reset all data for a player
/welcome reset PlayerName

# Resets:
# - Join count
# - Playtime data
# - Milestone tracking
# - Message preferences
```

#### Toggle Messages
```bash
# Toggle your own join/quit messages
/welcome toggle

# Toggles between:
# - Messages enabled
# - Messages disabled
```

## Configuration Examples

### Basic Message Configuration

#### Simple Join Messages
```yaml
# messages.yml
messages:
  join:
    default:
      - "&8[&a+&8] &e{player} &7joined the game"
      - "&8[&a+&8] &7Welcome back, &e{player}&7!"
      - "&8[&a+&8] &e{player} &7is now online"
```

#### First Join Messages
```yaml
messages:
  join:
    first-time:
      - "&8[&a+&8] <gradient:#FFD700:#FFA500>Welcome {player} to the server!</gradient> &7(#{ordinal} player)"
      - "&8[&a+&8] &d✨ <rainbow>A new adventurer {player} has arrived!</rainbow> &d✨"
```

### Rank-Based Messages

#### VIP Rank Messages
```yaml
messages:
  join:
    ranks:
      vip:
        - "&8[&a+&8] <gradient:#FFD700:#FFED4E>[VIP] {player}</gradient> &7joined the game"
        - "&8[&a+&8] &6✦ <gradient:#FFD700:#FFA500>VIP {player}</gradient> &6has arrived! ✦"
```

#### Admin Rank Messages
```yaml
messages:
  join:
    ranks:
      admin:
        - "&8[&a+&8] <gradient:#FF0000:#FF6B6B>[Admin] {player}</gradient> &cjoined the server"
        - "&8[&a+&8] &c🛡 <gradient:#FF4444:#FFAAAA>Administrator {player}</gradient> &cis now online 🛡"
```

### Theme-Based Messages

#### Halloween Theme
```yaml
messages:
  join:
    themes:
      halloween:
        first-time:
          - "&8[&a+&8] &c🎃 <gradient:#FF4500:#FF8C00>Welcome {player} to our spooky server!</gradient> &c🎃"
        default:
          - "&8[&a+&8] &c🦇 {player} &7joined the haunted server &c🦇"
```

#### Christmas Theme
```yaml
messages:
  join:
    themes:
      christmas:
        first-time:
          - "&8[&a+&8] &c🎄 <gradient:#FF0000:#00FF00>Welcome {player} to our festive server!</gradient> &c🎄"
        default:
          - "&8[&a+&8] &c🎅 {player} &7joined the winter wonderland &c🎅"
```

### Milestone Messages

#### Join Milestones
```yaml
messages:
  join:
    milestones:
      join-milestones:
        10:
          - "&8[&a+&8] &6🎉 <gradient:#FFD700:#FFA500>Milestone Alert!</gradient> &6{player} &7has joined &e10 times! &6🎉"
        100:
          - "&8[&a+&8] &6🏅 <gradient:#FFD700:#FF8C00>Century Club!</gradient> &6{player} &7has joined &e100 times! &6🏅"
```

#### Playtime Milestones
```yaml
messages:
  join:
    milestones:
      playtime-milestones:
        10:
          - "&8[&a+&8] &b⏱️ <gradient:#00BFFF:#4169E1>Double Digits!</gradient> &b{player} &7has played for &e10 hours! &b⏱️"
        100:
          - "&8[&a+&8] &e🕐 <gradient:#FFD700:#FFA500>Century Hours!</gradient> &e{player} &7has played for &e100 hours! &e🕐"
```

## Animation Examples

### Basic Animation Configuration
```yaml
# config.yml
animations:
  enabled: true
  default-duration: 60
  default-type: "typing"
  use-action-bar: true
  show-final-in-chat: true
```

### Per-Message Animation Settings
```yaml
animations:
  join:
    enabled: true
    type: "typing"
    duration: 60
    use-multi-layer: false
    multi-layer-type: "smooth_entrance"
  
  first-join:
    enabled: true
    type: "rainbow"
    duration: 80
    use-multi-layer: true
    multi-layer-type: "epic_welcome"
```

### Multi-Layer Animation Examples
```yaml
animations:
  multi-layer:
    enabled: true
    combinations:
      epic_welcome:
        effects: ["rainbow", "wave", "bounce"]
        duration: 100
        description: "Epic rainbow wave with bounce effect"
      
      mysterious_join:
        effects: ["glitch", "fade", "matrix"]
        duration: 80
        description: "Mysterious glitch with matrix effect"
      
      party_time:
        effects: ["bounce", "pulse", "rainbow", "wave"]
        duration: 150
        description: "Full party mode with all effects"
```

## Theme Examples

### Seasonal Theme Configuration
```yaml
# config.yml
themes:
  enabled: true
  current: "auto"
  auto-detect: true
  check-interval: 60
  
  seasonal:
    halloween:
      start: "10-01"
      end: "11-01"
      priority: 10
    christmas:
      start: "12-01"
      end: "12-31"
      priority: 10
```

### Time-Based Theme Configuration
```yaml
themes:
  time-based:
    enabled: true
    morning:
      start: "06:00"
      end: "12:00"
      priority: 1
    evening:
      start: "18:00"
      end: "22:00"
      priority: 1
```

## PlaceholderAPI Examples

### Available Placeholders
```
%welcome_joincount% - Player's join count
%welcome_firstjoin% - Whether it's first join (true/false)
%welcome_messagesdisabled% - Whether messages are disabled (true/false)
%welcome_lastseen% - Last seen timestamp
%welcome_firstjointime% - First join timestamp
%welcome_time_since_last_seen% - Time since last seen
%welcome_time_since_first_join% - Time since first join
%welcome_total_unique_joins% - Total unique joins on server
%welcome_join_ordinal% - Ordinal number for first joins
%welcome_player_ordinal% - Player's join ordinal
%welcome_status% - Player status (New Player, Regular Player, etc.)
%welcome_rank% - Player's rank
%welcome_time_greeting% - Time-based greeting (morning/afternoon/evening)
```

### Usage in Other Plugins
```yaml
# Example in another plugin's config
welcome_message: "&aWelcome %welcome_joincount% times, %player%!"
join_message: "&7[%welcome_rank%] &e%player% &7joined"
```

## Permission Examples

### Basic Permissions
```yaml
# Give basic access
welcome.use: true

# Give admin access
welcome.admin: true

# Give specific permissions
welcome.reload: true
welcome.test: true
welcome.stats: true
welcome.reset: true
welcome.toggle: true
```

### Rank Permissions
```yaml
# VIP rank
welcome.rank.vip: true

# MVP rank
welcome.rank.mvp: true

# Admin rank
welcome.rank.admin: true

# Owner rank
welcome.rank.owner: true
```

### Message Permissions
```yaml
# See join messages
welcome.see.join: true

# See quit messages
welcome.see.quit: true

# Exempt from join messages
welcome.exempt.join: true

# Exempt from quit messages
welcome.exempt.quit: true

# Bypass effect cooldowns
welcome.effects.bypass: true
```

## Troubleshooting

### Common Issues

#### Messages Not Showing
1. Check if messages are enabled in config
2. Verify player has `welcome.see.join` permission
3. Check if player has messages disabled (`/welcome toggle`)
4. Ensure plugin is loaded correctly

#### Animations Not Working
1. Verify animations are enabled in config
2. Check if action bar is supported by your server
3. Ensure player has proper permissions
4. Check console for errors

#### Themes Not Changing
1. Verify themes are enabled in config
2. Check theme detection settings
3. Ensure theme messages are configured
4. Check theme priority settings

#### PlaceholderAPI Not Working
1. Ensure PlaceholderAPI is installed
2. Check if expansion is registered
3. Verify placeholder syntax
4. Check console for errors

### Debug Mode
```yaml
# Enable debug mode
general:
  debug: true
```

### Performance Issues
```yaml
# Optimize performance
performance:
  async-messages: true
  cache-time: 5
  max-cache-size: 100
```

### Memory Issues
```yaml
# Reduce memory usage
general:
  save-interval: 5  # Save more frequently
performance:
  cache-time: 3     # Reduce cache time
  max-cache-size: 50 # Reduce cache size
```

## Advanced Examples

### Custom Rank System
```yaml
# config.yml
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
```

### Smart Recognition Configuration
```yaml
# config.yml
smart-recognition:
  enabled: true
  milestones:
    enabled: true
    join-milestones: [10, 25, 50, 100, 250, 500, 1000]
    playtime-milestones: [1, 5, 10, 25, 50, 100, 250, 500]
    streak-milestones: [3, 7, 14, 30, 60, 100, 365]
```

### Effect Configuration
```yaml
# config.yml
effects:
  title:
    enabled: true
    fade-in: 10
    stay: 70
    fade-out: 20
  
  sound:
    enabled: true
    volume: 1.0
    pitch: 1.0
    first-join: "UI_TOAST_CHALLENGE_COMPLETE"
    regular: "ENTITY_PLAYER_LEVELUP"
  
  particles:
    enabled: true
    animated: true
    first-join: "TOTEM"
    regular: "VILLAGER_HAPPY"
  
  fireworks:
    enabled: true
    all-joins: false
    amount: 3
    delay-between: 20
```

This comprehensive documentation provides detailed examples for every aspect of the WelcomeMessages plugin, making it easy for server administrators to configure and use all features effectively.
