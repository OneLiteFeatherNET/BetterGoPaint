# Design

## Decisions

### `InventoryHolder` subclasses for menus
`BrushMenuHolder` and `BrushPickerHolder` own their inventories; the listener checks
`getTopInventory().getHolder(false) instanceof BrushMenuHolder`. Title text stops mattering, which
also unblocks translation. *Alternative:* track open inventories in a map — rejected, needs cleanup
on close and quit.

### Persistent data with a versioned record
A `PersistentDataType` encodes an `ExportedPlayerBrush` record (with a format version) under
`bettergopaint:brush`. Unit tests round-trip the record without a server by mocking the container.

### Legacy parser kept, but total
The lore parser stays only for conversion, wraps every field in its own parse with a default, and
is deleted one major version later.

## Risks / Trade-offs

- [Exported items from 1.x lost if conversion fails] → conversion falls back to defaults rather than dropping the brush.
