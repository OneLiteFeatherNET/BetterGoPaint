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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The brush menu of one player, created on the first open of that menu.
 *
 * <p>A render writes only the slots whose item changed since the last render. A change made while nobody views
 * the menu only marks it dirty, and the next open renders it.
 */
public final class BrushMenu {

    private final Inventory inventory;
    private final GUI gui;
    private final MenuBackend backend;
    private final MenuItem[] shown = new MenuItem[BrushMenuHolder.SIZE];
    private boolean dirty = true;
    private int renderedGeneration;

    BrushMenu(@NotNull Inventory inventory, @NotNull GUI gui, @NotNull MenuBackend backend) {
        this.inventory = inventory;
        this.gui = gui;
        this.backend = backend;
    }

    public @NotNull Inventory inventory() {
        return inventory;
    }

    /**
     * Records that a setting changed while nobody views the menu.
     */
    public void markDirty() {
        dirty = true;
    }

    /**
     * Renders the menu if it is dirty or was rendered under another translation generation.
     *
     * @param brush the settings shown in the menu
     */
    public void refresh(@NotNull PlayerBrush brush) {
        if (dirty || renderedGeneration != gui.generation()) {
            render(brush);
        }
    }

    /**
     * Renders the menu now and writes only the slots whose item changed.
     *
     * @param brush the settings shown in the menu
     */
    public void render(@NotNull PlayerBrush brush) {
        MenuItem[] target = gui.layout(brush);
        Map<MenuItem, ItemStack> built = new HashMap<>();
        for (int slot = 0; slot < target.length; slot++) {
            if (!target[slot].equals(shown[slot])) {
                inventory.setItem(slot, built.computeIfAbsent(target[slot], backend::item));
                shown[slot] = target[slot];
            }
        }
        dirty = false;
        renderedGeneration = gui.generation();
    }
}
