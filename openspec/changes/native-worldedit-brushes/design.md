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

## Risks / Trade-offs

- [Subtle result differences per brush] → port brush by brush with a test each, and compare against
  the old code on `run-26.2` with a client before release.
- [FAWE-specific API surface] → stay on `com.sk89q.worldedit` API where possible so the plugin is
  not tied to FAWE internals.
