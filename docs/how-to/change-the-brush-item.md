# Change the brush item

By default the feather is the brush. To use another item:

1. Open `plugins/BetterGoPaint/config.yml`.
2. Set `generic.default-brush` to the item you want, for example `BLAZE_ROD`. It must be an item,
   not a block-only material such as `WATER`.
3. Run `/gopaint reload`.

If the value is not an item, BetterGoPaint logs an error on start and disables itself.
See [Configuration](../reference/configuration.md) for all keys.
