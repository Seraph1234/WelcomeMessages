=====================================================
    WelcomeMessages - v1.1.7
    For Minecraft 1.21.x
=====================================================

Hey! Thanks for downloading my plugin :)

QUICK START
-----------
1. Drop the WelcomeMessages-1.1.7.jar into your plugins folder
2. Restart your server (or reload if you're feeling risky)
3. Edit the config files to your liking
4. Done! (Plugin validates your config so it won't crash)

WHAT THIS DOES
--------------
Makes your server feel more alive with custom join/quit messages.
First time players get a special welcome, regulars get their own
messages, and your VIPs can have fancy entrances with fireworks
and everything. Now with RGB gradients, rainbow text, and config
validation so it won't break your server!

COMMANDS
--------
/welcome - Shows help (or /wm for short)
/welcome reload - Reloads the config (with validation!)
/welcome test - Preview your join message
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

COMMON ISSUES
-------------
"Effects not showing!"
- Make sure you didn't disable them in config.yml
- Check if the player has permission to see them

"Messages look weird!"
- You probably have a color code wrong, check your &'s
- RGB colors need Paper/Spigot 1.16+ to work properly

"Plugin not loading!"
- Need Java 21 for MC 1.21.x
- Check console for errors
- Plugin now validates configs, so check for validation errors

"Config validation failed!"
- Fix the errors shown in console
- Plugin will tell you exactly what's wrong

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

FINAL NOTES
-----------
I made this because the other welcome plugins were either
abandoned, bloated with features I didn't need, or cost money
for basic stuff. This one's free, always will be.

If you like it, leave a star on GitHub. If you don't,
tell me what sucks so I can fix it.

- FiveDollaGobby
