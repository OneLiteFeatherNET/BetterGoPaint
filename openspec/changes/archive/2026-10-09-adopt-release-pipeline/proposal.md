# Proposal

## Why

BetterGoPaint publishes every push to `master`/`develop` straight to Hangar and Modrinth, has no
commit conventions, no changelog automation, no dependency or artifact scanning, and its Renovate
config disables semantic commits. AntiRedstoneClock-Remastered already runs a release process
where versions, changelog and publishing follow from Conventional Commits and nothing public
happens before a security gate. BetterGoPaint should work the same way so both plugins are
maintained with one set of habits.

## What Changes

- Conventional Commits enforced on every pull request title and commit (`pr-lint` workflow,
  `commitlint.config.mjs`).
- Release Please on `main`: opens a release pull request, and merging it bumps
  `version` in `build.gradle.kts`, writes `CHANGELOG.md`, tags `vX.Y.Z` and creates the GitHub release.
- Release build → Trivy security gate on the built jar → upload to GitHub release, Hangar and
  Modrinth → CycloneDX SBOM to Dependency-Track, each as its own job. A `workflow_dispatch` path
  re-publishes an existing tag.
- Pull request build on Linux, Windows and macOS with Java 25, test result publication and an
  optional PR jar (`publish-pr` label).
- Weekly Trivy scan of the repository.
- **BREAKING** (for maintainers) Remove `publish.yml` (snapshot publishing on every push),
  release-drafter and the stale bot.
- Renovate uses the shared OneLiteFeatherNET presets.
- New issue forms (bug report with FAWE debugpaste, feature request), the organisation pull
  request template, CONTRIBUTING.md explaining commits and releases.

Out of scope: the GitHub repository settings (default branch, branch protection, secrets) — they
are listed as manual steps in tasks.md.

## Capabilities

### New Capabilities
- `release-pipeline`: How a change becomes a published, versioned and scanned release.

### Modified Capabilities

## Impact

- `.github/workflows/*`, `.github/ISSUE_TEMPLATE/*`, `.github/pull_request_template.md`,
  `release-please-config.json`, `.release-please-manifest.json`, `commitlint.config.mjs`,
  `renovate.json`, `CONTRIBUTING.md`.
- Secrets required: `HANGAR_KEY`, `MODRINTH_KEY`, plus whatever the shared
  `OneLiteFeatherNET/workflows` security and SBOM workflows inherit.
- Snapshot builds are no longer published to Hangar/Modrinth; testers use the PR jar instead.
