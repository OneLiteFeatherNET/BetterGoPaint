# Share a brush as an item

You can bake the current brush settings into an item and hand it to another builder.

1. Hold any item that is not a block and not the brush item, for example a stick.
2. Open the brush menu and left-click the item onto the brush slot (the brush item in the second
   row, slot 10 in the [brush menu reference](../reference/brush-menu.md)).

   ![Brush menu with the brush item in the second row, slot 10, which is the target of the left-click](../images/menu/menu-sphere-default.svg)
3. The item now carries the brush, its size, the palette and every brush setting.

Right-clicking with that item paints with the stored settings, independent of the holder's own
brush. The holder still needs `bettergopaint.use`.

The settings are stored in the item's data, not in its name or lore. Renaming the item in an anvil
or editing its lore does not change the brush.

## Items exported with BetterGoPaint 1.x

Older items kept their settings in the lore. They still work: the first right-click reads the lore,
uses the configured default for any line it cannot read, and converts the item to the new format.
