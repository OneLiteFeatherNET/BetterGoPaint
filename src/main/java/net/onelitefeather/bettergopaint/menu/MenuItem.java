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
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * What one menu slot shows, as plain data. Comparing two items needs no server, and an {@link org.bukkit.inventory.ItemStack}
 * is only built for a slot that changes.
 *
 * @param material the material of the item, the player head for a textured head
 * @param amount the amount shown
 * @param texture the base64 skin texture of a head, {@code null} for any other item
 * @param name the display name
 * @param lore the lore lines
 */
public record MenuItem(
        @NotNull Material material,
        int amount,
        @Nullable String texture,
        @NotNull Component name,
        @NotNull List<Component> lore
) {
}
