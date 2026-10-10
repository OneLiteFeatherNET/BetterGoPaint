# Spec Delta

## Purpose

Defines how BetterGoPaint checks Hangar for a newer release and who is informed, without affecting
server startup or gameplay.

## ADDED Requirements

### Requirement: No HTTP client while checks are disabled
The plugin SHALL NOT create an HTTP client when `generic.check-for-updates` is `false`.

#### Scenario: Disabled
- **WHEN** `generic.check-for-updates` is `false` and the plugin enables
- **THEN** no HTTP client has been created

#### Scenario: Enabled
- **WHEN** `generic.check-for-updates` is `true` and the first check runs
- **THEN** one HTTP client is created and closed on shutdown
