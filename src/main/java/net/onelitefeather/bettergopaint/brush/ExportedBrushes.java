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
package net.onelitefeather.bettergopaint.brush;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Reads and writes brushes stored on items. The settings live in the item's persistent data under
 * {@code bettergopaint:brush}; name and lore are only for display.
 */
public final class ExportedBrushes {

    public static final NamespacedKey KEY = Objects.requireNonNull(NamespacedKey.fromString("bettergopaint:brush"));

    private ExportedBrushes() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static void write(@NotNull ItemMeta meta, @NotNull BrushSettings settings) {
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, ExportedBrushCodec.encode(settings));
    }

    /**
     * Reads the brush stored on an item. Items exported by BetterGoPaint 1.x carry their settings
     * only in the lore; they are read through the legacy format and converted in place, so the
     * caller should put the item back into the inventory when {@code converted} is true.
     */
    public static @NotNull Optional<Result> read(@NotNull ItemStack item, @NotNull PlayerBrushManager brushManager) {
        if (!item.hasItemMeta()) {
            return Optional.empty();
        }
        ItemMeta meta = item.getItemMeta();
        String stored = meta.getPersistentDataContainer().get(KEY, PersistentDataType.STRING);
        if (stored != null) {
            return ExportedBrushCodec.decode(stored, brushManager::getBrushByName).map(brush -> new Result(brush, false));
        }
        List<Component> lore = meta.lore();
        Component displayName = meta.displayName();
        if (lore == null || lore.isEmpty() || displayName == null) {
            return Optional.empty();
        }
        PlainTextComponentSerializer plain = PlainTextComponentSerializer.plainText();
        return brushManager.getBrushHandler(plain.serialize(displayName)).map(brush -> {
            ExportedPlayerBrush legacy = ExportedBrushCodec.decodeLegacy(brush, lore.stream().map(plain::serialize).toList());
            item.editMeta(converted -> write(converted, legacy));
            return new Result(legacy, true);
        });
    }

    public record Result(@NotNull ExportedPlayerBrush brush, boolean converted) {
    }
}
