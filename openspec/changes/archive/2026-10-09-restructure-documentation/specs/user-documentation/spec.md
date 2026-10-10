# Spec Delta

## Purpose

Ensures server owners and builders can find every command, permission, config key, brush and
supported version in the published documentation, and that this stays true as the plugin changes.

## ADDED Requirements

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
