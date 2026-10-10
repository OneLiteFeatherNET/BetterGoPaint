# Proposal

## Why

The only user documentation is a README with a to-do list from 2023 and links to a personal
GitHub account. Commands, permissions, config keys and the brushes themselves are documented
nowhere, so every question ends up on Discord. AntiRedstoneClock-Remastered keeps a Diátaxis
structured `docs/` folder that GitBook publishes; BetterGoPaint should match it.

## What Changes

- `docs/` with tutorial, how-to, reference and explanation sections, navigation in `SUMMARY.md`,
  `.gitbook.yaml` for GitBook Git Sync.
- Reference pages for brushes, commands, permissions, every `config.yml` key and supported
  versions.
- A first-use tutorial and how-tos for the brush item, disabled worlds, sharing a brush as an item
  and adding a language.
- Developer pages: testing and how the docs are organised (which page to update for which change).
- README rewritten around requirements, documentation links and building.

Out of scope: setting up the GitBook space and domain; translating the docs.

## Capabilities

### New Capabilities
- `user-documentation`: What the published documentation covers and how it is kept in sync with the plugin.

### Modified Capabilities

## Impact

- `docs/**`, `.gitbook.yaml`, `README.md`.
- Contributors update reference pages when they change commands, permissions or config keys.
