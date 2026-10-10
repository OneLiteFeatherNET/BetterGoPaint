# Proposal

## Why

The published documentation is written mostly for server admins. `SUMMARY.md` lists eight admin how-to
guides and one builder how-to guide (`share-a-brush-as-an-item`). The people who paint every day have
almost nothing that answers "how do I do this with the brush". The brush settings and menu slots that
builders use are documented only as table rows in `reference/brushes.md`, without pictures, and the two
explanation pages on stroke application and settings loading describe their flows only in prose.

## What Changes

- Add five builder how-to guides, each for one task the plugin really supports:
  1. Paint a path with the Paint brush (click points, sneak to finish, 256-point limit).
  2. Shape cliffs with the Angle brush (angle distance and height difference, shift-click steps).
  3. Paint only on one block with a mask (mask toggle, mask block, and how it differs from `//gmask`).
  4. Mix a block palette (up to five palette blocks, equal chance per block).
  5. Blend a gradient (falloff and mixing strength).
- Embed the generated illustrations from change `generate-docs-illustrations`: all eleven brush shapes
  in `reference/brushes.md`, the matching shape in each guide, and the menu map in the tutorial
  `paint-your-first-terrain` and in a new reference page `reference/brush-menu.md`.
- Add Mermaid flowcharts to `explanation/how-a-brush-stroke-is-applied.md` and
  `explanation/how-settings-are-loaded.md`, each step checked against the code before it is drawn.
- Add `development/screenshot-shotlist.md`: file name, target page, what to show, and the client
  settings (resolution, GUI scale, resource pack, time of day) for each screenshot or GIF the team has
  to capture.
- Reference the planned screenshots from the guides with GitBook hint blocks that name the planned file
  under `images/guides/`. No planned file is ever an image link, so no link is broken before the
  screenshot exists.
- Update `docs/SUMMARY.md`: the "For builders" section lists the new guides, the Reference section lists
  the brush menu page. Link the builder guides from the builder entry point in `docs/index.md`.

Commit type and scope: `docs(guide)` for every commit of this change, including the Mermaid diagrams,
the shotlist and the shape embedding in `reference/brushes.md`, which all serve the builder guides. Pull
request title: `docs(guide): add builder guides with illustrations`.

Depends on change `generate-docs-illustrations` (pull request `build(docs): generate brush and menu
illustrations`). The generated SVG files and `checkDocsLinks` come from that change, so this pull
request merges only after it, and its branch is rebased onto `main` after that merge.

Not breaking: no command, permission, config key, brush or supported version changes.

## Capabilities

### New Capabilities
- `builder-guides`: which builder guides exist, what each one covers, and what each one shows.

### Modified Capabilities
- `user-documentation`: adds Mermaid explanations, the placeholder rule for planned screenshots, the
  shotlist, the brush menu reference page and the builder section of the navigation.

## Impact

- `docs/how-to/paint-a-path-with-the-paint-brush.md`, `shape-cliffs-with-the-angle-brush.md`,
  `paint-only-on-one-block-with-a-mask.md`, `mix-a-block-palette.md`, `blend-a-gradient.md` (new).
- `docs/reference/brush-menu.md` (new), `docs/reference/brushes.md` (shapes embedded).
- `docs/tutorial/paint-your-first-terrain.md` (menu map embedded).
- `docs/explanation/how-a-brush-stroke-is-applied.md`, `how-settings-are-loaded.md` (Mermaid).
- `docs/development/screenshot-shotlist.md` (new; `development/` is not in the navigation).
- `docs/SUMMARY.md`, `docs/index.md`.

## Out of scope

- The screenshots and GIFs themselves. The team captures them from the shotlist; until then the guides
  carry hint blocks, not images.
- Undo and redo as a separate how-to. The tutorial already shows `//undo`, and the explanation page
  states that a stroke is one history entry.
- An automated check that every generated image is embedded. That needs all images embedded first, so it
  is a follow-up change (see `generate-docs-illustrations`, design.md).
- Changes to the admin guides, the tutorial for server admins, or the plugin's behaviour.
- Translations of the guides. The guides describe English menu labels, as the tutorial does.
