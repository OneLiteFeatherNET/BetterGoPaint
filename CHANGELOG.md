# Changelog

## [2.0.1](https://github.com/OneLiteFeatherNET/BetterGoPaint/compare/v2.0.0...v2.0.1) (2026-10-10)


### Bug Fixes

* **brush:** paint strokes reach the last clicked point ([#57](https://github.com/OneLiteFeatherNET/BetterGoPaint/issues/57)) ([2937bdc](https://github.com/OneLiteFeatherNET/BetterGoPaint/commit/2937bdc9f4653a100179b7b1fa217ef9db978a3e))
* **i18n:** tell players how to re-enable a disabled brush ([#51](https://github.com/OneLiteFeatherNET/BetterGoPaint/issues/51)) ([389925c](https://github.com/OneLiteFeatherNET/BetterGoPaint/commit/389925ceeb5945f5dfcc47d6378a804874265fee))

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

## [1.1.0](https://github.com/OneLiteFeatherNET/BetterGoPaint/compare/1.0.0...1.1.0) (2024-12-05)

### Features

* Drop 1.20 support to add 1.21 support
* Send an update notification when starting and joining
* Add official 1.20.6 support

### Bug Fixes

* Fix relocation of dependencies

### Code Refactoring

* Migrate plugin descriptor to the Paper plugin format
* Improve annotation usage
* Code cleanup and performance improvements

## [1.0.0](https://github.com/OneLiteFeatherNET/BetterGoPaint/releases/tag/1.0.0) (2024-05-13)

### Features

* Hard requirement of FastAsyncWorldEdit
* Publish to Hangar and Modrinth
* Own bStats page
* New settings system
* XMaterial support compatible with 1.18 and upwards
* Add 1.20.2 - 1.20.4 support
* Drop 1.13 - 1.16.4 support
* Change release system

### Bug Fixes

* Fix loading issue
* Fix legacy colors

### Code Refactoring

* Remove NMS code, reflection and the Mojang authlib
* Remove hard-coded XMaterial
