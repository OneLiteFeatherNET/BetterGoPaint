## 1. Store page sync

- [x] 1.1 Read `docs/store-page.md` through a provider in `build.gradle.kts` and wire it to `resourcePage` on the Hangar publication and to `syncBodyFrom` on `modrinth`
- [x] 1.2 Confirm `./gradlew build` passes without `docs/store-page.md` and that `./gradlew tasks --all` configures with it present
- [x] 1.3 Add the `Sync store page to Hangar and Modrinth` step after the publish step in `.github/workflows/release-please.yml` with `continue-on-error: true`

## 2. Delivery

- [ ] 2.1 Open the pull request titled `build(publish): sync store page to hangar and modrinth`
