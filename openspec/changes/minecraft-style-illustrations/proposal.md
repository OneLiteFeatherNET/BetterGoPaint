# Proposal

## Why

Maintainer review of the generated illustrations found that they do not look like Minecraft. Andesite is
teal, stone is nearly black, every block the brush did not change is one flat grey, so builders cannot see
the grass and dirt they are painting on. The paint stroke is a solid blob with no visible path, and the
brush menu is a grid of text cells that does not resemble the inventory a builder sees. The 2D layout (top
view, side cross-section, legend; the 54-slot grid) is liked and stays.

## What Changes

- Colour every block with the representative colour of its Minecraft material (grass block, dirt, stone,
  andesite, cobblestone, gold block, sponge). The generator maps each test fixture to its real material name
  and no longer colours blocks by object identity, so the legend names the block ("Grass Block", "Gold Block").
- Draw every block face as a deterministic 8x8 pixel tile that the generator computes from the material name
  with a fixed seed: shades of the material's base colour. No Minecraft or third-party texture file is
  downloaded, copied, embedded or read.
- Draw blocks the brush did not change with their material tile at reduced opacity, blocks the brush changed
  at full opacity with a thin highlight border, air in a sky tint (`#dbeafe`), and the clicked block outlined.
- Build the brush scenes on grass over dirt over stone. The sphere and gradient scenes mix stone, andesite and
  cobblestone; the paint scene uses a thin brush so the stroke reads as a path through its six clicked points,
  which are numbered 1 to 6 in click order.
- Draw the brush menu as a Minecraft 6x9 chest container: a light grey panel, bevelled inset slots, glass panes
  as tinted translucent tiles, block items as block tiles, and the other items as small pixel icons defined in
  code. Numbered callouts sit on functional slots only, never on filler panes, and a legend gives number to
  English label (from `bettergopaint.properties`) with the slot number, because the pages refer to slots.
- Regenerate the eleven brush and three menu SVGs. The existing tasks are unchanged: `./gradlew
  generateDocsImages` writes them, `./gradlew checkDocsImages` and `./gradlew docsGenTest` run inside `check`.
- Document the procedural tiles and the rule against third-party or Mojang texture files in
  `docs/development/documentation.md`.

Commit type and scope: `build(docs)` for the generator, the tests and the images; `docs(development)` for the
documentation page; `docs(openspec)` for this proposal. Pull request title: `build(docs): draw illustrations
with minecraft style blocks`.

Not breaking: no command, permission, config key, brush, menu layout or supported version changes, so no
server owner has to act. The plugin jar does not change; the generator stays in the `docsGen` source set.

## Capabilities

### Modified Capabilities
- `docs-illustrations`: material colours, procedural block tiles without third-party assets, legend names,
  unchanged blocks shown with their material, paint control points, and the chest-style menu with callouts.

## Impact

- `src/docsGen/java/net/onelitefeather/bettergopaint/docsgen/**`: block looks and tiles, item icons, scene
  terrain and paint stroke, brush and menu rendering, SVG model with definitions, opacity, paths and circles.
- `src/docsGenTest/java/net/onelitefeather/bettergopaint/docsgen/**`: unit tests for each of the above.
- `src/test/java/net/onelitefeather/bettergopaint/support/Terrain.java`: one more block fixture
  (`cobblestone`), used only by the generator.
- `docs/images/brushes/*.svg` (11 files) and `docs/images/menu/*.svg` (3 files), regenerated.
- `docs/development/documentation.md`: the Illustrations section.

## Out of scope

- Any Minecraft texture, resource pack or third-party texture, in any form. The tiles are computed.
- Changing brush behaviour or the menu layout. `GUI.layout()` and the brush code are read as they are.
- Rewriting the user pages. Their captions and alt texts stay; only a caption that would become wrong changes.
- PNG or other raster output, animation and interactive pictures.
- Archiving this change. It is archived after the pull request is reviewed.
