# Screenshot shotlist

Team page. It lists every screenshot or GIF that a builder guide refers to through a hint block, before the
file exists. `checkDocsLinks` fails when a hint names a file that is not listed here. Captured files go to
`docs/images/screenshots/` under the exact name below, and the hint in the guide is then replaced by an image
link in the same pull request.

## Client settings for every capture

- Resolution 1920 by 1080, windowed, GUI scale 2, HUD visible.
- Resource pack none (vanilla), English language.
- Midday (`/time set 6000`), clear weather, no other players in frame.
- FastAsyncWorldEdit and BetterGoPaint at the version of the release being documented.
- GIFs at most 1280 pixels wide, 10 frames per second, at most 5 MB. PNGs lossless.
- Brush settings as the guide says before the capture. Start from a new player, so the defaults apply.

## Entries

| File | Target page | What the frame must show | Settings |
|---|---|---|---|
| `images/screenshots/paint-path-sneak.gif` | `how-to/paint-a-path-with-the-paint-brush.md` | Four clicks along a path with the point messages in chat, then one sneak click and the painted curve. | Paint Brush selected, mask disabled, palette of one block. |
| `images/screenshots/angle-cliff-before-after.png` | `how-to/shape-cliffs-with-the-angle-brush.md` | A cliff before the stroke and the same cliff after it, from the same spot. | Angle Brush selected, check distance 2, Maximum Angle 40.0, size 10. |
| `images/screenshots/mask-only-stone.png` | `how-to/paint-only-on-one-block-with-a-mask.md` | A patch of stone, grass and dirt after one stroke: only the stone changed. Brush menu open with the mask enabled and stone as mask block. | Sphere Brush, mask enabled, mask block stone. |
| `images/screenshots/palette-five-blocks.png` | `how-to/mix-a-block-palette.md` | The brush menu with five palette blocks in the bottom row and their percentages (20% each). | Sphere Brush, five different solid blocks in the palette. |
| `images/screenshots/gradient-mixing.png` | `how-to/blend-a-gradient.md` | A gradient sphere cut open, with the palette blocks as bands from bottom to top. Two frames side by side: mixing 0% and mixing 100%. | Gradient Brush, falloff 50, three palette blocks. |
