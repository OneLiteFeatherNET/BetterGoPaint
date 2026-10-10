# Tasks

## 1. Menu identity

- [x] 1.1 Introduce `BrushMenuHolder` and `BrushPickerHolder` and create menus through them; verify with `InventoryListenerTest` that the listener ignores an inventory with the same title but another holder
- [x] 1.2 Replace `§` codes in `GUI` and `Items` with components and `bettergopaint.menu.*` keys rendered for the player's locale; verify `TranslationBundleTest` passes (it now scans every key literal)

## 2. Exported brushes

- [x] 2.1 Store `ExportedPlayerBrush` as a versioned string under `bettergopaint:brush` and write it on export; verify the round trip in `ExportedBrushCodecTest`
- [x] 2.2 Read brushes from item data in `InteractListener`, fall back to the total legacy parser and convert; verify `ExportedBrushCodecTest` for edited and unreadable values
- [x] 2.3 Remove the deprecated `getDisplayName`/`getLore` calls; verify `./gradlew compileJava` shows no deprecation warnings from these classes
- [x] 2.4 Update docs/how-to/share-a-brush-as-an-item.md

## Workflow follow-up

- Archive the change after the pull request is merged.
