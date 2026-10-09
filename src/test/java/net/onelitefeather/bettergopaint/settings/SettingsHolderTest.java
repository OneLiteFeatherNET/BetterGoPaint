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
package net.onelitefeather.bettergopaint.settings;

import org.bukkit.Axis;
import org.bukkit.Material;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class SettingsHolderTest {

    @TempDir
    Path folder;

    @Test
    @DisplayName("a missing config.yml is written with every key and its default")
    void writesDefaults() throws IOException {
        Path file = folder.resolve("config.yml");

        SettingsHolder holder = new SettingsHolder(file);

        assertThat(holder.get()).isEqualTo(Settings.defaults());
        assertThat(Files.readString(file))
                .contains("max-size: 100", "default-brush: FEATHER", "check-for-updates: true", "max-fracture-distance: 5")
                .contains("# Largest brush size a player can set");
    }

    @Test
    @DisplayName("values from config.yml are read")
    void readsValues() throws IOException {
        Path file = folder.resolve("config.yml");
        Files.writeString(file, """
                generic:
                  max-size: 40
                  default-axis: x
                  default-brush: BLAZE_ROD
                  disabled-worlds:
                    - world_nether
                angle:
                  max-angle-height-difference: 70.5
                """);

        Settings settings = new SettingsHolder(file).get();

        assertThat(settings.generic().maxSize()).isEqualTo(40);
        assertThat(settings.generic().defaultAxis()).isEqualTo(Axis.X);
        assertThat(settings.generic().defaultBrush()).isEqualTo(Material.BLAZE_ROD);
        assertThat(settings.generic().disabledWorlds()).containsExactly("world_nether");
        assertThat(settings.angle().maxAngleHeightDifference()).isEqualTo(70.5);
    }

    @Test
    @DisplayName("invalid values fall back to their default")
    void invalidValuesUseDefaults() throws IOException {
        Path file = folder.resolve("config.yml");
        Files.writeString(file, """
                generic:
                  max-size: huge
                  default-axis: W
                  default-mask: NOT_A_BLOCK
                  check-for-updates: maybe
                """);

        Settings.Generic generic = new SettingsHolder(file).get().generic();
        Settings.Generic defaults = Settings.defaults().generic();

        assertThat(generic.maxSize()).isEqualTo(defaults.maxSize());
        assertThat(generic.defaultAxis()).isEqualTo(defaults.defaultAxis());
        assertThat(generic.defaultMask()).isEqualTo(defaults.defaultMask());
        assertThat(generic.checkForUpdates()).isEqualTo(defaults.checkForUpdates());
    }

    @Test
    @DisplayName("reload publishes a new snapshot without touching the old one")
    void reloadSwapsSnapshot() throws IOException {
        Path file = folder.resolve("config.yml");
        SettingsHolder holder = new SettingsHolder(file);
        Settings before = holder.get();

        Files.writeString(file, Files.readString(file).replace("max-size: 100", "max-size: 25"));
        Settings after = holder.reload();

        assertThat(after.generic().maxSize()).isEqualTo(25);
        assertThat(holder.get()).isSameAs(after);
        assertThat(before.generic().maxSize()).isEqualTo(100);
    }
}
