# Design

## Context

Paper API 26.x has no MockBukkit release; MockBukkit's newest artifact targets 1.21.

## Goals / Non-Goals

**Goals:** fast tests without a server; a place to add tests in every later change.

**Non-Goals:** testing painting results, GUI clicks or listeners end to end.

## Decisions

### Mockito for Bukkit interfaces instead of MockBukkit
`World`, `Player` and friends are interfaces and can be mocked. Classes that call `Bukkit.*`
statically are out of reach until the dependency-injection change removes those calls.
*Alternative:* MockBukkit 1.21 against Paper 26 API — rejected, mismatched API at runtime.

### FAWE on the test classpath
Brush classes reference WorldEdit types in their signatures, so loading them needs FAWE core and
bukkit (non-transitive) as `testImplementation`.

### Translation test reads the source
The test scans `src/main/java` for `translatable("bettergopaint.…")` and checks the keys against
every bundle, so it keeps working when new languages arrive through Crowdin.

## Risks / Trade-offs

- [Low coverage at first] → each later change adds tests for the code it touches.
