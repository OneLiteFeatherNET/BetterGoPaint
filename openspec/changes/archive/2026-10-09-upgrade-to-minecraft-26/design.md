# Design

## Context

See proposal.md for motivation. The reference for tooling is AntiRedstoneClock-Remastered, which
already builds with Gradle 9.8, Java 25 and Paper 26.1.2.

## Goals / Non-Goals

**Goals:**
- A build that works with Gradle 9 and no deprecated APIs in our own build scripts.
- One place for versions (version catalog) and one place for supported Minecraft versions.

**Non-Goals:**
- Supporting 1.21.x and 26.x from one jar.
- Refactoring plugin code beyond what the API upgrade forces.

## Decisions

### Compile against Paper 26.2, not 26.3
26.2 has stable builds; 26.3 is published only as `-beta`. Hangar rejects unknown platform versions
and server owners expect stable support. 26.3 is added once Paper marks it stable.
*Alternative:* 26.3 beta — rejected, the API may still change.

### Java 25 bytecode, 26.x only
Paper API 26.x is compiled for Java 25, so the plugin cannot be compiled with `--release 21` against
it. A multi-version jar would need a separate 1.21 source set. 1.21 users keep 1.1.x.
*Alternative:* keep 1.21 by compiling against 1.21.11 — rejected, it blocks use of current API and
keeps two API lines alive.

### FAWE version pinned directly
`com.intellectualsites.bom:bom-newest` 1.56 still resolves FAWE 2.15.0, which predates 26.x
support. FAWE 2.16.1 is referenced directly; `FastAsyncWorldEdit-Bukkit` is non-transitive so it
does not drag an old Paper API onto the classpath.

### Remove `build-logic`
It only wrapped `git` calls through `Project.exec` (removed in Gradle 9) for the old snapshot
publishing. Versioning moves to Release Please (separate change), so nothing needs git at
configuration time.

### `MiniMessageTranslationStore`
Adventure 5 removed `TranslationRegistry` and `UTF8ResourceBundleControl`. The store formats
MiniMessage directly and understands `<arg:n>`, which the bundles already use, so the bundles stay
unchanged. Java 9+ reads `.properties` bundles as UTF-8 by default.

### Drop ServerLib and paperlib
ServerLib only warned on unsafe forks of old versions; paperlib's async helpers are redundant on
Paper-only plugins. Both added shading and relocation for no benefit.

## Risks / Trade-offs

- [1.21 servers lose updates] → 1.1.x stays downloadable; README and supported-versions page say so.
- [Shading cloud without relocation] → same as AntiRedstoneClock; a later change can relocate.
- [Modrinth project still lists old loaders on old versions] → only new versions change.

## Migration Plan

1. Server owner updates to Java 25 and Paper 26.1.1+, installs FAWE 2.16.1+.
2. Replace the jar; `config.yml` keys are unchanged.
3. Rollback: put the 1.1.0 jar back on a 1.21 server.
