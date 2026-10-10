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
package net.onelitefeather.bettergopaint.objects.brush;

import com.sk89q.worldedit.math.BlockVector3;
import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.support.MemoryExtent;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static net.onelitefeather.bettergopaint.support.BrushAssertions.sphereOf;
import static net.onelitefeather.bettergopaint.support.StrokeBuilder.stroke;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The brushes that look at the shape of the terrain around the target.
 */
class TerrainBrushesTest {

    private final Terrain terrain = new Terrain();
    private final BlockVector3 target = Terrain.surface();

    private Set<BlockVector3> painted(MemoryExtent extent) {
        return extent.positionsOf(terrain.gold);
    }

    @Test
    @DisplayName("angle brush leaves flat ground alone")
    void angleBrushOnFlatGround() {
        MemoryExtent extent = terrain.flat();

        new AngleBrush().apply(extent, stroke().blocks(terrain.gold).size(8).build());

        assertThat(extent.written()).isEmpty();
    }

    @Test
    @DisplayName("angle brush paints only the blocks on the edge of a cliff")
    void angleBrushOnACliff() {
        MemoryExtent extent = terrain.step();

        new AngleBrush().apply(extent, stroke().blocks(terrain.gold).size(8).angle(1, 45).build());

        assertThat(painted(extent)).isNotEmpty()
                .allMatch(position -> position.x() == 0 || position.x() == 1)
                .isSubsetOf(sphereOf(extent, target, 8, terrain.gold));
    }

    @Test
    @DisplayName("angle brush ignores a cliff that is flatter than the wanted angle")
    void angleBrushNeedsASteepEnoughCliff() {
        MemoryExtent extent = terrain.step();

        new AngleBrush().apply(extent, stroke().blocks(terrain.gold).size(8).angle(1, 80).build());

        assertThat(extent.written()).isEmpty();
    }

    @Test
    @DisplayName("overlay brush paints the blocks that have air above them")
    void overlayBrush() {
        MemoryExtent extent = terrain.flat();

        new OverlayBrush().apply(extent, stroke().blocks(terrain.gold).size(8).thickness(1).build());

        assertThat(painted(extent)).isNotEmpty().allMatch(position -> position.y() == Terrain.SURFACE)
                .isEqualTo(sphereOf(extent, target, 8, terrain.gold));
    }

    @Test
    @DisplayName("overlay brush with a larger thickness reaches that many layers down")
    void overlayBrushThickness() {
        MemoryExtent extent = terrain.flat();

        new OverlayBrush().apply(extent, stroke().blocks(terrain.gold).size(8).thickness(2).build());

        assertThat(painted(extent)).isNotEmpty()
                .allMatch(position -> position.y() >= Terrain.SURFACE - 1)
                .anyMatch(position -> position.y() == Terrain.SURFACE - 1)
                .anyMatch(position -> position.y() == Terrain.SURFACE);
    }

    @Test
    @DisplayName("underlay brush paints the blocks that are covered")
    void underlayBrush() {
        MemoryExtent extent = terrain.flat();

        new UnderlayBrush().apply(extent, stroke().blocks(terrain.gold).size(8).thickness(1).build());

        assertThat(painted(extent)).isNotEmpty()
                .allMatch(position -> position.y() <= Terrain.SURFACE - 1)
                .anyMatch(position -> position.y() == Terrain.SURFACE - 1);
        assertThat(extent.typeAt(0, Terrain.SURFACE, 0)).isSameAs(terrain.stone);
    }

    @Test
    @DisplayName("underlay brush with a larger thickness needs that many covering layers")
    void underlayBrushThickness() {
        MemoryExtent extent = terrain.flat();

        new UnderlayBrush().apply(extent, stroke().blocks(terrain.gold).size(8).thickness(2).build());

        assertThat(painted(extent)).isNotEmpty().allMatch(position -> position.y() <= Terrain.SURFACE - 2);
    }

    @Test
    @DisplayName("fracture brush leaves flat ground alone")
    void fractureBrushOnFlatGround() {
        MemoryExtent extent = terrain.flat();

        new FractureBrush().apply(extent, stroke().blocks(terrain.gold).size(8).build());

        assertThat(extent.written()).isEmpty();
    }

