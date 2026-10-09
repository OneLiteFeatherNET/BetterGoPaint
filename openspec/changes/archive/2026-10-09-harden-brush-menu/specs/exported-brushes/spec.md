# Spec Delta

## Purpose

Defines how a brush configuration is stored on an item, how such an item paints, and how it behaves
when the stored data is missing or invalid.

## ADDED Requirements

### Requirement: Brush stored as item data
Exporting a brush onto an item SHALL store the brush type, size, palette and every brush setting as
structured item data, independent of the item's name and lore.

#### Scenario: Renamed item keeps working
- **WHEN** a player exports a brush onto a stick and renames the stick in an anvil
- **THEN** right-clicking with the stick paints with the exported settings

### Requirement: Invalid data never breaks painting
An item with missing or unreadable brush data SHALL be treated as a normal item, and invalid single
values SHALL fall back to the configured defaults.

#### Scenario: Edited lore
- **WHEN** a legacy exported item's lore line `Size: 10` was edited to `Size: ten`
- **THEN** right-clicking paints with the default size and no exception is logged

### Requirement: Legacy items are converted
Items exported by BetterGoPaint 1.x SHALL keep working and SHALL be converted to the new format the
first time they are used.

#### Scenario: Item from 1.1.0
- **WHEN** a player right-clicks with a brush item exported by 1.1.0
- **THEN** it paints with the stored settings and afterwards carries the new item data
