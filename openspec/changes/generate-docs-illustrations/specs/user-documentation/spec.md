## ADDED Requirements

### Requirement: Generated images live in one folder
Every image used by the documentation SHALL be stored in `docs/images/` and referenced by a relative
link. `docs/images/` SHALL contain only files produced by the generator, except image files a
maintainer adds on purpose and lists in `docs/development/documentation.md`.

#### Scenario: Image outside the folder
- **WHEN** a page embeds an image stored in `docs/.gitbook/assets/`
- **THEN** reviewers treat the pull request as incomplete, because the folder is not covered by the stale check

### Requirement: Regeneration is documented
`docs/development/documentation.md` SHALL state the command that regenerates the illustrations, the
command that checks them, and which pages embed each generated file.

#### Scenario: New brush
- **WHEN** a brush is added
- **THEN** the same pull request runs `./gradlew generateDocsImages`, commits the new SVG and updates `docs/reference/brushes.md`
