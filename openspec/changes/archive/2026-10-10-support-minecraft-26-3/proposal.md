# Proposal

## Why

FastAsyncWorldEdit 2.16.1, the current release, ships adapters for Minecraft 26.1, 26.2 and 26.3.
BetterGoPaint is only useful where FAWE can edit blocks, so it should follow FAWE: a Minecraft
version is supported as soon as the current FAWE release has an adapter for it, and not before.
Minecraft 26.3 now meets that bar.

## What Changes

- Add `26.3` to `supportedMinecraftVersions` (Hangar platform versions, Modrinth game versions and
  a `run-26.3` task).
- Document the policy "follow FAWE upward only": new versions are added once the current FAWE
  release supports them; the floor stays at 26.1 / Java 25 and there is no 1.21.x backport.
- Update `README.md` and `docs/reference/supported-versions.md`.
- Keep compiling against Paper API 26.2 (stable) and `apiVersion` 26.1; see `design.md`.

Commit type and scope: `feat(platform)`. Pull request title: `feat(platform): support minecraft 26.3`.

No server owner has to act: the jar is unchanged, the new version is only declared as supported,
so this is not BREAKING.

## Out of scope

- Compiling against a Paper 26.3 beta API.
- Backporting to Minecraft versions below 26.1 or to Java versions below 25.
- Automating the version bump from FAWE releases.

## Impact

- `build.gradle.kts`, `README.md`, `docs/reference/supported-versions.md`
- Spec `platform-compatibility`
