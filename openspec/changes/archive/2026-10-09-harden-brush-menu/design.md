# Design

## Decisions

### `InventoryHolder` subclasses for menus
`BrushMenuHolder` and `BrushPickerHolder` own their inventories; the listener checks
`getTopInventory().getHolder(false) instanceof BrushMenuHolder`. Title text stops mattering, which
also unblocks translation. *Alternative:* track open inventories in a map — rejected, needs cleanup
on close and quit.

### Persistent data with a versioned string
`ExportedBrushCodec` encodes an `ExportedPlayerBrush` as `version=1;brush=…;size=…` and stores it as
a `STRING` under `bettergopaint:brush`. Keeping the codec independent of `PersistentDataContainer`
lets unit tests round-trip it without a server. *Alternative:* a nested `TAG_CONTAINER` per field —
rejected, it needs a live container implementation to test.

### Render menu text server-side
Menu items are rendered with `GlobalTranslator.render` for the locale of the player who owns the
menu, so translations do not depend on the client rendering translatable components in item
names. Brush names and descriptions stay English for now.

### Legacy parser kept, but total
The lore parser stays only for conversion, wraps every field in its own parse with a default, and
is deleted one major version later.

## Risks / Trade-offs

- [Exported items from 1.x lost if conversion fails] → conversion falls back to defaults rather than dropping the brush.
