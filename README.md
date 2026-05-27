# Damage Indicator

A small client-side mod for **NeoForge** that shows floating damage numbers above entities when they get hit.

![Minecraft](https://img.shields.io/badge/Minecraft-26.1.2-brightgreen)
![NeoForge](https://img.shields.io/badge/NeoForge-26.1.2.66--beta-orange)

## Features

- Floating numbers pop up above any entity that takes damage
- Animated: scale-pop on appear, drift upward, fade out
- Distinct color/scale for critical hits
- Configurable via an in-game screen (Cloth Config) — colors, lifetime, render distance, format string, etc.

## Requirements

| Component | Version |
|---|---|
| Minecraft | `26.1.2` |
| NeoForge | `26.1.2.66-beta`+ |
| Cloth Config (NeoForge) | `26.1.0`+ |
| Java | 25 |

## Install

1. Install NeoForge for Minecraft 26.1.2.
2. Download `damage-indicator-*.jar` from [Releases](https://github.com/Eneryleen/damage-indicator/releases).
3. Also grab [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config) (NeoForge build, 26.1.0+).
4. Drop both jars into your `mods/` folder.

## Configuration

Open **Mods → Damage Indicator → Config** in the main menu, or edit `config/damage_indicator.json` directly. Available settings:

- **Display**: enable/disable, max render distance, base text scale
- **Text**: damage format string (`printf`-style), show/hide decimals
- **Color**: normal damage color, critical damage color
- **Animation**: lifetime, pop duration, fade timing, vertical drift speed, horizontal spread
- **Critical**: scale multiplier and pop intensity for crits

If the config file is missing or broken, the mod falls back to defaults and keeps running.

## Build from source

```sh
git clone https://github.com/Eneryleen/damage-indicator.git
cd damage-indicator
./gradlew build
```

Output: `build/libs/damage-indicator-<version>.jar`.

Toolchain: OpenJDK 25, Gradle 9.1.0, `net.neoforged.moddev` plugin 2.0.141.

## License

All Rights Reserved — see [LICENSE.txt](LICENSE.txt).
