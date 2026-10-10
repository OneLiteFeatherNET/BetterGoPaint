# Dependency injection

BetterGoPaint is wired with [Guice](https://github.com/google/guice), like
AntiRedstoneClock-Remastered. `BetterGoPaint#onEnable` builds the injector and only decides the
order in which things start; every other class gets what it needs through its `@Inject`
constructor (`jakarta.inject` annotations).

## Modules

| Module | Binds |
|---|---|
| `PlatformModule` | `Plugin`, `JavaPlugin`, the plugin's `ComponentLogger` |
| `SettingsModule` | `config.yml` and `lang/` paths, `SettingsHolder` |
| `BrushModule` | `PlayerBrushManager` |
| `ServiceModule` | `UpdateService`, `TranslationService`, `MetricsService` |
| `ListenerModule` | every listener into a `Set<Listener>` |
| `CommandModule` | cloud's `CommandManager<Source>` and `MinecraftHelp<Source>` |

## Settings

`Settings` is an immutable record tree. `SettingsHolder` loads it from `config.yml`, writes every
key back with its comment, and publishes a new snapshot on `/gopaint reload`. Read the current
snapshot with `settings.get()` each time you need a value; do not cache it in a field, or reloads
will not reach your code.

## Adding a class

1. Give it an `@Inject` constructor and mark shared state `@Singleton`.
2. Listeners: add them to `ListenerModule`. Commands: inject them in `BetterGoPaint#registerCommands`.
3. `DIValidationTest` builds the real module graph with a mocked plugin; extend it when you add a
   module or an entry point.

Guice, `jakarta.inject` and aopalliance are shaded and relocated below
`net.onelitefeather.bettergopaint.libs`; Guava comes from Paper.
