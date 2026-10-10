<p align="center">
  <img src="gopaint-logo.png" alt="BetterGoPaint logo" width="96">
</p>

<h1 align="center">BetterGoPaint</h1>

<p align="center">
  <a href="https://hangar.papermc.io/OneLiteFeather/BetterGoPaint"><img alt="Hangar downloads" src="https://img.shields.io/badge/dynamic/json?label=Hangar&query=%24.stats.downloads&url=https%3A%2F%2Fhangar.papermc.io%2Fapi%2Fv1%2Fprojects%2FOneLiteFeather%2FBetterGoPaint&logo=papermc"></a>
  <a href="https://modrinth.com/plugin/bettergopaint"><img alt="Modrinth downloads" src="https://img.shields.io/modrinth/dt/bettergopaint?label=Modrinth&logo=modrinth"></a>
  <a href="https://github.com/OneLiteFeatherNET/BetterGoPaint/releases/latest"><img alt="Latest release" src="https://img.shields.io/github/v/release/OneLiteFeatherNET/BetterGoPaint"></a>
  <a href="https://github.com/OneLiteFeatherNET/BetterGoPaint/actions/workflows/release-please.yml"><img alt="Release Please" src="https://github.com/OneLiteFeatherNET/BetterGoPaint/actions/workflows/release-please.yml/badge.svg"></a>
  <a href="https://github.com/OneLiteFeatherNET/BetterGoPaint/actions/workflows/build-pr.yml"><img alt="Build PR" src="https://github.com/OneLiteFeatherNET/BetterGoPaint/actions/workflows/build-pr.yml/badge.svg"></a>
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/github/license/OneLiteFeatherNET/BetterGoPaint"></a>
</p>

BetterGoPaint paints terrain for Paper servers. Hold a feather, pick a brush and a palette of up to
ten blocks, then right-click the ground. Every stroke is a
[FastAsyncWorldEdit](https://modrinth.com/plugin/fastasyncworldedit) edit and can be undone with
`//undo`.

BetterGoPaint is a maintained fork of [goPaint](https://github.com/Arcaniax-Development/goPaint_1.14)
by Arcaniax. The store listing text used on [Hangar](https://hangar.papermc.io/OneLiteFeather/BetterGoPaint)
and [Modrinth](https://modrinth.com/plugin/bettergopaint) is in [docs/store-page.md](docs/store-page.md).

## Requirements

| Requirement | Version |
|---|---|
| Server | Paper 26.1.1, 26.1.2, 26.2 or 26.3 ([details](docs/reference/supported-versions.md)) |
| Java | 25 |
| Plugin | FastAsyncWorldEdit 2.16 or newer, required |

Servers on Minecraft 1.21 keep using BetterGoPaint 1.1.x.

## Quick start

1. Install FastAsyncWorldEdit 2.16 or newer, then put the BetterGoPaint jar into `plugins/` and restart.
2. Give a builder a feather with `/give <player> feather`. Operators have the `bettergopaint.use`
   permission by default.
3. Left-click the feather to open the brush menu, choose a brush and a palette, then right-click to
   paint. Run `//undo` to take back a stroke.

The full walkthroughs are [set up BetterGoPaint for your build team](docs/tutorial/set-up-bettergopaint-for-your-build-team.md)
and [paint your first terrain](docs/tutorial/paint-your-first-terrain.md).

## Documentation

The documentation lives in [docs/](docs/index.md), organised by purpose:

- **Tutorials**: [docs/tutorial/](docs/tutorial/), for a first install and a first stroke.
- **How-to guides**: [docs/how-to/](docs/how-to/), for specific tasks such as
  [limiting what builders can do](docs/how-to/limit-what-builders-can-do.md) or
  [upgrading from 1.x](docs/how-to/upgrade-from-bettergopaint-1.md).
- **Reference**: [brushes](docs/reference/brushes.md), [commands](docs/reference/commands.md),
  [permissions](docs/reference/permissions.md), [configuration](docs/reference/configuration.md),
  [supported versions](docs/reference/supported-versions.md).
- **Explanation**: [docs/explanation/](docs/explanation/), for how settings and brush strokes work
  and what is out of scope.

## Links

- [Hangar](https://hangar.papermc.io/OneLiteFeather/BetterGoPaint) and [Modrinth](https://modrinth.com/plugin/bettergopaint) for downloads
- [Discord](https://discord.onelitefeather.net) for questions
- [Issues](https://github.com/OneLiteFeatherNET/BetterGoPaint/issues) for bug reports and feature requests
- [Changelog](CHANGELOG.md)

## Contributing and building

Java 25 is required. `./gradlew build` compiles, runs the tests and builds
`build/libs/BetterGoPaint-<version>.jar`.

- [CONTRIBUTING.md](CONTRIBUTING.md) covers commit conventions and the release process.
- [docs/development/](docs/development/) covers testing, dependency injection and documentation.

## Credits

BetterGoPaint is a fork of [goPaint](https://github.com/Arcaniax-Development/goPaint_1.14) by
Arcaniax and is released under the [GNU General Public License v3.0](LICENSE).
