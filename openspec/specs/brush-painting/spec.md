# brush-painting Specification

## Purpose
Defines how a brush stroke reads and modifies the world. Every stroke goes through the builder's
WorldEdit session, so it is thread-safe, undoable and limited like any other WorldEdit edit.

## Requirements

### Requirement: World access only through WorldEdit
A brush stroke SHALL read and write blocks only through the WorldEdit edit session of the stroke,
and SHALL NOT call Bukkit world, block or location methods while the stroke is computed.

#### Scenario: Stroke computed in FAWE's queue
- **WHEN** a builder right-clicks with a brush and FAWE runs the stroke on a worker thread
- **THEN** no Bukkit world or block method is called on that thread

### Requirement: One stroke, one undo step
Each brush stroke SHALL be recorded in the builder's WorldEdit history as exactly one entry.

#### Scenario: Undo a stroke
- **WHEN** a builder paints one stroke and runs `//undo`
- **THEN** every block changed by that stroke is restored and nothing else

### Requirement: WorldEdit global mask is respected
A brush stroke SHALL NOT change blocks that the builder's WorldEdit global mask excludes.

#### Scenario: Global mask set
- **WHEN** a builder ran `//gmask grass_block` and paints with a sphere brush over grass and stone
- **THEN** only grass blocks are changed

### Requirement: Palette and mask behave as before
A stroke SHALL place only blocks from the palette, chosen with equal weight, and with the
BetterGoPaint mask enabled it SHALL change only blocks of the mask type.

#### Scenario: Mask enabled
- **WHEN** the palette is stone and andesite, the mask is grass block and a sphere brush hits grass and dirt
- **THEN** only grass blocks become stone or andesite

### Requirement: Brush results are independent of the thread
For the same world, settings and random seed, a brush SHALL change the same blocks regardless of
which thread computes the stroke.

#### Scenario: Deterministic test
- **WHEN** a brush runs twice against identical in-memory extents with the same seed
- **THEN** both runs change exactly the same positions to the same blocks

### Requirement: Stroke points are bounded
The Paint Brush SHALL store at most 256 points of an unfinished stroke per player and SHALL refuse
further points with a message while keeping the stored ones.

#### Scenario: Limit reached
- **WHEN** a player who already stored 256 points clicks again without sneaking
- **THEN** the point is not stored and the player receives the limit message

#### Scenario: Finish at the limit
- **WHEN** a player at the limit sneaks and clicks
- **THEN** the stroke is painted with the 256 stored points

### Requirement: Quitting releases brush state
When a player quits, the plugin SHALL remove that player's brush settings and unfinished stroke.

#### Scenario: Quit mid stroke
- **WHEN** a player with stored stroke points quits
- **THEN** neither the player brush nor the stroke points remain

### Requirement: Each world position is read at most once per stroke
A brush stroke SHALL read the block at any given position through the extent at most once while it
selects its candidates, regardless of how many filters (air, mask, surface, thickness) inspect it.

#### Scenario: Sphere with mask and surface filter
- **WHEN** a Spray brush stroke with a mask and the DIRECT surface mode runs against a counting extent
- **THEN** no position is read more than once

#### Scenario: Overlay with thickness
- **WHEN** an Overlay brush stroke with thickness 3 runs against a counting extent
- **THEN** no position is read more than once

#### Scenario: Bucket flood fill
- **WHEN** a Bucket brush stroke fills a connected region
- **THEN** each candidate position is read at most once

### Requirement: Paint brush computes its curve once per stroke
The Paint Brush SHALL derive the relative curve and its sample offsets once per stroke and SHALL
reuse them, translated, for every candidate block. Each position SHALL be placed at most once.

#### Scenario: Many candidates
- **WHEN** a Paint Brush stroke with several hundred candidate blocks runs
- **THEN** the curve is constructed exactly once

#### Scenario: Same result as per-block curves
- **WHEN** a Paint Brush stroke runs with a fixed seed
- **THEN** the set of positions on each candidate's curve equals the set the per-block construction produced

### Requirement: Height results are shared within a stroke
A stroke SHALL scan a given world column for a given height query at most once, and a brush SHALL
compute the self height of a candidate once.

#### Scenario: Fracture brush
- **WHEN** a Fracture brush stroke runs against a counting extent
- **THEN** no column scan for the same query repeats and the self height of each candidate is computed once

### Requirement: Random filters run before world reads
A brush whose candidate filter depends only on random numbers and geometry SHALL apply that filter
before any mask, surface or other world-reading filter. The probability that a candidate is
selected SHALL be unchanged; the sequence of random draws consumed MAY differ from earlier versions.

#### Scenario: Chance filter rejects first
- **WHEN** a Spray brush runs with chance 0 against a counting extent
- **THEN** no mask or surface read happens and no block changes

#### Scenario: Chance 100
- **WHEN** a Spray brush runs with chance 100
- **THEN** every candidate that passes the mask and surface checks is selected

#### Scenario: Seeded output may differ from earlier versions
- **WHEN** a Spray, Splatter or Gradient stroke runs with the same seed as in an earlier version
- **THEN** the positions MAY differ, while every selected position passes the mask and lies inside the sphere

### Requirement: Sphere selection is unchanged
Sphere selection SHALL include exactly the positions whose distance to the middle point is
strictly less than `size / 2`, SHALL exclude positions outside the build limits and, unless air is
requested, air positions.

#### Scenario: Boundary voxel
- **WHEN** a voxel lies exactly `size / 2` from the middle point
- **THEN** it is not selected

#### Scenario: Equivalence for several sizes
- **WHEN** sphere selection runs for sizes 1 through 21 around a fixed middle point
- **THEN** the selected set equals the set produced by the strict distance definition

### Requirement: Brush jitter does not use a cryptographic generator
A stroke SHALL take its random numbers from a fast seeded generator created per stroke, and
`StrokeContext` SHALL accept an injected generator so tests can fix the sequence.

#### Scenario: Injected random
- **WHEN** a test builds a stroke context with a seeded random and runs the same brush twice
- **THEN** both runs select the same positions
