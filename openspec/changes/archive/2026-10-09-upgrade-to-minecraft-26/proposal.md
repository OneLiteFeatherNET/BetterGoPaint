# Proposal

## Why

BetterGoPaint 1.1.0 targets Paper 1.21.3 and is built with Gradle 8.7, a SNAPSHOT command
framework and a local `build-logic` that calls `Project.exec`, which Gradle 9 removed. The
newest stable Paper line is 26.2 (26.3 is in beta), FastAsyncWorldEdit 2.16.1 supports up to 26.3,
and Paper 26.x requires Java 25. Without this upgrade the plugin cannot be built with current
tooling and cannot be offered for current server versions.

## What Changes

- **BREAKING** Compile against Paper API 26.2 and Java 25; offer the plugin for Minecraft 26.1.1,
  26.1.2 and 26.2. Servers on 1.21.x stay on BetterGoPaint 1.1.x.
- **BREAKING** `api-version` becomes `26.1`. Modrinth no longer lists `bukkit` and `folia` loaders,
  because the plugin ships a `paper-plugin.yml` and has never supported Folia.
- Gradle 9.8 wrapper; dependency versions in a single version catalog in `settings.gradle.kts`;
  repositories declared once in `dependencyResolutionManagement`.
- Replace abandoned or renamed Gradle plugins: `com.gradleup.shadow` 9.6.1,
  `de.eldoria.plugin-yml.paper` 0.9.0, `xyz.jpenilla.run-paper` 3.1.0, Hangar publish 0.1.4,
  Spotless 8.10.4.
- FastAsyncWorldEdit 2.16.1 referenced directly instead of the lagging `bom-newest` (FAWE 2.15.0).
- Cloud command framework from `2.0.0-SNAPSHOT` to the released 2.0.1 / 2.1.0.
- Drop `ServerLib`, `paperlib`, `publishdata`, EldoNexus Maven publishing and the `build-logic`
  included build; nothing in the plugin consumes them on Paper 26.
- Migrate translations from the removed Adventure `TranslationRegistry` to
  `MiniMessageTranslationStore`. Language files in `plugins/BetterGoPaint/lang/` fall back to the
  bundled English file instead of failing.
- `run-<version>` tasks start Paper with FastAsyncWorldEdit preinstalled.

Out of scope: Folia support, Minecraft 26.3 (added once Paper publishes a stable build),
behaviour changes to brushes, commands or the GUI.

## Capabilities

### New Capabilities
- `platform-compatibility`: Which servers, Java versions and companion plugins BetterGoPaint runs
  on, and how it behaves when the environment is unsuitable.

### Modified Capabilities

## Impact

- `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, Gradle wrapper, removal of
  `build-logic/`.
- `BetterGoPaint.java` (translations, ServerLib removal), removal of `PluginTranslationRegistry`.
- Server owners must run Java 25 and Paper 26.1.1 or newer.
- Published artifacts on Hangar and Modrinth list only 26.x versions.
