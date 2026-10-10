![BetterGoPaint banner](https://i.imgur.com/ulEwPm9.jpg)

BetterGoPaint paints terrain for Paper servers. A builder holds a feather, picks a brush and a palette of up to ten blocks, and right-clicks the ground. Every stroke is a [FastAsyncWorldEdit](https://modrinth.com/plugin/fastasyncworldedit) edit and can be undone with `//undo`.

BetterGoPaint is a maintained fork of goPaint by Arcaniax.

## Features

- **Painting with a held item.** The feather is the default brush item. Left-click opens the brush menu, right-click paints.
- **Eleven brushes** for spheres, discs, cliffs, cracks, curved strokes and more. See the table below.
- **Block palettes** of up to ten blocks. Each block placed by a stroke is picked from the palette.
- **Masks.** A stroke can replace only a chosen mask block. The global mask of the builder's WorldEdit session (`//gmask`) is respected.
- **Surface modes** `DIRECT`, `RELATIVE` and `DISABLED` decide which blocks count as surface when a stroke is applied. `DISABLED` skips the check.
- **Undo and redo.** Each stroke is one FastAsyncWorldEdit edit, so `//undo` and `//redo` work stroke by stroke.
- **Per-player brushes.** Every builder has their own brush, size, palette and settings, changed in the brush menu.
- **Exported brushes.** A brush can be baked into any non-block item, for example a stick, and handed to another builder. The settings are stored in the item's data, so renaming the item does not change the brush.
- **World control.** Painting can be disabled in chosen worlds with `generic.disabled-worlds`. Builders with `bettergopaint.world.bypass` are not affected.
- **Permissions.** Painting needs `bettergopaint.use`, which operators have by default. Each command has its own permission node.
- **Translations.** English is built in. Further languages go into the `lang/` folder, and community translations are collected on Crowdin.
- **Update notification.** The plugin checks Hangar every three hours for a newer release. The check can be turned off with `generic.check-for-updates`.
- **Faster painting, lower memory (2.0.0).** The per-block work in the paint path is reduced. Brush menus are built when first opened, and rendered menu items are cached.

## Brushes

| Brush | What it paints |
|---|---|
| Sphere Brush | Every block in a sphere around the clicked block. |
| Spray Brush | Random blocks in the sphere. |
| Splatter Brush | Random blocks, more likely close to the clicked block. |
| Disc Brush | A flat disc on one axis through the clicked block. |
| Bucket Brush | Connected blocks of the same type as the clicked block. |
| Angle Brush | Only steep faces such as cliffs. |
| Overlay Brush | Blocks with air above them. |
| Underlay Brush | Blocks without air above them. |
| Fracture Brush | Cracks and fissures. |
| Gradient Brush | A gradient that blends the palette blocks into each other. |
| Paint Brush | Strokes along a curve through every point you click. Sneak to finish the stroke. |

Full settings and defaults are in the [brushes reference](https://github.com/OneLiteFeatherNET/BetterGoPaint/blob/main/docs/reference/brushes.md).

## Requirements

| Requirement | Version |
|---|---|
| Server | Paper 26.1.1, 26.1.2, 26.2 or 26.3 |
| Java | 25 |
| Plugin | FastAsyncWorldEdit 2.16 or newer, required |

A Minecraft version is added once the current FastAsyncWorldEdit release supports it. Servers on Minecraft 1.21 keep using BetterGoPaint 1.1.x. Folia is not supported yet. Spigot, CraftBukkit and other non-Paper servers cannot load the plugin.

Full list: [supported versions](https://github.com/OneLiteFeatherNET/BetterGoPaint/blob/main/docs/reference/supported-versions.md).

## Getting started

1. Install FastAsyncWorldEdit 2.16 or newer and the BetterGoPaint jar into `plugins/`, then restart the server.
2. Give a builder a feather with `/give <player> feather`. Operators have the `bettergopaint.use` permission by default.
3. Left-click the feather to open the brush menu. Choose a brush, set the size and fill the palette with blocks from your inventory.
4. Right-click the ground to paint.
5. Run `//undo` to take back the last stroke.

The step-by-step guides are [paint your first terrain](https://github.com/OneLiteFeatherNET/BetterGoPaint/blob/main/docs/tutorial/paint-your-first-terrain.md) for builders and [set up BetterGoPaint for your build team](https://github.com/OneLiteFeatherNET/BetterGoPaint/blob/main/docs/tutorial/set-up-bettergopaint-for-your-build-team.md) for server owners.

## Commands and permissions

`/gopaint` has the aliases `/gp` and `/bgp`.

| Command | What it does |
|---|---|
| `/gopaint size <size>` | Sets your brush size, up to `generic.max-size`. |
| `/gopaint toggle` | Enables or disables your brush. |
| `/gopaint info` | Shows the version and credits. |
| `/gopaint reload` | Re-reads `config.yml`. Admin permission. |
| `/gopaint help` | Lists the subcommands you may run. |

Full details: [commands](https://github.com/OneLiteFeatherNET/BetterGoPaint/blob/main/docs/reference/commands.md), [permissions](https://github.com/OneLiteFeatherNET/BetterGoPaint/blob/main/docs/reference/permissions.md), [configuration](https://github.com/OneLiteFeatherNET/BetterGoPaint/blob/main/docs/reference/configuration.md).

## Support

- Questions and help: [Discord](https://discord.onelitefeather.net)
- Bug reports and feature requests: [GitHub issues](https://github.com/OneLiteFeatherNET/BetterGoPaint/issues)
- Source, changelog and documentation: [GitHub](https://github.com/OneLiteFeatherNET/BetterGoPaint)

## Credits

BetterGoPaint is a fork of [goPaint](https://github.com/Arcaniax-Development/goPaint_1.14) by Arcaniax. It is maintained by OneLiteFeather and contributors and released under the GNU General Public License v3.0.
