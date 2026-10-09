# Tasks

## 1. Command tree

- [x] 1.1 Switch to cloud-paper's `PaperCommandManager` with Brigadier; verify on `run-26.2` that `/gopaint`, `/gp info` and `/gopaint reload` answer from the console
- [x] 1.2 Rewrite `GoPaintCommand` as annotated handlers for size, toggle, info and reload, delete the Bukkit `Command` subclass and `ReloadCommand`; verify with `BrushSizesTest` that bounds and suggestions follow the configured maximum
- [x] 1.3 Add permissions per subcommand and the `bettergopaint.admin` bundle in `build.gradle.kts`; update docs/reference/permissions.md

## 2. Messages

- [x] 2.1 Move all command messages to `bettergopaint.command.*` keys; verify `TranslationBundleTest` passes
- [x] 2.2 Update docs/reference/commands.md with arguments, aliases and permissions

## Workflow follow-up

- Archive the change after the pull request is merged.
