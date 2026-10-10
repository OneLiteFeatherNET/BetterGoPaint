# update-notification Specification

## Purpose
Defines how BetterGoPaint checks Hangar for a newer release and who is informed, without affecting
server startup or gameplay.

## Requirements

### Requirement: Startup is never blocked by the update check
The update check SHALL run off the main thread and SHALL NOT delay plugin enable.

#### Scenario: Hangar unreachable
- **WHEN** Hangar does not answer during server start
- **THEN** BetterGoPaint enables without waiting for the request

### Requirement: Update check can be disabled
The plugin SHALL NOT contact Hangar when `generic.check-for-updates` is `false`.

#### Scenario: Disabled
- **WHEN** `generic.check-for-updates` is `false` and the server starts
- **THEN** no request to Hangar is made

### Requirement: Admins are told about newer versions
When a newer release exists, the console SHALL be told once per check result and players with
`bettergopaint.notify.admin.update` SHALL be told once when they join.

#### Scenario: Admin joins
- **WHEN** a newer release is known and a player with the permission joins
- **THEN** the player receives one message with the installed and the latest version

### Requirement: Failures are quiet
A failed or unparsable update check SHALL NOT log a stack trace at WARN or ERROR level.

#### Scenario: Invalid response
- **WHEN** Hangar answers with a body that is not a version
- **THEN** the plugin logs one debug line and keeps running
