## MODIFIED Requirements

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
