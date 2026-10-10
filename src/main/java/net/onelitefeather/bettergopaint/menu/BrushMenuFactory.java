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
import net.onelitefeather.bettergopaint.utils.GUI;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Creates the brush menu of a player. Called once per player, on the first open of the menu.
 */
@Singleton
public final class BrushMenuFactory {

    @Inject
    public BrushMenuFactory() {
    }

    public @NotNull BrushMenu create(@NotNull Locale locale) {
        return new BrushMenu(new BrushMenuHolder(GUI.menuTitle(locale)).getInventory());
    }
}
