# Limit what builders can do

The examples use LuckPerms and a group called `builder`. Every node is listed in
[Permissions](../reference/permissions.md).

## Cap the brush size

Large brushes change many blocks per click. Lower `generic.max-size` in `config.yml` and run
`/gopaint reload`. Builders cannot set a larger size afterwards, neither with `/gopaint size` nor in
the menu.

## Take away a single command

Negate its node, for example to stop builders from changing their size by command:

```
lp group builder permission set bettergopaint.command.size false
```

`/gopaint help` stops listing that command for them.

## Keep builders out of a world

Add the world to `generic.disabled-worlds`; see
[Disable painting in a world](disable-painting-in-a-world.md). To let a trusted group paint
there anyway:

```
lp group lead-builder permission set bettergopaint.world.bypass true
```

## Let a group reload the configuration

```
lp group lead-builder permission set bettergopaint.admin true
```

This grants `/gopaint reload`.
