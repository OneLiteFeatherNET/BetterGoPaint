# Tasks

## 1. Scheduling

- [x] 1.1 Move the update check to `Bukkit.getAsyncScheduler().runAtFixedRate`; verify `grep -rn "getScheduler()" src/main/java` finds no `BukkitScheduler` use and `UpdateServiceTest` passes

## 2. Shared state

- [ ] 2.1 Make `PlayerBrushManager` use a `ConcurrentHashMap`; verify with a unit test that concurrent `getBrush` calls for one UUID return the same instance
- [ ] 2.2 Move Paint Brush points from the static map into per-player state that is cleared on quit; verify with a unit test

## 3. Folia switch

- [ ] 3.1 Drive `foliaSupported` from the `foliaSupported` Gradle property (default false); verify the generated `paper-plugin.yml` with and without `-PfoliaSupported=true`
- [ ] 3.2 Add `run-folia-<version>` tasks that install FAWE from `-PfoliaFaweUrl`; verify the task fails with a clear message when the property is missing
- [ ] 3.3 Document the Folia status in docs/explanation/scope-and-non-goals.md and docs/reference/supported-versions.md, and how to test on Folia in docs/development/testing.md

## Manual follow-up

- Test on Folia with `-PfoliaSupported=true -PfoliaFaweUrl=<fork build>` and a game client.
- Flip the default once a stable FastAsyncWorldEdit release declares Folia support.
