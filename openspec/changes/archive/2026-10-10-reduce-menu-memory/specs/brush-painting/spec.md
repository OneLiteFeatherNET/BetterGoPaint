# Spec Delta

## Purpose

Defines how brushes paint and which per-player state they keep.

## ADDED Requirements

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
