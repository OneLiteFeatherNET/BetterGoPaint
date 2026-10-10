# Proposal

## Why

The store page for BetterGoPaint (`docs/store-page.md`, added by #43) is the text a visitor reads on
Hangar and Modrinth. Today it has to be pasted into both project descriptions by hand, so the two
pages drift apart and go stale after a release. The release pipeline already publishes the jar to
both platforms; it should publish the store page too, from the same file.

## What Changes

- `build.gradle.kts` reads `docs/store-page.md` lazily, at task execution, and passes it to the
  Hangar resource page (`syncAllPagesToHangar`) and to the Modrinth description (`modrinthSyncBody`).
  Configuration never reads the file, so `./gradlew build` works without it.
- `.github/workflows/release-please.yml` runs the two sync tasks in a step after the publish step,
  with `continue-on-error: true`. A rejected description never fails a release whose jar is already
  live.
- The release-pipeline spec gains a requirement for the store page sync.

Commit type and scope: `build(publish)` for the Gradle change. The workflow step ships as a separate
`ci(release)` commit in the same pull request. Pull request title: `build(publish): sync store page to hangar and modrinth`.

This change depends on #43 (`docs/store-page.md`) and must merge after it. Nothing is BREAKING: no
server owner has to act, and the jar is unchanged.

## Out of scope

- Writing or editing the store page text itself (owned by #43).
- Syncing other project metadata (name, summary, links, icon).
- Running the sync tasks in pull request builds. They need tokens and write to the public pages.

## Impact

- `build.gradle.kts`, `.github/workflows/release-please.yml`
- Spec `release-pipeline`
