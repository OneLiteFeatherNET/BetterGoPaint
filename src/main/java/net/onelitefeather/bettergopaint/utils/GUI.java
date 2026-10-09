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
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.onelitefeather.bettergopaint.brush.PlayerBrush;
import net.onelitefeather.bettergopaint.menu.BrushMenuHolder;
import net.onelitefeather.bettergopaint.menu.BrushPickerHolder;
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
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Builds the brush menu and the brush picker. All text comes from the translation bundles and is
 * rendered for the locale of the player who owns the menu.
 */
public final class GUI {

    private static final Component SPACER = Component.text(" ");

    private GUI() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static @NotNull Inventory create(@NotNull PlayerBrush playerBrush) {
        Inventory inventory = new BrushMenuHolder(text(playerBrush.locale(), "bettergopaint.menu.title")).getInventory();
        update(inventory, playerBrush);
        return inventory;
    }

    public static @NotNull Inventory generateBrushes(@NotNull List<Brush> brushes, @NotNull Locale locale) {
        Inventory inventory = new BrushPickerHolder(text(locale, "bettergopaint.menu.picker.title")).getInventory();
        formatDefault(inventory);
        for (int index = 0; index < brushes.size() && index < inventory.getSize(); index++) {
            Brush brush = brushes.get(index);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(text(locale, "bettergopaint.menu.picker.select"));
            lore.add(Component.empty());
            lore.addAll(description(brush));
            inventory.setItem(index, Items.createHead(
                    brush.getHead(),
                    Component.text(brush.getName(), NamedTextColor.GOLD),
                    lore
            ));
        }
        return inventory;
    }

    /**
     * Lists every brush, the selected one highlighted.
     */
    public static @NotNull List<Component> brushList(@NotNull List<Brush> brushes, @NotNull Brush selected, @NotNull Locale locale) {
        return brushes.stream()
                .map(brush -> brush.equals(selected)
                        ? text(locale, "bettergopaint.menu.type.entry.selected", Component.text(brush.getName()))
                        : text(locale, "bettergopaint.menu.type.entry", Component.text(brush.getName())))
                .toList();
    }

    private static List<Component> description(Brush brush) {
        return Arrays.stream(brush.getDescription().split("\n"))
                .<Component>map(line -> LegacyComponentSerializer.legacySection().deserialize(line)
                        .colorIfAbsent(NamedTextColor.DARK_GRAY))
                .toList();
    }

    private static Component text(Locale locale, String key, ComponentLike... arguments) {
        return GlobalTranslator.render(Component.translatable(key, arguments), locale);
    }

