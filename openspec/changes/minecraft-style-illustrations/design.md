# Design

## Tiles as shared definitions

Every block face and every item icon is an 8x8 pixel tile. The SVG writes each tile once, as a `<g>` with
one `<rect>` per pixel in `<defs>`, and draws each cell as one `<use href>` with an integer position. A
picture with a few hundred cells then stays at a few hundred kilobytes, and no pattern tile has to align with
the grid. The tiles are sorted by id, so the output does not depend on the order in which cells are drawn.

## Procedural pattern

The shade of each pixel is the base colour of the material, lightened or darkened by a fixed step. A
`java.util.Random` seeded from the material name chooses the step of each pixel, and the probabilities of
light and dark pixels are fixed per material. The same name always gives the same tile, on every operating
system, and two materials give different tiles. The grass block side is a special case: two rows of grass
green, one jagged row, then dirt.

Base colours are representative averages of the Minecraft block faces, chosen by hand:

| Material | Base colour |
|---|---|
| `GRASS_BLOCK` (top) | `#7CBD6B` |
| `DIRT` (and the grass side below the strip) | `#866043` |
| `STONE` | `#7D7D7D` |
| `ANDESITE` | `#8A8B8C` |
| `COBBLESTONE` | `#6E6E6E` |
| `GOLD_BLOCK` | `#F9D849` |
| `SPONGE` | `#C3C04A` |

No two materials share a base colour; a unit test checks it.

## Block drawing

- Top view: a column shows its highest changed block at full opacity with a highlight border. A column the
  brush did not change shows its top block, faded.
- Side cross-section: a cell shows the side tile of its block (the grass side has the green strip on top);
  changed blocks are at full opacity, unchanged blocks faded, air in the sky tint.
- Clicked block: a two-pixel dark outline on the cell.
- Paint brush: the control points are numbered in click order, connected by a polyline. The brush size is
  the radius of the ball the stroke is swept through, so a size of 1 keeps the stroke thin.

## Scenes

Every scene stands on grass over three dirt blocks over stone. The scenes that need a palette mix three
stone-family blocks. The paint scene has six control points on the top layer, with the last one as the
target.

## Menu

The menu is drawn as the Minecraft 6x9 chest container at twice the inventory scale: 36 px per slot with a
2 px bevel, a 14 px light grey border, and the slot grid of `GUI.layout()` unchanged. Block items are block
tiles, glass panes are tinted translucent tiles, and other items are 8x8 pixel icons written in code.
A numbered badge marks each functional slot, and a legend beside the chest maps numbers to the English
label and the slot number. Filler panes have no badge.

Unknown items fail the generator and name the material, as an unknown block does today.
