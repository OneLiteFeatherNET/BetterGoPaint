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
