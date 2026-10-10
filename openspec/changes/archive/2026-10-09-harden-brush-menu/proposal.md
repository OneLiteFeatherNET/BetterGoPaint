# Proposal

## Why

The brush menu is recognised by comparing the inventory title text with "goPaint Menu", so any
other plugin or a renamed chest with that title is treated as the brush menu and clicks in it are
cancelled. Brush selection parses display names with legacy `§6` colour codes, and exported brushes
store their settings as lore text that is parsed back with `substring` and `Integer.parseInt` — a
renamed or edited item throws an exception on right-click. Both paths use deprecated Paper API
(`getDisplayName`, `getLore`) and `ExportedPlayerBrush.parse` is already marked for removal.

## What Changes

- Identify the brush menu and the brush picker by their `InventoryHolder`, not by title text.
- Store exported brush settings in the item's `PersistentDataContainer` under a
  `bettergopaint:brush` key; lore becomes display only.
- Exported items from 1.x (lore-only) are read once through the legacy parser, converted to the
  new format and never throw: unreadable values fall back to defaults.
- Menu titles, item names and lore use Adventure components and translation keys instead of `§`
  codes.

Out of scope: a new menu layout, new brush settings.

## Capabilities

### New Capabilities
- `brush-menu`: The inventory menu that edits a player's brush and how clicks in it are handled.
- `exported-brushes`: Items that carry a brush configuration and paint with it.

### Modified Capabilities

## Impact

- `utils/GUI.java`, `utils/Items.java`, `listeners/InventoryListener.java`,
  `listeners/InteractListener.java`, `brush/ExportedPlayerBrush.java`, `brush/PlayerBrush.java`,
  `bettergopaint.properties`, docs/how-to/share-a-brush-as-an-item.md.
