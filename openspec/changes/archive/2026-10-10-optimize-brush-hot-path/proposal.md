# Proposal

## Why

A single brush click evaluates every voxel of a cube around the target. For each candidate the
brushes repeat work that does not depend on the candidate or that was already done for a
neighbour:

- The Paint Brush builds a new Bezier spline (control-point solve plus length integration) per
  candidate block, although every spline is the same relative curve translated by the block
  position (`objects/brush/PaintBrush.java:97-123`).
- Height scans walk a column per call. Fracture runs about 9 scans per candidate and computes the
  same self height twice (`FractureBrush.java:42-52`); Angle and Paint run 8 per candidate
  (`AngleBrush.java:42-44`, `PaintBrush.java:101-102`; `utils/Height.java:38-103`).
- Spray runs the mask and the optional surface raytrace before a one-line random filter
  (`SprayBrush.java:40-41`). Splatter (`SplatterBrush.java:40-47`) and Gradient
  (`GradientBrush.java:46-53`) do the same with a distance-based random filter.
- `utils/Sphere.java:32-106` allocates a `BlockVector3` and takes a square root for every cube
  voxel, then streams the hits a second time to read each block for the air filter.
- The same position is read up to three times (sphere air filter `Sphere.java:76`, direct surface
  check `Surface.java:23`, mask). Overlay/Underlay re-read and allocate inside the thickness loop
  (`OverlayBrush.java:48`), and the bucket flood fill uses a `HashSet`, a `LinkedList` and a
  `BlockVector3` per face (`utils/ConnectedBlocks.java:64-79`).
- Loop invariants (`Math.tan(Math.toRadians(..))`, `size / 2`, the falloff factor) are recomputed
  per candidate (`AngleBrush.java:44`, `PaintBrush.java:102-108`, `SplatterBrush.java:42-45`,
  `GradientBrush.java:48-51`). `PlayerBrush.java:60` uses a `SecureRandom` and
  `InteractListener.java:147` creates a `new Random(..)` per click for brush jitter that needs no
  cryptographic strength.

Strokes are computed on the main thread (see Out of scope), so every wasted world read and
allocation is paid in server tick time.

## What Changes

Commit type and scope: `perf(brush)`. Pull request title:
`perf(brush): cut per-block work in the paint hot path`. Not breaking: no command, permission,
config key, Java or Minecraft version changes, and the set of selected blocks stays the same
(except the seeded-output note under C3).

- **C1 Paint curve once per stroke.** Compute the relative spline and its sample offsets once,
  translate per block, and drop duplicate positions.
- **C2 Height memo.** Cache column heights per stroke keyed by position, compute the self height of
  Fracture once, and avoid a `BlockVector3` allocation per neighbour.
- **C3 Random filter first.** Spray, Splatter and Gradient run their cheap random filter before
  `passesDefaultChecks`. The random distribution is unchanged; the order of RNG draws changes, so
  a fixed seed selects different (equally valid) positions than before.
- **C4 Allocation-free sphere.** Primitive loops, squared-distance comparison against
  `(radius / 2)^2` with the same strict `<` membership, allocate only hits, fold the air read into
  the same pass.
- **C5 One read per position.** A stroke reads each world position at most once; Overlay/Underlay
  and the bucket flood fill use that read and a grid or `ArrayDeque` based fill.
- **C6 Hoisted invariants and cheap RNG.** Compute tangents, half sizes and falloff factors once per
  stroke; use a cheap seeded generator per stroke instead of `SecureRandom` and a fresh `Random`
  per click. `StrokeContext` keeps its injectable random source for tests.

Tests are written first for each finding (red, then green) and assert call counts and equivalence,
never timings. See `design.md`.

## Capabilities

### New Capabilities

### Modified Capabilities
- `brush-painting`: stroke world reads are bounded, the Paint Brush curve is computed once, random
  filters run before world reads, and sphere membership is pinned to its existing definition.
- `build-verification`: the build runs read-count and equivalence tests for the brush hot path.

## Impact

- `objects/brush/{PaintBrush,FractureBrush,AngleBrush,SprayBrush,SplatterBrush,GradientBrush,OverlayBrush,UnderlayBrush}.java`,
  `utils/{Sphere,Height,Surface,ConnectedBlocks}.java`, `brush/PlayerBrush.java`,
  `listeners/InteractListener.java`, `StrokeContext`.
- Tests: `ShapeBrushesTest`, `TerrainBrushesTest`, `StrokeRulesTest`, `PaintBrushTest` and
  `support/StrokeBuilder` guard behaviour; tests that pin exact seeded output of Spray, Splatter or
  Gradient are updated (see C3 in `design.md`). A counting extent decorator is added under
  `src/test/java/.../support/`.

## Out of scope

- Moving stroke computation off the main thread (`InteractListener.java:155` runs the action
  synchronously). That is an architectural change and gets its own proposal.
- GUI and memory work, tracked in the separate change `reduce-menu-memory`.
- A JMH benchmark module. It may follow as a non-gating addition; this change adds no benchmark and
  no timing assertion.
