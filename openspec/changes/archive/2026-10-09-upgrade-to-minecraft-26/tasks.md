# Tasks

## 1. Build tooling

- [x] 1.1 Update the Gradle wrapper to 9.8.0 and verify `./gradlew --version` reports 9.8.0
- [x] 1.2 Move all versions into the version catalog in `settings.gradle.kts` and repositories into `dependencyResolutionManagement`; verify `./gradlew help` configures without warnings from our scripts
- [x] 1.3 Replace shadow, plugin-yml, run-paper, hangar and spotless plugins with their current coordinates; verify `./gradlew build` succeeds
- [x] 1.4 Remove `build-logic/`, `publishdata` and EldoNexus publishing; verify `./gradlew tasks` no longer lists `publish`

## 2. Platform upgrade

- [x] 2.1 Compile against Paper API 26.2 with Java 25 toolchain; verify `./gradlew compileJava` succeeds
- [x] 2.2 Reference FastAsyncWorldEdit 2.16.1 directly; verify the dependency report shows FAWE 2.16.1
- [x] 2.3 Set `api-version` to 26.1 and list 26.1.1, 26.1.2, 26.2 as supported versions; verify the generated `paper-plugin.yml` in the jar
- [x] 2.4 Drop ServerLib and paperlib; verify the shaded jar contains no `org/incendo/serverlib` or `io/papermc/lib`
- [x] 2.5 Update docs/reference/supported-versions.md and README requirements; verify they match `supportedMinecraftVersions`

## 3. Translations

- [x] 3.1 Replace `PluginTranslationRegistry` with `MiniMessageTranslationStore`; verify `TranslationBundleTest` passes
- [x] 3.2 Load `lang/` bundles with the plugin class loader as parent so missing locales fall back to English; verified on `run-26.2` with only a `de_DE` file in `lang/` and `languages: [de-DE]`

## 4. Verification

- [x] 4.2 Start `./gradlew run-26.1.2` and verify the plugin enables

## Manual follow-up (needs a Minecraft client)

Not tracked: the session that implemented this change had no game client. Server start, commands and config loading were verified on run-26.2 and run-26.1.2.

- Start `./gradlew run-26.2`, paint with every brush and `//undo` once; verify no errors in the console

## Workflow follow-up

- Archive the change after the pull request is merged.
