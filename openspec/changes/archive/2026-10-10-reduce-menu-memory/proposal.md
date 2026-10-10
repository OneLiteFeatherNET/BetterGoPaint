# Proposal

## Why

Every player who paints once gets a 54-slot brush menu built and kept until they quit, even if
they never open it. `PlayerBrush` creates its inventory in the constructor, and the constructor runs
inside `PlayerBrushManager.getBrush` (a `ConcurrentHashMap.computeIfAbsent`) on the first
right-click in `InteractListener`. Every settings change then rebuilds the whole menu: 54 new
filler panes, about 16 `Items.create` calls and about 55 `GlobalTranslator.render` calls that
re-parse MiniMessage, for a menu nobody is looking at. The brush picker is rebuilt (27 panes, 11
skull heads, a legacy serializer parse) on every open, and each head gets a random profile UUID.
The Paint Brush stores one `BlockVector3` per click without limit until the player sneaks. The
update check creates an `HttpClient` at enable even when `generic.check-for-updates` is `false`.

## What Changes

- Create the player's menu inventory lazily on first open; `getBrush` no longer builds heavy objects.
- Cache the rendered, locale dependent menu parts per locale and reuse one filler pane per fill.
- Update only the changed slots, and skip updates while nobody views the menu (mark it dirty and
  refresh on open).
- Build the brush picker once per locale; derive the skull profile UUID from the texture so equal
  heads are equal.
- Cap the number of points a player can store for an unfinished Paint Brush stroke.
- Create the Hangar `HttpClient` only when update checks are enabled.
- Add the missing test that quitting releases the player's brush and stroke state.

Not breaking: no command, permission, config key or supported version changes. The cap on stroke
points is a fixed constant, not a config key (see design.md).

Ships as `perf(gui)`; pull request title: `perf(gui): build brush menus lazily and cache rendered items`.
All items are performance or memory changes, so there is one type. The update client item touches
`service/` rather than the menu; `openspec/config.yaml` does not forbid a second scope inside one
type, so it stays in this change as its own commit
`perf(update): create the hangar http client lazily` and lands under the squash title above.

## Capabilities

### New Capabilities

### Modified Capabilities
- `brush-menu`: lazy creation, cached rendering, no rendering without a viewer, picker per locale, equal heads.
- `brush-painting`: bounded stroke points, state released on quit.
- `update-notification`: no HTTP client while checks are disabled.

## Impact

- `brush/PlayerBrush.java`, `brush/PlayerBrushManager.java`, `utils/GUI.java`, `utils/Items.java`,
  `listeners/InventoryListener.java`, `objects/brush/PaintBrush.java`,
  `service/HangarReleaseSource.java`, `service/UpdateService.java`, the `bettergopaint.properties`
  bundles (limit message), `docs/reference/brushes.md` (stroke point limit).

## Out of scope

- Brush painting CPU work, tracked as the separate change `optimize-brush-hot-path`.
- Moving brush computation off the main or region thread.
- A new menu layout, new brush settings or config keys.
- Static `ItemStack` constants (see design.md, rejected alternative).
