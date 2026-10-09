# How a brush stroke is applied

A right-click with the brush item goes through these steps:

1. **Permission and world check.** The player needs `bettergopaint.use`. Worlds listed in
   `generic.disabled-worlds` are skipped unless the player has `bettergopaint.world.bypass`.
2. **Target.** Clicking a block uses that block. Clicking into the air ray-traces up to 250 blocks.
3. **Settings.** The default brush item uses the player's own brush. Any other item with brush
   lore uses the settings stored on the item (see
   [Share a brush as an item](../how-to/share-a-brush-as-an-item.md)).
4. **Edit.** The brush computes the positions and places blocks inside one FastAsyncWorldEdit
   edit session, queued through FAWE's player action queue. That is why every stroke shows up as
   one entry in `//undo` and why large brushes do not freeze the main thread.

Brush settings live in memory per player and are dropped when the player leaves the server.
