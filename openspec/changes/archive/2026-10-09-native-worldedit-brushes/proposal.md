# Proposal

## Why

Every brush computes its target blocks by reading the Bukkit world (`Location#getBlock`,
`Block#getType`, `World#getBlockAt`) and only writes through a FastAsyncWorldEdit edit session.
That code runs inside FAWE's asynchronous player action queue, so the world is read off the server
thread. On Paper this is unsafe and can read stale or half-loaded chunks. On Folia it is forbidden
outright. The plugin also ignores what the builder already set up in WorldEdit: the read path
bypasses `//gmask`, and block types are converted per block through `BukkitAdapter`. Reading and
writing through WorldEdit's own extent fixes the thread-safety problem and makes the plugin behave
like a WorldEdit brush. It is also the prerequisite for Folia (see `folia-ready-scheduling`).

## What Changes

- Brushes compute on WorldEdit types only: `EditSession`/`Extent` for reads and writes,
  `BlockVector3` for positions, `BlockType`/`BlockState` for blocks.
- Each BetterGoPaint brush implements WorldEdit's brush contract (`build(EditSession, BlockVector3,
  Pattern, double)`). The palette becomes a WorldEdit `RandomPattern`, and the BetterGoPaint mask
  becomes a WorldEdit `Mask`.
- The geometry helpers (`Sphere`, `Height`, `Surface`, `ConnectedBlocks`) read from an `Extent`
  instead of a Bukkit `World`.
- Everything that needs the player (eye location for the relative surface mode, the target block)
  is captured on the player's thread before the stroke is queued. The queued work never touches
  Bukkit objects.
- The stroke respects the builder's WorldEdit session: global mask (`//gmask`), FAWE limits, and one
  history entry per stroke for `//undo`.

Out of scope: binding BetterGoPaint brushes to arbitrary items through WorldEdit's `/brush` tool
system (possible follow-up), new brushes, changes to brush results beyond fixing reads of stale
data.

## Capabilities

### New Capabilities
- `brush-painting`: How a brush stroke reads and changes the world, and how it interacts with the
  builder's WorldEdit session.

### Modified Capabilities

## Impact

- `objects/brush/*`, `utils/Sphere.java`, `utils/Height.java`, `utils/Surface.java`,
  `utils/ConnectedBlocks.java`, `utils/curve/*` (positions), `listeners/InteractListener.java`,
  `brush/BrushSettings.java` (Materials converted to WorldEdit block types at the edge).
- docs/explanation/how-a-brush-stroke-is-applied.md.
