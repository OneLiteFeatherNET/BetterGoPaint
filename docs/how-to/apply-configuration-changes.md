# Apply configuration changes

1. Edit `plugins/BetterGoPaint/config.yml`.
2. Run `/gopaint reload` in the console or in game.
3. Read the console. A line like
   `config.yml: generic.max-size has the invalid value 'huge', using 100` means that key was
   rejected and its default is in effect. Fix the value and reload again.

A reload does not cover every key; [How settings are loaded](../explanation/how-settings-are-loaded.md)
lists which ones need a restart.
