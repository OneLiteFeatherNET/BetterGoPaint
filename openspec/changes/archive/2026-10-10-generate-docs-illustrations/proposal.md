# Proposal

## Why

The documentation describes eleven brushes and a 54-slot brush menu, but shows neither. Screenshots
taken by hand go stale the first time a brush shape or a menu slot changes, and nothing would notice.
The brush shapes and the menu layout already exist as code: the brushes run against the in-memory test
world (`MemoryExtent`, `StrokeBuilder` with a fixed seed, `Terrain` fixtures) and `GUI.layout()` returns
every menu slot. An illustration generated from that code is exact and can be checked against the code
on every build.

## What Changes

- Add a Gradle source set `docsGen` with a generator that runs the real brush implementations on fixed
  terrain and renders the placed blocks as SVG: for each brush a top view and a side cross-section with
  a legend, written to `docs/images/brushes/`.
- Render the brush menu from `GUI.layout()` for three representative brush states as SVG grids
  labelled with each slot's purpose (English text resolved from `bettergopaint.properties`), written to
  `docs/images/menu/`.
- Add a Gradle task `generateDocsImages` that writes the images into `docs/images/`, and a task
  `checkDocsImages` that regenerates them in memory and fails when a committed file is missing, differs
  or is not produced by the generator. `checkDocsImages` runs inside `check`, so `./gradlew build` and the
  existing Build PR workflow fail on stale images.
- Add a link and asset checker (`checkDocsLinks`, same source set, also run by `check`) that fails when
  a relative link or image target in `docs/` does not resolve, when a page under the "For builders"
  section of `SUMMARY.md` has neither an image nor a reference to `development/screenshot-shotlist.md`,
  or when a planned screenshot named in a GitBook hint is missing from the shotlist. External links are
  not fetched.
- Keep generated SVGs byte-identical across runs and operating systems: LF line endings enforced with
  `.gitattributes`, UTF-8, integer coordinates, no timestamps, no font measuring.
- Add a unit test source set `docsGenTest` for the generator and checker. It is wired into `check` and
  writes files only into `@TempDir`.
- Document the regeneration workflow in `docs/development/documentation.md`.

Commit type and scope: `build(docs)`. Every commit of this change is `build(docs)` (generator, Gradle
wiring, checker, `.gitattributes`, generated files, and the development note in `documentation.md`,
which describes the tooling). Pull request title: `build(docs): generate brush and menu illustrations`.

Not breaking: no command, permission, config key, brush or supported version changes, so no server owner
has to act. The plugin jar does not change: the generator lives in its own source set and is not shipped
by `shadowJar`.

## Capabilities

### New Capabilities
- `docs-illustrations`: how brush and menu images are generated, what they show, determinism, and what
  the generator may touch.

### Modified Capabilities
- `build-verification`: adds the stale-image check, the link and asset check and the generator unit
  tests to the checks every build runs.
- `user-documentation`: adds the image location and the rule that `docs/images/` holds generated files
  only.

## Impact

- `build.gradle.kts`: source sets `docsGen` and `docsGenTest`, tasks `generateDocsImages`,
  `checkDocsImages`, `checkDocsLinks`, `docsGenTest`, `check` depends on them.
- `.gitattributes`: `docs/images/**/*.svg text eol=lf`.
- `src/docsGen/java/net/onelitefeather/bettergopaint/docsgen/**`: generator and checker.
- `src/docsGenTest/java/net/onelitefeather/bettergopaint/docsgen/**`: unit tests.
- `docs/images/brushes/*.svg` (11 files) and `docs/images/menu/*.svg` (3 files), generated.
- `docs/development/documentation.md`: regeneration workflow.

## Out of scope

- Writing any user page, guide or reference text. The illustrations are embedded by change
  `builder-guides-with-images`, which depends on this change.
- Screenshots and GIFs from a real client. Those are the shotlist of `builder-guides-with-images` and
  are never generated.
- Changing brush behaviour, the menu layout or the translation bundles. The generator reads them; it
  does not change them.
- Publishing images to a CDN or GitBook's own asset store.
- A GitHub Actions workflow change. The check runs inside the existing `./gradlew build` step.
