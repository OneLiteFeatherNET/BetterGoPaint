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
package net.onelitefeather.bettergopaint.docsgen;

import net.onelitefeather.bettergopaint.brush.PlayerBrush;
import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.menu.MenuItem;
import net.onelitefeather.bettergopaint.menu.MenuRenderCache;
import net.onelitefeather.bettergopaint.objects.brush.AngleBrush;
import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.objects.brush.SphereBrush;
import net.onelitefeather.bettergopaint.objects.brush.SprayBrush;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.utils.GUI;
import org.bukkit.Material;

import java.util.List;
import java.util.function.Consumer;

/**
 * The three menu states the documentation shows. They are the states {@code GUILayoutTest} pins, built with the same
 * calls, so an image shows what the test asserts.
 */
final class MenuStates {

    private MenuStates() {
    }

    /**
     * One menu state.
     *
     * @param fileName the file name below {@code docs/images/menu}
     * @param title    the title shown above the grid
     * @param layout   the items of the menu, one per slot
     */
    record State(String fileName, String title, MenuItem[] layout) {
    }

    /**
     * @return the three states, in the order of their files
     */
    static List<State> all() {
        return List.of(
                new State("menu-sphere-default.svg", "Brush menu: Sphere Brush, default settings", sphereDefault()),
                new State("menu-spray-disabled.svg",
                        "Brush menu: Spray Brush, disabled, two palette blocks, dirt mask", sprayDisabled()),
                new State("menu-angle.svg", "Brush menu: Angle Brush, surface disabled", angleSurfaceDisabled()));
    }

    static MenuItem[] sphereDefault() {
        return layout(SphereBrush.class, playerBrush -> {
        });
    }

    static MenuItem[] sprayDisabled() {
        return layout(SprayBrush.class, playerBrush -> {
            playerBrush.toggle();
            playerBrush.toggleMask();
            playerBrush.cycleSurfaceMode();
            playerBrush.addBlock(Material.GRASS_BLOCK, 2);
            playerBrush.setMask(Material.DIRT);
        });
    }

    static MenuItem[] angleSurfaceDisabled() {
        return layout(AngleBrush.class, playerBrush -> {
            playerBrush.cycleSurfaceMode();
            playerBrush.cycleSurfaceMode();
        });
    }

    private static MenuItem[] layout(Class<? extends Brush> type, Consumer<PlayerBrush> setup) {
        SettingsHolder settings = GeneratorFixtures.settings();
        PlayerBrushManager manager = GeneratorFixtures.brushManager(settings);
        PlayerBrush playerBrush = GeneratorFixtures.playerBrush(manager, settings);
        playerBrush.setBrush(manager.getBrushes().stream().filter(type::isInstance).findFirst().orElseThrow());
        setup.accept(playerBrush);
        // Labels are the translation keys themselves; the English text is resolved later from the bundle.
        GUI gui = new GUI(new MenuRenderCache(() -> 0, (locale, component) -> component));
        return gui.layout(playerBrush);
    }
}
