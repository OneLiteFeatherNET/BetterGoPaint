## ADDED Requirements

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
