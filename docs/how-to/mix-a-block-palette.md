# Mix a block palette

Fill the palette with up to five blocks, so that every stroke places a random block from them.

![Brush menu with a palette of one block: stone in palette slot 1 at 100 percent, and palette slots 2 to 5 empty, shown as barriers](../images/menu/menu-sphere-default.svg)

## What you need

- The brush item and the `bettergopaint.use` permission.
- Solid blocks in your inventory. Items and non-solid blocks, such as torches, are ignored.

## Steps

1. Left-click the brush item to open the brush menu.
2. Pick up a block from your inventory and left-click the first empty palette slot (the bottom row, slots 46 to 50).
   The block appears there.
3. Repeat for each block, filling the slots from the left. The palette holds at most five blocks.
4. To replace a block, left-click its slot with another block.
5. To remove a block, right-click its slot. The blocks after it move one slot to the left.
6. Close the menu.

Each slot shows its percentage. Every block has the same chance: 100 divided by the number of blocks, rounded
down. Three blocks give 33% each, five give 20% each.

{% hint style="info" %}
Planned screenshot: `images/screenshots/palette-five-blocks.png`, see the shotlist.
{% endhint %}

## What the palette does

- Each stroke picks one block per position at random from the palette.
- The same block in two slots gets both shares. A palette of stone, stone and gravel gives stone 67%.
- A palette with no blocks paints nothing. Removing the last block stops the brush.

## Related

- [Paint a path with the Paint brush](paint-a-path-with-the-paint-brush.md) and
  [Shape cliffs with the Angle brush](shape-cliffs-with-the-angle-brush.md) use the palette the same way.
- [Blend a gradient](blend-a-gradient.md) uses the palette in slot order instead of at random.
- [Brush menu](../reference/brush-menu.md) gives the slot of each palette item.
