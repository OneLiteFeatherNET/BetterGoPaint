# Proposal

## Why

Settings are a static singleton, listeners and commands receive the whole plugin instance, and
`PlayerBrush` builds its own GUI through static helpers. Classes cannot be constructed in tests
without a running server, which keeps most of the code out of reach of the test foundation.
AntiRedstoneClock-Remastered solved the same problem with Guice modules and service interfaces.

## What Changes

- Guice (with `jakarta.inject`) wires the plugin in modules: settings, brushes, listeners,
  commands, update service, metrics.
- `Settings.settings()` is replaced by an injected settings provider that is swapped atomically on
  reload.
- Listeners and commands depend on narrow services (`BrushService`, `MenuService`,
  `TranslationService`) instead of `BetterGoPaint`.
- A DI validation test builds the injector with mocked Bukkit services.

No user-visible behaviour changes. Out of scope: Folia support, new features.

## Capabilities

### New Capabilities

### Modified Capabilities

None — pure refactor; `skip_specs: true`.

## Impact

- Most classes under `net.onelitefeather.bettergopaint`; new `injection` package.
- New shaded dependencies: Guice 7, jakarta.inject 2 (relocated).
- docs/development/dependency-injection.md (new).
