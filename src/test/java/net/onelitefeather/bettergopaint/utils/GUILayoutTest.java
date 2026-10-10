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
import net.onelitefeather.bettergopaint.brush.PlayerBrush;
import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.menu.BrushMenuFactory;
import net.onelitefeather.bettergopaint.menu.BrushMenuHolder;
import net.onelitefeather.bettergopaint.menu.MenuItem;
import net.onelitefeather.bettergopaint.menu.MenuRenderCache;
import net.onelitefeather.bettergopaint.objects.brush.AngleBrush;
import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.objects.brush.GradientBrush;
import net.onelitefeather.bettergopaint.objects.brush.SphereBrush;
import net.onelitefeather.bettergopaint.objects.brush.SprayBrush;
import net.onelitefeather.bettergopaint.objects.other.SurfaceMode;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import org.bukkit.Material;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Pins the complete slot layout of the brush menu for a few brush states. The labels are the translation keys
 * themselves, since the render cache gets an identity renderer, so no translation bundle is needed.
 */
class GUILayoutTest {

    private static final Component SPACER = Component.text(" ");
    private static final Component EMPTY = Component.empty();

    private final GUI gui = new GUI(new MenuRenderCache(() -> 0, (locale, component) -> component));
    private final SettingsHolder settings = SettingsHolder.of(Settings.defaults());
    private final PlayerBrushManager manager = new PlayerBrushManager(settings, mock(BrushMenuFactory.class));

    private PlayerBrush playerBrush() {
        return new PlayerBrush(manager, settings, Locale.ENGLISH, mock(BrushMenuFactory.class));
    }

    private Brush brush(Class<? extends Brush> type) {
        return manager.getBrushes().stream().filter(type::isInstance).findFirst().orElseThrow();
    }

    @Test
    @DisplayName("the default sphere brush lays out every slot as pinned")
    void sphereWithDefaultsLayout() {
        PlayerBrush playerBrush = playerBrush();
        playerBrush.setBrush(brush(SphereBrush.class));

        MenuItem[] expected = expectedLayout();
        brushSlot(expected, true);
        typeSlot(expected, brush(SphereBrush.class));
        sizeSlot(expected, 10);
        maskSlot(expected, true);
        surfaceSlot(expected, SurfaceMode.DIRECT);
        emptyPalette(expected);
        block(expected, 46, Material.STONE, 1, 100);
        maskBlockSlot(expected, Material.SPONGE);

        assertThat(gui.layout(playerBrush)).as("the layout of the default sphere brush").containsExactly(expected);
    }

    @Test
    @DisplayName("a disabled spray brush with two palette blocks and a relative surface lays out as pinned")
    void sprayWithToggledStateLayout() {
        PlayerBrush playerBrush = playerBrush();
        playerBrush.setBrush(brush(SprayBrush.class));
        playerBrush.toggle();
        playerBrush.toggleMask();
        playerBrush.cycleSurfaceMode();
        playerBrush.addBlock(Material.GRASS_BLOCK, 2);
        playerBrush.setMask(Material.DIRT);

        MenuItem[] expected = expectedLayout();
        brushSlot(expected, false);
        typeSlot(expected, brush(SprayBrush.class));
        column(expected, 3, Material.WHITE_STAINED_GLASS_PANE, item(Material.GOLD_NUGGET, 1,
                label("bettergopaint.menu.chance", Component.text(50)), increaseDecrease()));
        sizeSlot(expected, 10);
        maskSlot(expected, false);
        surfaceSlot(expected, SurfaceMode.RELATIVE);
        emptyPalette(expected);
        block(expected, 46, Material.STONE, 50, 50);
        block(expected, 47, Material.GRASS_BLOCK, 50, 50);
        maskBlockSlot(expected, Material.DIRT);

        assertThat(gui.layout(playerBrush)).as("the layout of the toggled spray brush").containsExactly(expected);
    }

