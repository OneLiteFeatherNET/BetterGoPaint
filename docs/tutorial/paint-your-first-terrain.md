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

Left-click with the feather in your hand. The **goPaint Menu** opens. The top row shows the
current brush, its size and its settings. The two rows at the bottom are your block palette.

## 3. Fill the palette

Take stone, andesite and gravel from your inventory and left-click each of them onto one of the
palette slots. Right-clicking a palette slot removes the block again.

## 4. Pick the brush

Left-click the brush icon until it shows **Spray Brush**. Right-click goes backwards; shift-click
shows every brush at once.

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
