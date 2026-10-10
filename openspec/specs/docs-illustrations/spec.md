# docs-illustrations Specification

## Purpose
TBD - created by archiving change generate-docs-illustrations. Update Purpose after archive.

## Requirements

### Requirement: Every brush has an illustration
The generator SHALL render one SVG per brush in the brush registry of `PlayerBrushManager` into
`docs/images/brushes/`, each with a top view, a side cross-section through the target and a legend.

#### Scenario: Brush without a scene
- **WHEN** a brush is added to the registry and has no scene in the generator
- **THEN** `./gradlew docsGenTest` fails and names the brush

#### Scenario: Brush images exist
- **WHEN** `./gradlew generateDocsImages` runs
- **THEN** `docs/images/brushes/` contains exactly one `.svg` file per registered brush, named after the brush

### Requirement: Brush scenes run the real brush code
A brush illustration SHALL be produced by calling the brush's own `apply` on the in-memory test world,
with the fixed seed of the scene. The generator SHALL NOT contain its own copy of any brush algorithm.

#### Scenario: Brush behaviour changes
- **WHEN** a change alters which positions the sphere brush paints
- **THEN** the next `./gradlew generateDocsImages` changes `docs/images/brushes/sphere-brush.svg` and `./gradlew checkDocsImages` fails until the file is committed

### Requirement: Menu map matches the layout
The generator SHALL render the 54 slots of `GUI.layout()` for three brush states into
`docs/images/menu/`, each cell labelled with the English text of the slot's translation key from
`bettergopaint.properties`.

#### Scenario: Slot moved
- **WHEN** a pull request moves the mask toggle from slot 6 to another slot
- **THEN** `./gradlew checkDocsImages` fails on the menu images until they are regenerated

#### Scenario: Missing translation
- **WHEN** a menu slot uses a key that `bettergopaint.properties` does not define
- **THEN** the generator fails and names the key instead of writing the raw key into the image

### Requirement: Generation is deterministic
Two runs of the generator on the same sources SHALL produce byte-identical files, on every operating
system the build runs on. The output SHALL contain no timestamp, no generator version and no platform
line ending.

#### Scenario: Two runs
- **WHEN** `./gradlew generateDocsImages` runs twice without source changes
- **THEN** `cmp` reports no difference for any file in `docs/images/` and `git status` shows no change

#### Scenario: Windows checkout
- **WHEN** the repository is checked out on Windows with `core.autocrlf` enabled
- **THEN** the committed SVG files keep LF endings, because `.gitattributes` sets `eol=lf` for `docs/images/**/*.svg`

### Requirement: Generator needs no server, network or real clock
The generator and checker SHALL run without a Paper server, without network access and without reading
the system clock. Their sources SHALL NOT reference `Instant.now`, `System.currentTimeMillis`,
`System.nanoTime` or `LocalDate.now`.

#### Scenario: Run without network
- **WHEN** `./gradlew generateDocsImages` runs with the network disabled and the dependencies cached
- **THEN** it finishes and writes the same files as a run with the network enabled

#### Scenario: Clock reads in the generator
- **WHEN** the sources of `src/docsGen` are searched for the clock calls above
- **THEN** none is found

### Requirement: Generator unit tests write only to temporary folders
The generator and checker SHALL be covered by unit tests in source set `docsGenTest`. The tests SHALL
NOT sleep, SHALL NOT read the clock, and SHALL write files only below a `@TempDir`.

#### Scenario: SVG writer on an in-memory model
- **WHEN** `./gradlew docsGenTest` runs
- **THEN** the SVG writer is tested without touching the file system, and any file the tests write is under a temporary folder
