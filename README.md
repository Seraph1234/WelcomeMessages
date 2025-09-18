# WelcomeMessages
![Screenshot](images/screenshot.png)


A clean, lightweight welcome plugin for Minecraft servers that actually works. No bloat, no premium features locked behind paywalls, just good old-fashioned join/quit messages done right.

## Why Another Welcome Plugin?

Look, I needed a welcome plugin that wasn't from 2018, didn't break every update, and didn't try to do 500 things at once. Most welcome plugins are either abandoned, premium for basic features, or so bloated they cause lag. This one just works.

![Screenshot](images/IMG1.png)

## What It Does

- **Smart Messages** - Different messages for new players vs returning ones
- **Rank Support** - VIPs get VIP treatment (if you want)
- **Fancy Effects** - Particles, sounds, titles, fireworks - all optional and configurable
- **RGB & Gradients** - Support for modern color codes and rainbow text
- **Config Validation** - Won't crash your server with bad settings
- **PlaceholderAPI Support** - Use data in other plugins and chat
- **Full Spigot Compatibility** - Works on both Spigot and Paper
- **Not Annoying** - Everything's configurable, nothing's forced

## Screenshots

Well, it's a chat plugin... here's what messages look like:
```
[+] Welcome FiveDollaGobby to the server! (#42nd player)
[+] [VIP] SomeVIP joined the game
[-] Player123 left the game
```

But honestly, you'll customize these anyway.

## Installation

1. Download the jar
2. Put it in /plugins
3. Restart server
4. Edit configs if you want
5. You're done

Requires Paper/Spigot 1.21.x and Java 21 (because Minecraft requires it, not me). Works on both Spigot and Paper.

## Features That Actually Matter

### For Players
- Personal toggle command (finally!)
- Join counter that actually saves
- Effects that don't lag the server
- Messages that make sense
- RGB gradients and rainbow text support
- PlaceholderAPI integration for other plugins
- Full compatibility with both Spigot and Paper

### For Admins
- Works with your permission plugin (all of them)
- Config that's actually readable and validated
- Reload command that actually reloads
- No random database files everywhere
- Won't crash from bad configs anymore
- Works perfectly on both Spigot and Paper servers

### Performance
- Async everything (your TPS will thank me)
- Smart caching (not the dumb kind)
- Optional metrics (off by default because privacy)
- Works on potato servers

## Commands

All commands use `/welcome` or `/wm`:
- `/welcome` - Help (short and useful)
- `/welcome reload` - Actually reloads
- `/welcome test` - See your join message
- `/welcome toggle` - Turn your messages on/off
- `/welcome stats` - Join count stuff

## Permissions

**Basic permissions (work out of the box):**
- `welcome.use` - Use basic welcome commands (default: true)
- `welcome.see.join` - See join messages (default: true)
- `welcome.see.quit` - See quit messages (default: true)
- `welcome.toggle` - Toggle personal join/quit messages (default: true)

**Admin permissions:**
- `welcome.*` - All WelcomeMessages permissions (default: op)
- `welcome.admin` - Access to all admin commands (default: op)
- `welcome.reload` - Reload plugin configuration (default: op)
- `welcome.test` - Test join messages (default: op)
- `welcome.stats` - View player statistics (default: op)
- `welcome.reset` - Reset player data (default: op)

**Exemption permissions:**
- `welcome.exempt.join` - Exempt from having join messages shown (default: false)
- `welcome.exempt.quit` - Exempt from having quit messages shown (default: false)

**Effect permissions:**
- `welcome.effects.bypass` - Bypass effect cooldowns (default: op)

**Rank permissions:**
- `welcome.rank.*` - All rank permissions (default: false)
- `welcome.rank.vip` - VIP rank messages (default: false)
- `welcome.rank.mvp` - MVP rank messages (default: false)
- `welcome.rank.admin` - Admin rank messages (default: false)
- `welcome.rank.owner` - Owner rank messages (default: false)

## Config

Two files, both human-readable:

**config.yml** - Features on/off, performance stuff
**messages.yml** - All your messages (with examples)

