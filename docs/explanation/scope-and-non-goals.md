# Scope and non-goals

BetterGoPaint does one thing: interactive terrain painting with a held item.

Out of scope:

- **Servers other than Paper.** The plugin ships a `paper-plugin.yml` and uses Paper API.
  Spigot, CraftBukkit and hybrid servers cannot load it. Folia is not supported because the
  plugin relies on the global scheduler and FAWE's player queue.
- **Running without FastAsyncWorldEdit.** Every edit goes through FAWE; plain WorldEdit lacks the
  APIs the brushes use.
- **Old Minecraft versions.** Only the versions in [Supported versions](../reference/supported-versions.md)
  are built and tested. The original goPaint covers older versions.
- **Running next to goPaint.** BetterGoPaint replaces goPaint and disables itself if both are
  installed.
