# Brushes

Every brush places blocks from the palette. The brush decides which positions are painted.
Settings that a brush does not use are hidden in the menu.

| Brush | Paints | Extra setting |
|---|---|---|
| Sphere Brush | Every block in a sphere around the clicked block. | — |
| Spray Brush | Random blocks in the sphere. | Chance |
| Splatter Brush | Random blocks, more likely close to the clicked block. | Falloff |
| Disc Brush | A flat disc on one axis through the clicked block. | Axis |
| Bucket Brush | Connected blocks of the same type as the clicked block. | — |
| Angle Brush | Only steep faces such as cliffs. | Angle distance, angle height difference |
| Overlay Brush | Blocks with air above them. | Thickness |
| Underlay Brush | Blocks without air above them. | Thickness |
| Fracture Brush | Cracks and fissures. | Fracture distance |
| Gradient Brush | A gradient that blends the palette blocks into each other. | Mixing, falloff |
| Paint Brush | Strokes along a curve through every point you click; sneak to finish the stroke. | Falloff |

Settings shared by all brushes: size, mask (only replace the mask block) and surface mode
(`DIRECT`, `RELATIVE`, `DISABLED`). Defaults and limits are in [Configuration](configuration.md).
