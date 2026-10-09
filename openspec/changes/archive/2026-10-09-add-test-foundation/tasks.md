# Tasks

## 1. Test infrastructure

- [x] 1.1 Add JUnit 6, AssertJ, Mockito, Paper API and FAWE to the test classpath; verify `./gradlew test` runs
- [x] 1.2 Enable JaCoCo XML and HTML reports; verify `build/reports/jacoco/test/jacocoTestReport.xml` exists after `./gradlew test`
- [x] 1.3 Restrict Spotless to `src/**/*.java`; verify `./gradlew spotlessCheck` passes

## 2. First tests

- [x] 2.1 Add `PlayerBrushManagerTest` for cycling, wrap-around, lookup by name and lore highlighting; verify 6 tests pass
- [x] 2.2 Add `BezierSplineTest` for straight lines, end knots, flat curves and `getT`; verify 4 tests pass
- [x] 2.3 Add `TranslationBundleTest` for key completeness and MiniMessage parsing; verify it fails when a key is removed from `bettergopaint.properties`
- [x] 2.4 Write docs/development/testing.md; verify the commands in it run as written

## Workflow follow-up

- Archive the change after the pull request is merged.
