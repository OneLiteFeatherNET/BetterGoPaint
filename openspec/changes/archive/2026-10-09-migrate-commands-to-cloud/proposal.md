# Proposal

## Why

`/gopaint` is a hand-written Bukkit `Command` with nested `if` chains, hard-coded English text, a
wrong usage hint (`/gb size`), no tab completion and no number validation. At the same time a
second, cloud-based `/bgp|gp reload` is registered, so `/gp` is claimed by two command systems with
different permissions. AntiRedstoneClock-Remastered runs all commands through cloud annotations
with Brigadier and translatable messages.

## What Changes

- One command tree `/gopaint` (aliases `/gp`, `/bgp`) built with cloud annotations and Paper's
  Brigadier integration; the Bukkit `Command` subclass is removed.
- `size <1..max-size>` with tab completion and range validation, `toggle`, `info`, `reload`, and a
  generated help.
- Each subcommand has its own permission; `bettergopaint.use` stays as the base permission.
- Every message comes from the translation bundle (`bettergopaint.command.*` keys).
- **BREAKING** `/gopaint reload` requires `bettergopaint.command.admin.reload` instead of
  `bettergopaint.admin`; `bettergopaint.admin` becomes a bundle permission that grants it.

Out of scope: new subcommands, console support for player-only commands.

## Capabilities

### New Capabilities
- `commands`: The `/gopaint` command tree, its arguments, permissions and feedback.

### Modified Capabilities

## Impact

- `command/GoPaintCommand.java`, `command/ReloadCommand.java`, command registration in
  `BetterGoPaint.java`, `bettergopaint.properties`, `build.gradle.kts` permissions,
  docs/reference/commands.md and permissions.md.
