## 1. Build scaffolding (build(docs))

- [x] 1.1 Red: add `src/docsGenTest/java/net/onelitefeather/bettergopaint/docsgen/SvgWriterTest.java` that fails to compile against the missing `SvgWriter` (no test body passes yet)
- [x] 1.2 Green: add source sets `docsGen` and `docsGenTest` extending the test dependencies, tasks `docsGenTest`, `generateDocsImages`, `checkDocsImages`, `checkDocsLinks` in group `documentation`, `check` depends on the three checks; add `.gitattributes` with `docs/images/**/*.svg text eol=lf`

## 2. Deterministic SVG writer (build(docs))

- [x] 2.1 Red: tests that the writer escapes `< > & " '`, writes the same model to the same string twice, uses only `\n` and integer coordinates, and writes nothing to disk
- [x] 2.2 Green: implement `SvgWriter` on an in-memory `SvgDocument` model with `Locale.ROOT` number formatting and UTF-8 output

## 3. Brush illustrations (build(docs))

- [x] 3.1 Red: tests that every brush in `PlayerBrushManager`'s registry has a scene (names the missing one), that the sphere scene places at least one stone block and that two renders of the same scene are equal
- [x] 3.2 Green: implement `BrushScenes` (terrain, `StrokeBuilder` settings, seed 1 per the design table) and `BrushIllustration` (top view, cross-section, legend); the generate mode writes `docs/images/brushes/*.svg`; run `./gradlew generateDocsImages` and commit the eleven files

## 4. Menu map (build(docs))

- [x] 4.1 Red: tests that the menu map has 54 cells, that slot 52 shows the mask block, that the mask toggle label resolves to its English text, and that an unknown key fails with its name
- [x] 4.2 Green: implement `MenuMap` over `GUI.layout()` for the three states of the design and the English resolution from `bettergopaint.properties`; write `docs/images/menu/*.svg`; commit the three files

## 5. Stale check (build(docs))

- [ ] 5.1 Red: tests in `@TempDir` that a missing, a differing and an extra file each make `check` mode return a failure that names the file, and that a matching folder passes
- [ ] 5.2 Green: implement check mode in the generator entry point and make `checkDocsImages` run it; confirm `./gradlew checkDocsImages` passes on the committed files and fails after a one-byte edit (then revert the edit)

## 6. Link and asset checker (build(docs))

- [ ] 6.1 Red: tests in `@TempDir` for a broken relative link, a broken image, an anchor (ignored), an `https` link (not fetched), a "For builders" page without image or shotlist reference, and a hint file missing from the shotlist
- [ ] 6.2 Green: implement `DocsLinkChecker` and wire `checkDocsLinks`; run it on `main`'s docs; if it reports existing violations, fix only the link targets in this change or stop for a decision and record the list in the pull request

## 7. Determinism and offline run (build(docs))

- [ ] 7.1 Run `./gradlew generateDocsImages` twice, then `git status --porcelain docs/images` must be empty and `cmp` must report no difference
- [ ] 7.2 Run `./gradlew generateDocsImages --offline` with the network disabled (for example `unshare -rn`) and confirm the output is unchanged
- [ ] 7.3 Search `src/docsGen` for `Instant.now`, `System.currentTimeMillis`, `System.nanoTime` and `LocalDate.now`; none may be found

## 8. Documentation of the tooling (build(docs))

- [ ] 8.1 Update `docs/development/documentation.md` with the regeneration and check commands and the list of pages that embed each generated file (no user page is written in this change)

## 9. Delivery

- [ ] 9.1 Run `./gradlew check` and confirm no sleeps, clock reads or shared static state in the new tests, and that the spotless header check passes for the new sources
- [ ] 9.2 Mark the draft pull request titled `build(docs): generate brush and menu illustrations` ready for review, with the body ending in the session link
