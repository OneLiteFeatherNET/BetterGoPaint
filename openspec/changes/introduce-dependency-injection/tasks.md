# Tasks

## 1. Container

- [ ] 1.1 Add Guice and jakarta.inject to the catalog, shade and relocate them; verify the jar contains only relocated packages
- [ ] 1.2 Create `PlatformModule`, `SettingsModule`, `BrushModule`, `ListenerModule`, `CommandModule`, `ServiceModule`; verify `DIValidationTest` builds the injector with mocks

## 2. Migration

- [ ] 2.1 Replace `Settings.settings()` with an injected provider and atomic reload; verify a unit test that reload swaps values
- [ ] 2.2 Inject services into listeners and commands instead of `BetterGoPaint`; verify unit tests for `InteractListener` permission and world checks
- [ ] 2.3 Write docs/development/dependency-injection.md

## Workflow follow-up

- Archive the change after the pull request is merged.
