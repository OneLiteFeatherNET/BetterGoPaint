# Design

## Decision 1: images live in `docs/images/`, not in `docs/.gitbook/assets/`

GitBook Git Sync takes its content from the project directory, which is `docs/` here (`.gitbook.yaml`:
`root: ./docs/`). GitBook resolves asset paths only inside that synced scope, and both candidate folders
are inside it, so both work for publishing. The choice is about the repository:

- `docs/.gitbook/assets/` is the folder GitBook uses for images uploaded in its editor. Generated files
  would share a namespace with files a writer may upload by hand, and a hidden folder is easy to miss in
  a review on GitHub.
- `docs/images/` is a plain folder that reviewers see in the diff. Relative links such as
  `../images/brushes/sphere-brush.svg` resolve on GitHub and on GitBook alike, so the same markdown is
  checked by `checkDocsLinks` without GitBook running.

Sources: GitBook content configuration (synced scope and asset paths) at
https://docs.gitbook.com/getting-started/git-sync/content-configuration and the Git Sync overview.

## Decision 2: SVG, not PNG

SVG output is text. It is byte-identical across runs and across the three operating systems of the Build
PR workflow, and the diff is reviewable. PNG would need a rasteriser (AWT, headless fonts) whose glyph
output differs between platforms, which would make the stale check fail on one OS and pass on another.
GitBook renders SVG in markdown images. The generator uses no image library.

## Decision 3: a dedicated source set, not a JUnit test

Writing into `docs/` from a test breaks F.I.R.S.T. (a test that writes into the repository is neither
independent nor repeatable). The generator therefore lives in source set `docsGen`, run by `JavaExec`
tasks. `docsGen` compiles against `main` and `test` output, because the brush scenes use the in-memory
fixtures in `src/test/.../support`. The generator's own unit tests live in source set `docsGenTest`.
They depend on `docsGen`, not the other way round, and `src/test` does not depend on the generator. That
avoids a cycle between the source sets.

Dependencies: `docsGen` and `docsGenTest` extend `testImplementation` and `testRuntimeOnly`, so the
generator gets the same Paper, FAWE, Mockito and JUnit versions the tests use. Nothing is added to the
shipped jar, because `shadowJar` packages only `main`.

## Decision 4: block types in the fixtures are mocks

`TestBlocks.type(...)` builds Mockito mocks. A mock does not expose its id, so the generator cannot read
a block name from a `BlockType`. Each scene uses the fixture instances from `Terrain` and `TestBlocks`,
and the generator maps each instance by identity to a fixed colour and label in one table. A block that
is not in the table throws `IllegalStateException` naming the position. The generator never guesses a
colour.

## Decision 5: brush scenes

Each brush is applied through `Brush.apply(extent, context)` on a `MemoryExtent` that starts as the
fixture terrain. The context comes from `StrokeBuilder` with seed `1`, so random brushes place the same
blocks every run. Scenes (fixed; the implementation may tune sizes only if the illustration is still
clear):

| Brush | Terrain | Settings |
|---|---|---|
| Sphere | `Terrain.flat()` | size 5, palette stone, andesite, gold |
| Spray | `Terrain.flat()` | size 5, chance 50 |
| Splatter | `Terrain.flat()` | size 5, falloff 2 |
| Disc | `Terrain.flat()` | size 5, axis X |
| Bucket | `Terrain.flatWithTop(grass)` | size 5, clicked grass |
| Angle | `Terrain.step()` | size 6, angle distance 2, height difference 40 |
| Overlay | `Terrain.flatWithTop(grass)` | size 5, thickness 2 |
| Underlay | `Terrain.flatWithTop(grass)` | size 5, thickness 2 |
| Fracture | `Terrain.flatWithTop(stone)` | size 5, fracture distance 2 |
| Gradient | `Terrain.flat()` | size 5, mixing 2, falloff 2, palette stone + andesite |
| Paint | `Terrain.flat()` | points along a fixed curve of 6 points, falloff 2 |

Brushes are taken from `PlayerBrushManager`'s registry, the same list the game uses, so a new brush
without a scene fails `testDocsGen`.

