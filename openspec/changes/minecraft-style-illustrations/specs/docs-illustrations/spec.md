## ADDED Requirements

### Requirement: Blocks are drawn in their Minecraft material colours
The generator SHALL draw every block with the representative colour of its Minecraft material: grass block
top `#7CBD6B`, dirt `#866043`, stone `#7D7D7D`, andesite `#8A8B8C`, cobblestone `#6E6E6E`, gold block
`#F9D849`, sponge `#C3C04A`, and any other material the scenes or menus use in the same way. Two different
materials SHALL NOT share a base colour. The generator SHALL map each test fixture block to its real material
name and SHALL NOT colour blocks by object identity.

#### Scenario: Two materials share a colour
- **WHEN** a material is added to the palette with the base colour of another material
- **THEN** `./gradlew docsGenTest` fails and names both materials

#### Scenario: Unchanged grass
- **WHEN** `./gradlew generateDocsImages` renders the sphere brush
- **THEN** a grass block the brush did not change is drawn with the grass block tile, not with a grey fill

### Requirement: Block tiles are computed without third-party assets
Every block face and item icon SHALL be an 8x8 pixel tile that the generator computes from the material name
with a fixed seed, using shades of the material's base colour. The generator SHALL NOT download, copy, embed or
read any Minecraft texture, resource pack or other third-party texture file. The same material name SHALL give
the same tile on every run, and two materials SHALL give different tiles.

#### Scenario: Tile of the same material twice
- **WHEN** the tile of `STONE` is computed twice in one run or in two runs
- **THEN** both tiles have the same 64 pixel colours

#### Scenario: Texture files in the sources
- **WHEN** the sources of `src/docsGen` are searched for image or texture file references
- **THEN** none is found, because the tiles are computed in code

### Requirement: Unchanged blocks show their material and changed blocks stand out
Blocks the brush did not change SHALL be drawn with their material tile at reduced opacity, so builders see
the grass, dirt and stone they paint on. Blocks the brush changed SHALL be drawn at full opacity with a thin
highlight border. Air SHALL be drawn in the sky tint `#dbeafe`. The clicked block SHALL be outlined.

#### Scenario: Changed block next to unchanged block
- **WHEN** the sphere brush changes a block next to an unchanged block in the top view
- **THEN** the changed block is drawn at full opacity with a highlight border and the unchanged block is drawn faded

#### Scenario: Air
- **WHEN** a cell of the picture is air
- **THEN** the cell is filled with `#dbeafe`

### Requirement: Legend names the materials of the picture
The legend of a brush illustration SHALL list every material shown in the picture by its Minecraft name, for
example "Grass Block", "Dirt", "Gold Block", and SHALL list the markers "changed by the brush", "unchanged
(faded)", "air" and "clicked block". The paint brush legend SHALL also list the numbered control points.

#### Scenario: Sphere legend
- **WHEN** `./gradlew generateDocsImages` renders the sphere brush
- **THEN** the legend of `docs/images/brushes/sphere-brush.svg` contains the text "Grass Block"

### Requirement: Paint brush shows its control points and path
The paint brush illustration SHALL number each clicked control point in click order, starting at 1, SHALL
connect the points with the path of the stroke, and SHALL paint blocks along a thin path rather than a solid
area, so that the curve is visible.

#### Scenario: Control points
- **WHEN** `./gradlew generateDocsImages` renders the paint brush
- **THEN** `docs/images/brushes/paint-brush.svg` contains the numbers 1 to 6, one for each control point

#### Scenario: Stroke is a path
- **WHEN** `./gradlew docsGenTest` renders the paint scene
- **THEN** the painted blocks fill less than half of their bounding box in the top view; a stroke that fills more fails the test

## MODIFIED Requirements

### Requirement: Menu map matches the layout
The generator SHALL render the 54 slots of `GUI.layout()` for three brush states into `docs/images/menu/`, as a
Minecraft 6x9 chest container: a light grey panel, bevelled inset slots, glass panes as tinted translucent
tiles, block items as block tiles and other items as pixel icons defined in code. A numbered callout SHALL mark
each functional slot and SHALL NOT mark a filler pane. A legend SHALL map each callout number to the English
text of the slot's translation key from `bettergopaint.properties` and to the slot number.

#### Scenario: Slot moved
- **WHEN** a pull request moves the mask toggle from slot 6 to another slot
- **THEN** `./gradlew checkDocsImages` fails on the menu images until they are regenerated

#### Scenario: Missing translation
- **WHEN** a menu slot uses a key that `bettergopaint.properties` does not define
- **THEN** the generator fails and names the key instead of writing the raw key into the image

#### Scenario: Filler panes have no callout
- **WHEN** the menu of the sphere brush is rendered
- **THEN** the callouts are on the functional slots only, one per slot, and no callout is on a filler pane

#### Scenario: Item without an icon
- **WHEN** a menu slot holds a non-block item that has no pixel icon in the generator
- **THEN** the generator fails and names the material
