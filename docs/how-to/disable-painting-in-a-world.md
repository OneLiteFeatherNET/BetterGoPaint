# Disable painting in a world

1. Open `plugins/BetterGoPaint/config.yml`.
2. Add the world name to `generic.disabled-worlds`:

   ```yaml
   generic:
     disabled-worlds:
       - world_nether
   ```

3. Run `/gopaint reload`.

Players with `bettergopaint.world.bypass` can still paint in those worlds.
