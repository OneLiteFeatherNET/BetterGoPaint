## ADDED Requirements

### Requirement: Builders have how-to guides for the brush menu
The "For builders" section of `docs/SUMMARY.md` SHALL list at least five how-to guides besides
"Share a brush as an item": the Paint brush path, the Angle brush cliffs, the mask, the block palette and
the gradient.

#### Scenario: Builder section after this change
- **WHEN** the navigation of `docs/SUMMARY.md` is read
- **THEN** the "For builders" section lists the six how-to guides

### Requirement: Each builder guide solves one task with real limits
Each builder guide SHALL describe one task, in numbered steps, and every slot number, limit and
behaviour it states SHALL match the code it cites in `design.md`.

#### Scenario: Paint path limit
- **WHEN** the path guide states the stroke limit
- **THEN** it says 256 points, the value of `PaintBrush.MAX_STROKE_POINTS`

#### Scenario: Palette limit
- **WHEN** the palette guide states how many blocks a palette holds
- **THEN** it says five, the number of palette slots in `GUI.paletteSection`

### Requirement: Each builder guide shows the product
Each builder guide SHALL embed at least one generated image from `docs/images/` or refer to a planned
screenshot in `development/screenshot-shotlist.md`.

#### Scenario: Guide without picture
- **WHEN** a builder guide has no image link and no shotlist reference
- **THEN** `./gradlew check` fails in `checkDocsLinks`

### Requirement: Brush reference shows every shape
`docs/reference/brushes.md` SHALL embed the generated shape of each of the eleven brushes.

#### Scenario: Brush added without a shape
- **WHEN** a brush is added to the reference table without its image
- **THEN** the pull request is incomplete and review rejects it

### Requirement: Brush menu reference page
`docs/reference/brush-menu.md` SHALL describe every section of the brush menu with its slot numbers and
embed the three generated menu images.

#### Scenario: Menu reference page
- **WHEN** a reader opens the brush menu reference
- **THEN** each section of the menu has its slot numbers and the three menu images are shown
