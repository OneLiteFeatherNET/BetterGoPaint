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
