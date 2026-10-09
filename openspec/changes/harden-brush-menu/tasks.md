# Tasks

## 1. Menu identity

- [ ] 1.1 Introduce `BrushMenuHolder` and `BrushPickerHolder` and create menus through them; verify with a unit test that the listener ignores an inventory with the same title but another holder
- [ ] 1.2 Replace `§` codes in `GUI` and `Items` with components and `bettergopaint.menu.*` keys; verify `TranslationBundleTest` passes

## 2. Exported brushes

- [ ] 2.1 Add the persistent data type for `ExportedPlayerBrush` and write it on export; verify a round-trip unit test
- [ ] 2.2 Read brushes from item data in `InteractListener`, fall back to the total legacy parser and convert; verify unit tests for edited and missing lore
- [ ] 2.3 Remove the deprecated `getDisplayName`/`getLore` calls; verify `./gradlew compileJava` shows no deprecation warnings from these classes
- [ ] 2.4 Update docs/how-to/share-a-brush-as-an-item.md

## Workflow follow-up

- Archive the change after the pull request is merged.