Each SVG has three parts: the top view (one cell per x/z column, the highest changed block coloured,
unchanged surface in grey), the cross-section through the target at its z (one cell per x/y), and a
legend listing each block colour with its name. Cells use integer coordinates on a fixed grid, so no
text measuring is needed.

## Decision 6: menu map

The menu is rendered from `GUI.layout(playerBrush)`, built the way `GUILayoutTest` builds it:
`MenuRenderCache(() -> 0, (locale, component) -> component)`, so each label is a translation key. The
generator resolves the English text of each key from `src/main/resources/bettergopaint.properties`,
strips the MiniMessage tags and writes a 6 by 9 grid of 54 cells. Each cell shows the slot number, the
material and the purpose text.

Three states: the default sphere brush (`menu-sphere-default.svg`), a disabled spray brush with two
palette blocks and a dirt mask (`menu-spray-disabled.svg`), and an angle brush with the surface disabled
(`menu-angle.svg`). These are the states `GUILayoutTest` already pins, so the images show exactly what
the tests assert.

## Decision 7: the stale check and where it runs

`checkDocsImages` runs the generator in memory and compares every produced file with the file in
`docs/images/`. It reports missing, differing and extra files and exits with status 1. It is a
dependency of `check`, and `check` is a dependency of `build`. The existing `build-pr.yml` runs
`./gradlew clean build test` on Ubuntu, Windows and macOS, so the check runs in CI without a workflow
change. `.gitattributes` (`docs/images/**/*.svg text eol=lf`) keeps the committed bytes identical after a
Windows checkout.

Rejected: a CI step running `git diff --exit-code docs/images` after `generateDocsImages`. It only
works after a write, leaves the tree dirty when it fails, and does not run locally on `build`.

## Decision 8: the link and asset checker is build code

`checkDocsLinks` is a Java class in `docsGen`, not a script, so it is unit tested like the generator. It
checks:

1. every relative link and image target in `docs/**/*.md` resolves to a file, relative to the page
   (a leading `/` is relative to `docs/`); anchors are stripped before the file check, `http(s)` and
   `mailto` targets are not fetched;
2. every page listed under the "For builders" heading of `SUMMARY.md` contains at least one image or a
   link to `development/screenshot-shotlist.md`;
3. every file name inside a `{% hint %}` block that starts with `images/` is listed in the shotlist.

An orphan rule (every file in `docs/images/` linked from a page) is deliberately not part of this change:
it would fail this change's own build, because the images ship before any page embeds them. Change
`builder-guides-with-images` embeds them and records the embedding in its pull request; an automated
orphan rule is a follow-up that can land once every generated image is embedded.

The builder rule reads the section heading of `SUMMARY.md`, so no page list is hard-coded. Planned
screenshots appear only in hints, never as image links, so rule 1 does not fail on them.

Existing pages were not checked before this change. The first task runs the checker on `main`; any
violation is either fixed in the same change (only the link target, no wording) or stops the change
for a decision.

## Decision 9: determinism and the clock

The generator reads no clock (`Instant`, `System.currentTimeMillis`, `System.nanoTime`, `LocalDate`),
writes no timestamp or generator version into the output, formats numbers with `Locale.ROOT` and writes
UTF-8 with `\n` line endings. Iteration follows the registry order and `TreeMap` for maps. The two runs
in the spec are compared with `cmp`.

## Alternatives considered

- **Generate PNG with AWT.** Rejected: font rasterisation differs between operating systems, so the
  stale check would be platform dependent (decision 2).
- **Generate inside a JUnit test.** Rejected: writes into `docs/` from a test (decision 3).
- **Generate with a plain `JavaExec` on the main source set.** Rejected: the brush scenes need the
  test fixtures, which `main` does not contain.
- **Manual screenshots for the brush shapes.** Rejected: they cannot be made exact and go stale on the
  first change of a brush.
- **Mermaid for the brush shapes.** Rejected: Mermaid draws graphs, not block grids.
- **Render the menu from the live Paper `Inventory`.** Rejected: it needs a server, which the generator
  must not use.
