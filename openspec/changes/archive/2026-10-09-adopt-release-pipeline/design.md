# Design

## Context

AntiRedstoneClock-Remastered's workflows are proven in production (they recovered from a failed
2.9.0 publish). BetterGoPaint copies them with project names, Java 25 and jar paths adjusted.

## Goals / Non-Goals

**Goals:** identical release mechanics across OneLiteFeather plugins; nothing public before the gate.

**Non-Goals:** snapshot channels on Hangar; Maven publishing of the plugin.

## Decisions

### Release Please on `main`
The repository has `develop` as default branch plus stale `main`/`master`. Release Please needs one
release branch; `main` matches the other OneLiteFeather repositories. `develop` becomes obsolete.
*Alternative:* run on `develop` — rejected, it diverges from the organisation convention.

### Version lives in `build.gradle.kts`
`version = "1.1.0" // x-release-please-version`, updated through the `generic` extra-file updater.
The release job refuses to publish when the tag and the declared version disagree.
`bootstrap-sha` is the current `develop` head so the first changelog does not replay 2023 history.

### Exclude the unshaded jar
The build produces `BetterGoPaint-<v>.jar` (shaded) and `-unshaded.jar`. Uploads exclude the
unshaded one so users cannot download a jar that misses its libraries.

### Shared workflows pinned to `v2.9.0`
Security scan and SBOM publishing come from `OneLiteFeatherNET/workflows`, pinned by tag; Renovate
updates the pin.

## Risks / Trade-offs

- [Dependency-Track parent UUID copied from AntiRedstoneClock] → confirm the parent project with the
  Dependency-Track admin before the first release.
- [Default branch still `develop`] → manual step in tasks.md.

## Migration Plan

1. Merge this change into `develop`, fast-forward `main`, switch the default branch to `main`.
2. Make sure `HANGAR_KEY` and `MODRINTH_KEY` exist as repository secrets.
3. The next `feat`/`fix` merge opens the first release pull request.
