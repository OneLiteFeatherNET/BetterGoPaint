# Design

## Decision 1: five guides, chosen by settings the menu really has

Every guide covers one menu feature whose slot, limit or behaviour is visible in code. Each claim was
checked against the source before it went into the guide (task 1.1 records the file and line for each):

| Guide | Feature | Code it rests on |
|---|---|---|
| Paint a path | Paint brush, sneak to finish, 256-point limit | `PaintBrush.MAX_STROKE_POINTS`, `PaintBrush.prepare` |
| Shape cliffs | Angle brush, slots 3 and 4 | `GUI.angleSection`, `AngleBrush.apply` |
| Paint only on one block | Mask toggle (slot 6) and mask block (slot 52) | `GUI.maskToggleSection`, `GUI.maskBlockSection`, `Brush.masked` |
| Mix a block palette | Palette slots 46 to 50, equal chance per block | `GUI.paletteSection` (`100 / size`) |
| Blend a gradient | Gradient brush, falloff (slot 3) and mixing (slot 4) | `GUI.falloffSection`, `GUI.mixingSection`, `GradientBrush` |

Undo and redo were considered and left out. `paint-your-first-terrain` already shows `//undo`, and the
explanation page already states that one stroke is one history entry. A separate guide would repeat them.

## Decision 2: the Diátaxis how-to shape

Each guide solves one task, as `docs/development/documentation.md` asks. The structure is the same in
every guide: a one-sentence goal, what you need, numbered steps in the imperative, what you should see,
and related pages. No guide explains why a setting exists; that goes to the reference page, and each
guide links to it. Each guide embeds at least one generated image (shape or menu map), so the builder
rule of `docs/` checks passes by image, and the planned screenshot is a hint block (decision 4).

## Decision 3: image placement

| Page | Generated image |
|---|---|
| `reference/brushes.md` | all eleven `brushes/*.svg`, one per brush under a `### <name>` heading |
| Paint a path | `brushes/paint-brush.svg` |
| Shape cliffs | `brushes/angle-brush.svg`, `menu/menu-angle.svg` |
| Paint only on one block | `menu/menu-spray-disabled.svg` |
| Mix a block palette | `menu/menu-sphere-default.svg` |
| Blend a gradient | `brushes/gradient-brush.svg` |
| `tutorial/paint-your-first-terrain.md` | `menu/menu-sphere-default.svg` at step 2 |
| `reference/brush-menu.md` | the three `menu/*.svg` files, one per state |

Links are relative (`../images/brushes/paint-brush.svg`), so `checkDocsLinks` resolves them on disk and
GitBook resolves them in the same synced scope.

The brush shapes embedded in `reference/brushes.md` also satisfy "every brush in the brushes reference
has an illustration", because the reference page is the only place the brush list is written out.

## Decision 4: placeholders for planned screenshots

A planned screenshot appears in a guide as a GitBook hint that names the file, never as an image link:

```
{% hint style="info" %}
Planned screenshot: `images/guides/paint-path-sneak.gif`, see the shotlist.
{% endhint %}
```

Before the file exists a link would render as a broken image. A hint renders as text, and the file name is
the same one the shotlist assigns, so the two are checked against each other by `checkDocsLinks` rule 3
(change `generate-docs-illustrations`). When the team commits the file, the hint is replaced by an image
link in the same pull request.

## Decision 5: the shotlist is a development page

`docs/development/screenshot-shotlist.md` is written for the team, not for readers, so it stays out of
`SUMMARY.md`. `ReferenceDocsTest.everyPageIsInTheNavigation` already skips `development/`, and the
shotlist does not need a navigation entry to be checked. Each entry lists: the file name under
`docs/images/guides/` or `docs/images/tutorial/`, the target page, what the frame must show, and the client
settings. The shared client settings are written once at the top of the page:

- resolution 1920 by 1080, windowed, GUI scale 2, HUD visible;
- resource pack none (vanilla), English language;
- midday (`/time set 6000`), clear weather, no other players in frame;
- FastAsyncWorldEdit and BetterGoPaint at the version of the release being documented;
- GIFs at most 1280 pixels wide, 10 frames per second, at most 5 MB; PNGs lossless.

## Decision 6: Mermaid flows are checked against the code before they are drawn

The two explanation pages get a `flowchart TD` each. Every node names the class or setting it stands
for, and every edge is one the code takes. Tasks 5.1 to 5.3 read the code first:

- stroke application: `InteractListener` (the permission and world check, `getTargetBlockExact(250, ...)`,
  the settings choice between the player brush and the item lore through `ExportedBrushes`, the empty
  palette and disabled checks, `paint(...)`), `StrokeContextFactory` (the captured context),
  `Brush.prepare` (the Paint brush collects points unless sneaking), `Brush.bind`, and the
  `actor.runAction(... StrokeRunner.run ...)` call in `InteractListener` (line 155 at the time of writing);
- settings loading: `SettingsLoader` (key by key, defaults for invalid values, the write-back of the
  file), `SettingsHolder` (the single snapshot swap), and the reload command path.

The explanation pages already describe these flows in prose, so any difference between prose and code is
a finding. Where the code and the prose disagree, the task records it and the prose is corrected in the
same pull request. The diagrams are validated with the Mermaid validator before they are committed.

## Decision 7: the brush menu reference is a hand-written table over generated images

`reference/brush-menu.md` describes the sections of the menu (brush toggle and type, size, mask, surface,
palette, mask block) with their slot numbers, taken from the section comments in `GUI.java`. The three
generated menu images show the slots as they are. Residual risk: the table is written by hand, so it can
drift from `GUI.java` without a failing check. The mitigation is the review note in the reference page
and the images, which are checked by `checkDocsImages`.

## Decision 8: navigation

`SUMMARY.md` gets the five guides under "For builders", in the order of the table in Decision 1, and
`reference/brush-menu.md` under "Reference" after "Brushes". `docs/index.md` links the builder guides from
its builder entry point. `ReferenceDocsTest.everyPageIsInTheNavigation` fails for any new user page that
is missing from `SUMMARY.md`.

## Alternatives considered

- **Put the guides in the tutorial.** Rejected: a tutorial teaches one path, while a how-to solves one
  task; the builder would have to read the whole tutorial first.
- **Real screenshots committed now.** Rejected: they cannot be produced in this change without a server,
  and the team captures them later from the shotlist.
- **Draw.io for the flows.** Rejected: its export is an image that goes stale, while Mermaid is text, is
  reviewed in the diff and renders in GitBook.
- **Image links to planned files.** Rejected: they render as broken images until the file exists
  (decision 4).
- **A shotlist in `SUMMARY.md`.** Rejected: readers would see team notes in the published space.
