# Commands

`/gopaint` has the aliases `/gp` and `/bgp`. Every subcommand needs `bettergopaint.use` **and**
its own permission. `/gopaint help` lists only the subcommands you may run, and arguments are
completed with tab.

| Command | Does | Permission (besides `bettergopaint.use`) | Who |
|---|---|---|---|
| `/gopaint` or `/gopaint help [query]` | Lists the subcommands. | `bettergopaint.command.help` | anyone |
| `/gopaint size <size>` | Sets your brush size. `<size>` must be between 1 and `generic.max-size`. | `bettergopaint.command.size` | players |
| `/gopaint toggle` | Enables or disables your brush. | `bettergopaint.command.toggle` | players |
| `/gopaint info` | Shows version and credits. | `bettergopaint.command.info` | anyone |
| `/gopaint reload` | Re-reads `config.yml`. | `bettergopaint.command.admin.reload` | anyone |

All messages come from the translation bundle (`bettergopaint.command.*` keys).
