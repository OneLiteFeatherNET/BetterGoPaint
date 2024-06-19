/*
 * goPaint is designed to simplify painting inside of Minecraft.
 * Copyright (C) Arcaniax-Development
 * Copyright (C) Arcaniax team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.onelitefeather.bettergopaint.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.onelitefeather.bettergopaint.BetterGoPaint;
import net.onelitefeather.bettergopaint.brush.PlayerBrush;
import net.onelitefeather.bettergopaint.objects.brush.AngleBrush;
import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.objects.brush.DiscBrush;
import net.onelitefeather.bettergopaint.objects.brush.FractureBrush;
import net.onelitefeather.bettergopaint.objects.brush.GradientBrush;
import net.onelitefeather.bettergopaint.objects.brush.OverlayBrush;
import net.onelitefeather.bettergopaint.objects.brush.PaintBrush;
import net.onelitefeather.bettergopaint.objects.brush.SplatterBrush;
import net.onelitefeather.bettergopaint.objects.brush.SprayBrush;
import net.onelitefeather.bettergopaint.objects.brush.UnderlayBrush;
import net.onelitefeather.bettergopaint.objects.other.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A utility class to generate GUIs for the goPaint plugin.
 * @version 1.0.0
 * @since 1.0.0
 */
public final class GUI {

    private static final String INCREASE_DECREASE_LORE = "\n§7Left click to increase\n§7Right click to decrease";
    private static final ItemStack WHITE_DECORATION = Items.create(Material.WHITE_STAINED_GLASS_PANE, 1, "§7", "");
    private static final ItemStack LIME_DECORATION = Items.create(Material.LIME_STAINED_GLASS_PANE, 1, "§7", "");
    private static final ItemStack YELLOW_DECORATION = Items.create(Material.YELLOW_STAINED_GLASS_PANE, 1, "§7", "");
    private static final ItemStack RED_DECORATION = Items.create(Material.RED_STAINED_GLASS_PANE, 1, "§7", "");
    private static final ItemStack ORANGE_DECORATION = Items.create(Material.ORANGE_STAINED_GLASS_PANE, 1, "§7", "");
    private static final ItemStack EMPTY_SLOT = Items.create(Material.BARRIER, 1, "§cEmpty Slot", "\n§7Click with a block to set");
    private static final BetterGoPaint plugin = JavaPlugin.getPlugin(BetterGoPaint.class);

