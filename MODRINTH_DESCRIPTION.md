# WelcomeMessages

A **simple, lightweight** welcome message plugin that actually works and doesn't break your server. Now with **enhanced security** and **improved stability**!

![IMG1](https://cdn.modrinth.com/data/cached_images/bcd519b13826f6444ccc1bb15ba2127bff43963e.png)

## ✨ Features

![winter](https://i.ibb.co/hJLWHgTJ/2025-09-26-08-15-02.png)

- **Smart Messages** - Different messages for new players vs returning ones
- **Custom Rank System** - Define unlimited custom ranks with any names
- **12 Text Animations** - typing, fade, slide, wave, rainbow, glitch, typewriter, bounce, shake, pulse, matrix, scramble
- **Fancy Effects** - Particles, sounds, titles, fireworks - all optional and configurable
- **RGB & Gradients** - Support for modern color codes and rainbow text
- **Config Validation** - Won't crash your server with bad settings
- **PlaceholderAPI Support** - Use data in other plugins and chat
- **Full Spigot Compatibility** - Works on Spigot, Paper, Purpur, and Pufferfish
- **Enhanced Security** - Improved security with input validation and rate limiting
- **Rate Limiting** - Built-in command cooldowns to prevent spam
- **Input Sanitization** - Protection against malicious input and injection
- **Memory Management** - Automatic cleanup to prevent memory leaks
- **Thread Safety** - Fully thread-safe operations for maximum stability
- **Not Annoying** - Everything's configurable, nothing's forced

![IMG2](https://cdn.modrinth.com/data/cached_images/e802f6a28b3312d64e8da8b54b040edfc53164ba.png)

## 🎮 What It Does

![afternoon](https://i.ibb.co/d0gKKfJW/2025-09-26-08-15-10.png)

Makes your server feel more alive with custom join/quit messages. First time players get a special welcome, regulars get their own messages, and your VIPs can have fancy entrances with fireworks and everything. **NEW in v1.3.6:** Full compatibility with all Minecraft 1.21.x versions (1.21 through 1.21.9) on both Spigot and Paper servers. **Previous v1.3.5:** Fixed critical title display issues, message formatting bugs, and memory leaks. **Previous v1.3.4:** Fixed firework damage issues and enhanced theme system with automatic conflict detection. **Previous v1.3.1:** Enhanced security with rate limiting and input sanitization. **Previous v1.2.5:** 12 stunning text animations that play in the action bar (no chat spam!).

![IMG3](https://cdn.modrinth.com/data/cached_images/fa6e007fb779d4f6e34b805751654e39f44b5469.png)

## 🚀 Quick Start

1. Drop the jar into your plugins folder
2. Restart your server
3. Edit the config files to your liking
4. Done! (Plugin validates your config so it won't crash)

![IMG4](https://cdn.modrinth.com/data/cached_images/b89036f41b9b67452a1985be99628e171aeab700.png)

## 📋 Commands

- `/welcome` - Shows help (or `/wm` for short)
- `/welcome reload` - Reloads the config (with validation!)
- `/welcome test` - Test messages without joining/leaving
- `/welcome testall` - Test all features and animations
- `/welcome testanim <type>` - Test specific animation types
- `/welcome stats` - See join counts and stuff
- `/welcome reset <player>` - Reset a player's data
- `/welcome toggle` - Toggle your own messages on/off
- `/welcome version` - Check what version you're running

![IMG5](https://cdn.modrinth.com/data/cached_images/a160872edaa6440d17c2a21d393a263fff1be2dd.png)

## 🔧 Configuration

Everything's in `config.yml` and `messages.yml`. The config is actually readable and the plugin validates it on startup so you won't get random crashes.

![IMG6](https://cdn.modrinth.com/data/cached_images/a68c9d8aa92b407e44565d049d80cba53fb1275f.png)

## 🏆 Custom Rank System

**NEW in v1.2.0!** Define unlimited custom ranks in your `config.yml`:

```yaml
custom-ranks:
  enabled: true
  ranks:
    - name: "founder"
      permission: "welcome.rank.founder"
      priority: 100
    - name: "coowner"
      permission: "welcome.rank.coowner"
      priority: 90
    - name: "manager"
      permission: "welcome.rank.manager"
      priority: 80
    - name: "moderator"
      permission: "welcome.rank.moderator"
      priority: 70
    - name: "helper"
      permission: "welcome.rank.helper"
      priority: 60
    - name: "vip"
      permission: "welcome.rank.vip"
      priority: 50
    - name: "member"
      permission: "welcome.rank.member"
      priority: 40
```

**Perfect for:**
- Multiple VIP tiers (VIP1, VIP2, VIP3, VIP4)
- Custom server ranks (Builder, Helper, Moderator, etc.)
- Any rank structure you want

**How it works:**
1. Define ranks in `config.yml` (in priority order)
2. Add messages for each rank in `messages.yml`
3. Give players `welcome.rank.<rankname>` permissions
4. Plugin automatically detects and uses the highest rank

## 🎨 Text Animations

**NEW in v1.2.5!** 12 stunning text animations that play in the action bar:

- **typing** - Typewriter effect with realistic typing speed
- **fade** - Smooth fade in/out effect
- **slide** - Text slides in from different directions
- **wave** - Text waves up and down
- **rainbow** - Colorful rainbow cycling effect
- **glitch** - Matrix-style glitch effect
- **typewriter** - Character-by-character reveal
- **bounce** - Bouncy text animation
- **shake** - Shaking text effect
- **pulse** - Pulsing text animation
- **matrix** - Matrix-style falling characters
- **scramble** - Text scrambling effect

**Features:**
- Private action bar animations (no chat spam!)
- Configurable duration and type per message
- Works with all message types (join, quit, first join)
- Smooth performance with async processing

## 🎨 PlaceholderAPI Support

Works with any plugin that supports PlaceholderAPI:
- `%welcome_joincount%` - How many times they've joined
- `%welcome_firstjoin%` - Is this their first time?
- `%welcome_status%` - New Player, Regular Player, etc.
- `%welcome_rank%` - Their rank (VIP, ADMIN, etc.)
- `%welcome_messagesdisabled%` - Are their messages disabled?
- `%welcome_lastseen%` - When they last joined
- `%welcome_firstjointime%` - When they first joined
- `%welcome_time_since_last_seen%` - How long since last join
- `%welcome_time_since_first_join%` - How long since first join
- `%welcome_total_unique_joins%` - Total unique joins on server
- `%welcome_join_ordinal%` - Ordinal of their join (1st, 2nd, etc.)
- `%welcome_player_ordinal%` - Their join count ordinal
- `%welcome_time_greeting%` - Time-based greeting (morning/afternoon/evening)
- `%welcome_server_uptime%` - Server uptime

## 🛠️ Performance

- Async everything (your TPS will thank me)
- Smart caching (not the dumb kind)
- Optional metrics (off by default because privacy)
- No database files cluttering your server
- Cross-version compatibility (Spigot, Paper, Purpur, Pufferfish)

## 📋 Changelog

### v1.3.6 - Full 1.21.x Compatibility & Enhanced Support
- **🌐 ENHANCED: Full 1.21.x Support** - Now compatible with all Minecraft 1.21.x versions (1.21 through 1.21.9)
- **🔧 IMPROVED: Spigot & Paper Compatibility** - Enhanced support for both Spigot and Paper servers across all 1.21.x versions
- **📦 UPDATED: Dependencies** - Updated to latest Paper API 1.21.9 and Spigot API for maximum compatibility
- **🛡️ ENHANCED: Version Detection** - Better server version detection and compatibility handling
- **📚 UPDATED: Documentation** - Updated compatibility information and installation instructions

### v1.3.5 - Critical Bug Fixes & Formatting Improvements
- **🔧 FIXED: Title Display Issues** - Welcome titles now show consistently on player join with proper timing and online checks
- **🎨 FIXED: Message Formatting** - Resolved HTML entity corruption that was causing `mp:8` instead of `&8` color codes
- **🛡️ FIXED: Division by Zero** - Added safety checks to prevent crashes when animation duration is set to 0
- **💾 FIXED: Memory Leaks** - Proper cleanup of firework tasks and animation resources when plugin disables
- **🔍 IMPROVED: HTML Entity Handling** - Added automatic cleanup of HTML entities in messages
- **⚡ ENHANCED: Resource Management** - Better cleanup system for all plugin resources
- **🔧 CODE QUALITY** - Fixed deprecated method usage and improved error handling

### v1.3.4 - Theme System & Firework Safety Improvements
- **🎆 FIXED: Firework Damage Issue** - Fireworks no longer cause damage to players during welcome effects
- **🎨 IMPROVED: Theme System** - Enhanced theme conflict detection and resolution with automatic validation
- **⏰ FIXED: Time Range Overlaps** - Resolved overlapping time-based theme conflicts (morning/afternoon/evening/night)
- **📅 ENHANCED: Date Range Logic** - Improved year rollover handling for seasonal themes (winter, christmas, etc.)
- **🔍 NEW: Theme Validation** - Automatic detection and reporting of theme configuration conflicts on startup
- **🛡️ IMPROVED: Error Handling** - Better validation for date and time parsing with comprehensive error logging
- **⚡ PERFORMANCE: Memory Management** - Enhanced cleanup system for firework effects to prevent memory leaks
- **🔧 CODE QUALITY** - Zero linter errors, improved code structure and documentation

### v1.3.1 - Security & Performance Improvements
- **🔒 Security Improvements** - Fixed various security issues and improved code safety
- **⚡ Rate Limiting System** - Built-in command cooldowns to prevent spam and abuse
- **🛡️ Input Sanitization** - Protection against malicious input and injection attacks
- **💾 Memory Management** - Automatic cleanup system to prevent memory leaks
- **🔧 Thread Safety** - All operations are now fully thread-safe
- **🚫 Integer Overflow Protection** - Prevents crashes from large configuration values
- **✅ Null Pointer Safety** - Comprehensive null checks throughout the codebase
- **🧹 Resource Leak Prevention** - Proper cleanup of all resources
- **⚡ Performance Improvements** - Replaced Random with ThreadLocalRandom
- **📊 Better Error Handling** - Improved logging and error recovery
- **✨ Code Quality** - Zero linter errors, perfect code quality

### v1.2.5 - Animation System & Bug Fixes
- **🎨 12 Text Animations** - typing, fade, slide, wave, rainbow, glitch, typewriter, bounce, shake, pulse, matrix, scramble
- **🎯 Action Bar Display** - Private animations (no chat spam!)
- **🔧 Bug Fixes** - Fixed typing animations, typewriter cursor, and all linter errors
- **⚡ Performance** - Optimized animation rendering and compatibility

## 🔐 Permissions

### Admin Permissions
- `welcome.*` - Everything (all permissions)
- `welcome.reload` - Reload command
- `welcome.stats` - Stats command
- `welcome.reset` - Reset command
- `welcome.version` - Version command
- `welcome.testall` - Test all command
- `welcome.testanim` - Test animation command

### Player Permissions
- `welcome.toggle` - Toggle command
- `welcome.test` - Test command

### Rank Permissions (Custom!)
- `welcome.rank.<rankname>` - Any custom rank you define
- Examples: `welcome.rank.founder`, `welcome.rank.vip4`, `welcome.rank.helper`

### Effect Permissions
- `welcome.effects.title` - Title effects
- `welcome.effects.sound` - Sound effects
- `welcome.effects.particle` - Particle effects
- `welcome.effects.firework` - Firework effects

### Message Permissions
- `welcome.messages.join` - Join messages
- `welcome.messages.quit` - Quit messages
- `welcome.messages.firstjoin` - First join messages
- `welcome.messages.returning` - Returning player messages

### Bypass Permissions
- `welcome.bypass.cooldown` - Bypass message cooldown
- `welcome.bypass.effects` - Bypass effect restrictions

## 🤝 Contributing

Found a bug? Open an issue.
Fixed a bug? Open a PR.
Want a feature? Also GitHub!

## 📄 License

MIT License - do whatever you want with it.

---

**Made by FiveDollaGobby** - because the other welcome plugins were either abandoned, bloated, or cost money for basic stuff. This one's free, always will be.
