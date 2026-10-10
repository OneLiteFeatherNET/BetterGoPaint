# Spec Delta

## Purpose

Defines how the brush menu and the brush picker are opened and identified, and that clicks are only
intercepted in those inventories.

## ADDED Requirements

### Requirement: Menu inventory is created on first open
The plugin SHALL NOT create a menu inventory for a player who has not opened the menu.

#### Scenario: Painting without the menu
- **WHEN** a player right-clicks with the brush item and paints, never opening the menu
- **THEN** no menu inventory has been created for that player

#### Scenario: First open
- **WHEN** the player opens the menu for the first time
- **THEN** exactly one menu inventory is created and rendered

### Requirement: Rendered menu text is cached
The plugin SHALL NOT render the same translation again for the same locale while neither the
translations nor the shown setting changed.

#### Scenario: Open twice
- **WHEN** a player opens the menu twice without changing any setting
- **THEN** the second open triggers no translation render

#### Scenario: Translations reloaded
- **WHEN** the translation bundles are reloaded and the player opens the menu
- **THEN** the text is rendered again

### Requirement: Updates need a viewer
A settings change SHALL NOT render the menu while the owner is not viewing it, and SHALL write only
the slots whose content changed while the owner is viewing it.

#### Scenario: Change while closed
- **WHEN** a setting changes and the menu is closed
- **THEN** no render happens until the next open, which shows the new value

#### Scenario: Change while open
- **WHEN** the player changes the brush size in the open menu
- **THEN** only the slots that show the size are written

### Requirement: Picker is built once per locale
The brush picker content SHALL be built once per locale, and every viewer SHALL get an inventory of
their own.

#### Scenario: Two players, same locale
- **WHEN** two players with the same locale open the picker
- **THEN** the content is built once and the two inventories are different instances

### Requirement: Equal heads are equal
Heads created from the same texture SHALL be equal.

#### Scenario: Same texture
- **WHEN** two heads are created from the same texture
- **THEN** the two item stacks are equal
