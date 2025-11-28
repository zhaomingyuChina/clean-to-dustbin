# Clean to Dustbin Mod

A server-side mod for Minecraft Forge 1.20.1 that automatically collects and stores all dropped items in an accessible dustbin.

## Features

### 🗑️ Smart Dustbin System
- Automatically collects and saves all dropped items worldwide
- 6-row large chest interface with multi-page support
- Red stained glass pane navigation buttons for intuitive operation
- Real-time display of current page number
- Previous page button hidden on first page, next page button hidden on last page

### ⏰ Scheduled Cleanup Mechanism
- Configurable cleanup interval
- 12 reminder broadcasts before cleanup:
  - 1 minute reminder
  - 30 seconds reminder
  - Countdown from 10 seconds to 1 second
- Broadcasts the number of collected items after cleanup

### 📝 Highly Configurable
- Customizable reminder messages
- Adjustable cleanup interval
- Configurable dustbin pages
- Flexible configuration options

### 🎮 Convenient Command System
- `/ljt` - Open dustbin interface
- `/ljt <page>` - Jump directly to specified page
- `/ljt reload` - Reload configuration (requires admin permission)

## Installation

1. Ensure your server has **Forge 1.20.1** installed
2. Download the `clean-to-dustbin-x.x.x.jar` file
3. Place the jar file into your server's `mods` folder
4. Start the server, the mod will generate configuration files automatically
5. Modify the configuration files as needed

## Usage

### Opening the Dustbin
- Enter `/ljt` command in-game
- The dustbin interface will open as a 6-row large chest

### Page Navigation
- **Previous Page**: Click the red stained glass pane at the bottom-left (row 6, column 3)
- **Current Page**: The sign in the middle displays current page and total pages
- **Next Page**: Click the red stained glass pane at the bottom-right (row 6, column 5)

### Direct Jump
- Enter `/ljt <page>` to jump directly to the specified page
- Example: `/ljt 3` jumps to page 3

## Configuration Options

Configuration file is located at `serverconfig/cleantodustbin-server.toml`, you can customize the mod behavior through the following options:

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `CLEANUP_INTERVAL` | Integer | 3600 | Cleanup interval in seconds |
| `DUSTBIN_PAGES` | Integer | 5 | Number of dustbin pages |
| `REMINDER_MESSAGES` | String List | Built-in messages | Reminder messages before cleanup |

### Custom Reminder Messages Example
```toml
REMINDER_MESSAGES = [
    "&aItem cleanup will start in 1 minute!",
    "&aItem cleanup will start in 30 seconds!",
    "&aItem cleanup will start in 10 seconds!",
    "&aItem cleanup will start in 9 seconds!",
    "&aItem cleanup will start in 8 seconds!",
    "&aItem cleanup will start in 7 seconds!",
    "&aItem cleanup will start in 6 seconds!",
    "&aItem cleanup will start in 5 seconds!",
    "&aItem cleanup will start in 4 seconds!",
    "&aItem cleanup will start in 3 seconds!",
    "&aItem cleanup will start in 2 seconds!",
    "&aItem cleanup will start in 1 second!"
]
```

## Command Description

| Command | Permission | Description |
|---------|------------|-------------|
| `/ljt` | All players | Open dustbin interface |
| `/ljt <page>` | All players | Jump directly to specified page |
| `/ljt reload` | Admin | Reload configuration file |

## Interface Description

### Dustbin Interface Layout
```
┌─────────────────────────────────────────────────┐
│                                                 │
│                                                 │
│                                                 │
│                                                 │
│                                                 │
│                                                 │
│  [Red Glass]  [Sign]  [Red Glass]               │
│  [Prev Page]  [Page]  [Next Page]               │
└─────────────────────────────────────────────────┘
```

- **Red Glass Buttons**: Click to switch pages
- **Sign**: Displays current page and total pages
- **Item Slots**: Stores collected dropped items

## Development Information

### Tech Stack
- **Minecraft Forge**: 1.20.1
- **Java**: 17
- **Gradle**: 8.8

### Project Structure
```
clean-to-dustbin/
├── src/main/java/com/example/cleantodustbin/
│   ├── CleanToDustbin.java          # Main mod class
│   ├── config/ModConfig.java        # Configuration class
│   ├── command/DustbinCommand.java  # Command handling
│   ├── cleanup/CleanupScheduler.java # Cleanup scheduler
│   └── dustbin/                     # Dustbin related
│       ├── DustbinManager.java      # Dustbin management
│       ├── DustbinInventory.java    # Dustbin inventory
│       └── DustbinContainer.java    # Dustbin container
└── build.gradle                     # Gradle configuration
```

## Notes

1. This mod only runs on the server, no client installation required
2. Ensure the server has enough memory to store dustbin data
3. Clean the dustbin regularly to avoid excessive memory usage
4. Use `/ljt reload` command to reload configuration after modification

## License

MIT License

## Support

If you have any issues or suggestions, please submit an Issue or Pull Request.

---

**Enjoy a clean Minecraft world!** 🎉