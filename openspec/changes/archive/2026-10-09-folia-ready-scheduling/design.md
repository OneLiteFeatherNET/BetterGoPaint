# Design

## Context

Paper exposes the Folia scheduler API (`Bukkit.getAsyncScheduler()`,
`Bukkit.getGlobalRegionScheduler()`, `Entity#getScheduler()`) on regular Paper too, so one code
path serves both. Event handlers on Folia already run on the region thread that owns the player,
so opening the brush menu from `PlayerInteractEvent` needs no rescheduling.

## Decisions

### No scheduler abstraction layer
AntiRedstoneClock needs a `SchedulerService` because it supports Spigot-era APIs. BetterGoPaint is
Paper-only, and Paper's unified API already behaves correctly on both servers. *Alternative:* copy
AntiRedstoneClock's `SchedulerService`. Rejected as an extra layer without a second implementation.

### `ConcurrentHashMap` for per-player state
Writes happen on the player's region thread, reads can come from commands or the FAWE worker. A
concurrent map is enough. Each `PlayerBrush` is only mutated by its owner's thread.

### Gradle property for the Folia flag
`foliaSupported = providers.gradleProperty("foliaSupported").map(String::toBoolean).orElse(false)`.
Release builds never set it, so a test flag cannot leak into a release by accident.

### Folia test server with a configurable FAWE URL
`run-folia-<version>` uses run-paper's Folia download service and installs FAWE from the URL in
the Gradle property `foliaFaweUrl`. Without that property the task fails with a message explaining
what to set.

## Risks / Trade-offs

- [Community FAWE forks behave differently from upstream] → used only for local testing, never
  shipped or documented as supported.
- [bStats on Folia] → bStats 3.x schedules through its own Folia-aware logic; verify on the test server.
