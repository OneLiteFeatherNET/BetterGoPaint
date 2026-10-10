# Supported versions

| BetterGoPaint | Minecraft | Server | Java | FastAsyncWorldEdit |
|---|---|---|---|---|
| 2.x | 26.1.1, 26.1.2, 26.2, 26.3 | Paper | 25 | 2.16 or newer |
| 1.1.x | 1.21 – 1.21.3 | Paper | 21 | — |

The source of truth is `supportedMinecraftVersions` in `build.gradle.kts`; the versions listed
there are the ones offered on Hangar and Modrinth.

A Minecraft version is added once the current FastAsyncWorldEdit release has an adapter for it;
versions below 26.1 are not supported. BetterGoPaint compiles against the stable Paper 26.2 API,
which is expected to load on 26.3 as well.

Paper 26.x requires Java 25 and BetterGoPaint is compiled for it, so a jar built for 26.x cannot
load on 1.21.x. Servers that stay on 1.21 keep using BetterGoPaint 1.1.x.

Folia is not supported (yet). See [Scope and non-goals](../explanation/scope-and-non-goals.md).
