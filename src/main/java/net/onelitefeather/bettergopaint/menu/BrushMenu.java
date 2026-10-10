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

import net.onelitefeather.bettergopaint.brush.PlayerBrush;
import net.onelitefeather.bettergopaint.utils.GUI;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * The brush menu of one player, created on the first open of that menu.
 */
public final class BrushMenu {

    private final Inventory inventory;

    BrushMenu(@NotNull Inventory inventory) {
        this.inventory = inventory;
    }

    public @NotNull Inventory inventory() {
        return inventory;
    }

    public void render(@NotNull PlayerBrush brush) {
        GUI.update(inventory, brush);
    }
}
