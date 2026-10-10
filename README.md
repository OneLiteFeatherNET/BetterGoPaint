<p>
    <img src="https://i.imgur.com/ulEwPm9.jpg">
</p>

---

BetterGoPaint paints terrain in Minecraft: hold a feather, pick a brush and a block palette,
right-click. Every stroke is a [FastAsyncWorldEdit](https://modrinth.com/plugin/fastasyncworldedit)
edit and can be undone with `//undo`.

It is a maintained [fork](https://github.com/Arcaniax-Development/goPaint_1.14) of goPaint by Arcaniax.

## Requirements

| | |
|---|---|
| Server | Paper 26.1.1, 26.1.2, 26.2, 26.3 ([details](docs/reference/supported-versions.md)) |
| Java | 25 |
| Required plugin | FastAsyncWorldEdit 2.16 or newer |

Servers on Minecraft 1.21 keep using BetterGoPaint 1.1.x.

## Documentation

- [Paint your first terrain](docs/tutorial/paint-your-first-terrain.md)
- [Brushes](docs/reference/brushes.md), [commands](docs/reference/commands.md),
  [permissions](docs/reference/permissions.md), [configuration](docs/reference/configuration.md)

## Links

* [Download on Hangar](https://hangar.papermc.io/OneLiteFeather/BetterGoPaint) ·
  [Modrinth](https://modrinth.com/plugin/bettergopaint)
* [Discord](https://discord.onelitefeather.net)
* [Issues](https://github.com/OneLiteFeatherNET/BetterGoPaint/issues)

## Building

Java 25 is required. `./gradlew build` compiles, tests and builds `build/libs/BetterGoPaint-<version>.jar`.
See [CONTRIBUTING.md](CONTRIBUTING.md) for commit conventions and the release process.
