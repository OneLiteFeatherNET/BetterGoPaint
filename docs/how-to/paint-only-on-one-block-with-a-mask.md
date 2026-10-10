# Paint only on one block with a mask

Limit every stroke to one block type, so that a stroke over stone changes stone and nothing else.

![Brush menu with the mask turned off: the Mask item shows a carved pumpkin and the Current Mask item is dirt](../images/menu/menu-spray-disabled.svg)

## What you need

- The brush item and the `bettergopaint.use` permission.
- A solid block of the type you want to paint on, in your inventory.

The defaults start with the mask **on** and sponge as the mask block. A new builder who right-clicks grass with
the defaults changes nothing, until the mask is set to grass or turned off.

## Steps

1. Left-click the brush item to open the brush menu.
2. Pick up the block from your inventory and left-click the **Current Mask** item (slot 52). The item changes to
   that block.
3. Click the **Mask** item (slot 15). Its lore changes to **Enabled** and it becomes a jack o'lantern.
4. Close the menu and right-click the area you want to paint.

Click the **Mask** item again to turn the mask off. Its lore then says **Disabled**.

{% hint style="info" %}
Planned screenshot: `images/screenshots/mask-only-stone.png`, see the shotlist.
{% endhint %}

## How the mask decides

- A stroke changes a block only if the block has exactly the mask type. Stone does not match andesite,
  polished stone or cobblestone.
- The mask applies to every brush.

## Mask or `//gmask`

`//gmask` is a WorldEdit mask on your session. It applies to every WorldEdit command and to brush strokes.
The brush mask is set in the brush menu, applies only to brush strokes and accepts one block type. A stroke
changes a block only when both masks accept it.

## Related

- [Brush menu](../reference/brush-menu.md) gives the slot of each menu item.
- [How a brush stroke is applied](../explanation/how-a-brush-stroke-is-applied.md) describes the two masks in order.
