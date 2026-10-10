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
import net.onelitefeather.bettergopaint.brush.PlayerBrush;
import net.onelitefeather.bettergopaint.menu.BrushMenuHolder;
import net.onelitefeather.bettergopaint.menu.BrushPickerHolder;
import net.onelitefeather.bettergopaint.menu.MenuItem;
import net.onelitefeather.bettergopaint.menu.MenuRenderCache;
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
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Lays out the brush menu and the brush picker as plain {@link MenuItem}s. Labels are rendered through the
 * {@link MenuRenderCache} of the owning factory, so nothing here is static.
 */
public final class GUI {

    private static final Component SPACER = Component.text(" ");

    private final MenuRenderCache cache;

    public GUI(@NotNull MenuRenderCache cache) {
        this.cache = cache;
    }

    /**
     * @return the translation generation the labels are rendered under
     */
    public int generation() {
        return cache.generation();
    }

    public @NotNull Component menuTitle(@NotNull Locale locale) {
        return text(locale, "bettergopaint.menu.title");
    }

    public @NotNull Component pickerTitle(@NotNull Locale locale) {
        return text(locale, "bettergopaint.menu.picker.title");
    }

    /**
     * @return one item per picker slot: the brushes first, gray panes after them
     */
    public @NotNull MenuItem[] picker(@NotNull List<Brush> brushes, @NotNull Locale locale) {
        MenuItem[] items = filled(BrushPickerHolder.SIZE);
        for (int index = 0; index < brushes.size() && index < items.length; index++) {
            Brush brush = brushes.get(index);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(text(locale, "bettergopaint.menu.picker.select"));
            lore.add(Component.empty());
            lore.addAll(description(brush));
            items[index] = head(brush.getHead(), Component.text(brush.getName(), NamedTextColor.GOLD), lore);
        }
        return items;
    }

    /**
     * @return one item per slot of the brush menu
     */
    public @NotNull MenuItem[] layout(@NotNull PlayerBrush playerBrush) {
        Locale locale = playerBrush.locale();
        MenuItem[] items = filled(BrushMenuHolder.SIZE);

        brushToggleSection(items, playerBrush, locale);
        typeSection(items, playerBrush, locale);
        List<Component> increaseDecrease = lore(locale, "bettergopaint.menu.click.increase", "bettergopaint.menu.click.decrease");
        brushSettingSection(items, playerBrush, locale, increaseDecrease);
        angleSection(items, playerBrush, locale, increaseDecrease);
        mixingSection(items, playerBrush, locale, increaseDecrease);
        falloffSection(items, playerBrush, locale, increaseDecrease);
        sizeSection(items, playerBrush, locale);
        maskToggleSection(items, playerBrush, locale);
        surfaceSection(items, playerBrush, locale);
        paletteSection(items, playerBrush, locale);
        maskBlockSection(items, playerBrush, locale);
        return items;
    }

    /**
     * Slot 1: the brush item, showing whether the brush is enabled and how to export or toggle it.
     */
    private void brushToggleSection(MenuItem[] items, PlayerBrush playerBrush, Locale locale) {
        List<Component> brushLore = new ArrayList<>();
        brushLore.add(text(locale, playerBrush.enabled() ? "bettergopaint.menu.brush.enabled" : "bettergopaint.menu.brush.disabled"));
        brushLore.addAll(lore(locale, "bettergopaint.menu.brush.export", "bettergopaint.menu.brush.toggle"));
        column(items, 1, playerBrush.enabled() ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE,
                item(playerBrush.settings().generic().defaultBrush(), 1, text(locale, "bettergopaint.menu.brush.name"), brushLore));
    }

    /**
     * Slot 2: the head of the current brush type, with every brush listed in its lore.
     */
    private void typeSection(MenuItem[] items, PlayerBrush playerBrush, Locale locale) {
        List<Component> typeLore = lore(locale, "bettergopaint.menu.type.select", "bettergopaint.menu.type.cycle");
        typeLore.add(Component.empty());
        typeLore.addAll(brushList(playerBrush.brushes(), playerBrush.brush(), locale));
        column(items, 2, Material.ORANGE_STAINED_GLASS_PANE,
                head(playerBrush.brush().getHead(), text(locale, "bettergopaint.menu.type.name"), typeLore));
    }

