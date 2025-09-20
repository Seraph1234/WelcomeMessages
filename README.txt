=====================================================
    WelcomeMessages - v1.3.0
    For Minecraft 1.21.x
=====================================================

Hey! Thanks for downloading my plugin :)

QUICK START
-----------
1. Drop the WelcomeMessages-1.3.0.jar into your plugins folder
2. Restart your server (or reload if you're feeling risky)
3. Edit the config files to your liking
4. Done! (Plugin validates your config so it won't crash)

WHAT THIS DOES
--------------
Makes your server feel more alive with custom join/quit messages.
First time players get a special welcome, regulars get their own
messages, and your VIPs can have fancy entrances with fireworks
and everything. Now with 12 amazing text animations, RGB gradients,
rainbow text, config validation, PlaceholderAPI support, custom rank 
system, enhanced security, rate limiting, input sanitization,
memory management, thread safety, and full Spigot compatibility!

COMMANDS
--------
/welcome - Shows help (or /wm for short)
/welcome reload - Reloads the config (with validation!)
/welcome test - Preview your join message
/welcome testall - Test all features including animations
/welcome testanim <type> [player] - Test specific animation types
/welcome toggle - Turn your messages on/off
/welcome stats <player> - Check someone's join count
/welcome reset <player> - Reset someone's data (admin only)
/welcome version - Show plugin version

PERMISSIONS
-----------
Most stuff works out of the box, but if you want rank messages:
- welcome.rank.vip - VIP join messages
- welcome.rank.mvp - MVP join messages
- welcome.rank.admin - Admin messages
- welcome.rank.owner - Owner messages

For admins:
- welcome.admin - All admin commands
- welcome.reload - Just the reload command
- welcome.test - Test messages
- welcome.stats - View stats
- welcome.reset - Reset player data

For players:
- welcome.toggle - Turn messages on/off
- welcome.see.join - See join messages
- welcome.see.quit - See quit messages

ANIMATIONS (NEW IN v1.2.5!)
----------------------------
WelcomeMessages now includes 12 amazing text animations:

Animation Types:
- typing - Character-by-character reveal
- typewriter - Typewriter with blinking cursor
- fade - Smooth fade-in effect
- slide - Text slides in from side
- wave - Text waves up and down
- rainbow - Rainbow color cycling
- glitch - Random characters appear/disappear
- bounce - Text bounces up and down
- shake - Text shakes left and right
- pulse - Text pulses in brightness
- matrix - Matrix-style falling characters
- scramble - Text scrambles then reveals

Features:
- Animations display in action bar (private to player, no chat spam!)
- Configurable duration for each animation
- Different animations for join, quit, and first-join messages
- Perfect color code preservation
- Works on both Spigot and Paper

Testing:
/welcome testall - Test all features including animations
/welcome testanim typing - Test specific animation type
/welcome testanim bounce - Test bounce animation
# ... and 10 more animation types!

CONFIG TIPS
-----------
The main things you'll probably want to change:

In config.yml:
- effects.fireworks.all-joins: false
  (set to true if you want chaos lol)

- effects.sound.regular: "ENTITY_PLAYER_LEVELUP"
  (change to whatever sound you like, there's tons)

In messages.yml:
- Just edit the messages to whatever you want
- Use {player} for the player's name, {displayname} for display name
- Use & for color codes (&6 = gold, &b = aqua, etc)
- Use &#FF0000 for RGB colors, <rainbow>text</rainbow> for rainbow
- Use <gradient:#FF0000:#0000FF>text</gradient> for gradients
- You can add as many messages as you want to each list

PLACEHOLDERAPI SUPPORT
----------------------
If you have PlaceholderAPI installed, you can use these placeholders:

Player data:
- %welcome_joincount% - How many times joined
- %welcome_firstjoin% - true/false if first join
- %welcome_messagesdisabled% - true/false if disabled
- %welcome_lastseen% - When last seen
- %welcome_rank% - Player rank (VIP, MVP, etc.)

Server data:
- %welcome_total_unique_joins% - Total unique joins
- %welcome_time_greeting% - Morning/afternoon/evening
- %welcome_server_uptime% - Server uptime


PERFORMANCE
-----------
If your server is laggy:
- Set async-messages to true in config
- Reduce particle counts
- Increase save-interval

For small servers just leave everything on, it's fine.

SUPPORT
-------
Found a bug? Let me know on GitHub!
Want a feature? Also GitHub!
Everything broken? ...yeah, GitHub.

CHANGELOG
---------
v1.3.0 - Security & Performance Improvements
- NEW: Security Improvements (fixed various security issues)
- NEW: Rate Limiting System (command cooldowns to prevent spam)
- NEW: Input Sanitization (protection against malicious input)
- NEW: Memory Management (automatic cleanup to prevent leaks)
- NEW: Thread Safety (all operations are fully thread-safe)
- FIXED: Integer Overflow (prevents crashes from large config values)
- FIXED: Division by Zero (safety checks in animation system)
- FIXED: Null Pointer Exceptions (comprehensive null checks)
- FIXED: Resource Leaks (proper cleanup of animation tasks)
- IMPROVED: Performance (replaced Random with ThreadLocalRandom)
- IMPROVED: Error Handling (better logging and error recovery)
- IMPROVED: Code Quality (zero linter errors, perfect quality)

v1.2.5 - Animation System & Bug Fixes
- NEW: 12 Text Animations (typing, fade, slide, wave, rainbow, glitch, typewriter, bounce, shake, pulse, matrix, scramble)
- NEW: Action Bar Display (private animations, no chat spam!)
- NEW: Animation Commands (/welcome testall and /welcome testanim <type>)
- FIXED: Typing animations now work perfectly with color codes
- FIXED: Typewriter animation with blinking cursor
- FIXED: All linter errors and warnings
- IMPROVED: Performance and compatibility

v1.2.0 - Custom Rank System
- Added unlimited custom rank support
- Define your own ranks in config.yml
- Perfect for multiple VIP tiers or custom server ranks
- Works with any permission plugin
- Updated testall command to showcase custom ranks

v1.1.9 - Spigot Compatibility Fix
- Fixed all compatibility issues with Spigot servers
- Plugin now works perfectly on both Spigot and Paper
- No more runtime errors or crashes

v1.1.8 - PlaceholderAPI Support
- Added 13+ placeholders for other plugins to use
- Auto-detects PlaceholderAPI and enables support
- Works with any plugin that supports PlaceholderAPI

v1.1.7 - Bug Fixes & Config Validation
- Fixed syntax error that was breaking the build
- Added config validation so plugin won't crash
- Better error messages and auto-correction

FINAL NOTES
-----------
I made this because the other welcome plugins were either
abandoned, bloated with features I didn't need, or cost money
for basic stuff. This one's free, always will be.

If you like it, leave a star on GitHub. If you don't,
tell me what sucks so I can fix it.

- FiveDollaGobby
