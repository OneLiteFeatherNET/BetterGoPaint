# Permissions

| Permission | Default | Grants |
|---|---|---|
| `bettergopaint.use` | op | Painting with the brush and every `/gopaint` command. |
| `bettergopaint.admin` | false | `/gopaint reload` and the admin variant of the help text. |
| `bettergopaint.command.admin.reload` | op | Reload through the cloud command `/bgp reload`. |
| `bettergopaint.world.bypass` | false | Painting in worlds listed in `generic.disabled-worlds`. |
| `bettergopaint.notify.admin.update` | op | A message on join when a newer version is on Hangar. |
| `bettergopaint.notify.disable.donation` | false | Hides the donation message on join. |

The list is generated into `paper-plugin.yml` from `build.gradle.kts`; keep both in sync.
