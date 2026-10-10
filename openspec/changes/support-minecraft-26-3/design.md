# Design

## Compile against Paper API 26.2, not 26.3

Paper publishes `paper-api` 26.3 only as beta builds (for example `26.3.build.169-beta`).
Depending on a beta API would make every build and every release depend on an artifact that can
change or disappear. The version catalog stays on `26.2.build.+`.

A jar compiled against 26.2 is expected to load on 26.3, because Paper keeps the API backwards
compatible within the 26.x line. `apiVersion` in `paper-plugin.yml` stays `26.1`, the lowest
supported line, so Paper accepts the plugin on every supported version.

Revisit when Paper publishes a stable 26.3 API: bump the catalog entry and keep the lowest
common API in mind for `apiVersion`.

## Follow FAWE upward only

The list of supported versions is driven by FAWE adapters, because a server without an adapter
cannot run the plugin's edit sessions. Versions are therefore added when the current FAWE release
supports them, never ahead of it. The floor stays at 26.1 (Paper 26.x requires Java 25); older
lines remain on BetterGoPaint 1.1.x.
