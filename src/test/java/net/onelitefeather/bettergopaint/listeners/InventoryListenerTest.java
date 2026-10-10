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
package net.onelitefeather.bettergopaint.listeners;

import net.kyori.adventure.text.Component;
import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.menu.BrushMenuFactory;
import net.onelitefeather.bettergopaint.menu.BrushMenuHolder;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InventoryListenerTest {

    private final PlayerBrushManager brushManager = mock(PlayerBrushManager.class);
    private final InventoryListener listener = new InventoryListener(
            brushManager, SettingsHolder.of(Settings.defaults()), mock(BrushMenuFactory.class));

    private InventoryClickEvent clickInForeignMenuTitled(String title) {
        Inventory top = mock(Inventory.class);
        when(top.getHolder(false)).thenReturn(mock(InventoryHolder.class));
        InventoryView view = mock(InventoryView.class);
        when(view.getTopInventory()).thenReturn(top);
        when(view.getInventory(10)).thenReturn(top);
        when(view.getPlayer()).thenReturn(mock(Player.class));
        when(view.title()).thenReturn(Component.text(title));
        return new InventoryClickEvent(view, InventoryType.SlotType.CONTAINER, 10, ClickType.LEFT, InventoryAction.PICKUP_ALL);
    }

    @Test
    @DisplayName("a foreign inventory titled like the brush menu is left alone")
    void foreignMenuIsIgnored() {
        InventoryClickEvent event = clickInForeignMenuTitled("goPaint Menu");

        listener.menuClick(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    @DisplayName("a foreign inventory titled like the brush picker is left alone")
    void foreignPickerIsIgnored() {
        InventoryClickEvent event = clickInForeignMenuTitled("goPaint Brushes");

        listener.menuBrushClick(event);

        assertThat(event.isCancelled()).isFalse();
    }

    private InventoryDragEvent dragIntoMenu(int rawSlot) {
        Inventory top = mock(Inventory.class);
        when(top.getHolder(false)).thenReturn(mock(BrushMenuHolder.class));
        when(top.getSize()).thenReturn(BrushMenuHolder.SIZE);
        InventoryView view = mock(InventoryView.class);
        when(view.getTopInventory()).thenReturn(top);
        Map<Integer, ItemStack> newItems = new HashMap<>();
        newItems.put(rawSlot, null);
        return new InventoryDragEvent(view, null, null, false, newItems);
    }

    @Test
    @DisplayName("dragging an item into the brush menu is cancelled, so no slot changes outside the plugin")
    void dragIntoMenuIsCancelled() {
        InventoryDragEvent event = dragIntoMenu(10);

        listener.menuDrag(event);

        assertThat(event.isCancelled()).as("dragging into a menu slot").isTrue();
    }

    @Test
    @DisplayName("dragging inside the player's own inventory next to the menu is still allowed")
    void dragInsideOwnInventoryIsAllowed() {
        InventoryDragEvent event = dragIntoMenu(BrushMenuHolder.SIZE + 5);

        listener.menuDrag(event);

        assertThat(event.isCancelled()).as("dragging in the player's own inventory").isFalse();
    }
}
