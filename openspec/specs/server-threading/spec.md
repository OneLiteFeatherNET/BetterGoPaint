# server-threading Specification

## Purpose
Defines how BetterGoPaint schedules work and shares state across threads, so that it runs on Paper
today and on Folia as soon as its required dependencies do.

## Requirements

### Requirement: Region-aware scheduling only
The plugin SHALL schedule work only through Paper's async, global region and entity schedulers, and
SHALL NOT use the legacy `BukkitScheduler`.

#### Scenario: Update check on Folia
- **WHEN** the plugin starts on a Folia server with update checks enabled
- **THEN** the update check is scheduled without an `UnsupportedOperationException`

### Requirement: Thread-safe per-player state
Per-player brush state SHALL be safe to read and write from different threads at the same time.

#### Scenario: Two players paint in different regions
- **WHEN** two builders in different Folia regions paint and change brush settings at the same time
- **THEN** each keeps their own settings and no state is lost or corrupted

### Requirement: Folia declared only with a Folia-capable FAWE
The published plugin SHALL NOT declare Folia support while no stable FastAsyncWorldEdit release
supports Folia. Declaring it SHALL require changing only one build property.

#### Scenario: Default build
- **WHEN** the plugin is built without extra properties
- **THEN** the generated `paper-plugin.yml` does not declare `folia-supported: true`

#### Scenario: Opt-in test build
- **WHEN** the plugin is built with `-PfoliaSupported=true`
- **THEN** the generated `paper-plugin.yml` declares `folia-supported: true`
