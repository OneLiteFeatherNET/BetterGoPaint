# Design

## Context

Reference: AntiRedstoneClock-Remastered `injection/*Module.java` and
docs/development/dependency-injection.md.

## Decisions

### Guice over manual constructor wiring
Same library and module layout as AntiRedstoneClock, so contributors move between the projects
without relearning. *Alternative:* manual wiring in `onEnable` — fewer dependencies, but grows into
the same God-object the plugin class is today.

### Settings as provider
`Provider<Settings>` keeps `/gopaint reload` cheap: a new immutable snapshot is published and
consumers read it per use instead of caching fields.

### Relocate Guice
Guice and its transitive Guava/AOP alliance are relocated under `net.onelitefeather.bettergopaint.libs`
to avoid clashes with other plugins.

## Risks / Trade-offs

- [Jar size grows by ~1 MB] → acceptable, matches AntiRedstoneClock.
- [Big diff] → land it after the command and menu changes, module by module.
