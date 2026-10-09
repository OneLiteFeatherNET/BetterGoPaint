# Proposal

## Why

BetterGoPaint has no tests. Every change, including the Paper 26 upgrade, can only be checked by
starting a server and painting by hand. Translation bundles and the brush registry break silently.
AntiRedstoneClock-Remastered runs JUnit tests with coverage on every pull request; BetterGoPaint
needs the same baseline before larger refactors (commands, GUI, dependency injection) start.

## What Changes

- JUnit 6, AssertJ and Mockito on the test classpath; JaCoCo XML and HTML coverage reports after
  every test run.
- First tests for logic that does not need a server: brush cycling and lookup
  (`PlayerBrushManager`), the Bézier curve of the Paint Brush, and the translation bundles.
- `./gradlew build` runs the tests and the license-header check (Spotless).
- docs/development/testing.md describes how to run and add tests.

Out of scope: MockBukkit (no release for Paper 26 yet), integration tests that start a server,
coverage thresholds.

## Capabilities

### New Capabilities
- `build-verification`: The automated checks every build runs before code can be merged or released.

### Modified Capabilities

## Impact

- `build.gradle.kts` (test dependencies, JaCoCo, Spotless target), `src/test/java/**`,
  `docs/development/testing.md`.
- CI time grows by the test run (seconds).
