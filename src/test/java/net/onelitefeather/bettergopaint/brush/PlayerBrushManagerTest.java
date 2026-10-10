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

import net.onelitefeather.bettergopaint.menu.BrushMenu;
import net.onelitefeather.bettergopaint.menu.BrushMenuFactory;
import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.objects.brush.PaintBrush;
import net.onelitefeather.bettergopaint.objects.brush.SphereBrush;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlayerBrushManagerTest {

    private PlayerBrushManager manager;
    private List<Brush> brushes;

    @BeforeEach
    void setUp() {
        manager = new PlayerBrushManager(SettingsHolder.of(Settings.defaults()), mock(BrushMenuFactory.class));
        brushes = manager.getBrushes();
    }

    @Test
    @DisplayName("a player without a brush starts on the first one")
    void cycleForwardWithoutBrushStartsAtFirst() {
        assertThat(manager.cycleForward(null)).isInstanceOf(SphereBrush.class);
        assertThat(manager.cycleBack(null)).isInstanceOf(SphereBrush.class);
    }

    @Test
    @DisplayName("cycling forward walks the list and wraps to the first brush")
    void cycleForwardWraps() {
        assertThat(manager.cycleForward(brushes.getFirst())).isSameAs(brushes.get(1));
        assertThat(manager.cycleForward(brushes.getLast())).isSameAs(brushes.getFirst());
    }

    @Test
    @DisplayName("cycling backwards walks the list and wraps to the last brush")
    void cycleBackWraps() {
        assertThat(manager.cycleBack(brushes.get(1))).isSameAs(brushes.getFirst());
        assertThat(manager.cycleBack(brushes.getFirst())).isSameAs(brushes.getLast());
    }

    @Test
    @DisplayName("every brush can be found again by its name")
    void everyBrushIsFoundByName() {
        for (Brush brush : brushes) {
            assertThat(manager.getBrushHandler(brush.getName()))
                    .as("lookup of %s", brush.getName())
                    .containsSame(brush);
        }
        assertThat(manager.getBrushHandler("Paint Brush")).containsInstanceOf(PaintBrush.class);
    }

    @Test
    @DisplayName("an unknown name finds no brush")
    void unknownNameFindsNothing() {
        assertThat(manager.getBrushHandler("Teleport Brush")).isEmpty();
    }

    @Test
    @DisplayName("an exact name finds exactly that brush")
    void exactNameLookup() {
        for (Brush brush : brushes) {
            assertThat(manager.getBrushByName(brush.getName())).containsSame(brush);
        }
        assertThat(manager.getBrushByName(" ♦ Sphere Brush ♦ ")).isEmpty();
    }

    @Test
    @DisplayName("concurrent lookups for one player share a single brush")
    void concurrentLookupsShareOneBrush() throws Exception {
        AtomicInteger created = new AtomicInteger();
        PlayerBrushManager concurrent = new PlayerBrushManager(
                SettingsHolder.of(Settings.defaults()),
                (ignored, locale) -> {
                    created.incrementAndGet();
                    return mock(PlayerBrush.class);
                }
        );
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.locale()).thenReturn(Locale.ENGLISH);

        int threads = 16;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<PlayerBrush>> results = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                results.add(pool.submit((Callable<PlayerBrush>) () -> {
                    start.await();
                    return concurrent.getBrush(player);
                }));
            }
            start.countDown();

            Set<PlayerBrush> distinct = Collections.newSetFromMap(new IdentityHashMap<>());
            for (Future<PlayerBrush> result : results) {
                distinct.add(result.get(10, TimeUnit.SECONDS));
            }
            assertThat(distinct).hasSize(1);
            assertThat(created).hasValue(1);
        } finally {
            pool.shutdownNow();
        }
    }

    private static Player player() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.locale()).thenReturn(Locale.ENGLISH);
        return player;
    }

    @Test
    @DisplayName("getting a brush does not create its menu")
    void gettingBrushCreatesNoMenu() {
        BrushMenuFactory menus = mock(BrushMenuFactory.class);
        PlayerBrushManager lazy = new PlayerBrushManager(SettingsHolder.of(Settings.defaults()), menus);

        lazy.getBrush(player());

        verify(menus, never()).create(any());
    }

    @Test
    @DisplayName("the menu is created on the first open and reused by later opens")
    void menuIsCreatedOnFirstOpenOnly() {
        BrushMenuFactory menus = mock(BrushMenuFactory.class);
        when(menus.create(any())).thenReturn(mock(BrushMenu.class));
        PlayerBrushManager lazy = new PlayerBrushManager(SettingsHolder.of(Settings.defaults()), menus);
        Player player = player();
        PlayerBrush brush = lazy.getBrush(player);

        brush.openMenu(player);
        brush.openMenu(player);

        verify(menus, times(1)).create(any());
    }
}
