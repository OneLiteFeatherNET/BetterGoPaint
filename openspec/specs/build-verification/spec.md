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

### Requirement: Committed illustrations match the generator
`./gradlew check` SHALL fail when a generated folder under `docs/images/` (`brushes/`, `menu/`) does not
contain exactly the generated files: a file is missing, differs from the generated file, or is not produced
by the generator. Other folders under `docs/images/`, such as `screenshots/`, are not checked for staleness.

#### Scenario: Hand-edited illustration
- **WHEN** a committed `docs/images/brushes/sphere-brush.svg` is edited by hand
- **THEN** `./gradlew build` fails in `checkDocsImages` and names the file

#### Scenario: Extra file in a generated folder
- **WHEN** `docs/images/brushes/extra.svg` is added without being generated
- **THEN** `./gradlew check` fails in `checkDocsImages` and names the extra file

#### Scenario: Hand-added screenshot
- **WHEN** a captured `docs/images/screenshots/tour.gif` is added and a page links to it
- **THEN** `checkDocsImages` passes, and `checkDocsLinks` passes when the link resolves

### Requirement: Documentation links resolve
`./gradlew check` SHALL fail when a relative link or image target in `docs/` does not resolve to a file.

#### Scenario: Broken image link
- **WHEN** a page under `docs/how-to/` links `![Cliff](../images/brushes/angle.svg)` and the file does not exist
- **THEN** `./gradlew check` fails in `checkDocsLinks` and names the page and the target

#### Scenario: External link
- **WHEN** a page links to an `https://` address
- **THEN** the checker does not fetch it and does not fail on it

### Requirement: Builder pages show the product
`./gradlew check` SHALL fail when a page listed under the "For builders" heading of `docs/SUMMARY.md`
has neither an image link nor a link to `development/screenshot-shotlist.md`.

#### Scenario: Builder guide without illustration
- **WHEN** a builder how-to page has no image and no shotlist reference
- **THEN** `./gradlew check` fails in `checkDocsLinks` and names the page

### Requirement: Planned screenshots are listed in the shotlist
`./gradlew check` SHALL fail when a file name inside a GitBook hint block under `docs/images/` is not
listed in `docs/development/screenshot-shotlist.md`.

#### Scenario: Placeholder without shotlist entry
- **WHEN** a hint names `docs/images/guides/cliff.gif` and the shotlist does not list it
- **THEN** `./gradlew check` fails and names the file
