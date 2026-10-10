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

import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.support.CountingTextRenderer;
import net.onelitefeather.bettergopaint.support.RecordingMenuBackend;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class BrushPickerTest {

    private final RecordingMenuBackend backend = new RecordingMenuBackend();
    private final BrushMenuFactory menus = new BrushMenuFactory(new AtomicInteger()::get, new CountingTextRenderer(), backend);
    private final PlayerBrushManager manager = new PlayerBrushManager(SettingsHolder.of(Settings.defaults()), mock(BrushMenuFactory.class));

    @Test
    @DisplayName("the picker items are built once per locale, not again for the next viewer")
    void pickerIsBuiltOncePerLocale() {
        menus.createPicker(manager.getBrushes(), Locale.ENGLISH);
        int built = backend.items();
        assertThat(built).as("items built for the first viewer").isPositive();

        menus.createPicker(manager.getBrushes(), Locale.ENGLISH);

        assertThat(backend.items()).as("items built for the second viewer in the same locale").isEqualTo(built);
    }

    @Test
    @DisplayName("every viewer of the picker gets an inventory of their own")
    void everyViewerGetsItsOwnPicker() {
        menus.createPicker(manager.getBrushes(), Locale.ENGLISH);
        menus.createPicker(manager.getBrushes(), Locale.ENGLISH);

        assertThat(backend.pickers()).as("picker inventories handed out").hasSize(2);
        assertThat(backend.pickers().get(0)).as("the picker of the first viewer").isNotSameAs(backend.pickers().get(1));
    }
}
