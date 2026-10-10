# Writing documentation

The docs follow [Diátaxis](https://diataxis.fr/): `tutorial/` teaches, `how-to/` solves one task,
`reference/` lists facts, `explanation/` gives background. `SUMMARY.md` is the navigation; a page
that is not listed there is not published.

Update these pages in the same pull request when you change:

| Change | Page |
|---|---|
| A config key | `reference/configuration.md` |
| A command | `reference/commands.md` |
| A permission | `reference/permissions.md` (and `build.gradle.kts`) |
| `supportedMinecraftVersions` | `reference/supported-versions.md` |
| A brush | `reference/brushes.md` |

`ReferenceDocsTest` enforces this for the reference pages: it fails the build when a config key
or its default, a permission from `build.gradle.kts`, a `/gopaint` subcommand or a supported
Minecraft version is missing from its page, or when a user page is missing from `SUMMARY.md`.
The comment next to each key in `SettingsLoader` is what server owners see in `config.yml`; keep
it and the "Meaning" column of the configuration page saying the same thing.

## Audiences

Pages are written for two readers. Server admins install, configure and permission the plugin;
builders paint with it. `index.md` sends each to their entry points, and `SUMMARY.md` groups the
how-to guides by audience. Tutorials: one per audience, each ending with a hand-off to the next
pages.

## Illustrations

The brush and menu pictures in `docs/images/` are generated from the code, never drawn by hand.
The generator is the `docsGen` source set (`src/docsGen`); it runs the real brush code on the test
fixtures and renders the brush menu from `GUI.layout()`. Output is SVG with a fixed seed, LF line
endings and no timestamp, so two runs produce the same bytes on every operating system.

| Command | What it does |
|---|---|
| `./gradlew generateDocsImages` | Writes `docs/images/brushes/*.svg` and `docs/images/menu/*.svg` |
| `./gradlew checkDocsImages` | Fails when a generated image is missing or differs, or when a generated folder (`brushes/`, `menu/`) holds a file the generator does not produce; the message names the file |
| `./gradlew checkDocsLinks` | Fails when a relative link or image in `docs/` does not resolve, when a builder page has neither an image nor a link to `development/screenshot-shotlist.md`, or when a planned image named in a hint is not listed in that shotlist; external links are not fetched |
| `./gradlew docsGenTest` | Unit tests of the generator and the checker |

`checkDocsImages`, `checkDocsLinks` and `docsGenTest` run inside `./gradlew check`, so `./gradlew build`
fails on stale images and broken links.

Rules for the generated files:

- The generated folders `docs/images/brushes/` and `docs/images/menu/` hold generated files only.
  `checkDocsImages` fails on any other file in them.
- Hand-captured images, such as in-game screenshots and GIFs for the builder guides, go in
  `docs/images/screenshots/`. `checkDocsImages` does not check that folder for staleness, and
  `checkDocsLinks` still fails when a page links to a missing file there. A planned capture named in a
  hint block must be listed in `docs/development/screenshot-shotlist.md`.
- A new brush needs a scene in `BrushScenes`; `docsGenTest` fails and names the brush until it has one.
  Run `./gradlew generateDocsImages`, commit the new SVG and update `reference/brushes.md` in the same
  pull request.
- A change to the menu layout needs no code change here: run `./gradlew generateDocsImages` and commit
  the menu SVGs.
- The blocks are drawn in Minecraft's colours and patterns, but no Minecraft texture is used. Each material has a
  representative colour and an 8x8 pixel tile that the generator computes from the material name (`BlockLooks`);
  pixel icons for menu items that are not cubes are shapes written in code (`ItemIcons`). The generator never
  downloads, copies, embeds or reads a Minecraft texture, a resource pack or any other third-party texture file,
  because the licence of those files does not allow it. Do not add one to `src/docsGen` or to the pictures.
- A new block material needs an entry in `BlockPalette` (the fixture it stands for) and in `BlockLooks`; a new
  non-cube menu item needs an icon in `ItemIcons`. Until then the generator fails and names the material.
- Reading the brush pictures: a block the brush changed is drawn at full colour with a white border, a block it did
  not change is faded, air is the light blue sky, and the clicked block is outlined. The legend names every material
  by its Minecraft name. The paint brush numbers its clicked points in click order and joins them with a line. In the
  menu, a numbered badge marks each functional slot and the legend gives the number, the slot and the English text;
  filler panes have no badge.
- The generator reads no clock and no network. Fixed terrain comes from the test fixtures
  (`MemoryExtent`, `StrokeBuilder`, `Terrain`); the generator contains no copy of a brush algorithm.
- Every builder page needs an image or a link to the screenshot shotlist; `DocsLinkChecker` fails the
  build for a builder page that has neither, so give a new builder page an image in the same pull request.

Pages that embed each generated file:

| Generated file | Embedded by |
|---|---|
| `images/brushes/*.svg`, one per brush | `reference/brushes.md` (all eleven). Also `how-to/paint-a-path-with-the-paint-brush.md` (`paint-brush.svg`), `how-to/shape-cliffs-with-the-angle-brush.md` (`angle-brush.svg`) and `how-to/blend-a-gradient.md` (`gradient-brush.svg`) |
| `images/menu/menu-sphere-default.svg` | `tutorial/paint-your-first-terrain.md`, `how-to/mix-a-block-palette.md`, `how-to/share-a-brush-as-an-item.md`, `reference/brush-menu.md` |
| `images/menu/menu-spray-disabled.svg` | `how-to/paint-only-on-one-block-with-a-mask.md`, `reference/brush-menu.md` |
| `images/menu/menu-angle.svg` | `how-to/shape-cliffs-with-the-angle-brush.md`, `reference/brush-menu.md` |
