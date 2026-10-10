# Spec Delta

## Purpose

Defines the `/gopaint` command tree: which subcommands exist, which arguments and permissions they
take, and what feedback players receive.

## ADDED Requirements

### Requirement: Single command tree
The plugin SHALL register exactly one command `/gopaint` with the aliases `/gp` and `/bgp`.

#### Scenario: Alias resolves
- **WHEN** a player runs `/bgp toggle`
- **THEN** the same handler as `/gopaint toggle` runs

### Requirement: Brush size argument
`/gopaint size <size>` SHALL accept integers from 1 to `generic.max-size` and SHALL reject other
input with a translated error that names the allowed range.

#### Scenario: Too large
- **WHEN** `generic.max-size` is 100 and a player runs `/gopaint size 500`
- **THEN** the size is unchanged and the player is told the allowed range is 1–100

#### Scenario: Tab completion
- **WHEN** a player types `/gopaint size ` and presses tab
- **THEN** suggestions within the allowed range are offered

### Requirement: Permission per subcommand
Each subcommand SHALL require its own permission in addition to `bettergopaint.use`, and the
generated help SHALL only list subcommands the sender may run.

#### Scenario: Player without reload permission
- **WHEN** a player without `bettergopaint.command.admin.reload` runs `/gopaint help`
- **THEN** `reload` is not listed

### Requirement: Translated feedback
Every message sent by a command SHALL be resolved from the translation bundles in the player's locale.

#### Scenario: German client
- **WHEN** a player with a German client and a German bundle installed runs `/gopaint toggle`
- **THEN** the confirmation is shown in German
