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

import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import net.onelitefeather.bettergopaint.objects.other.SurfaceMode;
import net.onelitefeather.bettergopaint.support.TestBlocks;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class StrokeContextFactoryTest {

    private final World world = mock(World.class);
    private final BrushSettings settings = mock(BrushSettings.class);
    private final Map<Material, BlockType> converted = new HashMap<>();
    private final Function<Material, BlockType> converter =
            material -> converted.computeIfAbsent(material, key -> TestBlocks.solid(key.name()));

    private void stubSettings(boolean maskEnabled, Material... blocks) {
        when(settings.blocks()).thenReturn(List.of(blocks));
        when(settings.maskEnabled()).thenReturn(maskEnabled);
        when(settings.mask()).thenReturn(Material.GRASS_BLOCK);
        when(settings.axis()).thenReturn(Axis.X);
        when(settings.surfaceMode()).thenReturn(SurfaceMode.RELATIVE);
        when(settings.size()).thenReturn(7);
        when(settings.chance()).thenReturn(40);
        when(settings.falloffStrength()).thenReturn(30);
        when(settings.mixingStrength()).thenReturn(20);
        when(settings.thickness()).thenReturn(3);
        when(settings.fractureDistance()).thenReturn(4);
        when(settings.angleDistance()).thenReturn(5);
        when(settings.angleHeightDifference()).thenReturn(33.5);
    }

    private StrokeContext capture(Random random) {
        return StrokeContextFactory.capture(
                new Location(world, 10, 64, -20), new Location(world, 1.5, 70, 2.5), settings, converter, random
        );
    }

    @Test
    @DisplayName("capturing a context never asks the world for anything")
    void needsNoWorldAccess() {
        stubSettings(true, Material.STONE, Material.ANDESITE);

        capture(new Random(1));

        verifyNoInteractions(world);
    }

    @Test
    @DisplayName("the context holds the settings, the target and the eye position")
    void snapshotsSettings() {
        stubSettings(false, Material.STONE);

        StrokeContext context = capture(new Random(1));

        assertThat(context.target()).isEqualTo(BlockVector3.at(10, 64, -20));
        assertThat(context.points()).containsExactly(BlockVector3.at(10, 64, -20));
        assertThat(context.eye().x()).isCloseTo(1.5, within(1e-9));
        assertThat(context.eye().y()).isCloseTo(71.5, within(1e-9));
        assertThat(context.eye().z()).isCloseTo(2.5, within(1e-9));
        assertThat(context.axis()).isEqualTo(Axis.X);
        assertThat(context.surfaceMode()).isEqualTo(SurfaceMode.RELATIVE);
        assertThat(context.size()).isEqualTo(7);
        assertThat(context.chance()).isEqualTo(40);
        assertThat(context.falloffStrength()).isEqualTo(30);
        assertThat(context.mixingStrength()).isEqualTo(20);
        assertThat(context.thickness()).isEqualTo(3);
        assertThat(context.fractureDistance()).isEqualTo(4);
        assertThat(context.angleDistance()).isEqualTo(5);
        assertThat(context.angleHeightDifference()).isEqualTo(33.5);
    }

    @Test
    @DisplayName("materials are converted once and keep their order")
    void convertsMaterials() {
        stubSettings(false, Material.STONE, Material.ANDESITE, Material.STONE);

        StrokeContext context = capture(new Random(1));

        assertThat(context.blocks()).containsExactly(
                converted.get(Material.STONE), converted.get(Material.ANDESITE), converted.get(Material.STONE)
        );
        assertThat(converted).hasSize(2);
    }

    @Test
    @DisplayName("a disabled mask is null")
    void disabledMask() {
        stubSettings(false, Material.STONE);

        assertThat(capture(new Random(1)).mask()).isNull();
    }

    @Test
    @DisplayName("an enabled mask accepts the mask material")
    void enabledMask() {
        stubSettings(true, Material.STONE);

        StrokeContext context = capture(new Random(1));

        assertThat(context.mask()).isInstanceOfSatisfying(SingleTypeMask.class,
                mask -> assertThat(mask.type()).isSameAs(converted.get(Material.GRASS_BLOCK)));
    }

    @Test
    @DisplayName("the palette picks every listed block with the same weight")
    void paletteWeightsEntriesEqually() {
        BlockType stone = TestBlocks.solid("stone");
        BlockType andesite = TestBlocks.solid("andesite");
        RandomPattern palette = StrokeContextFactory.palette(List.of(stone, andesite), new Random(7));

        Map<BlockType, Integer> counts = new HashMap<>();
        for (int i = 0; i < 4000; i++) {
            counts.merge(palette.applyBlock(BlockVector3.at(i, 0, 0)).getBlockType(), 1, Integer::sum);
        }

        assertThat(counts).containsOnlyKeys(stone, andesite);
        assertThat(counts.get(stone)).isBetween(1800, 2200);
        assertThat(counts.get(andesite)).isBetween(1800, 2200);
    }

    @Test
    @DisplayName("a block that is listed twice is picked twice as often")
    void paletteKeepsDuplicates() {
        BlockType stone = TestBlocks.solid("stone");
        BlockType andesite = TestBlocks.solid("andesite");
        RandomPattern palette = StrokeContextFactory.palette(List.of(stone, stone, andesite), new Random(7));

        Map<BlockType, Integer> counts = new HashMap<>();
        for (int i = 0; i < 6000; i++) {
            counts.merge(palette.applyBlock(BlockVector3.at(i, 0, 0)).getBlockType(), 1, Integer::sum);
        }

        assertThat(counts.get(stone)).isBetween(3700, 4300);
        assertThat(counts.get(andesite)).isBetween(1700, 2300);
    }

    @Test
    @DisplayName("the same seed gives the same palette picks")
    void paletteIsSeeded() {
        BlockType stone = TestBlocks.solid("stone");
        BlockType andesite = TestBlocks.solid("andesite");
        RandomPattern first = StrokeContextFactory.palette(List.of(stone, andesite), new Random(99));
        RandomPattern second = StrokeContextFactory.palette(List.of(stone, andesite), new Random(99));

        for (int i = 0; i < 50; i++) {
            BlockVector3 position = BlockVector3.at(i, 0, 0);
            assertThat(first.applyBlock(position).getBlockType()).isSameAs(second.applyBlock(position).getBlockType());
        }
    }
}
