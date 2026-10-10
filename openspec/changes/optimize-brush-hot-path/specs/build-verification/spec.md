# Spec Delta

## ADDED Requirements

### Requirement: Hot-path behaviour is verified without timing
The build SHALL verify the brush hot-path rules (read counts, curve computed once, sphere
equivalence, random filter order) with unit tests that count calls and compare selections. These
tests SHALL NOT sleep, read the system clock or assert elapsed time, and SHALL use fixed seeds and
a fresh extent per test.

#### Scenario: Extra world read introduced
- **WHEN** a change makes a stroke read one position twice
- **THEN** `./gradlew test` fails and names the position-count test

#### Scenario: Timing is never asserted
- **WHEN** the hot-path tests are searched for `Thread.sleep`, `System.nanoTime` and `Instant.now`
- **THEN** none is found