    @Test
    @DisplayName("an angle brush with a disabled surface shows its distance and height settings")
    void angleWithDisabledSurfaceLayout() {
        PlayerBrush playerBrush = playerBrush();
        playerBrush.setBrush(brush(AngleBrush.class));
        playerBrush.cycleSurfaceMode();
        playerBrush.cycleSurfaceMode();

        MenuItem[] expected = expectedLayout();
        brushSlot(expected, true);
        typeSlot(expected, brush(AngleBrush.class));
        column(expected, 3, Material.WHITE_STAINED_GLASS_PANE, item(Material.DAYLIGHT_DETECTOR, 1,
                label("bettergopaint.menu.angle_distance", Component.text(2)), increaseDecrease()));
        column(expected, 4, Material.WHITE_STAINED_GLASS_PANE, item(Material.BLAZE_ROD, 1,
                label("bettergopaint.menu.angle_height", Component.text(40.0)),
                List.of(EMPTY, label("bettergopaint.menu.click.increase"), label("bettergopaint.menu.click.decrease"),
                        label("bettergopaint.menu.click.shift_15"))));
        sizeSlot(expected, 10);
        maskSlot(expected, true);
        surfaceSlot(expected, SurfaceMode.DISABLED);
        emptyPalette(expected);
        block(expected, 46, Material.STONE, 1, 100);
        maskBlockSlot(expected, Material.SPONGE);

        assertThat(gui.layout(playerBrush)).as("the layout of the angle brush with a disabled surface").containsExactly(expected);
    }

    @Test
    @DisplayName("a gradient brush shows its falloff and mixing settings")
    void gradientLayout() {
        PlayerBrush playerBrush = playerBrush();
        playerBrush.setBrush(brush(GradientBrush.class));

        MenuItem[] expected = expectedLayout();
        brushSlot(expected, true);
        typeSlot(expected, brush(GradientBrush.class));
        column(expected, 3, Material.WHITE_STAINED_GLASS_PANE, item(Material.BLAZE_POWDER, 1,
                label("bettergopaint.menu.falloff", Component.text(50)), increaseDecrease()));
        column(expected, 4, Material.WHITE_STAINED_GLASS_PANE, item(Material.MAGMA_CREAM, 1,
                label("bettergopaint.menu.mixing", Component.text(50)), increaseDecrease()));
        sizeSlot(expected, 10);
        maskSlot(expected, true);
        surfaceSlot(expected, SurfaceMode.DIRECT);
        emptyPalette(expected);
        block(expected, 46, Material.STONE, 1, 100);
        maskBlockSlot(expected, Material.SPONGE);

        assertThat(gui.layout(playerBrush)).as("the layout of the gradient brush").containsExactly(expected);
    }

    /**
     * The expected items: every slot is the gray filler, the sections below overwrite their slots.
     */
    private static MenuItem[] expectedLayout() {
        MenuItem[] items = new MenuItem[BrushMenuHolder.SIZE];
        Arrays.fill(items, pane(Material.GRAY_STAINED_GLASS_PANE));
        return items;
    }

    private static void brushSlot(MenuItem[] items, boolean enabled) {
        column(items, 1, enabled ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE,
                item(Material.FEATHER, 1, label("bettergopaint.menu.brush.name"), List.of(
                        label(enabled ? "bettergopaint.menu.brush.enabled" : "bettergopaint.menu.brush.disabled"),
                        EMPTY, label("bettergopaint.menu.brush.export"), label("bettergopaint.menu.brush.toggle"))));
    }

    private void typeSlot(MenuItem[] items, Brush selected) {
        List<Component> lore = new ArrayList<>(List.of(EMPTY, label("bettergopaint.menu.type.select"),
                label("bettergopaint.menu.type.cycle"), EMPTY));
        for (Brush brush : manager.getBrushes()) {
            lore.add(brush == selected
                    ? label("bettergopaint.menu.type.entry.selected", Component.text(brush.getName()))
                    : label("bettergopaint.menu.type.entry", Component.text(brush.getName())));
        }
        column(items, 2, Material.ORANGE_STAINED_GLASS_PANE,
                head(selected.getHead(), label("bettergopaint.menu.type.name"), lore));
    }

