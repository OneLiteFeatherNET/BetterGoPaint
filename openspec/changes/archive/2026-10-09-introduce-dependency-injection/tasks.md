# Tasks

## 1. Container

- [x] 1.1 Add Guice and jakarta.inject to the catalog, shade and relocate them; verify the jar contains no `com/google/inject`, `jakarta/inject` or `org/aopalliance` outside `net/onelitefeather/bettergopaint/libs`
- [x] 1.2 Create `PlatformModule`, `SettingsModule`, `BrushModule`, `ListenerModule`, `CommandModule`, `ServiceModule`; verify `DIValidationTest` builds the injector with a mocked plugin

## 2. Migration

- [x] 2.1 Replace `Settings.settings()` (FAWE `Config`) with immutable records, `SettingsLoader` and the injected `SettingsHolder`; verify `SettingsHolderTest` (defaults, invalid values, reload swaps the snapshot)
- [x] 2.2 Inject services into listeners and commands instead of `BetterGoPaint`; verify unit tests for `InteractListener` permission and world checks
- [x] 2.3 Write docs/development/dependency-injection.md

## Workflow follow-up

- Archive the change after the pull request is merged.
