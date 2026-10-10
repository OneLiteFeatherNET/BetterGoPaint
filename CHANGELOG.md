# Changelog

## [2.0.0](https://github.com/OneLiteFeatherNET/BetterGoPaint/compare/1.1.0...v2.0.0) (2026-10-10)


### ⚠ BREAKING CHANGES

* requires Paper 26.1.1 or newer and Java 25. Servers on 1.21 stay on 1.1.x.

### Features

* modernise BetterGoPaint for Minecraft 26 and AntiRedstoneClock quality ([#34](https://github.com/OneLiteFeatherNET/BetterGoPaint/issues/34)) ([93bc6ad](https://github.com/OneLiteFeatherNET/BetterGoPaint/commit/93bc6ad44868390413f7f33429c30dec137d844a))
* **platform:** support minecraft 26.3 ([#37](https://github.com/OneLiteFeatherNET/BetterGoPaint/issues/37)) ([2eb99cf](https://github.com/OneLiteFeatherNET/BetterGoPaint/commit/2eb99cf55bf4c7cffbeba8f42cc3882c7cf8a68b))


### Performance Improvements

* **brush:** cut per-block work in the paint hot path ([#39](https://github.com/OneLiteFeatherNET/BetterGoPaint/issues/39)) ([ce62b41](https://github.com/OneLiteFeatherNET/BetterGoPaint/commit/ce62b4120ac4994af6bab1a04a2a52ee89232b8d))
* **brush:** drop the shared securerandom from exported brushes ([#41](https://github.com/OneLiteFeatherNET/BetterGoPaint/issues/41)) ([135ab0c](https://github.com/OneLiteFeatherNET/BetterGoPaint/commit/135ab0cc51d0b8e450e26fab4a09871733159969))
* **gui:** build brush menus lazily and cache rendered items ([#38](https://github.com/OneLiteFeatherNET/BetterGoPaint/issues/38)) ([0fce23b](https://github.com/OneLiteFeatherNET/BetterGoPaint/commit/0fce23ba10f6194adbb46252775d01fcbdeac759))


### Code Refactoring

* **gui:** split menu layout into sections ([#42](https://github.com/OneLiteFeatherNET/BetterGoPaint/issues/42)) ([f5f45c1](https://github.com/OneLiteFeatherNET/BetterGoPaint/commit/f5f45c17b8d0cbed484b9cfdc74f818f4993fc6e))

## [Unreleased]

### Added
- Hard requirement of FastAsyncWorldEdit
- Modrinth support
- Hangar support

### Changed
- XMaterial support now compatible with 1.18 upwards
- Settings system
- [Own BStats page](https://bstats.org/plugin/bukkit/BetterGoPaint/18734)
- Build gradle - Improved many thing
- Remove reflection code

### Deprecated

### Removed
- NMS Code
- Hard Coded XMaterial
- Authlib from Mojang
### Fixed

### Security