    private GUI() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Creates a new {@link Inventory} with the current {@link PlayerBrush} settings.
     * @param pb the player brush to get the settings from
     * @return the generated inventory
     */
    public static @NotNull Inventory create(@NotNull PlayerBrush pb) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("goPaint Menu", NamedTextColor.DARK_BLUE));
        update(inv, pb);
        return inv;
    }

    /**
     * Generates an {@link Inventory} with all the brushes represented by an {@link ItemStack}.
     * @return the generated inventory
     */
    public static @NotNull Inventory generateBrushes() {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("goPaint Brushes", NamedTextColor.DARK_BLUE));
        // FILLER
        formatDefault(inv);
        for (int index = 0; index < plugin.getBrushManager().getBrushes().size(); index++) {
            Brush brush = plugin.getBrushManager().getBrushes().get(index);
            inv.setItem(index, Items.createHead(brush.getHead(), 1, "§6" + brush.getName(),
                    "\n§7Click to select\n\n§8" + brush.getDescription()
            ));
        }
        return inv;
    }

    /**
     * Applies a default formating to the given {@link Inventory}.
     * @param inventory the inventory to format
     */
    private static void formatDefault(@NotNull Inventory inventory) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, LIME_DECORATION);
        }
    }

    /**
     * Updates the given {@link Inventory} with the current {@link PlayerBrush} settings.
     * @param inventory the inventory to update
     * @param playerBrush the player brush to get the settings from
     */
    public static void update(@NotNull Inventory inventory, @NotNull PlayerBrush playerBrush) {
        Brush brush = playerBrush.brush();

        // FILLER
        formatDefault(inventory);
        // goPaint toggle
        setPaintToggle(inventory, playerBrush);
        // Brushes + Chance
        inventory.setItem(2, ORANGE_DECORATION);

        String clicks = "\n§7Shift click to select\n§7Click to cycle brush\n\n";

        inventory.setItem(11, Items.createHead(brush.getHead(), 1, "§6Selected Brush type",
                clicks + plugin.getBrushManager().getBrushLore(brush)
        ));
        inventory.setItem(20, ORANGE_DECORATION);

        // chance
        if (brush instanceof SprayBrush) {
            inventory.setItem(3, WHITE_DECORATION);
            inventory.setItem(12, Items.create(Material.GOLD_NUGGET, 1,
                    "§6Place chance: §e" + playerBrush.chance() + "%",
                    INCREASE_DECREASE_LORE
            ));
            inventory.setItem(21, WHITE_DECORATION);
        }

        // axis
        if (brush instanceof DiscBrush) {
            inventory.setItem(3, WHITE_DECORATION);
            inventory.setItem(12, Items.create(Material.COMPASS, 1,
                    "§6Axis: §e" + playerBrush.axis(), "\n§7Click to change"
            ));
            inventory.setItem(21, WHITE_DECORATION);
        }


        // thickness
        if (brush instanceof OverlayBrush || brush instanceof UnderlayBrush) {
            inventory.setItem(3, WHITE_DECORATION);
            inventory.setItem(12, Items.create(Material.BOOK, 1,
                    "§6Layer Thickness: §e" + playerBrush.thickness(),
                    INCREASE_DECREASE_LORE
            ));
            inventory.setItem(21, WHITE_DECORATION);
        }

        // angle settings
        if (brush instanceof AngleBrush) {
            inventory.setItem(3, WHITE_DECORATION);
            inventory.setItem(12, Items.create(Material.DAYLIGHT_DETECTOR, 1,
                    "§6Angle Check Distance: §e" + playerBrush.angleDistance(),
                    INCREASE_DECREASE_LORE
            ));
            inventory.setItem(21, WHITE_DECORATION);
            inventory.setItem(4, WHITE_DECORATION);
            inventory.setItem(13, Items.create(Material.BLAZE_ROD, 1,
                    "§6Maximum Angle: §e" + playerBrush.angleHeightDifference() + "°",
                    "\n§7Left click to increase\n§7Right click to decrease\n§7Shift click to change by 15"
            ));
            inventory.setItem(22, WHITE_DECORATION);
        }

        // fracture settings
        if (brush instanceof FractureBrush) {
            inventory.setItem(3, WHITE_DECORATION);
            inventory.setItem(12, Items.create(Material.DAYLIGHT_DETECTOR, 1,
                    "§6Fracture Check Distance: §e" + playerBrush.fractureDistance(),
                    INCREASE_DECREASE_LORE
            ));
            inventory.setItem(21, WHITE_DECORATION);
        }

        if (brush instanceof SplatterBrush || brush instanceof PaintBrush || brush instanceof GradientBrush) {
            inventory.setItem(3, WHITE_DECORATION);
            inventory.setItem(12, Items.create(Material.BLAZE_POWDER, 1,
                    "§6Falloff Strength: §e" + playerBrush.falloffStrength() + "%",
                    INCREASE_DECREASE_LORE
            ));
            inventory.setItem(21, WHITE_DECORATION);
        }
        // angle settings
        if (brush instanceof GradientBrush) {
            inventory.setItem(4, WHITE_DECORATION);
            inventory.setItem(13, Items.create(Material.MAGMA_CREAM, 1,
                    "§6Mixing Strength: §e" + playerBrush.mixingStrength() + "%",
                    INCREASE_DECREASE_LORE
            ));
            inventory.setItem(22, WHITE_DECORATION);
        }


        // Size
        inventory.setItem(5, WHITE_DECORATION);
        inventory.setItem(14, Items.create(Material.BROWN_MUSHROOM, 1,
                "§6Brush Size: §e" + playerBrush.size(),
                "\n§7Left click to increase\n§7Right click to decrease\n§7Shift click to change by 10"
        ));
        inventory.setItem(23, WHITE_DECORATION);

        // Mask toggle
        setMaskItems(inventory, playerBrush);

        // Surface Mode toggle
        addSurfaceModeSwitch(inventory, playerBrush);

        // Place Block
        for (int x = 37; x <= 41; x++) {
            inventory.setItem(x, YELLOW_DECORATION);
        }
        for (int x = 46; x <= 50; x++) {
            inventory.setItem(x, EMPTY_SLOT);
        }

        // Block Change
        setBlockChangeItems(inventory, playerBrush);

        // Mask Block
        inventory.setItem(43, YELLOW_DECORATION);
        inventory.setItem(52, Items.create(playerBrush.mask(), 1, "§6Current Mask", "\n§7Left click with a block to change"));
    }

    private static void setBlockChangeItems(@NotNull Inventory inventory, @NotNull PlayerBrush playerBrush) {
        final List<Material> blocks = playerBrush.blocks();
        if (blocks.isEmpty()) return;
        int x = 46;
        int chance = blocks.isEmpty() ? 0 : 100 /  blocks.size();
        for (Material material : playerBrush.blocks()) {
            if (chance > 64) {
                inventory.setItem(x, Items.create(material, 1,
                        "§aSlot " + (x - 45) + " §7" + chance + "%",
                        "\n§7Left click with a block to change\n§7Right click to clear"
                ));
            } else {
                inventory.setItem(x, Items.create(material, chance,
                        "§aSlot " + (x - 45) + " §7" + chance + "%",
                        "\n§7Left click with a block to change\n§7Right click to clear"
                ));
            }
            x++;
        }
    }

    /**
     * Set the relevant {@link ItemStack}'s into an {@link Inventory} to represent the goPaint toggle
     *
     * @param inventory   the inventory to set the items in
     * @param playerBrush the {@link PlayerBrush} to get the goPaint status from
     */
    private static void setPaintToggle(@NotNull Inventory inventory, @NotNull PlayerBrush playerBrush) {
        final String lore = "§a§lEnabled\n\n§7Left click with item to export\n§7Right click to toggle";
        final String displayName = "§6goPaint Brush";
        final boolean hasBrushEnabled = playerBrush.enabled();
        inventory.setItem(1, hasBrushEnabled ? LIME_DECORATION : RED_DECORATION);
        inventory.setItem(10, Items.create(Settings.settings().generic.DEFAULT_BRUSH, 1, displayName, lore));
        inventory.setItem(19, hasBrushEnabled ? LIME_DECORATION : RED_DECORATION);
    }

    /**
     * Set the relevant {@link ItemStack}'s into an {@link Inventory} to represent the mask toggle
     *
     * @param inventory   the inventory to set the items in
     * @param playerBrush the {@link PlayerBrush} to get the mask status from
     */
    private static void setMaskItems(@NotNull Inventory inventory, @NotNull PlayerBrush playerBrush) {
        if (playerBrush.maskEnabled()) {
            inventory.setItem(6, LIME_DECORATION);
            inventory.setItem(15, Items.create(Material.JACK_O_LANTERN, 1,
                    "§6Mask",
                    "§a§lEnabled\n\n§7Click to toggle"
            ));
            inventory.setItem(24, LIME_DECORATION);
            return;
        }
        final ItemStack mask = Items.create(Material.CARVED_PUMPKIN, 1, "§6Mask", "§c§lDisabled\n\n§7Click to toggle");
        inventory.setItem(6, RED_DECORATION);
        inventory.setItem(15, mask);
        inventory.setItem(24, RED_DECORATION);
    }

    /**
     * Add the surface mode switch to the {@link Inventory}.
     *
     * @param inv         the inventory to add the switch to
     * @param playerBrush the {@link PlayerBrush} to get the surface mode from
     */
    private static void addSurfaceModeSwitch(@NotNull Inventory inv, @NotNull PlayerBrush playerBrush) {
        // Reuses the constant for the pane to reduce object creation
        ItemStack pane = switch (playerBrush.surfaceMode()) {
            case DIRECT -> LIME_DECORATION;
            case DISABLED -> RED_DECORATION;
            case RELATIVE -> ORANGE_DECORATION;
        };
        String color = switch (playerBrush.surfaceMode()) {
            case DIRECT -> "§a";
            case DISABLED -> "§c";
            case RELATIVE -> "§6";
        };

        inv.setItem(7, pane);
        inv.setItem(16, Items.create(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 1,
                "§6Surface Mode",
                color + "§l" + playerBrush.surfaceMode().getName() + "\n\n§7Click to toggle"
        ));
        inv.setItem(25, pane);
    }
}
