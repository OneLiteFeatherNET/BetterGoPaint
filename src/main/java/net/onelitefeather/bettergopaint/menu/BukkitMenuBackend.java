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
import net.onelitefeather.bettergopaint.utils.Items;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The production backend: real inventories from the holder types and items from {@link Items}.
 */
public final class BukkitMenuBackend implements MenuBackend {

    @Override
    public @NotNull Inventory menu(@NotNull Component title) {
        return new BrushMenuHolder(title).getInventory();
    }

    @Override
    public @NotNull Inventory picker(@NotNull Component title) {
        return new BrushPickerHolder(title).getInventory();
    }

    @Override
    public @NotNull ItemStack item(@NotNull MenuItem item) {
        if (item.texture() == null) {
            return Items.create(item.material(), item.amount(), item.name(), item.lore());
        }
        return Items.createHead(item.texture(), item.name(), item.lore());
    }
}
