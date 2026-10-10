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