    @Test
    @DisplayName("fracture brush paints the blocks inside a crack")
    void fractureBrushInATrench() {
        MemoryExtent extent = terrain.trench();

        new FractureBrush().apply(extent, stroke().blocks(terrain.gold).size(8).target(BlockVector3.at(0, Terrain.SURFACE - 3, 0))
                .fractureDistance(2).build());

        assertThat(painted(extent)).isNotEmpty()
                .allMatch(position -> position.x() == 0)
                .contains(BlockVector3.at(0, Terrain.SURFACE - 3, 0));
        assertThat(extent.typeAt(1, Terrain.SURFACE - 3, 0)).isSameAs(terrain.stone);
    }

    @Test
    @DisplayName("gradient brush lays the palette out from the bottom of the sphere to the top")
    void gradientBrush() {
        MemoryExtent extent = terrain.flat();
        StrokeContext context = stroke().blocks(terrain.dirt, terrain.andesite, terrain.gold).size(6).mixing(0).falloff(0)
                .build();

        new GradientBrush().apply(extent, context);

        // The sphere of size 6 around y = 63 reaches down to y = 61 (y = 60 is exactly 3 blocks away).
        // The gradient starts at y = 60, so the block index is (y - 60) / 6 * 3 rounded down.
        assertThat(extent.typeAt(0, 60, 0)).isSameAs(terrain.stone);
        assertThat(extent.typeAt(0, 61, 0)).isSameAs(terrain.dirt);
        assertThat(extent.typeAt(0, 62, 0)).isSameAs(terrain.andesite);
        assertThat(extent.typeAt(0, 63, 0)).isSameAs(terrain.andesite);
        assertThat(extent.written()).hasSize(sphereOf(terrain.flat(), target, 6, terrain.stone).size());
    }

    @Test
    @DisplayName("gradient brush with a single block paints that block")
    void gradientBrushSingleBlock() {
        MemoryExtent extent = terrain.flat();

        new GradientBrush().apply(extent, stroke().blocks(terrain.gold).size(6).mixing(100).falloff(0).build());

        assertThat(painted(extent)).isEqualTo(sphereOf(terrain.flat(), target, 6, terrain.stone));
    }

    @Test
    @DisplayName("paint brush with a single point paints the sphere around it")
    void paintBrushSinglePoint() {
        MemoryExtent extent = terrain.flat();

        new PaintBrush(SettingsHolder.of(Settings.defaults()))
                .apply(extent, stroke().blocks(terrain.gold).size(6).build());

        assertThat(painted(extent)).isEqualTo(sphereOf(terrain.flat(), target, 6, terrain.stone));
    }

    @Test
    @DisplayName("paint brush with several points follows the curve")
    void paintBrushCurve() {
        MemoryExtent single = terrain.flat();
        MemoryExtent curve = terrain.flat();
        BlockVector3 second = target.add(6, 0, 0);
        PaintBrush brush = new PaintBrush(SettingsHolder.of(Settings.defaults()));

        brush.apply(single, stroke().blocks(terrain.gold).size(4).build());
        brush.apply(curve, stroke().blocks(terrain.gold).size(4).points(target, second).target(second).build());

        assertThat(painted(curve)).containsAll(painted(single)).hasSizeGreaterThan(painted(single).size());
        assertThat(painted(curve)).allMatch(position -> position.y() <= Terrain.SURFACE);
    }

    @Test
    @DisplayName("paint brush only collects points until the player sneaks")
    void paintBrushCollectsPoints() {
        PaintBrush brush = new PaintBrush(SettingsHolder.of(Settings.defaults()));
        UUID player = UUID.randomUUID();
        net.kyori.adventure.audience.Audience feedback = net.kyori.adventure.audience.Audience.empty();
        StrokeContext first = stroke().blocks(terrain.gold).build();
        StrokeContext second = stroke().blocks(terrain.gold).target(target.add(4, 0, 0)).build();

        assertThat(brush.prepare(player, false, feedback, first)).isNull();
        StrokeContext result = brush.prepare(player, true, feedback, second);

        assertThat(result).isNotNull();
        assertThat(result.points()).containsExactly(target, target.add(4, 0, 0));
        assertThat(brush.prepare(player, true, feedback, first).points()).containsExactly(target);
    }
}
