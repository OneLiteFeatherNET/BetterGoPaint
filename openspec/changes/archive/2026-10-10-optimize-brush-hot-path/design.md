# Design

## Context

Brushes select candidates with `Sphere.getBlocksInRadius`, filter them, then place blocks through
the WorldEdit extent. Cost per click is candidates x per-candidate work. The goal is to cut
per-candidate work without changing which blocks a stroke selects.

## Verification approach

Timing is not asserted anywhere (F.I.R.S.T.: Fast, Repeatable). Instead:

- A `CountingExtent` decorator in `src/test/java/.../support/` wraps the existing `MemoryExtent`
  and counts `getBlock(BlockVector3)` and `getBlock(int, int, int)` calls per position. It is a
  fresh instance per test, with no static state.
- Each test builds the stroke with `StrokeBuilder` and a fixed seed, runs one brush, and asserts
  one behaviour, for example "no position is read more than once" or "the same positions change
  as the reference selection".
- For equivalence, a test keeps a small reference implementation of the old membership rule (the
  `distance < radius / 2d` check) and compares sets for several radii, including odd and even sizes
  and radii where a voxel lies exactly on the boundary.
- Existing `ShapeBrushesTest`, `TerrainBrushesTest`, `StrokeRulesTest` and `PaintBrushTest` stay
  green and guard behaviour. The Paint Brush curve-once rule is tested through an injected seam
  (a curve factory the test can count), not by measuring time.
- A JMH module may be added later as a non-gating follow-up. It is not part of this change.

## Decisions

### C1 Relative spline per stroke
`newCurve[k] = block + (points[k] - first)` is a translation, so the spline shape and arc length do
not depend on the block. The brush computes the spline from `points - first` once, plus the sample
offsets `getPoint((i / maxCount) * (points.size() - 1))`, and per block adds the block position to
each offset and drops duplicates with a position set. *Alternative:* cache by block hash. Rejected,
the curve is identical for all blocks.

Care: the current curve starts with `block.toVector3()` as an extra first control point; the
relative curve keeps the origin as that point so sampling is identical. The conversion to
`BlockVector3` truncates; because the translation is by an integer, truncating after translation
equals translating after truncation for non-negative offsets but NOT for negative ones, so the
implementation must translate first and convert second, exactly like today. An equivalence test
against the old per-block construction covers negative offsets.

### C2 Per-stroke height memo
A small `HeightCache` owned by the stroke maps a packed column key (x, z) to the scan result for
the given parameters, using primitive-keyed storage to avoid boxing and `BlockVector3` allocation.
Fracture computes the self height once per candidate and reuses it for both averages. The cache
lives for one stroke and is never static, so tests stay independent.

### C3 Random filter before world reads
Spray's `nextInt(100) < chance` and the distance-based `nextDouble()` filters of Splatter and
Gradient need no world data, so they run first. The probability that a candidate is selected is the
same. The sequence of draws consumed changes, because candidates that fail the mask no longer
consume a draw. Consequence: a fixed seed selects different positions than before. Tests that pin
exact seeded output of these brushes are rewritten to assert invariants (all selected positions
pass the mask, none lie outside the sphere, chance 0 selects nothing, chance 100 selects every
mask-passing candidate) or are re-recorded with new golden values, one per behaviour. The Paint
Brush already draws random after its height checks and keeps its order.

### C4 Sphere without per-voxel allocation
Loop over int coordinates and compare `dx*dx + dy*dy + dz*dz < r2` with `r2 = (radius / 2d)^2` in
`double`. Both sides are non-negative, so squaring preserves the strict ordering and membership
equals the old `sqrt(..) < radius / 2d`. Voxel centers are integers and the middle point is an
integer `BlockVector3`, so the squared sum is exact. Only hits allocate a `BlockVector3`. The
bounds check and the air read happen in the same pass. The `Axis` variants keep their plane
semantics, and the public signature and the `air` parameter stay.

### C5 One read per position
The Sphere pass reads a block once; the result flows to the mask, the direct surface check and
Overlay/Underlay through a per-stroke read cache (primitive-keyed, fresh per stroke). Overlay and
Underlay hoist the position's own `isSolid` check out of the thickness loop. `ConnectedBlocks`
uses a dense grid over the bounding box of the candidates and an `ArrayDeque` of packed indices;
visit order stays deterministic. *Alternative:* a caching extent wrapped around the whole stroke.
Rejected: writes during placement would need invalidation and it interferes with FAWE queue
extents. The cache therefore covers only the read phase that precedes placement.

### C6 Invariants and RNG
Per-stroke values (`tan(angle)`, `size / 2.0`, falloff factor and denominator) are computed once
and reused in the lambdas. `PlayerBrush` replaces `SecureRandom` with a non-crypto generator, and
`InteractListener` derives one cheap generator per stroke. `StrokeContext` keeps its injectable
random source so tests pass a seeded instance.

## Risks / Trade-offs

- [C3 changes seeded output] -> the spec states the distribution is the contract, tests are
  updated deliberately, and the PR description says so.
- [Cache stale after writes] -> caches only cover the read phase; placement happens after the
  candidate list is final.
- [Memory growth of caches on large brushes] -> bounded by the candidate cube, released with the
  stroke.
- [Rounding differences in C1] -> covered by an equivalence test including negative offsets.
