# release-pipeline Specification

## Purpose
Describes how changes to BetterGoPaint turn into a versioned release, which checks gate it and
where it is published.

## Requirements

### Requirement: Conventional commit messages
Every pull request title and every commit in a pull request SHALL follow Conventional Commits with
one of the types build, chore, ci, docs, feat, fix, perf, refactor, revert, style or test.

#### Scenario: Non-conventional title
- **WHEN** a pull request is opened with the title "Update stuff"
- **THEN** the pr-lint check fails and names the expected format

#### Scenario: Conventional title
- **WHEN** a pull request is opened with the title "fix: keep the gradient brush inside the mask"
- **THEN** the pr-lint title check passes

### Requirement: Version derived from commits
The next version SHALL be derived from the commits since the last release: a breaking change bumps
MAJOR, `feat` bumps MINOR, `fix` bumps PATCH, other types do not release.

#### Scenario: Feature merged
- **WHEN** the last release is 1.1.0 and a `feat:` commit is merged into main
- **THEN** the release pull request targets 1.2.0

### Requirement: Releases happen only by merging the release pull request
Pushing to main SHALL NOT publish anything. Merging the release pull request SHALL tag the version,
update the changelog and start publishing.

#### Scenario: Ordinary merge
- **WHEN** a feature pull request is merged into main
- **THEN** no artifact is uploaded to Hangar or Modrinth and the release pull request is updated

### Requirement: Security gate before publishing
The built jar SHALL be scanned before any public upload, and a HIGH or CRITICAL finding SHALL stop
the release.

#### Scenario: Vulnerable dependency shaded
- **WHEN** the release jar contains a dependency with a CRITICAL CVE
- **THEN** the publish job does not run and nothing is uploaded

### Requirement: Published artifacts
A successful release SHALL attach the shaded jar to the GitHub release and publish the same jar to
Hangar and Modrinth for every supported Minecraft version, and SHALL upload a CycloneDX SBOM.

#### Scenario: Hangar rejects the upload
- **WHEN** Hangar rejects the upload
- **THEN** the jar is still attached to the GitHub release and the tag can be re-published with workflow_dispatch

### Requirement: Pull request builds
Every pull request SHALL be built and tested on Linux, Windows and macOS.

#### Scenario: Test fails on one OS
- **WHEN** a test fails on Windows only
- **THEN** the pull request build is red

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
