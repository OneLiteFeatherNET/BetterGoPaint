# Tasks

## 0. Test support

- [x] 0.1 Add `CountingExtent` (decorator over `MemoryExtent`, counts both `getBlock` overloads per position, fresh per test) under `src/test/java/.../support/`; verify with a unit test that two reads of one position count 2
- [x] 0.2 Add a reference sphere helper (strict `distance < size / 2d`) for equivalence tests; verify it matches `SphereTest` expectations

## 1. C1 Paint curve once per stroke

- [x] 1.1 Write failing tests: curve constructed once per stroke, and curve positions equal the per-block construction for a fixed seed, including negative offsets
- [x] 1.2 Compute the relative spline and sample offsets once per stroke, translate per block, dedupe positions; verify `PaintBrushTest` and the new tests pass

## 2. C2 Height memo

- [x] 2.1 Write failing tests: no repeated column scan per query, Fracture self height computed once (counting extent)
- [x] 2.2 Add a per-stroke `HeightCache`, hoist the Fracture self height, drop the per-neighbour `BlockVector3`; verify `HeightTest` and `TerrainBrushesTest` pass

## 3. C3 Random filter first

- [x] 3.1 Write failing tests: Spray chance 0 performs no mask or surface read; chance 100 selects all passing candidates; selected positions satisfy mask and sphere invariants
- [x] 3.2 Reorder filters in `SprayBrush`, `SplatterBrush`, `GradientBrush`; update or replace tests that pin exact seeded output with invariant tests or new golden values; verify `ShapeBrushesTest` and `StrokeRulesTest` pass
- [ ] 3.3 Note the changed seeded output in the PR description

## 4. C4 Allocation-free sphere

- [x] 4.1 Write equivalence tests for sizes 1..21 and the boundary voxel against the reference helper (they must pass before and after the rewrite)
- [x] 4.2 Rewrite `Sphere.getBlocksInRadius` with int loops, squared distance, allocation only for hits and the air read in the same pass; verify `SphereTest` and the equivalence tests pass

## 5. C5 One read per position

- [x] 5.1 Write failing tests: no position read more than once for Spray (mask + DIRECT surface), Overlay/Underlay (thickness 3) and Bucket
- [x] 5.2 Add the per-stroke read cache, hoist the solid check out of the thickness loop, rewrite `ConnectedBlocks` with a grid and `ArrayDeque`; verify `ConnectedBlocksTest` and `SurfaceTest` pass

## 6. C6 Invariants and cheap RNG

- [x] 6.1 Write a test: a context built with an injected seeded random yields identical selections across two runs
- [x] 6.2 Hoist `tan`, `size / 2` and the falloff factor in Angle, Paint, Splatter, Gradient; verify existing brush tests pass
- [ ] 6.3 Replace `SecureRandom` in `PlayerBrush` and the per-click `new Random` in `InteractListener` with a cheap per-stroke generator; keep the `StrokeContext` injection point; verify `InteractListenerTest` and `StrokeContextFactoryTest` pass

## 7. Wrap up

- [x] 7.1 Run `./gradlew build` and confirm no hot-path test uses `Thread.sleep`, `System.nanoTime` or `Instant.now`
- [ ] 7.2 Open the pull request under the title `perf(brush): cut per-block work in the paint hot path` (opened as a draft with this proposal, marked ready when tasks 0.1 to 7.1 are done)
