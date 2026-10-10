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
import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.support.CountingTextRenderer;
import net.onelitefeather.bettergopaint.support.RecordingMenuBackend;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BrushMenuRenderingTest {

    private final CountingTextRenderer renderer = new CountingTextRenderer();
    private final RecordingMenuBackend backend = new RecordingMenuBackend();
    private final AtomicInteger translations = new AtomicInteger();
    private final BrushMenuFactory menus = new BrushMenuFactory(translations::get, renderer, backend);
    private final PlayerBrushManager manager = new PlayerBrushManager(SettingsHolder.of(Settings.defaults()), menus);

    private long players;

    private Player player(Locale locale) {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(new UUID(0, ++players));
        when(player.locale()).thenReturn(locale);
        return player;
    }

    private static InventoryView viewing(Inventory top) {
        InventoryView view = mock(InventoryView.class);
        when(view.getTopInventory()).thenReturn(top);
        return view;
    }

    @Test
    @DisplayName("opening the menu twice without a change renders the translations only once")
    void secondOpenRendersNothing() {
        Player player = player(Locale.ENGLISH);
        PlayerBrush brush = manager.getBrush(player);
        brush.openMenu(player);
        int rendered = renderer.calls();
        assertThat(rendered).as("the first open renders the menu").isPositive();

        brush.openMenu(player);

        assertThat(renderer.calls()).as("renders after a second open without changes").isEqualTo(rendered);
    }

    @Test
    @DisplayName("a setting changed while the menu is closed renders nothing until the next open")
    void changeWhileClosedWaitsForNextOpen() {
        Player player = player(Locale.ENGLISH);
        PlayerBrush brush = manager.getBrush(player);
        brush.openMenu(player);
        int rendered = renderer.calls();

        brush.increaseBrushSize(false);

        assertThat(renderer.calls()).as("renders while the menu is closed").isEqualTo(rendered);

        brush.openMenu(player);

        assertThat(renderer.calls()).as("renders on the next open").isGreaterThan(rendered);
    }

    @Test
    @DisplayName("a setting changed while the menu is open writes only the slot that shows it")
    void changeWhileOpenWritesOnlyTheChangedSlot() {
        Player player = player(Locale.ENGLISH);
        PlayerBrush brush = manager.getBrush(player);
        brush.openMenu(player);
        Inventory menu = backend.menus().getFirst();
        InventoryView view = viewing(menu);
        when(player.getOpenInventory()).thenReturn(view);
        clearInvocations(menu);

        brush.increaseBrushSize(false);

        verify(menu, times(1)).setItem(anyInt(), any());
        verify(menu, times(1)).setItem(eq(14), any());
    }

    @Test
    @DisplayName("a translation reload renders the text again on the next open")
    void translationReloadRendersAgain() {
        Player player = player(Locale.ENGLISH);
        PlayerBrush brush = manager.getBrush(player);
        brush.openMenu(player);
        int rendered = renderer.calls();

        translations.incrementAndGet();
        brush.openMenu(player);

        assertThat(renderer.calls()).as("renders after a translation reload").isGreaterThan(rendered);
    }

    @Test
    @DisplayName("a second player in the same locale reuses the rendered labels")
    void sameLocaleReusesLabels() {
        Player first = player(Locale.ENGLISH);
        manager.getBrush(first).openMenu(first);
        int labels = renderer.labels();
        assertThat(labels).as("labels rendered for the first player").isPositive();

        Player second = player(Locale.ENGLISH);
        manager.getBrush(second).openMenu(second);

        assertThat(renderer.labels()).as("labels rendered again for the second player").isEqualTo(labels);
    }

    @Test
    @DisplayName("a player in another locale gets the labels rendered for that locale")
    void otherLocaleRendersItsOwnLabels() {
        Player english = player(Locale.ENGLISH);
        manager.getBrush(english).openMenu(english);

        Player german = player(Locale.GERMAN);
        manager.getBrush(german).openMenu(german);

        assertThat(renderer.labelsFor(Locale.GERMAN)).as("labels rendered for German").isPositive();
    }
}
