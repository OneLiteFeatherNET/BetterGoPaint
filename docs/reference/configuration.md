# Configuration

`plugins/BetterGoPaint/config.yml` is written on first start and rewritten on every reload, so
new keys appear automatically. Materials use the names from the
[Paper Material list](https://jd.papermc.io/paper/org/bukkit/Material.html).

## generic

| Key | Default | Meaning |
|---|---|---|
| `languages` | `[]` | Extra locales to load from `lang/`, for example `de-DE`. English is always loaded. |
| `default-brush` | `FEATHER` | Item that acts as the brush. Must be an item. |
| `max-size` | `100` | Largest brush size a player can set. |
| `default-size` | `10` | Brush size of a new player. |
| `default-chance` | `50` | Starting chance of the Spray Brush, in percent. |
| `default-axis` | `Y` | Starting axis of the Disc Brush: `X`, `Y` or `Z`. |
| `default-falloff-strength` | `50` | Starting falloff. |
| `default-mixing-strength` | `50` | Starting mixing of the Gradient Brush. |
| `check-for-updates` | `true` | Ask Hangar every three hours for a newer release. Nothing is sent when `false`. |
| `prefix` | `<aqua>BetterGoPaint > </aqua>` | MiniMessage prefix of chat messages. |
| `disabled-worlds` | `[]` | Worlds in which painting is blocked. |
| `enabled-by-default` | `true` | Whether a new player's brush starts enabled. |
| `mask-enabled` | `true` | Whether the mask starts enabled. |
| `default-mask` | `SPONGE` | Starting mask block. |
| `surface-mode` | `DIRECT` | Starting surface mode: `DIRECT`, `RELATIVE` or `DISABLED`. |

## thickness

| Key | Default | Meaning |
|---|---|---|
| `default-thickness` | `1` | Starting thickness of the Overlay and Underlay Brush. |
| `max-thickness` | `5` | Largest thickness. |

## angle

| Key | Default | Meaning |
|---|---|---|
| `default-angle-distance` | `2` | Starting angle distance. |
| `max-angle-distance` | `5` | Largest angle distance. |
| `min-angle-height-difference` | `10.0` | Smallest angle height difference. |
| `default-angle-height-difference` | `40.0` | Starting angle height difference. |
| `max-angle-height-difference` | `85.0` | Largest angle height difference. |

## fracture

| Key | Default | Meaning |
|---|---|---|
| `default-fracture-distance` | `2` | Starting fracture distance. |
| `max-fracture-distance` | `5` | Largest fracture distance. |
