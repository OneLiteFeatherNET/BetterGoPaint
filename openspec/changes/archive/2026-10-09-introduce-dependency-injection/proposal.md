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
  reload. The settings stop extending FAWE's `Config`: on Paper 26 that loader could not set the
  plugin's instance fields (one "Failed to set config option" warning per key), so edits to
  `config.yml` were ignored. A small YAML loader reads the same keys, writes them back with
  comments and replaces invalid values with their defaults.
- Listeners and commands depend on narrow services (`BrushService`, `MenuService`,
  `TranslationService`) instead of `BetterGoPaint`.
- A DI validation test builds the injector with mocked Bukkit services.

No new features. The only visible effect is that `config.yml` is honoured again. Out of scope: Folia support.

## Capabilities

### New Capabilities

### Modified Capabilities

None — pure refactor; `skip_specs: true`.

## Impact

- Most classes under `net.onelitefeather.bettergopaint`; new `injection` package.
- New shaded dependencies: Guice 7, jakarta.inject 2 (relocated).
- docs/development/dependency-injection.md (new).
