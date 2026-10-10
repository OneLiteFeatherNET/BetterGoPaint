# Paint a path with the Paint brush

Paint a curved strip of blocks through the points you click, for example a road or a river bed.

![Paint brush shape, top view and side cross-section: the painted blocks follow a curve through the clicked points](../images/brushes/paint-brush.svg)

## What you need

- The brush item (the feather by default) and the `bettergopaint.use` permission.
- A palette with at least one block. See [Mix a block palette](mix-a-block-palette.md).

## Steps

1. Left-click the brush item to open the brush menu.
2. Shift-click the **Selected Brush type** item in the second row, then click **Paint Brush** in the list.
3. Close the menu.
4. Right-click the block where the path starts. Chat shows `Paint brush point #1 set`.
5. Right-click each further point. Every click adds one point and shows its number.
6. Hold Shift (sneak) and right-click the last point. The stroke paints the curve through all points.

To start a new path, right-click again without sneaking.

{% hint style="info" %}
Planned screenshot: `images/screenshots/paint-path-sneak.gif`, see the shotlist.
{% endhint %}

## Limits

- One stroke holds at most **256 points**. A click without sneaking after the 256th point is refused with
  the message `Paint brush stroke is full`. Sneak and right-click to paint the stroke with the points it has.
- Air along the path stays air. Only blocks are changed.
- **Brush Size** (slot 14 in the menu) sets the size of the brush that is moved along the path.
  **Falloff Strength** (slot 12) makes the edges of that brush fade.

## Result

The whole stroke is one history entry. `//undo` takes back all of it.

## Related

- [Brushes](../reference/brushes.md) lists every brush and its settings.
- [Brush menu](../reference/brush-menu.md) gives the slot of each menu item.
- [How a brush stroke is applied](../explanation/how-a-brush-stroke-is-applied.md) explains the stroke and the undo entry.
