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
| `./gradlew checkDocsImages` | Fails when a generated image is missing or differs, or when `docs/images` holds a file the generator does not produce; the message names the file |
| `./gradlew checkDocsLinks` | Fails when a relative link or image in `docs/` does not resolve, when a builder page has neither an image nor a link to `development/screenshot-shotlist.md`, or when a planned image named in a hint is not listed in that shotlist; external links are not fetched |
| `./gradlew docsGenTest` | Unit tests of the generator and the checker |

`checkDocsImages`, `checkDocsLinks` and `docsGenTest` run inside `./gradlew check`, so `./gradlew build`
fails on stale images and broken links.

Rules for the generated files:

- `docs/images/` holds generated files only. `checkDocsImages` fails on any other file, so a picture
  that is not generated does not belong there.
- A new brush needs a scene in `BrushScenes`; `docsGenTest` fails and names the brush until it has one.
  Run `./gradlew generateDocsImages`, commit the new SVG and update `reference/brushes.md` in the same
  pull request.
- A change to the menu layout needs no code change here: run `./gradlew generateDocsImages` and commit
  the menu SVGs.
- The generator reads no clock and no network. Fixed terrain comes from the test fixtures
  (`MemoryExtent`, `StrokeBuilder`, `Terrain`); the generator contains no copy of a brush algorithm.
- Builder pages that still have no illustration are listed in
  `DocsLinkChecker.BUILDER_PAGES_AWAITING_ILLUSTRATIONS`. Remove the entry when the page embeds an
  image, and give a new builder page an image in the same pull request.

Pages that embed each generated file:

| Generated file | Embedded by |
|---|---|
| `images/brushes/*.svg`, one per brush | No page yet; the change `builder-guides-with-images` embeds them |
| `images/menu/menu-sphere-default.svg`, `menu-spray-disabled.svg`, `menu-angle.svg` | No page yet; the change `builder-guides-with-images` embeds them |
