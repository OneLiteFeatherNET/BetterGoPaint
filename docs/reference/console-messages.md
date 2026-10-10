# Console messages

Messages BetterGoPaint writes to the server console, with their level and what the plugin does next.

| Level | Message | Plugin then |
|---|---|---|
| ERROR | `BetterGoPaint is a replacement for goPaint. Please use one instead of both` | disables itself |
| ERROR | `<MATERIAL> is not a valid default brush, it has to be an item` | disables itself |
| WARN | `config.yml: <key> has the invalid value '<value>', using <default>` | uses the default for that key |
| WARN | `Your version(<installed>) is older than our latest published version (<latest>). …` | keeps running |
| INFO | `To keep the project alive, we ask for donations …` | keeps running |

Failed update checks (Hangar unreachable, unexpected answer) are logged at DEBUG level only.
