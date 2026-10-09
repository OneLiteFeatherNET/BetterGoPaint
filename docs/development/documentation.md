# Writing documentation

The docs follow [Diátaxis](https://diataxis.fr/): `tutorial/` teaches, `how-to/` solves one task,
`reference/` lists facts, `explanation/` gives background. `SUMMARY.md` is the navigation; a page
that is not listed there is not published.

Update these pages in the same pull request when you change:

| Change | Page |
|---|---|
| A config key | `reference/configuration.md` |
| A command | `reference/commands.md` |
| A permission | `reference/permissions.md` (and `build.gradle.kts`) |
| `supportedMinecraftVersions` | `reference/supported-versions.md` |
| A brush | `reference/brushes.md` |

`ReferenceDocsTest` enforces this for the reference pages: it fails the build when a config key
or its default, a permission from `build.gradle.kts`, a `/gopaint` subcommand or a supported
Minecraft version is missing from its page, or when a user page is missing from `SUMMARY.md`.
The comment next to each key in `SettingsLoader` is what server owners see in `config.yml`; keep
it and the "Meaning" column of the configuration page saying the same thing.

## Audiences

Pages are written for two readers. Server admins install, configure and permission the plugin;
builders paint with it. `index.md` sends each to their entry points, and `SUMMARY.md` groups the
how-to guides by audience. Tutorials: one per audience, each ending with a hand-off to the next
pages.
