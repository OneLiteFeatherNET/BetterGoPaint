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
package net.onelitefeather.bettergopaint.support;

import net.kyori.adventure.text.Component;
import net.onelitefeather.bettergopaint.menu.MenuBackend;
import net.onelitefeather.bettergopaint.menu.MenuItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * Stands in for the Bukkit server: inventories are mocks and no item is ever created, so menu tests run
 * without a server. Keeps the inventories it handed out, so a test can inspect the menu it opened.
 */
public final class RecordingMenuBackend implements MenuBackend {

    private final List<Inventory> menus = new ArrayList<>();
    private final List<Inventory> pickers = new ArrayList<>();
    private int items;

    @Override
    public Inventory menu(Component title) {
        Inventory inventory = mock(Inventory.class);
        menus.add(inventory);
        return inventory;
    }

    @Override
    public Inventory picker(Component title) {
        Inventory inventory = mock(Inventory.class);
        pickers.add(inventory);
        return inventory;
    }

    @Override
    public ItemStack item(MenuItem item) {
        items++;
        return null;
    }

    public List<Inventory> menus() {
        return List.copyOf(menus);
    }

    public List<Inventory> pickers() {
        return List.copyOf(pickers);
    }

    public int items() {
        return items;
    }
}
