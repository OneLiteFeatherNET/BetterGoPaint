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

import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.objects.other.Settings;
import net.onelitefeather.bettergopaint.objects.other.SurfaceMode;
import org.bukkit.Axis;
import org.bukkit.Material;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExportedBrushCodecTest {

    private final PlayerBrushManager manager = new PlayerBrushManager();
    private final Brush disc = manager.getBrushByName("Disc Brush").orElseThrow();

    @Test
    @DisplayName("every setting survives encoding and decoding")
    void roundTrip() {
        ExportedPlayerBrush original = ExportedPlayerBrush.builder(disc)
                .size(17)
                .chance(30)
                .thickness(3)
                .axis(Axis.X)
                .angleDistance(4)
                .angleHeightDifference(55.5)
                .fractureDistance(3)
                .falloffStrength(70)
                .mixingStrength(20)
                .blocks(List.of(Material.STONE, Material.ANDESITE))
                .mask(Material.GRASS_BLOCK)
                .surfaceMode(SurfaceMode.RELATIVE)
                .build();

        String encoded = ExportedBrushCodec.encode(original);

        assertThat(encoded).startsWith("version=" + ExportedBrushCodec.FORMAT_VERSION + ";");
        assertThat(ExportedBrushCodec.decode(encoded, manager::getBrushByName)).contains(original);
    }

    @Test
    @DisplayName("unreadable values fall back to the configured defaults")
    void invalidValuesUseDefaults() {
        var decoded = ExportedBrushCodec.decode("version=1;brush=Disc Brush;size=ten;axis=W;blocks=minecraft:stone,nope",
                manager::getBrushByName);

        assertThat(decoded).hasValueSatisfying(brush -> {
            assertThat(brush.size()).isEqualTo(Settings.settings().generic.DEFAULT_SIZE);
            assertThat(brush.axis()).isEqualTo(Settings.settings().generic.DEFAULT_AXIS);
            assertThat(brush.blocks()).containsExactly(Material.STONE);
            assertThat(brush.maskEnabled()).isFalse();
        });
    }

    @Test
    @DisplayName("an unknown or missing brush yields no brush")
    void unknownBrushIsEmpty() {
        assertThat(ExportedBrushCodec.decode("version=1;brush=Teleport Brush;size=3", manager::getBrushByName)).isEmpty();
        assertThat(ExportedBrushCodec.decode("garbage", manager::getBrushByName)).isEmpty();
    }

    @Test
    @DisplayName("lore written by 1.x is read line by line")
    void legacyLoreIsRead() {
        ExportedPlayerBrush brush = ExportedBrushCodec.decodeLegacy(disc, List.of(
                "",
                "Size: 12",
                "Axis: z",
                "Blocks: minecraft:stone, minecraft:dirt",
                "Mask: minecraft:sponge",
                "Surface Mode: Relative"
        ));

        assertThat(brush.size()).isEqualTo(12);
        assertThat(brush.axis()).isEqualTo(Axis.Z);
        assertThat(brush.blocks()).containsExactly(Material.STONE, Material.DIRT);
        assertThat(brush.mask()).isEqualTo(Material.SPONGE);
        assertThat(brush.surfaceMode()).isEqualTo(SurfaceMode.RELATIVE);
    }

    @Test
    @DisplayName("edited legacy lore never throws and keeps the default")
    void editedLegacyLoreUsesDefaults() {
        ExportedPlayerBrush brush = ExportedBrushCodec.decodeLegacy(disc, List.of("Size: ten", "Chance: %", "Axis:"));

        assertThat(brush.size()).isEqualTo(Settings.settings().generic.DEFAULT_SIZE);
        assertThat(brush.chance()).isEqualTo(Settings.settings().generic.DEFAULT_CHANCE);
    }
}
