![https://i.postimg.cc/VLwf7Mjw/screenshot.png](https://i.postimg.cc/VLwf7Mjw/screenshot.png "https://i.postimg.cc/VLwf7Mjw/screenshot.png")
# WelcomeMessages v1.3.5

**The Ultimate Welcome Plugin for Minecraft Servers**

*Clean, lightweight, and packed with features that actually work!*

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.x-green?style=flat-square)
![Java Version](https://img.shields.io/badge/Java-21-orange?style=flat-square)
![Spigot Compatible](https://img.shields.io/badge/Spigot-Compatible-yellow?style=flat-square)
![Paper Compatible](https://img.shields.io/badge/Paper-Compatible-red?style=flat-square)

---

## 🎉 What's New in v1.3.5

### 🔧 **Critical Bug Fixes & Formatting Improvements**
WelcomeMessages v1.3.5 focuses on fixing critical issues and improving message formatting:

- **🔧 Title Display Fixes** - Welcome titles now show consistently on player join with proper timing and online checks
- **🎨 Message Formatting** - Resolved HTML entity corruption that was causing `mp:8` instead of `&8` color codes
- **🛡️ Division by Zero Protection** - Added safety checks to prevent crashes when animation duration is set to 0
- **💾 Memory Leak Fixes** - Proper cleanup of firework tasks and animation resources when plugin disables
- **🔍 HTML Entity Handling** - Added automatic cleanup of HTML entities in messages
- **⚡ Resource Management** - Better cleanup system for all plugin resources
- **🔧 Code Quality** - Fixed deprecated method usage and improved error handling

### 🎆 **Previous v1.3.4: Firework Safety & Theme System Improvements**
WelcomeMessages v1.3.4 focused on making your server experience safer and more reliable:

- **🎆 Firework Safety** - Fixed critical issue where welcome fireworks were causing damage to players
- **🎨 Theme System Enhancement** - Completely overhauled theme conflict detection and resolution
- **⏰ Time Range Fixes** - Resolved overlapping time-based theme conflicts for smoother transitions
- **📅 Date Logic Improvements** - Better handling of seasonal themes that cross year boundaries
- **🔍 Automatic Validation** - Plugin now detects and reports theme configuration conflicts on startup
- **🛡️ Enhanced Error Handling** - Comprehensive validation for all date and time parsing
- **⚡ Memory Management** - Improved cleanup system for firework effects to prevent memory leaks
- **🔧 Code Quality** - Zero linter errors, improved documentation and code structure

### 🚀 **Previous v1.3.1 Improvements**
- **Security Fixes** - Fixed various security issues and improved code safety
- **Rate Limiting** - Built-in command cooldowns to prevent spam and abuse
- **Input Sanitization** - Protection against malicious input and injection attacks
- **Thread Safety** - All operations are now fully thread-safe
- **Performance Optimization** - ThreadLocalRandom usage and memory management improvements

---

## 🎉 Previous Features (v1.2.5)

![https://i.postimg.cc/wTk6nvVm/Rainbow.gif](https://i.postimg.cc/wTk6nvVm/Rainbow.gif "https://i.postimg.cc/wTk6nvVm/Rainbow.gif")

### ✨ **12 Amazing Text Animations**
WelcomeMessages now includes a complete animation system with 12 different effects:

- **Typing** - Character-by-character reveal with perfect color handling
- **Typewriter** - Classic typewriter effect with blinking cursor
- **Fade** - Smooth fade-in effect with opacity changes
- **Slide** - Text slides in from the side
- **Wave** - Text waves up and down like ocean waves
- **Rainbow** - Colors cycle through rainbow spectrum
- **Glitch** - Random characters appear and disappear
- **Bounce** - Text bounces up and down with varying heights
- **Shake** - Text shakes left and right randomly
- **Pulse** - Text pulses in brightness (bright → yellow → gray)
- **Matrix** - Matrix-style falling characters with green color
- **Scramble** - Text scrambles with random characters, then reveals correctly

### 🎯 **Action Bar Display**
- Animations are **private to the target player** (no chat spam!)
- Configurable duration for each animation
- Different animations for join, quit, and first-join messages
- Perfect color code preservation
- Smooth performance optimization

### 🛠️ **Bug Fixes & Improvements**
- **FIXED:** Tab completion for `/welcome testanim` command
- **FIXED:** Typing animations now work perfectly with color codes
- **FIXED:** Typewriter animation with proper blinking cursor
- **FIXED:** All linter errors and warnings (zero errors!)
- **IMPROVED:** Performance and compatibility across all server types

---

## 🚀 Key Features

![https://i.postimg.cc/sgV22SXm/IMG1.png](https://i.postimg.cc/sgV22SXm/IMG1.png "https://i.postimg.cc/sgV22SXm/IMG1.png")

### 🎨 **Visual Excellence**
- **RGB & Gradient Support** - Modern color codes and rainbow text
- **12 Text Animations** - Make your messages truly special
- **Custom Rank System** - Unlimited custom ranks with any names
- **Smart Effects** - Particles, sounds, titles, fireworks (all optional)

### 🔧 **Admin-Friendly**
- **Config Validation** - Won't crash your server with bad settings
- **PlaceholderAPI Support** - 13+ placeholders for other plugins
- **Full Spigot/Paper Compatibility** - Works on both server types
- **Async Performance** - Your TPS will thank you
- **Smart Caching** - Optimized for server performance

### 👥 **Player Experience**
- **Personal Toggle** - Players can turn messages on/off
- **Join Counter** - Tracks player visits and saves data
- **First Join Detection** - Special messages for new players
- **Rank-Based Messages** - VIPs get VIP treatment
- **Private Animations** - No chat spam, just beautiful effects

---

## 📋 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/welcome` | Show help menu | `welcome.use` |
| `/welcome reload` | Reload configuration | `welcome.reload` |
| `/welcome test` | Preview your join message | `welcome.test` |
| `/welcome testall` | Test all features including animations | `welcome.testall` |
| `/welcome testanim &lt;type&gt; [player]` | Test specific animation types | `welcome.testanim` |
| `/welcome toggle` | Turn your messages on/off | `welcome.toggle` |
| `/welcome stats [player]` | Check join count statistics | `welcome.stats` |
| `/welcome reset [player]` | Reset player data | `welcome.reset` |
| `/welcome version` | Show plugin version | `welcome.use` |

---

## 🎮 Animation Testing

Test all the amazing animations with these commands:

```
# Test all features including animations
/welcome testall

# Test specific animation types
/welcome testanim typing      # Character-by-character reveal
/welcome testanim typewriter  # Typewriter with blinking cursor
/welcome testanim bounce      # Bouncy text effect
/welcome testanim matrix      # Matrix-style falling characters
/welcome testanim scramble    # Scramble and reveal effect
# ... and 7 more animation types!
```

---

## ⚙️ Configuration

### Animation Settings
```yaml
animations:
  enabled: true
  default-duration: 60
  default-type: "typing"
  use-action-bar: true
  show-final-in-chat: true
  
  join:
    enabled: false  # Disabled by default to prevent chat spam
    type: "typing"
    duration: 60
  quit:
    enabled: false  # Disabled by default to prevent chat spam
    type: "fade"
    duration: 40
  first-join:
    enabled: true   # First join animations are special
    type: "rainbow"
    duration: 80
```

### Custom Rank System
```yaml
custom-ranks:
  enabled: true
  ranks:
    - "owner"      # Server owner
    - "admin"      # Administrator  
    - "moderator"  # Moderator
    - "mvp"        # MVP rank
    - "vip"        # VIP rank
    - "premium"    # Premium rank
    - "donator"    # Donator rank
    - "member"     # Member rank
```

---

## 🔗 PlaceholderAPI Support

If you have PlaceholderAPI installed, you can use these placeholders:

| Placeholder | Description |
|-------------|-------------|
| `%welcome_join_count%` | Player's join count |
| `%welcome_first_join%` | Whether it's player's first join |
| `%welcome_messages_disabled%` | Whether player has messages disabled |
| `%welcome_last_seen%` | Time since player was last seen |
| `%welcome_first_join_time%` | When player first joined |
| `%welcome_time_since_last_seen%` | Time since last seen |
| `%welcome_time_since_first_join%` | Time since first join |
| `%welcome_total_unique_joins%` | Total unique players who joined |
| `%welcome_join_ordinal%` | Player's join order (1st, 2nd, etc.) |
| `%welcome_player_ordinal%` | Player's current position |
| `%welcome_status%` | Player status (New/Returning) |
| `%welcome_rank%` | Player's highest rank |
| `%welcome_time_greeting%` | Time-based greeting |
| `%welcome_server_uptime%` | Server uptime |

---

## 📊 Performance

- **Async Everything** - Your TPS will thank you
- **Smart Caching** - Not the dumb kind
- **Optional Metrics** - Off by default for privacy
- **Works on Potato Servers** - Optimized for all server sizes
- **Zero Linter Errors** - Perfect code quality

---

## 🛡️ Compatibility

- **Minecraft:** 1.21.x
- **Java:** 21 (required by Minecraft)
- **Server Software:** Spigot, Paper, Purpur, Pufferfish
- **Permission Plugins:** LuckPerms, PEX, GroupManager, etc.
- **Other Plugins:** PlaceholderAPI, bStats

---

## 📥 Installation

1. Download the `WelcomeMessages-1.2.5.jar` file
2. Place it in your server's `/plugins` folder
3. Restart your server
4. Edit the configuration files to your liking
5. Enjoy your new welcome system!

---

## 🎯 Why Choose WelcomeMessages?

- **No Bloat** - Does one thing and does it well
- **No Premium Features** - Everything is included
- **Actually Works** - No crashes, no broken updates
- **Modern Features** - RGB colors, animations, PlaceholderAPI
- **Admin Friendly** - Config validation, easy setup
- **Player Friendly** - Personal toggles, beautiful effects
- **Performance Focused** - Async, caching, optimization
- **Community Driven** - Open source, active development

---

## 🐛 Support & Feedback

- **GitHub Issues:** [Report bugs and request features](https://github.com/FiveDollaGobby/WelcomeMessages/issues)
- **GitHub Repository:** [View source code and documentation](https://github.com/FiveDollaGobby/WelcomeMessages)
- **Releases:** [Download latest version](https://github.com/FiveDollaGobby/WelcomeMessages/releases)
- **Updates:** Regular updates with new features and fixes

---

**Made with ❤️ by FiveDollaGobby**

*The welcome plugin that actually works!*

[![GitHub](https://img.shields.io/badge/GitHub-Repository-black?style=for-the-badge&logo=github)](https://github.com/FiveDollaGobby/WelcomeMessages)
[![Download](https://img.shields.io/badge/Download-Latest-green?style=for-the-badge)](https://github.com/FiveDollaGobby/WelcomeMessages/releases)

---

## 🔗 Links

- **GitHub Repository:** [https://github.com/FiveDollaGobby/WelcomeMessages](https://github.com/FiveDollaGobby/WelcomeMessages)
- **Latest Release:** [v1.2.5 Download](https://github.com/FiveDollaGobby/WelcomeMessages/releases/latest)
- **Report Issues:** [GitHub Issues](https://github.com/FiveDollaGobby/WelcomeMessages/issues)
- **View Source Code:** [GitHub Code](https://github.com/FiveDollaGobby/WelcomeMessages)
