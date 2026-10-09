# Testing

`./gradlew build` compiles, runs the unit tests, checks the license headers and builds the
shaded jar. `./gradlew test` runs only the tests; the JaCoCo report lands in
`build/reports/jacoco/test/html`.

## What is tested

- `PlayerBrushManagerTest` – brush cycling and lookup by name.
- `BezierSplineTest` – the curve the Paint Brush follows.
- `StrokeContextFactoryTest` – the stroke context is captured without asking the world for
  anything, and the palette weights every entry equally.
- `SphereTest`, `HeightTest`, `SurfaceTest`, `ConnectedBlocksTest` – the geometry helpers against an
  in-memory extent.
- `ShapeBrushesTest`, `TerrainBrushesTest`, `StrokeRulesTest` – every brush with a fixed seed and
  expected positions, plus palette, mask, the global mask of the session and thread independence.
- `NoBukkitWorldAccessTest` – the brush and geometry sources contain no Bukkit world, block,
  location or player calls.
- `TranslationBundleTest` – every translation key used in the code exists in every bundle and
  parses as MiniMessage.
- `ExportedBrushCodecTest` – brushes stored on items, including 1.x lore and edited values.
- `BrushSizesTest` – range and tab completion of `/gopaint size`.
- `SettingsHolderTest` – reading, defaults, invalid values and reload of `config.yml`.
- `UpdateServiceTest` – the update check with a stubbed Hangar.
- `ConnectListenerTest`, `InteractListenerTest`, `InventoryListenerTest` – permission, world and
  menu checks of the listeners.
- `DIValidationTest` – the Guice module graph.

Tests use JUnit 6, AssertJ and Mockito. Bukkit types such as `World` are mocked; there is no
running server. Code that needs a server (the GUI, listeners, running a stroke in a real edit
session) is covered by manual testing with `./gradlew run-<version>`, which starts Paper with
FastAsyncWorldEdit preinstalled.

## Testing brushes without a server

Brushes work on a WorldEdit `Extent`, so the tests paint on `MemoryExtent`
(`src/test/java/.../support`), an extent backed by a map. WorldEdit's block registry only exists
on a running server, so `BlockTypes.STONE` cannot be initialised in a unit test. `TestBlocks`
creates `BlockType` mocks that answer the default state and whether the material is air or solid,
which is all the brushes ask. `Terrain` builds the small worlds, `StrokeBuilder` builds a stroke
context with a seeded random generator. `BlockVector3`, `Vector3`, `RandomPattern` and `Mask`
work offline as they are.

A real `EditSession` needs a platform as well, so the tests call `Brush#apply(Extent,
StrokeContext)` and wrap the extent with the session mask the way the stroke does.

## Adding tests

Put tests next to the package they cover under `src/test/java`. `./gradlew spotlessApply` adds the
license header.
