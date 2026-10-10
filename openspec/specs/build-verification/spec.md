# build-verification Specification

## Purpose
Defines the automated checks that every BetterGoPaint build runs, so regressions in brush logic,
translations and source hygiene are caught before a merge or release.

## Requirements

### Requirement: Tests run with every build
The build SHALL run all unit tests, and a failing test SHALL fail the build.

#### Scenario: Broken brush cycling
- **WHEN** cycling forward from the last brush no longer returns the first brush
- **THEN** `./gradlew build` fails and names the failing test

### Requirement: Coverage report
Every test run SHALL produce a JaCoCo coverage report in XML and HTML.

#### Scenario: Report after tests
- **WHEN** `./gradlew test` finishes
- **THEN** `build/reports/jacoco/test/jacocoTestReport.xml` exists

### Requirement: Translation keys are complete
Every translation key used in the source SHALL exist in every shipped language bundle, and every
message SHALL parse as MiniMessage.

#### Scenario: Key missing from a bundle
- **WHEN** code references `bettergopaint.notify.new` and a bundle lacks that key
- **THEN** the build fails and names the bundle and key

### Requirement: License headers
Every Java source file SHALL carry the GPL license header from `HEADER.txt`.

#### Scenario: New file without header
- **WHEN** a Java file without the header is added
- **THEN** `./gradlew build` fails in `spotlessJavaCheck` and `./gradlew spotlessApply` adds the header

### Requirement: Hot-path behaviour is verified without timing
The build SHALL verify the brush hot-path rules (read counts, curve computed once, sphere
equivalence, random filter order) with unit tests that count calls and compare selections. These
tests SHALL NOT sleep, read the system clock or assert elapsed time, and SHALL use fixed seeds and
a fresh extent per test.

#### Scenario: Extra world read introduced
- **WHEN** a change makes a stroke read one position twice
- **THEN** `./gradlew test` fails and names the position-count test

#### Scenario: Timing is never asserted
- **WHEN** the hot-path tests are searched for `Thread.sleep`, `System.nanoTime` and `Instant.now`
- **THEN** none is found