    /**
     * Slot 3 for the brushes with one setting: the chance, the axis, the thickness or the fracture distance.
     */
    private void brushSettingSection(MenuItem[] items, PlayerBrush playerBrush, Locale locale, List<Component> increaseDecrease) {
        Brush brush = playerBrush.brush();
        if (brush instanceof SprayBrush) {
            column(items, 3, Material.WHITE_STAINED_GLASS_PANE, item(Material.GOLD_NUGGET, 1,
                    text(locale, "bettergopaint.menu.chance", Component.text(playerBrush.chance())), increaseDecrease));
        }
        if (brush instanceof DiscBrush) {
            column(items, 3, Material.WHITE_STAINED_GLASS_PANE, item(Material.COMPASS, 1,
                    text(locale, "bettergopaint.menu.axis", Component.text(playerBrush.axis().name())),
                    lore(locale, "bettergopaint.menu.click.change")));
        }
        if (brush instanceof OverlayBrush || brush instanceof UnderlayBrush) {
            column(items, 3, Material.WHITE_STAINED_GLASS_PANE, item(Material.BOOK, 1,
                    text(locale, "bettergopaint.menu.thickness", Component.text(playerBrush.thickness())), increaseDecrease));
        }
        if (brush instanceof FractureBrush) {
            column(items, 3, Material.WHITE_STAINED_GLASS_PANE, item(Material.DAYLIGHT_DETECTOR, 1,
                    text(locale, "bettergopaint.menu.fracture_distance", Component.text(playerBrush.fractureDistance())),
                    increaseDecrease));
        }
    }

    /**
     * Slots 3 and 4 for the angle brush: the check distance and the height difference.
     */
    private void angleSection(MenuItem[] items, PlayerBrush playerBrush, Locale locale, List<Component> increaseDecrease) {
        if (playerBrush.brush() instanceof AngleBrush) {
            column(items, 3, Material.WHITE_STAINED_GLASS_PANE, item(Material.DAYLIGHT_DETECTOR, 1,
                    text(locale, "bettergopaint.menu.angle_distance", Component.text(playerBrush.angleDistance())),
                    increaseDecrease));
            column(items, 4, Material.WHITE_STAINED_GLASS_PANE, item(Material.BLAZE_ROD, 1,
                    text(locale, "bettergopaint.menu.angle_height", Component.text(playerBrush.angleHeightDifference())),
                    lore(locale, "bettergopaint.menu.click.increase", "bettergopaint.menu.click.decrease",
                            "bettergopaint.menu.click.shift_15")));
        }
    }

    /**
     * Slot 4 for the gradient brush: the mixing strength.
     */
    private void mixingSection(MenuItem[] items, PlayerBrush playerBrush, Locale locale, List<Component> increaseDecrease) {
        if (playerBrush.brush() instanceof GradientBrush) {
            column(items, 4, Material.WHITE_STAINED_GLASS_PANE, item(Material.MAGMA_CREAM, 1,
                    text(locale, "bettergopaint.menu.mixing", Component.text(playerBrush.mixingStrength())), increaseDecrease));
        }
    }

    /**
     * Slot 3 for the brushes with a falloff: the splatter, the paint and the gradient brush.
     */
    private void falloffSection(MenuItem[] items, PlayerBrush playerBrush, Locale locale, List<Component> increaseDecrease) {
        Brush brush = playerBrush.brush();
        if (brush instanceof SplatterBrush || brush instanceof PaintBrush || brush instanceof GradientBrush) {
            column(items, 3, Material.WHITE_STAINED_GLASS_PANE, item(Material.BLAZE_POWDER, 1,
                    text(locale, "bettergopaint.menu.falloff", Component.text(playerBrush.falloffStrength())), increaseDecrease));
        }
    }

    /**
     * Slot 5: the brush size.
     */
    private void sizeSection(MenuItem[] items, PlayerBrush playerBrush, Locale locale) {
        column(items, 5, Material.WHITE_STAINED_GLASS_PANE, item(Material.BROWN_MUSHROOM, 1,
                text(locale, "bettergopaint.menu.size", Component.text(playerBrush.size())),
                lore(locale, "bettergopaint.menu.click.increase", "bettergopaint.menu.click.decrease",
                        "bettergopaint.menu.click.shift_10")));
    }

