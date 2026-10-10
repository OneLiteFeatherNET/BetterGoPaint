# platform-compatibility Specification

## Purpose
Defines the server software, Minecraft versions, Java runtime and companion plugins BetterGoPaint
supports, and how the plugin reacts when it is started in an unsuitable environment.

## Requirements

### Requirement: Supported Minecraft versions
The plugin SHALL be published for, and SHALL load on, Paper for every Minecraft version in the
supported-versions list. The list SHALL contain 26.1.1, 26.1.2, 26.2 and 26.3. A Minecraft
version SHALL be added to the list once the current FastAsyncWorldEdit release supports it, and
not before. Minecraft versions below 26.1 SHALL NOT be supported.

#### Scenario: Release lists supported versions
- **WHEN** a release is published to Hangar and Modrinth
- **THEN** the release lists exactly the Minecraft versions of the supported-versions list

#### Scenario: Plugin loads on a supported version
- **WHEN** the plugin jar is placed on a Paper 26.3 server running Java 25 with FastAsyncWorldEdit 2.16.1
- **THEN** the plugin enables without errors

#### Scenario: Version without FAWE support is not listed
- **WHEN** a Minecraft version has no adapter in the current FastAsyncWorldEdit release
- **THEN** it is not part of the supported-versions list

### Requirement: Java runtime
The plugin SHALL require Java 25 at runtime.

#### Scenario: Older Java is rejected by the server
- **WHEN** the plugin jar is loaded by a JVM older than Java 25
- **THEN** the server refuses to load the plugin classes and BetterGoPaint does not enable

### Requirement: FastAsyncWorldEdit is required
The plugin SHALL declare FastAsyncWorldEdit as a required dependency that loads before it.

#### Scenario: FAWE missing
- **WHEN** the server starts without FastAsyncWorldEdit installed
- **THEN** Paper does not enable BetterGoPaint and reports the missing dependency

### Requirement: Refuse to run next to goPaint
The plugin SHALL disable itself when the original goPaint plugin is installed at the same time.

#### Scenario: Both plugins installed
- **WHEN** goPaint and BetterGoPaint are both installed
- **THEN** BetterGoPaint logs that only one of them may be used and disables itself

### Requirement: Valid brush item
The plugin SHALL disable itself when the configured brush material is not an item.

#### Scenario: Block-only material configured
- **WHEN** `generic.default-brush` is set to a material that is not an item and the server starts
- **THEN** the plugin logs the invalid value and a link to the material list, and disables itself

### Requirement: Additional languages fall back to English
The plugin SHALL load extra languages from `plugins/BetterGoPaint/lang/` and SHALL fall back to
the bundled English messages for any key or locale not provided there.

#### Scenario: Only a German file is provided
- **WHEN** `lang/` contains only `bettergopaint_de_DE.properties` and `generic.languages` lists `de-DE`
- **THEN** the plugin enables, German clients receive German messages and all other clients receive English
