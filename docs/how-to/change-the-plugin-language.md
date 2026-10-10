# Change the plugin language

Messages are sent in the language of the player's client when a translation exists, and in
English otherwise.

To add a language:

1. Create `plugins/BetterGoPaint/lang/`.
2. Put a file named `bettergopaint_<locale>.properties` into it, for example
   `bettergopaint_de_DE.properties`. Use the keys of the bundled `bettergopaint.properties`.
3. Add the locale to `generic.languages` in `config.yml`, for example `de-DE`.
4. Restart the server.

Messages use [MiniMessage](https://docs.advntr.dev/minimessage/format.html). `<arg:0>`, `<arg:1>`
and so on are replaced with values such as the prefix or a version number; keep them in your
translation.

Translations are also collected on [Crowdin](https://crowdin.com); contributions there are
shipped with the next release.
