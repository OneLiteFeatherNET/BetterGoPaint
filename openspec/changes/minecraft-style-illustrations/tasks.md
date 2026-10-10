## 1. Proposal (docs(openspec))

- [ ] 1.1 Write the proposal, design and delta spec of `docs-illustrations`; `openspec validate minecraft-style-illustrations --strict` passes

## 2. Red tests (build(docs))

- [ ] 2.1 Add failing tests in `src/docsGenTest` for: distinct base colours and real material names in the legend, tile determinism and the grass side, faded unchanged blocks and the sky tint, paint control points and a thin stroke, menu callouts on functional slots only, unknown items naming their material, and the SVG model elements (definitions, use, opacity, polyline, circle, centred text)
- [ ] 2.2 Run `./gradlew docsGenTest` and confirm the new tests fail for the stated reasons before any production code changes

## 3. Block looks and tiles (build(docs))

- [ ] 3.1 Green: `BlockLooks` with the base colours and procedural 8x8 tiles, `BlockPalette` mapping each fixture to its material name, `Terrain.cobblestone` fixture
- [ ] 3.2 Green: SVG model with definitions, `use`, opacity, polyline and circle elements, and the writer for them

## 4. Scenes (build(docs))

- [ ] 4.1 Green: grass over dirt over stone ground for every scene, sphere and gradient with stone, andesite and cobblestone, paint scene with six numbered control points and a thin size

## 5. Brush and menu pictures (build(docs))

- [ ] 5.1 Green: brush picture with faded unchanged blocks, highlight border on changed blocks, sky tint, named legend, numbered control points and path
- [ ] 5.2 Green: menu picture as a chest container with glass panes, block tiles, pixel icons, callouts on functional slots and a legend

## 6. Images (build(docs))

- [ ] 6.1 Run `./gradlew generateDocsImages`, rasterise the 14 SVGs at 800 px into the scratch folder outside the repository, inspect each picture and iterate until each is readable and looks like Minecraft blocks
- [ ] 6.2 Run `./gradlew generateDocsImages` twice and confirm `git status --porcelain docs/images` changes only with the sources

## 7. Documentation of the tooling (docs(development))

- [ ] 7.1 Update the Illustrations section of `docs/development/documentation.md`: the material colours, the computed tiles, the rule against Minecraft or third-party texture files, the legend, the callouts

## 8. Delivery

- [ ] 8.1 Run `./gradlew spotlessApply` and `./gradlew check`; both pass with no sleeps, clock reads or shared static state in the new tests
- [ ] 8.2 Push the branch and open the pull request titled `build(docs): draw illustrations with minecraft style blocks`; the body ends with the session link
