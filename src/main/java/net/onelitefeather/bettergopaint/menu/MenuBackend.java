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

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Creates the Bukkit objects a menu shows. Production uses {@link BukkitMenuBackend}; unit tests replace it,
 * because creating inventories and items needs a running server.
 */
public interface MenuBackend {

    /**
     * @param title the title of the brush menu
     * @return a new, empty brush menu inventory
     */
    @NotNull Inventory menu(@NotNull Component title);

    /**
     * @param title the title of the brush picker
     * @return a new, empty brush picker inventory
     */
    @NotNull Inventory picker(@NotNull Component title);

    /**
     * @param item the plain description of the item
     * @return a new item stack for the description
     */
    @NotNull ItemStack item(@NotNull MenuItem item);
}