Placeholders that work:
- `{player}` - The player's name (obviously)
- `{displayname}` - Player's display name
- `{world}` - Current world
- `{online}` - Online count
- `{max}` - Max players
- `{joincount}` - How many times they've joined
- `{time}` - Morning/afternoon/evening
- `{ordinal}` - First join position (1st, 2nd, 3rd, etc.)
- More in the config comments

## PlaceholderAPI Support

If you have PlaceholderAPI installed, you can use these placeholders in other plugins:

**Player Data:**
- `%welcome_joincount%` - How many times the player has joined
- `%welcome_firstjoin%` - true/false if it's their first join
- `%welcome_messagesdisabled%` - true/false if they disabled messages
- `%welcome_lastseen%` - When they were last seen (formatted)
- `%welcome_firstjointime%` - When they first joined (formatted)
- `%welcome_time_since_last_seen%` - How long since last seen
- `%welcome_time_since_first_join%` - How long since first join

**Server Data:**
- `%welcome_total_unique_joins%` - Total unique players who joined
- `%welcome_join_ordinal%` - Server's join count as ordinal
- `%welcome_time_greeting%` - Morning/afternoon/evening
- `%welcome_server_uptime%` - How long server has been running

**Status:**
- `%welcome_status%` - Player status (New Player, Regular Player, etc.)
- `%welcome_rank%` - Player's rank (VIP, MVP, ADMIN, OWNER, DEFAULT)

## Building From Source

```bash
git clone https://github.com/FiveDollaGobby/WelcomeMessages.git
cd WelcomeMessages
./gradlew jar
```

The jar's in build/libs/

## Changelog

### v1.1.9 - Spigot Compatibility Fix
- Fixed all compatibility issues with Spigot servers
- Reverted to deprecated but stable API methods for maximum compatibility
- Plugin now works perfectly on both Spigot and Paper
- No more runtime errors or crashes

### v1.1.8 - PlaceholderAPI Support
- Added comprehensive PlaceholderAPI integration
- 13+ placeholders for player data, server stats, and status
- Works with any plugin that supports PlaceholderAPI
- Auto-detects PlaceholderAPI and enables support

### v1.1.7 - Bug Fixes & Config Validation
- Fixed syntax error in MessageUtils.java
- Added comprehensive configuration validation
- Plugin won't start with broken settings
- Better error messages and auto-correction

## Known Issues

- Fireworks might scare your pets (in-game ones)
- Too many particles can lag potato clients (not the server)
- Color codes are still using & because I'm oldschool
- Some older permission plugins might not recognize the new permissions (just give them manually)

## Planned Features

Stuff I might add if people actually use this:
- [ ] PlaceholderAPI support (if enough people ask)
- [ ] MySQL support (but why?)
- [ ] Discord webhooks (maybe)
- [ ] Custom sounds (when I figure out resource packs)
- [X] RGB gradient text (done!)
- [X] Config validation (done!)
- [X] PlaceholderAPI support (done!)
- [X] Full Spigot compatibility (done!)
- [ ] World-specific messages (maybe)

## Contributing

Found a bug? Open an issue.
Fixed a bug? Open a PR.
Want a feature? Open an issue, but no promises.

Code style: Just make it readable. I'm not picky.

## FAQ

**Q: Performance impact?**
A: Basically zero. I benchmarked it.

**Q: Works with X plugin?**
A: Probably. It doesn't do anything weird.

**Q: 1.20.x support?**
A: Use an older Paper version and it might work. No promises.

## Support

Create a GitHub issue. I check them... sometimes.

Discord support? Nah, GitHub issues work fine.

## License

MIT - Do whatever you want with it. Sell it, modify it, claim you made it, I don't care. Just don't blame me if it breaks.

## Credits

- Paper team for the API
- Spigot for existing I guess
- Coffee for keeping me awake
- That one Stack Overflow answer that saved my sanity

---

*mild frustration by FiveDollaGobby*

*If this saved you time, star the repo. If it didn't, tell me why.*