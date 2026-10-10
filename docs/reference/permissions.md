# Permissions

| Permission | Default | Grants |
|---|---|---|
| `bettergopaint.use` | op | Painting with the brush; also required for every `/gopaint` subcommand. |
| `bettergopaint.command.help` | true | `/gopaint` and `/gopaint help`. |
| `bettergopaint.command.size` | true | `/gopaint size`. |
| `bettergopaint.command.toggle` | true | `/gopaint toggle`. |
| `bettergopaint.command.info` | true | `/gopaint info`. |
| `bettergopaint.command.admin.reload` | op | `/gopaint reload`. |
| `bettergopaint.admin` | false | Bundle: grants `bettergopaint.command.admin.reload`. Also shows the donation notice on join. |
| `bettergopaint.world.bypass` | false | Painting in worlds listed in `generic.disabled-worlds`. |
| `bettergopaint.notify.admin.update` | op | A message on join when a newer version is on Hangar. |
| `bettergopaint.notify.disable.donation` | false | Hides the donation message on join. |

Every `/gopaint` subcommand checks `bettergopaint.use` and its own `bettergopaint.command.*` node.
The subcommand nodes default to `true`, so `bettergopaint.use` alone grants every player subcommand.

The list is generated into `paper-plugin.yml` from `build.gradle.kts`; keep both in sync.
