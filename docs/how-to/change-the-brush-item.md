# Change the brush item

By default the feather is the brush. To use another item:

1. Open `plugins/BetterGoPaint/config.yml`.
2. Set `generic.default-brush` to the item you want, for example `BLAZE_ROD`. It must be an item,
   not a block-only material such as `WATER`.
3. Run `/gopaint reload`.

The value must be a material that can be held, such as `BLAZE_ROD`, not one that only exists as a
block, such as `WATER`. With such a value BetterGoPaint disables itself on the next start; after a
reload, nobody can hold the brush until you fix the value.
See [Configuration](../reference/configuration.md) for all keys.
