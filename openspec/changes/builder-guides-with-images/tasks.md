## 0. Preconditions (docs(guide))

- [ ] 0.1 Wait until `build(docs): generate brush and menu illustrations` is merged into `main`; rebase `docs/builder-guides` onto the new `main`
- [ ] 0.2 Confirm `docs/images/brushes/` holds the eleven generated SVG files and `docs/images/menu/` the three menu files, and that `./gradlew check` passes on `main`

## 1. Verify the facts before writing (docs(guide))

- [ ] 1.1 Record in the pull request description a table with each claim of the five guides and the file and line that proves it (`PaintBrush.MAX_STROKE_POINTS`, the sneak branch of `PaintBrush.prepare`, `GUI.angleSection` and the shift step, `GUI.maskToggleSection` and `GUI.maskBlockSection`, `GUI.paletteSection`, `GUI.falloffSection` and `GUI.mixingSection`)
- [ ] 1.2 Read `reference/brushes.md` and the two explanation pages and list every statement the guides repeat, so each is written once and linked from the others

## 2. Navigation red then green (docs(guide))

- [ ] 2.1 Red: create the five guide files as stubs with a title and no content, without `SUMMARY.md` entries; `./gradlew test --tests '*ReferenceDocsTest'` fails and names each page
- [ ] 2.2 Green: add the five entries under "For builders" in `docs/SUMMARY.md`; the test passes

## 3. Builder guides, one at a time (docs(guide))

Each guide: red by `./gradlew checkDocsLinks` on a stub with no picture and no shotlist reference (the builder rule fails and names the page), then green by writing the guide with its image and hint.

- [ ] 3.1 `how-to/paint-a-path-with-the-paint-brush.md`: image `../images/brushes/paint-brush.svg`; hint for `images/guides/paint-path-sneak.gif`; the 256-point limit and what sneak does
- [ ] 3.2 `how-to/shape-cliffs-with-the-angle-brush.md`: images `../images/brushes/angle-brush.svg` and `../images/menu/menu-angle.svg`; hint for `images/guides/angle-cliff-before-after.png`; slots 3 and 4, the shift-click step of 15
- [ ] 3.3 `how-to/paint-only-on-one-block-with-a-mask.md`: image `../images/menu/menu-spray-disabled.svg`; hint for `images/guides/mask-only-stone.png`; the mask toggle and mask block, and the difference from `//gmask`
- [ ] 3.4 `how-to/mix-a-block-palette.md`: image `../images/menu/menu-sphere-default.svg`; hint for `images/guides/palette-five-blocks.png`; palette slots 46 to 50, equal chance, removing a block by right-click
- [ ] 3.5 `how-to/blend-a-gradient.md`: image `../images/brushes/gradient-brush.svg`; hint for `images/guides/gradient-mixing.png`; falloff and mixing strength

## 4. Brush reference and brush menu reference (docs(guide))

- [ ] 4.1 Red: `./gradlew checkDocsLinks` passes but a manual grep of `reference/brushes.md` for the eleven `brushes/*.svg` names finds none; record this as the failing state in the pull request
- [ ] 4.2 Green: add a "Shapes" section to `reference/brushes.md` with one `### <name>` heading and one image per brush, keeping the existing table and the shared settings paragraph
- [ ] 4.3 Create `reference/brush-menu.md` with the sections of the menu and their slot numbers (from the section comments of `GUI.java`), and embed the three `menu/*.svg` files with a review note that the table is hand-written
- [ ] 4.4 Add `reference/brush-menu.md` to "Reference" in `SUMMARY.md`, after "Brushes"

## 5. Mermaid flows, checked against the code first (docs(guide))

- [ ] 5.1 Read `InteractListener` (permission and world check, `getTargetBlockExact`, the settings choice and `ExportedBrushes`, the empty and disabled checks, `paint`, `runAction`), `StrokeContextFactory`, `Brush.prepare` and `Brush.bind`, and `StrokeRunner.run`; write the node list with file and line in the pull request
- [ ] 5.2 Read `SettingsLoader`, `SettingsHolder` and the reload path; write the node list with file and line in the pull request
- [ ] 5.3 Red: add a `flowchart TD` block with the nodes of 5.1 to `how-a-brush-stroke-is-applied.md` and validate it with the Mermaid validator; fix any node the code does not perform, and correct the prose where it disagrees with the code
- [ ] 5.4 Green: the same for `how-settings-are-loaded.md` from 5.2; validate both blocks

## 6. Shotlist and placeholders (docs(guide))

- [ ] 6.1 Red: add a hint for `images/guides/paint-path-sneak.gif` to the path guide without a shotlist entry; `./gradlew checkDocsLinks` fails naming the file
- [ ] 6.2 Green: create `development/screenshot-shotlist.md` with the shared client settings at the top and one entry per planned file (file name, target page, what to show, settings); the check passes
- [ ] 6.3 Confirm every hint in the five guides names a file that the shotlist lists, and that no planned file is linked as an image

## 7. Tutorial and index (docs(guide))

- [ ] 7.1 Embed `../images/menu/menu-sphere-default.svg` at step 2 of `tutorial/paint-your-first-terrain.md`, next to the text about the top row and the palette rows
- [ ] 7.2 Link the five builder guides from the builder entry point of `docs/index.md`

## 8. Verification (docs(guide))

- [ ] 8.1 Run `./gradlew check`, including `checkDocsLinks` (builder rule, image links, hints) and `ReferenceDocsTest`
- [ ] 8.2 Run a manual grep that each of the eleven brush images and the three menu images is linked from at least one page, and record the result in the pull request (automated orphan check is a follow-up, see `generate-docs-illustrations`)

## 9. Delivery (docs(guide))

- [ ] 9.1 Mark the draft pull request titled `docs(guide): add builder guides with illustrations` ready for review once change `build(docs): generate brush and menu illustrations` is merged; the body ends with the session link