    /**
     * Slot 6: the mask toggle, a jack o'lantern while the mask is enabled.
     */
    private void maskToggleSection(MenuItem[] items, PlayerBrush playerBrush, Locale locale) {
        List<Component> maskLore = new ArrayList<>();
        maskLore.add(text(locale, playerBrush.maskEnabled() ? "bettergopaint.menu.brush.enabled" : "bettergopaint.menu.brush.disabled"));
        maskLore.addAll(lore(locale, "bettergopaint.menu.click.toggle"));
        column(items, 6, playerBrush.maskEnabled() ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE,
                item(playerBrush.maskEnabled() ? Material.JACK_O_LANTERN : Material.CARVED_PUMPKIN, 1,
                        text(locale, "bettergopaint.menu.mask"), maskLore));
    }

    /**
     * Slot 7: the surface mode, colored by the mode and described in the lore.
     */
    private void surfaceSection(MenuItem[] items, PlayerBrush playerBrush, Locale locale) {
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
        column(items, 7, surfacePane, item(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 1,
                text(locale, "bettergopaint.menu.surface"), surfaceLore));
    }

    /**
     * Slots 37 to 41 and 46 to 50: the yellow frame above the palette, then the blocks of the brush or barriers for the empty ones.
     */
    private void paletteSection(MenuItem[] items, PlayerBrush playerBrush, Locale locale) {
        for (int slot = 37; slot <= 41; slot++) {
            items[slot] = pane(Material.YELLOW_STAINED_GLASS_PANE);
        }
        for (int slot = 46; slot <= 50; slot++) {
            items[slot] = item(Material.BARRIER, 1, text(locale, "bettergopaint.menu.slot.empty"),
                    lore(locale, "bettergopaint.menu.slot.empty.hint"));
        }
        int slot = 46;
        int size = playerBrush.blocks().size();
        int chance = size == 0 ? 0 : 100 / size;
        for (Material material : playerBrush.blocks()) {
            items[slot] = item(material, chance > 64 ? 1 : Math.max(1, chance),
                    text(locale, "bettergopaint.menu.slot", Component.text(slot - 45), Component.text(chance)),
                    lore(locale, "bettergopaint.menu.slot.change", "bettergopaint.menu.slot.clear"));
            slot++;
        }
    }

    /**
     * Slots 43 and 52: the frame next to the palette and the mask block.
     */
    private void maskBlockSection(MenuItem[] items, PlayerBrush playerBrush, Locale locale) {
        items[43] = pane(Material.YELLOW_STAINED_GLASS_PANE);
        items[52] = item(playerBrush.mask(), 1, text(locale, "bettergopaint.menu.mask.current"),
                lore(locale, "bettergopaint.menu.mask.change"));
    }

    private List<Component> brushList(List<Brush> brushes, Brush selected, Locale locale) {
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

    /**
     * A label without arguments, rendered once per locale.
     */
    private Component text(Locale locale, String key) {
        return cache.label(locale, key);
    }

    /**
     * A text with arguments, rendered on every call.
     */
    private Component text(Locale locale, String key, ComponentLike... arguments) {
        return cache.render(locale, key, arguments);
    }

    private List<Component> lore(Locale locale, String... keys) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.empty());
        for (String key : keys) {
            lines.add(text(locale, key));
        }
        return lines;
    }

    private static MenuItem pane(Material material) {
        return item(material, 1, SPACER, List.of());
    }

    private static MenuItem item(Material material, int amount, Component name, List<Component> lore) {
        return new MenuItem(material, amount, null, name, List.copyOf(lore));
    }

    private static MenuItem head(String texture, Component name, List<Component> lore) {
        return new MenuItem(Material.PLAYER_HEAD, 1, texture, name, List.copyOf(lore));
    }

    /**
     * Every slot starts with the same gray filler; the filler is one shared description.
     */
    private static MenuItem[] filled(int size) {
        MenuItem filler = pane(Material.GRAY_STAINED_GLASS_PANE);
        MenuItem[] items = new MenuItem[size];
        Arrays.fill(items, filler);
        return items;
    }

    private static void column(MenuItem[] items, int top, Material frame, MenuItem item) {
        items[top] = pane(frame);
        items[top + 9] = item;
        items[top + 18] = pane(frame);
    }
}
