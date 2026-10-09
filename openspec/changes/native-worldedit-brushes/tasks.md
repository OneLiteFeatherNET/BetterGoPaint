# Tasks

## 1. Foundation

- [x] 1.1 Add `StrokeContext` (captured target, player eye position, settings snapshot, palette `Pattern`, mask, `Random`) and build it on the player's thread in `InteractListener`; verify with a unit test that building it needs no world access (`StrokeContextFactoryTest` verifies the mocked `World` is never touched; the listener calls `StrokeContextFactory.capture` before `runAction`)
- [x] 1.2 Add an in-memory test `Extent` and verify it round-trips `setBlock`/`getBlock` (`MemoryExtent`, `MemoryExtentTest`; block types are Mockito mocks because WorldEdit's registry cannot initialise offline, see `TestBlocks`)

## 2. Geometry on extents

- [x] 2.1 Port `Sphere`, `Height`, `Surface`, `ConnectedBlocks` to `Extent` + `BlockVector3`; verify each with unit tests against the in-memory extent (`SphereTest`, `HeightTest`, `SurfaceTest`, `ConnectedBlocksTest`)

## 3. Brushes

- [x] 3.1 Make the brush base implement WorldEdit's `Brush` and port Sphere, Spray, Splatter, Disc and Bucket brushes; verify each with a deterministic test (fixed seed, expected positions) (`ShapeBrushesTest`; the base exposes WorldEdit's `Brush` through `Brush#bind`, see design.md)
- [x] 3.2 Port Angle, Overlay, Underlay, Fracture, Gradient and Paint brushes; verify each with a deterministic test (`TerrainBrushesTest`, `StrokeRulesTest#everyBrushIsDeterministic`)
- [x] 3.3 Remove every Bukkit world/block call from brush code; verify `grep -rn "getBlock()\|getBlockAt\|org.bukkit.block.Block" src/main/java/net/onelitefeather/bettergopaint/objects src/main/java/net/onelitefeather/bettergopaint/utils` returns nothing (grep is empty; `NoBukkitWorldAccessTest` repeats it in the build)
- [x] 3.4 Run strokes through the session's `EditSession` so the global mask and history apply; verify with a test that a mask on the session blocks changes (`StrokeRunner` uses `LocalSession#createEditSession` and `remember`; `StrokeRulesTest#sessionMaskBlocksChanges` tests the masking wrapper against the in-memory extent, a real `EditSession` needs a server, so history and `//gmask` still need the manual check)

## 4. Docs

- [ ] 4.1 Update docs/explanation/how-a-brush-stroke-is-applied.md (stroke context, WorldEdit session, `//gmask`)

## Manual follow-up (needs a Minecraft client)

- On `run-26.2`, paint with every brush, compare with 2.0 behaviour, `//undo` once, and try `//gmask`.
