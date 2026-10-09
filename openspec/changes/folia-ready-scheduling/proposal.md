# Proposal

## Why

Folia runs each region of the world on its own thread and rejects code that assumes a single main
thread. BetterGoPaint schedules its update check with the legacy `BukkitScheduler`, which throws
on Folia. It also keeps per-player state in plain `HashMap`s that are written from several threads,
including a static map in the Paint Brush. Once brush strokes no longer read the Bukkit world
(`native-worldedit-brushes`), these are the remaining blockers.

Folia cannot be declared supported yet. Stable FastAsyncWorldEdit, which BetterGoPaint requires,
does not support Folia; only experimental upstream work and community forks do. BetterGoPaint
should be ready, so that switching the flag is the only step left when FAWE ships it.

## What Changes

- Use Paper's region-aware scheduler API, which exists on both Paper and Folia: the async scheduler
  for the update check, the entity scheduler for anything that touches a player, the global region
  scheduler for server-wide work.
- Per-player state (`PlayerBrushManager`, Paint Brush points) becomes thread-safe, and the Paint
  Brush points move from a static map into the player's brush state.
- `foliaSupported` in `paper-plugin.yml` is driven by a Gradle property that defaults to `false`.
- `run-folia-<version>` tasks start a Folia test server with a Folia-capable FAWE build from a
  configurable URL, for testing only.
- docs/explanation/scope-and-non-goals.md and the supported-versions page state the Folia status.

Out of scope: declaring Folia support, publishing a Folia loader on Hangar or Modrinth, changing
FastAsyncWorldEdit.

## Capabilities

### New Capabilities
- `server-threading`: Which threads BetterGoPaint runs on, how it schedules work, and under which
  condition it declares Folia support.

### Modified Capabilities

## Impact

- `service/UpdateService.java`, `brush/PlayerBrushManager.java`, `brush/PlayerBrush.java`,
  `objects/brush/PaintBrush.java`, `build.gradle.kts`, `gradle.properties`, docs.
- Depends on `native-worldedit-brushes` being applied first.
