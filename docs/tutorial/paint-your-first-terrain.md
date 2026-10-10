# Paint your first terrain

In this tutorial you paint a patch of grass with a mix of three blocks and then undo it. It takes
about five minutes. You need operator rights on a Paper server that has BetterGoPaint and
FastAsyncWorldEdit installed.

## 1. Take the brush

Give yourself a feather:

```
/give @s feather
```

The feather is the brush. Holding anything else does nothing.

## 2. Open the brush menu

Left-click with the feather in your hand. The **goPaint Menu** opens. The second row shows the current brush,
its settings, its size and the mask. The bottom row is your block palette, in palette slots 1 to 5.

![Brush menu after the first left-click: the brush and its settings are in the second row, and the palette is in the bottom row](../images/menu/menu-sphere-default.svg)

## 3. Fill the palette

Take stone, andesite and gravel from your inventory and left-click each of them onto one of the
palette slots. Right-clicking a palette slot removes the block again.

## 4. Pick the brush

Left-click the **Selected Brush type** item until the **Spray Brush** entry in its lore is highlighted.
Right-click goes backwards; shift-click shows every brush at once.

The mask is on by default and only changes sponge blocks, so turn it off: click the **Mask** item once.
It turns into a carved pumpkin and its lore says **Disabled**.

## 5. Paint

Close the menu, look at a grassy area and right-click. A circle of mixed stone, andesite and gravel
appears where you looked. Right-click a few more times while walking to paint a path.

## 6. Undo

Run:

```
//undo
```

Your last stroke disappears. Every stroke is one FastAsyncWorldEdit edit, so `//undo` and `//redo`
work stroke by stroke.

## What you learned

- The feather opens the menu with a left-click and paints with a right-click.
- The palette decides which blocks are placed; the brush decides where.
- Strokes are normal FastAsyncWorldEdit edits.

Next, look at [Brushes](../reference/brushes.md) to see what the other brushes do.
