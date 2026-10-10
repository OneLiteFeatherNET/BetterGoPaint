# Set up BetterGoPaint for your build team

In this tutorial you install BetterGoPaint on a Paper server, check that it runs, and give a group
of builders the right to paint. At the end, a builder on your server can open the brush menu.
It takes about fifteen minutes.

## What you need

- A Paper 26.2 server running on Java 25 that you can stop and start.
- Console access to that server.
- [LuckPerms](https://luckperms.net) installed on the server, with a group called `builder`.

## 1. Download the plugins

Download these two jars:

- FastAsyncWorldEdit 2.16.1 or newer from
  [Modrinth](https://modrinth.com/plugin/fastasyncworldedit)
- BetterGoPaint from [Hangar](https://hangar.papermc.io/OneLiteFeather/BetterGoPaint)

## 2. Install them

Stop the server. Copy both jars into the `plugins/` folder, then start the server again.

Watch the console. You will see these lines, in this order:

```
[FastAsyncWorldEdit] Enabling FastAsyncWorldEdit v2.16.1
[BetterGoPaint] Enabling BetterGoPaint v2.0.0
```

The version numbers on your server may be higher. No line from BetterGoPaint is marked `ERROR`.

## 3. Check that the plugin answers

Type into the console:

```
gopaint info
```

The console answers with the BetterGoPaint version and a link to GitHub. The plugin is running.

## 4. Look at the configuration

Open `plugins/BetterGoPaint/config.yml`. BetterGoPaint wrote it on first start. Every key has a
comment above it that says what it does. You do not need to change anything for this tutorial.

## 5. Let your builders paint

Type into the console:

```
lp group builder permission set bettergopaint.use true
```

Every member of the `builder` group can now hold a feather, left-click to open the brush menu and
right-click to paint.

## 6. Try it yourself

Join the server with an account in the `builder` group and run:

```
/give @s feather
```

Left-click with the feather in your hand. The **goPaint Menu** opens.

## What you have now

- BetterGoPaint and FastAsyncWorldEdit run on your server.
- `config.yml` exists and holds the defaults.
- Your builders have `bettergopaint.use`.

Next steps:

- Show your builders [Paint your first terrain](paint-your-first-terrain.md).
- Set limits for them with [Limit what builders can do](../how-to/limit-what-builders-can-do.md).
- Coming from BetterGoPaint 1.x on an older server? Read
  [Upgrade from BetterGoPaint 1.x](../how-to/upgrade-from-bettergopaint-1.md).
