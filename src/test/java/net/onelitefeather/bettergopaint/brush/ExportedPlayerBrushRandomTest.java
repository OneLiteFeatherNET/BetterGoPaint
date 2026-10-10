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

import net.onelitefeather.bettergopaint.menu.BrushMenuFactory;
import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ExportedPlayerBrushRandomTest {

    private final Settings settings = Settings.defaults();
    private final PlayerBrushManager manager = new PlayerBrushManager(SettingsHolder.of(settings), mock(BrushMenuFactory.class));
    private final Brush disc = manager.getBrushByName("Disc Brush").orElseThrow();

    @Test
    @DisplayName("the jitter source of an exported brush is not a cryptographic generator")
    void jitterSourceIsNotSecureRandom() {
        ExportedPlayerBrush brush = ExportedPlayerBrush.builder(disc, settings).build();

        assertThat(brush.random())
                .as("brush jitter needs no cryptographic strength, a SecureRandom is a synchronized hot spot")
                .isNotInstanceOf(SecureRandom.class);
    }

    @Test
    @DisplayName("two exported brushes do not share one jitter source")
    void exportedBrushesDoNotShareOneRandom() {
        ExportedPlayerBrush first = ExportedPlayerBrush.builder(disc, settings).build();
        ExportedPlayerBrush second = ExportedPlayerBrush.builder(disc, settings).build();

        assertThat(first.random())
                .as("each exported brush owns its generator instead of a shared static one")
                .isNotSameAs(second.random());
    }
}
