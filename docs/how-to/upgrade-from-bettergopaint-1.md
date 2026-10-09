# Upgrade from BetterGoPaint 1.x

BetterGoPaint 2 runs only on Paper 26.1.1 or newer with Java 25. If your server stays on
Minecraft 1.21, keep BetterGoPaint 1.1.x.

1. Back up `plugins/BetterGoPaint/`.
2. Update the server to a version listed in [Supported versions](../reference/supported-versions.md)
   and switch it to Java 25.
3. Update FastAsyncWorldEdit to 2.16.1 or newer.
4. Replace the BetterGoPaint jar in `plugins/` with the 2.x jar.
5. Start the server. Your `config.yml` is kept; its keys did not change. BetterGoPaint rewrites the
   comments and adds the new key `generic.check-for-updates`.
6. Check the console for `config.yml: … has the invalid value` warnings and fix the named keys.
   See [Console messages](../reference/console-messages.md).
7. Review your permissions:
   - `/gopaint reload` now needs `bettergopaint.command.admin.reload`. Groups that have
     `bettergopaint.admin` keep it, because `bettergopaint.admin` grants that node.
   - `/gopaint size`, `toggle`, `info` and `help` each have their own node. They default to `true`,
     so groups with `bettergopaint.use` keep every subcommand. See
     [Limit what builders can do](limit-what-builders-can-do.md) to take one away.

Brush items that builders exported with 1.x keep working. Each one is converted to the new format
the first time someone paints with it.

If something goes wrong, stop the server, put the 1.x jar and your backup back, and start it on the
old Minecraft version again.
