# brush-menu Specification

## Purpose
Defines how the brush menu and the brush picker are opened and identified, and that clicks are only
intercepted in those inventories.

## Requirements

### Requirement: Menu identity does not depend on the title
The plugin SHALL handle clicks only in inventories it created for the brush menu or brush picker,
regardless of the inventory title.

#### Scenario: Foreign inventory with the same title
- **WHEN** another plugin opens a chest titled "goPaint Menu" and the player clicks in it
- **THEN** BetterGoPaint does not cancel or handle the click

#### Scenario: Translated title
- **WHEN** the menu title is translated into German
- **THEN** clicks in the menu still change the brush settings

### Requirement: Player inventory stays usable
While the brush menu is open, clicks in the player's own inventory SHALL only be cancelled for
shift-clicks and collect-to-cursor, which would move items into the menu.

#### Scenario: Normal click in own inventory
- **WHEN** the brush menu is open and the player left-clicks an item in their own inventory
- **THEN** the item is picked up normally

### Requirement: Translated menu text
Menu titles, item names and lore SHALL be resolved from the translation bundles.

#### Scenario: German client
- **WHEN** a German client opens the brush menu and a German bundle is installed
- **THEN** titles and item texts are German

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
