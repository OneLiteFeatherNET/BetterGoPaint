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
package net.onelitefeather.bettergopaint.docsgen;

import net.onelitefeather.bettergopaint.brush.PlayerBrush;
import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.menu.BrushMenuFactory;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;

import java.util.Locale;

import static org.mockito.Mockito.mock;

/**
 * Builds the brush registry and the player brush the generator reads, with the same classes the plugin uses. The
 * menu factory of the plugin needs a server to open an inventory, and the generator never opens one, so it gets a
 * stand-in.
 */
final class GeneratorFixtures {

    private GeneratorFixtures() {
    }

    static SettingsHolder settings() {
        return SettingsHolder.of(Settings.defaults());
    }

    static PlayerBrushManager brushManager(SettingsHolder settings) {
        return new PlayerBrushManager(settings, mock(BrushMenuFactory.class));
    }

    static PlayerBrush playerBrush(PlayerBrushManager manager, SettingsHolder settings) {
        return new PlayerBrush(manager, settings, Locale.ENGLISH, mock(BrushMenuFactory.class));
    }
}
