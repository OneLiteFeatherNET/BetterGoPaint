## ADDED Requirements

### Requirement: Store page sync on release
A successful release SHALL sync `docs/store-page.md` as the project description on Hangar and on
Modrinth. The file SHALL be read when the sync runs, not when the build is configured. A failed sync
SHALL NOT fail the release: the jar and the GitHub release stay published.

#### Scenario: Release with a store page
- **WHEN** a release is published and `docs/store-page.md` exists
- **THEN** the Hangar resource page and the Modrinth description both carry the text of that file

#### Scenario: Store page sync rejected
- **WHEN** Hangar or Modrinth rejects the description sync after the jar was published
- **THEN** the release run stays successful, the failed sync step is reported, and the jar and SBOM are unaffected

#### Scenario: Build without the store page
- **WHEN** `./gradlew build` runs in a checkout where `docs/store-page.md` does not exist
- **THEN** the build succeeds, because the file is only read by the sync tasks
