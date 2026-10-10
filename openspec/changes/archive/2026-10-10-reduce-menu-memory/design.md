# Design

## Decisions

### Lazy menu inventory
`PlayerBrush` keeps no `Inventory` after construction. `PlayerBrush.menu()` creates the
`BrushMenuHolder` inventory on first call and `openMenu(Player)` renders it before showing it.
`getBrush` therefore only allocates settings, which is cheap inside `computeIfAbsent`.
*Alternative:* keep eager creation but move it out of `computeIfAbsent` — rejected, the memory is
still spent for players who never open the menu.

### Render cache per locale
A `MenuRenderCache` (an instance owned by the menu factory, not a static) maps `Locale` to the
rendered static components. Entries depend only on the translation store, so they stay valid until
`TranslationService` reloads, and the reload clears the cache. Dynamic parts (selected brush, size,
numbers) are still rendered per update, but through the cached static pieces. One filler
`ItemStack` per fill is shared by all slots of that inventory (Bukkit copies on `setItem`).
*Rejected alternative:* static `ItemStack` constants as tried in PR #24 (`feature/improveGUI`).
Static stacks created at class init cannot follow the per-player locale, make unit tests depend on
a bootstrapped server, and that branch also turned the filler lime by mistake.

### Dirty flag instead of eager updates
`updateInventory()` keeps its name and its roughly 27 call sites. When the owner is viewing the
menu it refreshes immediately and writes only the slots whose rendered item changed; otherwise it
sets a dirty flag and the next open renders.

### Picker once per locale
`GUI.generateBrushes` depends only on the brush list and the locale. The rendered items are cached
per locale and each open copies them into a fresh `BrushPickerHolder` inventory (an inventory must
not be shared between viewers).

### Stable head profile
`Items.createHead` uses `UUID.nameUUIDFromBytes(texture)` for the profile, so two heads with the
same texture are equal and the server does not see a new profile per build.

### Stroke point cap: refuse with a message
`PaintBrush` stores at most `MAX_STROKE_POINTS` (256) points per player. At the limit the click is
refused with a translated message and the stroke is kept; the player can still sneak to finish it.
*Alternative:* drop the oldest point — rejected, it silently changes the shape the player is
drawing and they cannot see which point vanished. A constant instead of a config key avoids a new
setting for server owners (not breaking). 256 is far above realistic hand placed strokes.

### Lazy HTTP client
`HangarReleaseSource` creates its `HttpClient` on the first `get()` and `close()` only closes it if
it exists. `UpdateService.start()` already returns before any request when checks are disabled, so
no client exists in that case. Note that `UpdateService` builds the source in its constructor
(`new HangarReleaseSource()`), which Guice runs at enable under `Stage.PRODUCTION`
(`BetterGoPaint.java`); that is why the field initializer matters.
*Alternative:* bind the source in Guice only when enabled — rejected, the service should own that
decision.

## How to verify

All tests follow F.I.R.S.T.: no sleeps, no clock reads, no shared static state, a fresh fixture per
test, one behaviour per test. The repository tests use JUnit 6, AssertJ and Mockito without a
server (see `PlayerBrushManagerTest`, `InventoryListenerTest`, `UpdateServiceTest`); MockBukkit is
not used.

- Lazy creation: inject a counting menu factory into `PlayerBrushManager`; `getBrush` leaves the
  count at 0, the first open makes it 1, the second open stays at 1.
- No re-render: inject the text renderer (a `BiFunction<Locale, Component, Component>` that wraps
  `GlobalTranslator`) into the factory as a counting spy; opening twice without changes adds no
  calls the second time.
- Skipped update: mark dirty with no viewer and assert the render count is 0; open and assert it
  renders.
- Changed slots only: use a recording `Inventory` mock and assert only the slots of the changed
  setting get `setItem`.
- Stroke cap: call `PaintBrush` 257 times with a recording `Audience` and assert 256 points and one
  limit message.
- Heads: derive the profile id through a small `Items.profileId(String)` helper and assert equal
  texture gives equal ids.
- HTTP client: `HangarReleaseSource` takes a `Supplier<HttpClient>`; with checks disabled the test
  asserts the supplier is never called.
- Quit: `PlayerBrushManagerTest` asserts `removeBrush` removes the `PlayerBrush` and the stroke;
  `ConnectListenerTest` asserts `onQuit` calls `removeBrush`.

## Decisions made during implementation

- `MenuBackend` seam: creating an `ItemStack` in a unit test fails without a server registry
  (`No RegistryAccess implementation found`). `BrushMenu` therefore turns `MenuItem` records into
  Bukkit items through `MenuBackend`; production uses `BukkitMenuBackend`, tests use a recording
  backend. Menu layout logic in `GUI` stays server-free.
- Drag guard: because renders write only changed slots, an item dragged into the brush menu or the
  picker would otherwise stay there. `InventoryListener` cancels drags into both inventories.
- Translation reload: translations are registered only at enable today, so the generation counter
  changes once; the reload invalidation is exercised by tests only.

## Risks / Trade-offs

- [Stale cache after translation reload] -> the reload clears the cache; covered by a test.
- [Shared filler stack mutated] -> the stack is never exposed; Bukkit copies on `setItem`.
- [Refusing clicks past 256 surprises a user] -> the message names the limit and how to finish.
