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

import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class Items {

    private Items() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static @NotNull ItemStack create(
            @NotNull Material material,
            int amount,
            @NotNull Component name,
            @NotNull List<Component> lore
    ) {
        ItemStack itemStack = new ItemStack(material);
        itemStack.setAmount(amount);
        itemStack.editMeta(itemMeta -> {
            itemMeta.displayName(plain(name));
            if (!lore.isEmpty()) {
                itemMeta.lore(lore.stream().map(Items::plain).toList());
            }
        });
        return itemStack;
    }

    public static @NotNull ItemStack createHead(
            @NotNull String texture,
            @NotNull Component name,
            @NotNull List<Component> lore
    ) {
        ItemStack head = create(Material.PLAYER_HEAD, 1, name, lore);
        head.editMeta(SkullMeta.class, skullMeta -> {
            var profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", texture));
            skullMeta.setPlayerProfile(profile);
        });
        return head;
    }

    private static @NotNull Component plain(@NotNull Component component) {
        return component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}