    private static List<Component> lore(Locale locale, String... keys) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.empty());
        for (String key : keys) {
            lines.add(text(locale, key));
        }
        return lines;
    }

    private static ItemStack pane(Material material) {
        return Items.create(material, 1, SPACER, List.of());
    }

    private static void formatDefault(@NotNull Inventory inventory) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, pane(Material.GRAY_STAINED_GLASS_PANE));
        }
    }

    private static void column(Inventory inventory, int top, Material frame, ItemStack item) {
        inventory.setItem(top, pane(frame));
        inventory.setItem(top + 9, item);
        inventory.setItem(top + 18, pane(frame));
    }

    public static void update(@NotNull Inventory inventory, @NotNull PlayerBrush playerBrush) {
        Brush brush = playerBrush.brush();
        Locale locale = playerBrush.locale();

        formatDefault(inventory);

        // brush item: export and toggle
        List<Component> brushLore = new ArrayList<>();
        brushLore.add(text(locale, playerBrush.enabled() ? "bettergopaint.menu.brush.enabled" : "bettergopaint.menu.brush.disabled"));
        brushLore.addAll(lore(locale, "bettergopaint.menu.brush.export", "bettergopaint.menu.brush.toggle"));
        column(inventory, 1, playerBrush.enabled() ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE,
                Items.create(Settings.settings().generic.DEFAULT_BRUSH, 1, text(locale, "bettergopaint.menu.brush.name"), brushLore));

        // brush type
        List<Component> typeLore = lore(locale, "bettergopaint.menu.type.select", "bettergopaint.menu.type.cycle");
        typeLore.add(Component.empty());
        typeLore.addAll(brushList(playerBrush.brushes(), brush, locale));
        column(inventory, 2, Material.ORANGE_STAINED_GLASS_PANE,
                Items.createHead(brush.getHead(), text(locale, "bettergopaint.menu.type.name"), typeLore));

        List<Component> increaseDecrease = lore(locale, "bettergopaint.menu.click.increase", "bettergopaint.menu.click.decrease");

        // first brush specific setting
        if (brush instanceof SprayBrush) {
            column(inventory, 3, Material.WHITE_STAINED_GLASS_PANE, Items.create(Material.GOLD_NUGGET, 1,
                    text(locale, "bettergopaint.menu.chance", Component.text(playerBrush.chance())), increaseDecrease));
        }
        if (brush instanceof DiscBrush) {
            column(inventory, 3, Material.WHITE_STAINED_GLASS_PANE, Items.create(Material.COMPASS, 1,
                    text(locale, "bettergopaint.menu.axis", Component.text(playerBrush.axis().name())),
                    lore(locale, "bettergopaint.menu.click.change")));
        }
        if (brush instanceof OverlayBrush || brush instanceof UnderlayBrush) {
            column(inventory, 3, Material.WHITE_STAINED_GLASS_PANE, Items.create(Material.BOOK, 1,
                    text(locale, "bettergopaint.menu.thickness", Component.text(playerBrush.thickness())), increaseDecrease));
        }
        if (brush instanceof AngleBrush) {
            column(inventory, 3, Material.WHITE_STAINED_GLASS_PANE, Items.create(Material.DAYLIGHT_DETECTOR, 1,
                    text(locale, "bettergopaint.menu.angle_distance", Component.text(playerBrush.angleDistance())),
                    increaseDecrease));
            column(inventory, 4, Material.WHITE_STAINED_GLASS_PANE, Items.create(Material.BLAZE_ROD, 1,
                    text(locale, "bettergopaint.menu.angle_height", Component.text(playerBrush.angleHeightDifference())),
                    lore(locale, "bettergopaint.menu.click.increase", "bettergopaint.menu.click.decrease",
                            "bettergopaint.menu.click.shift_15")));
        }
        if (brush instanceof FractureBrush) {
            column(inventory, 3, Material.WHITE_STAINED_GLASS_PANE, Items.create(Material.DAYLIGHT_DETECTOR, 1,
                    text(locale, "bettergopaint.menu.fracture_distance", Component.text(playerBrush.fractureDistance())),
                    increaseDecrease));
        }
        if (brush instanceof GradientBrush) {
            column(inventory, 4, Material.WHITE_STAINED_GLASS_PANE, Items.create(Material.MAGMA_CREAM, 1,
                    text(locale, "bettergopaint.menu.mixing", Component.text(playerBrush.mixingStrength())), increaseDecrease));
        }
        if (brush instanceof SplatterBrush || brush instanceof PaintBrush || brush instanceof GradientBrush) {
            column(inventory, 3, Material.WHITE_STAINED_GLASS_PANE, Items.create(Material.BLAZE_POWDER, 1,
                    text(locale, "bettergopaint.menu.falloff", Component.text(playerBrush.falloffStrength())), increaseDecrease));
        }

        // size
        column(inventory, 5, Material.WHITE_STAINED_GLASS_PANE, Items.create(Material.BROWN_MUSHROOM, 1,
                text(locale, "bettergopaint.menu.size", Component.text(playerBrush.size())),
                lore(locale, "bettergopaint.menu.click.increase", "bettergopaint.menu.click.decrease",
                        "bettergopaint.menu.click.shift_10")));

        // mask toggle
        List<Component> maskLore = new ArrayList<>();
        maskLore.add(text(locale, playerBrush.maskEnabled() ? "bettergopaint.menu.brush.enabled" : "bettergopaint.menu.brush.disabled"));
        maskLore.addAll(lore(locale, "bettergopaint.menu.click.toggle"));
        column(inventory, 6, playerBrush.maskEnabled() ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE,
                Items.create(playerBrush.maskEnabled() ? Material.JACK_O_LANTERN : Material.CARVED_PUMPKIN, 1,
                        text(locale, "bettergopaint.menu.mask"), maskLore));

        // surface mode
        Material surfacePane = switch (playerBrush.surfaceMode()) {
            case DIRECT -> Material.LIME_STAINED_GLASS_PANE;
            case DISABLED -> Material.RED_STAINED_GLASS_PANE;
            case RELATIVE -> Material.ORANGE_STAINED_GLASS_PANE;
        };
        String surfaceKey = switch (playerBrush.surfaceMode()) {
            case DIRECT -> "bettergopaint.menu.surface.direct";
            case DISABLED -> "bettergopaint.menu.surface.disabled";
            case RELATIVE -> "bettergopaint.menu.surface.relative";
        };
        List<Component> surfaceLore = new ArrayList<>();
        surfaceLore.add(text(locale, surfaceKey));
        surfaceLore.addAll(lore(locale, "bettergopaint.menu.click.toggle"));
        column(inventory, 7, surfacePane, Items.create(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 1,
                text(locale, "bettergopaint.menu.surface"), surfaceLore));

        // palette
        for (int slot = 37; slot <= 41; slot++) {
            inventory.setItem(slot, pane(Material.YELLOW_STAINED_GLASS_PANE));
        }
        for (int slot = 46; slot <= 50; slot++) {
            inventory.setItem(slot, Items.create(Material.BARRIER, 1, text(locale, "bettergopaint.menu.slot.empty"),
                    lore(locale, "bettergopaint.menu.slot.empty.hint")));
        }
        int slot = 46;
        int size = playerBrush.blocks().size();
        int chance = size == 0 ? 0 : 100 / size;
        for (Material material : playerBrush.blocks()) {
            inventory.setItem(slot, Items.create(material, chance > 64 ? 1 : Math.max(1, chance),
                    text(locale, "bettergopaint.menu.slot", Component.text(slot - 45), Component.text(chance)),
                    lore(locale, "bettergopaint.menu.slot.change", "bettergopaint.menu.slot.clear")));
            slot++;
        }

        // mask block
        inventory.setItem(43, pane(Material.YELLOW_STAINED_GLASS_PANE));
        inventory.setItem(52, Items.create(playerBrush.mask(), 1, text(locale, "bettergopaint.menu.mask.current"),
                lore(locale, "bettergopaint.menu.mask.change")));
    }
}
