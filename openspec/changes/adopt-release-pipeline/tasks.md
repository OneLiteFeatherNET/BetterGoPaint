# Tasks

## 1. Commit conventions

- [x] 1.1 Add `commitlint.config.mjs` and the `pr-lint` workflow; verify `npx commitlint --from HEAD~1` accepts a `feat:` commit
- [x] 1.2 Document the commit types and their effect on the version in CONTRIBUTING.md

## 2. Release automation

- [x] 2.1 Add `release-please-config.json`, `.release-please-manifest.json` (1.1.0) and the `x-release-please-version` marker; verify the marker is on the `version =` line of `build.gradle.kts`
- [x] 2.2 Add `release-please.yml` with build, security gate, publish and SBOM jobs; verify `./gradlew check shadowJar cyclonedxBom` produces `build/libs/BetterGoPaint-<v>.jar` and `build/reports/cyclonedx/bom.xml`
- [x] 2.3 Remove `publish.yml`, release-drafter and stale config; verify no workflow publishes on push

## 3. Pull request checks and hygiene

- [x] 3.1 Port `build-pr.yml` with Java 25 and the unshaded jar excluded from the PR artifact
- [x] 3.2 Add the weekly `security.yml` Trivy scan
- [x] 3.3 Switch `renovate.json` to the shared OneLiteFeatherNET presets and remove `.github/renovate.json`
- [x] 3.4 Replace issue templates and the pull request template; verify GitHub renders the forms on the branch

## 4. Repository settings (manual, maintainers)

- [ ] 4.1 Bring `main` up to date and make it the default branch
- [ ] 4.2 Protect `main` and require `Build Pull Request Branch`, `Conventional PR title` and `Conventional commits`
- [ ] 4.3 Verify `HANGAR_KEY`, `MODRINTH_KEY` and the Dependency-Track secrets exist
- [ ] 4.4 Confirm the Dependency-Track `parent-uuid` in `release-please.yml`

## Workflow follow-up

- Archive the change after the first release went through the new pipeline.
