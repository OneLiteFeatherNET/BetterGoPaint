## 1. Lazy menu (M1)

- [x] 1.1 Red: test that `getBrush` creates no menu inventory (counting menu factory) and that two opens create one
- [x] 1.2 Green: create the inventory on first open in `PlayerBrush`, remove `GUI.create(this)` from the constructor

## 2. Cheaper updates (M2)

- [x] 2.1 Red: tests that opening twice renders no translation again, a change while closed renders nothing, a change while open writes only the changed slots, and a translation reload re-renders
- [x] 2.2 Green: add the per-locale render cache, one filler pane per fill, slot diff and dirty flag behind `updateInventory()`

## 3. Bounded stroke (M3)

- [x] 3.1 Red: test that point 257 is refused with a message and that sneaking at the limit paints 256 points
- [x] 3.2 Green: add `MAX_STROKE_POINTS` and the refusal message to `PaintBrush` and the bundles; update `docs/reference/brushes.md`

## 4. Picker and heads (M4)

- [x] 4.1 Red: tests that the picker is built once per locale with a separate inventory per viewer and that equal textures give equal heads
- [x] 4.2 Green: cache the picker per locale and derive the profile UUID with `UUID.nameUUIDFromBytes`

## 5. Lazy HTTP client (M5)

- [x] 5.1 Red: test that with checks disabled the client supplier is never called
- [x] 5.2 Green: create the client on first `get()` in `HangarReleaseSource`, close only if created; commit as `perf(update)`

## 6. Quit releases state

- [ ] 6.1 Add tests in `PlayerBrushManagerTest` and `ConnectListenerTest` that quitting removes the `PlayerBrush` and the stroke

## 7. Delivery

- [ ] 7.1 Run `./gradlew check` and confirm no sleeps, clock reads or shared static state in the new tests
- [ ] 7.2 Open the pull request titled `perf(gui): build brush menus lazily and cache rendered items`