    private static void sizeSlot(MenuItem[] items, int size) {
        column(items, 5, Material.WHITE_STAINED_GLASS_PANE, item(Material.BROWN_MUSHROOM, 1,
                label("bettergopaint.menu.size", Component.text(size)),
                List.of(EMPTY, label("bettergopaint.menu.click.increase"), label("bettergopaint.menu.click.decrease"),
                        label("bettergopaint.menu.click.shift_10"))));
    }

    private static void maskSlot(MenuItem[] items, boolean enabled) {
        column(items, 6, enabled ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE,
                item(enabled ? Material.JACK_O_LANTERN : Material.CARVED_PUMPKIN, 1, label("bettergopaint.menu.mask"),
                        List.of(label(enabled ? "bettergopaint.menu.brush.enabled" : "bettergopaint.menu.brush.disabled"),
                                EMPTY, label("bettergopaint.menu.click.toggle"))));
    }

    private static void surfaceSlot(MenuItem[] items, SurfaceMode mode) {
        Material frame = switch (mode) {
            case DIRECT -> Material.LIME_STAINED_GLASS_PANE;
            case DISABLED -> Material.RED_STAINED_GLASS_PANE;
            case RELATIVE -> Material.ORANGE_STAINED_GLASS_PANE;
        };
        String key = switch (mode) {
            case DIRECT -> "bettergopaint.menu.surface.direct";
            case DISABLED -> "bettergopaint.menu.surface.disabled";
            case RELATIVE -> "bettergopaint.menu.surface.relative";
        };
        column(items, 7, frame, item(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 1, label("bettergopaint.menu.surface"),
                List.of(label(key), EMPTY, label("bettergopaint.menu.click.toggle"))));
    }

    /**
     * The palette before any block is placed: yellow panes, then the empty barrier slots.
     */
    private static void emptyPalette(MenuItem[] items) {
        for (int slot = 37; slot <= 41; slot++) {
            items[slot] = pane(Material.YELLOW_STAINED_GLASS_PANE);
        }
        for (int slot = 46; slot <= 50; slot++) {
            items[slot] = item(Material.BARRIER, 1, label("bettergopaint.menu.slot.empty"),
                    List.of(EMPTY, label("bettergopaint.menu.slot.empty.hint")));
        }
    }

    private static void block(MenuItem[] items, int slot, Material material, int amount, int percent) {
        items[slot] = item(material, amount, label("bettergopaint.menu.slot", Component.text(slot - 45), Component.text(percent)),
                List.of(EMPTY, label("bettergopaint.menu.slot.change"), label("bettergopaint.menu.slot.clear")));
    }

    private static void maskBlockSlot(MenuItem[] items, Material mask) {
        items[43] = pane(Material.YELLOW_STAINED_GLASS_PANE);
        items[52] = item(mask, 1, label("bettergopaint.menu.mask.current"),
                List.of(EMPTY, label("bettergopaint.menu.mask.change")));
    }

    private static List<Component> increaseDecrease() {
        return List.of(EMPTY, label("bettergopaint.menu.click.increase"), label("bettergopaint.menu.click.decrease"));
    }

    private static void column(MenuItem[] items, int top, Material frame, MenuItem item) {
        items[top] = pane(frame);
        items[top + 9] = item;
        items[top + 18] = pane(frame);
    }

    private static Component label(String key, Component... arguments) {
        return arguments.length == 0 ? Component.translatable(key) : Component.translatable(key, arguments);
    }

    private static MenuItem pane(Material material) {
        return item(material, 1, SPACER, List.of());
    }

    private static MenuItem item(Material material, int amount, Component name, List<Component> lore) {
        return new MenuItem(material, amount, null, name, lore);
    }

    private static MenuItem head(String texture, Component name, List<Component> lore) {
        return new MenuItem(Material.PLAYER_HEAD, 1, texture, name, lore);
    }
}
