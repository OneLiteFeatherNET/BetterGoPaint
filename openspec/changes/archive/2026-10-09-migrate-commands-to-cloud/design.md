# Design

## Context

Cloud 2.x is already shaded. Registration currently uses `LegacyPaperCommandManager`; AntiRedstoneClock
uses the same library with annotations and a Brigadier mapping.

## Decisions

### `PaperCommandManager` with Brigadier
Paper plugins get first-class Brigadier registration through cloud-paper's modern manager, which also
gives client-side validation of integer ranges. *Alternative:* keep `LegacyPaperCommandManager` —
rejected, it registers through the Bukkit command map and loses argument types.

### Range from config at parse time
The upper bound of `size` is read from settings when the command is parsed, so a reload changes it
without re-registering commands.

### `bettergopaint.admin` as bundle
Keeps existing permission setups working while giving each subcommand a fine-grained node.

## Risks / Trade-offs

- [Permission plugins keyed on `bettergopaint.admin`] → bundle keeps it working; release notes mention the new nodes.
