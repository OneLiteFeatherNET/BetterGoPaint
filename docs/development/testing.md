# Testing

`./gradlew build` compiles, runs the unit tests, checks the license headers and builds the
shaded jar. `./gradlew test` runs only the tests; the JaCoCo report lands in
`build/reports/jacoco/test/html`.

## What is tested

- `PlayerBrushManagerTest` – brush cycling and lookup by name.
- `BezierSplineTest` – the curve the Paint Brush follows.
- `TranslationBundleTest` – every translation key used in the code exists in every bundle and
  parses as MiniMessage.

Tests use JUnit 6, AssertJ and Mockito. Bukkit types such as `World` are mocked; there is no
running server. Code that needs a server (brush painting, GUI, listeners) is covered by manual
testing with `./gradlew run-<version>`, which starts Paper with FastAsyncWorldEdit preinstalled.

## Adding tests

Put tests next to the package they cover under `src/test/java`. `./gradlew spotlessApply` adds the
license header.
