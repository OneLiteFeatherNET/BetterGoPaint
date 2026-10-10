## ADDED Requirements

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
