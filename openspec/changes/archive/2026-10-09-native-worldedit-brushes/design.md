# Design

## Context

`Brush#performEdit` opens an `EditSession` from the player's `LocalSession` and writes through it,
but the target set is built from Bukkit `Block`s (`Sphere.getBlocksInRadius`, `Height`, `Surface`,
`ConnectedBlocks`, `Block#getType` in the mask check). `InteractListener` wraps the whole stroke in
`BukkitPlayer#runAction(…, async = true)`, so these Bukkit reads run on a FAWE worker thread.

## Goals / Non-Goals

**Goals:** no Bukkit world access during a stroke; strokes behave like WorldEdit brushes (mask,
limits, history); brush logic unit-testable against an in-memory extent.

**Non-Goals:** WorldEdit tool binding (`/brush`), performance tuning beyond removing per-block
Bukkit round trips.

## Decisions

### Implement WorldEdit's `Brush` interface
Each brush becomes a `com.sk89q.worldedit.command.tool.brush.Brush` whose
`build(EditSession, BlockVector3, Pattern, double)` does the work. BetterGoPaint-specific inputs
(falloff, mixing, angle, surface mode, the player's eye position) go into an immutable
`StrokeContext` captured on the player's thread. *Alternative:* keep our own abstract `Brush` and
just swap the read calls. Rejected, because implementing the WorldEdit contract is what makes a
later `/brush` binding a small change.

### Palette as `RandomPattern`, mask as `Mask`
The palette becomes a `RandomPattern` with weight 1 per block. The BetterGoPaint mask becomes a
`BlockTypeMask`, combined with the session's global mask that `LocalSession#createEditSession`
already applies. Materials are converted once with `BukkitAdapter.asBlockType` while the context is
captured, not per block.

### Extent-based geometry helpers
`Sphere`, `Height`, `Surface` and `ConnectedBlocks` take an `Extent` plus `BlockVector3` and return
`BlockVector3`s. "Empty" means `BlockState#getBlockType().getMaterial().isAir()`. Min and max
height come from `EditSession#getMinY()/getMaxY()`.

### Randomness
`StrokeContext` carries a `Random`. Production seeds it per stroke, tests pass a fixed seed. This
makes "same seed, same result" testable.

### Tests against an in-memory extent
A small test `Extent` backed by a `Map<BlockVector3, BlockState>` implements `getBlock`/`setBlock`.
If FAWE's interfaces make that impractical, tests use `BlockArrayClipboard` where it works offline.

### Implementation notes (deviations from the decisions above)

- **`Brush#bind` instead of an inheriting base.** Brush objects are shared singletons (the settings
  of every player point at the same `SphereBrush`), so they cannot hold the context of a stroke and
  also be WorldEdit's `Brush`. The base class keeps the brush metadata and gets
  `apply(Extent, StrokeContext)`; `Brush#bind(StrokeContext)` returns a private
  `com.sk89q.worldedit.command.tool.brush.Brush` whose `build(EditSession, BlockVector3, Pattern,
  double)` hands position, pattern and size to the brush through `StrokeContext#with`. A later
  `/brush` binding only has to produce a `StrokeContext`.
- **`apply` takes an `Extent`, not an `EditSession`.** An `EditSession` cannot be created without a
  platform, and an `Extent` is all a brush needs. `build` wraps the session in a `MaskingExtent`
  with the session mask, so no block the global mask excludes is written, whatever extent the
  brush gets.
- **Own `SingleTypeMask` instead of `BlockTypeMask`.** `BlockTypeMask` sizes a lookup table from the
  block registry when it is constructed, which fails without a server. `SingleTypeMask` extends
  `AbstractExtentMask` and compares the block type directly. It starts without a real extent
  (`NullExtent`), because the extent of the stroke is not known on the player's thread. The stroke
  tests it with `test(Extent, BlockVector3)`.
- **Palette weights follow the list.** Palette entries are aggregated before they go into the
  `RandomPattern`, so a material that is listed twice keeps twice the weight, as with the old
  `randomBlock()`. The `RandomPattern` draws from the stroke's `Random` through a `SimpleRandom`.
- **Read first, write second.** FAWE's edit session shows earlier writes of the same stroke to later
  reads, while the old code read the untouched Bukkit world. Every brush therefore collects its
  positions first and writes afterwards, which keeps old and new results the same. The Gradient
  brush draws its random numbers in the collect step and writes afterwards.
- **`BezierSpline` uses `Vector3`.** The public API takes and returns WorldEdit `Vector3`. Inside,
  FAWE's `MutableVector3` replaces the mutable `Location`s of the control point maths, which is
  pure arithmetic and unchanged. The unused `getWorld()` was removed because a curve has no world.
- **Solid and air checks.** Bukkit's `Block#isEmpty` becomes `BlockMaterial#isAir()` and
  `Block#isSolid` becomes `BlockMaterial#isSolid()`. Positions outside the build limits read as
  air and are never returned by `Sphere`.
- **Multi-click state stays on the player's thread.** `Brush#prepare` runs in the listener before
  the stroke is queued. The Paint Brush collects its points there and returns the context with all
  points, or null while the player is still clicking.
- **The eye position keeps the old 1.5 block offset** above the player's feet for the relative
  surface mode, so existing builds look the same.

### Test doubles
WorldEdit's `BlockTypes` needs the block registry of a running platform, so no real `BlockType` or
`BlockState` exists in a unit test. The tests use `MemoryExtent` (an `Extent` over a map of block
types) and Mockito mocks of `BlockType` that answer the default state and whether the material is
air or solid. `BlockVector3`, `Vector3`, `RandomPattern` and `Mask` work offline. A real
`EditSession` cannot be built either, so the tests call `Brush#apply` and check the session-mask
wrapper directly. History and `//gmask` on a real session stay in the manual check.

## Risks / Trade-offs

- [Subtle result differences per brush] → port brush by brush with a test each, and compare against
  the old code on `run-26.2` with a client before release.
- [FAWE-specific API surface] → stay on `com.sk89q.worldedit` API where possible so the plugin is
  not tied to FAWE internals.
