## ADDED Requirements

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
