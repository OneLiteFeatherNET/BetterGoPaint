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
package net.onelitefeather.bettergopaint.menu;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.service.TranslationService;
import net.onelitefeather.bettergopaint.utils.GUI;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;

/**
 * Creates the brush menu of a player and the brush picker. Owns the render cache, so every factory has its own.
 */
@Singleton
public final class BrushMenuFactory {

    private final GUI gui;
    private final MenuBackend backend;

    @Inject
    public BrushMenuFactory(@NotNull TranslationService translations) {
        this(translations::generation, (locale, component) -> GlobalTranslator.render(component, locale), new BukkitMenuBackend());
    }

    BrushMenuFactory(
            @NotNull IntSupplier translationGeneration,
            @NotNull BiFunction<Locale, Component, Component> renderer,
            @NotNull MenuBackend backend
    ) {
        this.gui = new GUI(new MenuRenderCache(translationGeneration, renderer));
        this.backend = backend;
    }

    /**
     * @param locale the locale of the player who owns the menu
     * @return a new brush menu; it is not rendered yet
     */
    public @NotNull BrushMenu create(@NotNull Locale locale) {
        return new BrushMenu(backend.menu(gui.menuTitle(locale)), gui, backend);
    }

    /**
     * @param brushes the brushes the picker lists
     * @param locale the locale of the player who opens the picker
     * @return a new picker inventory for one viewer
     */
    public @NotNull Inventory createPicker(@NotNull List<Brush> brushes, @NotNull Locale locale) {
        Inventory inventory = backend.picker(gui.pickerTitle(locale));
        MenuItem[] items = gui.picker(brushes, locale);
        for (int slot = 0; slot < items.length; slot++) {
            inventory.setItem(slot, backend.item(items[slot]));
        }
        return inventory;
    }
}
