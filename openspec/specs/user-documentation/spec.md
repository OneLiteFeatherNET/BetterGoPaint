# user-documentation Specification

## Purpose
Ensures server owners and builders can find every command, permission, config key, brush and
supported version in the published documentation, and that this stays true as the plugin changes.

## Requirements

### Requirement: Reference pages are complete
The documentation SHALL contain reference pages listing every command, every permission, every
`config.yml` key with its default, every brush and every supported Minecraft version.

#### Scenario: Looking up a permission
- **WHEN** a server owner opens the permissions reference
- **THEN** every permission declared in `paper-plugin.yml` is listed with its default and what it grants

### Requirement: Reference pages change with the plugin
A pull request that adds, removes or renames a command, permission, config key, brush or supported
version SHALL update the matching reference page in the same pull request.

#### Scenario: New config key
- **WHEN** a pull request adds `generic.max-palette-size`
- **THEN** the same pull request adds the key to docs/reference/configuration.md

### Requirement: Published navigation
Every page under `docs/` intended for users SHALL be listed in `docs/SUMMARY.md`.

#### Scenario: Page missing from navigation
- **WHEN** a how-to page exists but is not listed in `SUMMARY.md`
- **THEN** reviewers treat the pull request as incomplete

### Requirement: Generated images live in one folder
Every image used by the documentation SHALL be stored in `docs/images/` and referenced by a relative
link. The generated folders (`docs/images/brushes/`, `docs/images/menu/`) SHALL contain only files produced by
the generator. Hand-captured images, such as in-game screenshots and GIFs, SHALL go in `docs/images/screenshots/`,
which the stale check does not cover; `docs/development/documentation.md` documents this folder.

#### Scenario: Image outside the folder
- **WHEN** a page embeds an image stored in `docs/.gitbook/assets/`
- **THEN** reviewers treat the pull request as incomplete, because the folder is not covered by the link check

#### Scenario: Hand-captured screenshot
- **WHEN** a maintainer adds `docs/images/screenshots/cliff.png` and a page links to it
- **THEN** the pull request is complete, because `checkDocsImages` ignores the folder and `checkDocsLinks` resolves the link

### Requirement: Regeneration is documented
`docs/development/documentation.md` SHALL state the command that regenerates the illustrations, the
command that checks them, and which pages embed each generated file.

#### Scenario: New brush
- **WHEN** a brush is added
- **THEN** the same pull request runs `./gradlew generateDocsImages`, commits the new SVG and updates `docs/reference/brushes.md`

### Requirement: Explanation flows are diagrams that match the code
The explanation pages on stroke application and settings loading SHALL show their flow as a Mermaid
flowchart. Each node SHALL name the class or setting it stands for, and each edge SHALL be a step the
code takes.

#### Scenario: Diagram step the code does not take
- **WHEN** a reviewer finds a node in the stroke diagram that no code path performs
- **THEN** the pull request is not merged until the node or the prose is corrected

#### Scenario: Diagram renders
- **WHEN** the Mermaid blocks in the two explanation pages are validated
- **THEN** each one parses without error

### Requirement: Planned screenshots are placeholders, never broken images
A guide that refers to a screenshot or GIF that does not exist yet SHALL name the planned file in a
GitBook hint block, not in an image link, and the file name SHALL be listed in
`docs/development/screenshot-shotlist.md`.

#### Scenario: Placeholder before capture
- **WHEN** a guide refers to `images/guides/paint-path-sneak.gif` before the GIF is committed
- **THEN** the page shows a hint, no image is requested, and `./gradlew check` passes

#### Scenario: Placeholder without shotlist entry
- **WHEN** a hint names a file that the shotlist does not list
- **THEN** `./gradlew check` fails in `checkDocsLinks`

### Requirement: Shotlist records what to capture and how
`docs/development/screenshot-shotlist.md` SHALL list each planned file with its target page, what the
frame must show and the client settings: resolution, GUI scale, resource pack, time of day.

#### Scenario: Team captures a screenshot
- **WHEN** a team member opens the shotlist for `images/guides/paint-path-sneak.gif`
- **THEN** the entry gives the target page, the frame content and the settings needed to reproduce it

### Requirement: New user pages are in the navigation
Every new user page SHALL be listed in `docs/SUMMARY.md`. The shotlist is a development page and is not
listed there.

#### Scenario: Guide missing from navigation
- **WHEN** a builder guide exists under `docs/how-to/` but is not in the "For builders" section
- **THEN** `./gradlew test` fails in `ReferenceDocsTest` and reviewers treat the pull request as incomplete
