# Damage Indicator

A small mod for **Fabric** that shows floating damage numbers above entities when they get hit.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-brightgreen)
![Fabric](https://img.shields.io/badge/Fabric%20Loader-0.19.3+-blue)

> Looking for the NeoForge version (MC 26.1.2)? It lives at the git tag
> [`v1.3.0-neoforge-mc26.1.2`](https://github.com/Eneryleen/damage-indicator/tree/v1.3.0-neoforge-mc26.1.2).

## Features

- Floating numbers pop up above any entity that takes damage
- Animated: scale-pop on appear, drift upward, fade out
- Distinct color/scale for critical hits
- Configurable via an in-game screen (Cloth Config + ModMenu) — colors, lifetime, render distance, format string, etc.

## Requirements

| Component | Version |
|---|---|
| Minecraft | `1.21.11` |
| Fabric Loader | `0.19.3`+ |
| Fabric API | `0.141.4+1.21.11`+ |
| Cloth Config (optional, for the config screen) | `21.11.153`+ |
| ModMenu (optional, for the config button) | `17.0.0`+ |
| Java | 21 |

The mod must be installed **on the server and on the client**: the server detects damage
and sends it to nearby players; the client draws the numbers.

## Install

1. Install Fabric Loader for Minecraft 1.21.11.
2. Download `damage-indicator-*.jar` from [Releases](https://github.com/Eneryleen/damage-indicator/releases).
3. Also grab [Fabric API](https://modrinth.com/mod/fabric-api); for the in-game config screen add [Cloth Config](https://modrinth.com/mod/cloth-config) (Fabric build) and [ModMenu](https://modrinth.com/mod/modmenu). Without them the mod still works — edit the JSON config by hand.
4. Drop the jars into your `mods/` folder.

## Configuration

Open **Mods → Damage Indicator → Config** (ModMenu) in the main menu, or edit `config/damage_indicator.json` directly. Available settings:

- **Display**: enable/disable, max render distance, base text scale
- **Text**: damage format string (`printf`-style), show/hide decimals
- **Color**: normal damage color, critical damage color
- **Animation**: lifetime, pop duration, fade timing, vertical drift speed, horizontal spread
- **Critical**: scale multiplier and pop intensity for crits

If the config file is missing or broken, the mod falls back to defaults and keeps running.

## Known limitations (Fabric event model)

- The displayed number includes shield/freezing mitigation but **not armor/enchantment
  reduction** — Fabric's `AFTER_DAMAGE` event does not expose the final HP loss.
- No number is shown for the **killing blow** — the event does not fire when the hit kills.

## Build from source

```sh
git clone https://github.com/Eneryleen/damage-indicator.git
cd damage-indicator
./gradlew build
```

Output: `build/libs/damage-indicator-<version>.jar`.

Toolchain: JDK 21+, Gradle 9.5.1, `net.fabricmc.fabric-loom-remap` 1.17, official Mojang mappings.

## License

All Rights Reserved — see [LICENSE.txt](LICENSE.txt).
