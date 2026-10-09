# Files and folders

All paths are relative to the server directory.

| Path | Written by | Content |
|---|---|---|
| `plugins/BetterGoPaint/config.yml` | BetterGoPaint, on every start and `/gopaint reload` | Settings; see [Configuration](configuration.md). |
| `plugins/BetterGoPaint/lang/` | the server admin | Extra translations named `bettergopaint_<locale>.properties`, for example `bettergopaint_de_DE.properties`. |

BetterGoPaint stores no player data on disk. Brush settings live in memory and are dropped when the
player leaves. Brushes exported onto items are stored in the item itself.
