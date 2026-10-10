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
import com.sk89q.worldedit.function.mask.Mask;
import net.onelitefeather.bettergopaint.brush.SingleTypeMask;
import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.support.MemoryExtent;
import net.onelitefeather.bettergopaint.support.Terrain;
import net.onelitefeather.bettergopaint.objects.other.SurfaceMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.onelitefeather.bettergopaint.support.BrushAssertions.sphereOf;
import static net.onelitefeather.bettergopaint.support.StrokeBuilder.stroke;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The rules every stroke follows, independent of the brush: palette, mask, the session mask and threads.
 */
class StrokeRulesTest {

    private final Terrain terrain = new Terrain();
    private final BlockVector3 target = Terrain.surface();

    @Test
    @DisplayName("a stroke only places blocks of the palette")
    void placesOnlyPaletteBlocks() {
        MemoryExtent extent = terrain.flat();

        new SphereBrush().apply(extent, stroke().blocks(terrain.gold, terrain.andesite).size(10).build());

        Set<BlockVector3> placed = extent.written();
        assertThat(placed).isNotEmpty();
        assertThat(placed).allMatch(position -> extent.typeAt(position).equals(terrain.gold)
                || extent.typeAt(position).equals(terrain.andesite));
    }

    @Test
    @DisplayName("every block of the palette is chosen about equally often")
    void paletteIsEvenlyWeighted() {
        MemoryExtent extent = terrain.flat();

        new SphereBrush().apply(extent, stroke().blocks(terrain.gold, terrain.andesite).size(16).seed(3).build());

        int gold = extent.positionsOf(terrain.gold).size();
        int andesite = extent.positionsOf(terrain.andesite).size();
        assertThat(gold).isGreaterThan(100);
        assertThat(andesite).isGreaterThan(100);
        assertThat((double) gold / (gold + andesite)).isBetween(0.4, 0.6);
    }

    private MemoryExtent grassAndDirt() {
        MemoryExtent extent = terrain.flat()
                .fill(BlockVector3.at(-12, Terrain.SURFACE, -12), BlockVector3.at(-2, Terrain.SURFACE, 12), terrain.grass)
                .fill(BlockVector3.at(-1, Terrain.SURFACE, -12), BlockVector3.at(12, Terrain.SURFACE, 12), terrain.dirt);
        return extent.set(0, Terrain.SURFACE, 0, terrain.grass);
    }

    @Test
    @DisplayName("with the mask enabled only blocks of the mask type change")
    void maskLimitsTheStroke() {
        MemoryExtent extent = grassAndDirt();
        Set<BlockVector3> grass = sphereOf(grassAndDirt(), target, 10, terrain.grass);
        Mask mask = new SingleTypeMask(terrain.grass);
        StrokeContext context = stroke().blocks(terrain.gold, terrain.andesite).size(10).mask(mask).build();

        new SphereBrush().apply(extent, context);

        assertThat(grass).contains(target).hasSizeGreaterThan(1);
        assertThat(extent.written()).isEqualTo(grass);
        assertThat(extent.typeAt(1, Terrain.SURFACE, 0)).isSameAs(terrain.dirt);
        assertThat(extent.typeAt(0, Terrain.SURFACE - 1, 0)).isSameAs(terrain.stone);
    }

    @Test
    @DisplayName("overlay, underlay and fracture brushes respect the mask as well")
    void maskAppliesToEveryBrush() {
        Mask nothing = new SingleTypeMask(terrain.grass);

        for (Brush brush : new Brush[]{new OverlayBrush(), new UnderlayBrush(), new FractureBrush(), new AngleBrush(),
                new SprayBrush(), new SplatterBrush(), new DiscBrush(), new BucketBrush(), new GradientBrush()}) {
            MemoryExtent extent = terrain.step();
            brush.apply(extent, stroke().blocks(terrain.gold).size(8).mask(nothing).chance(100).build());
            assertThat(extent.written()).as(brush.getName()).isEmpty();
        }
    }

    @Test
    @DisplayName("blocks the mask of the session rejects stay untouched")
    void sessionMaskBlocksChanges() {
        MemoryExtent extent = terrain.flatWithTop(terrain.grass);
        Mask sessionMask = new SingleTypeMask(extent, terrain.grass);

        new SphereBrush().apply(Brush.masked(extent, sessionMask), stroke().blocks(terrain.gold).size(8).build());

        assertThat(extent.positionsOf(terrain.gold)).isNotEmpty().allMatch(position -> position.y() == Terrain.SURFACE);
        assertThat(extent.typeAt(0, Terrain.SURFACE - 1, 0)).isSameAs(terrain.stone);
        assertThat(extent.typeAt(0, Terrain.SURFACE, 0)).isSameAs(terrain.gold);
    }

    @Test
    @DisplayName("without a session mask every block of the stroke changes")
    void noSessionMask() {
        MemoryExtent extent = terrain.flatWithTop(terrain.grass);

        new SphereBrush().apply(Brush.masked(extent, null), stroke().blocks(terrain.gold).size(8).build());

        assertThat(extent.typeAt(0, Terrain.SURFACE - 1, 0)).isSameAs(terrain.gold);
    }

    @Test
    @DisplayName("the surface mode decides which blocks of the sphere are painted")
    void surfaceModeApplies() {
        MemoryExtent extent = terrain.flat();

        new SphereBrush().apply(extent, stroke().blocks(terrain.gold).size(8).surfaceMode(SurfaceMode.DIRECT).build());

        assertThat(extent.written()).isNotEmpty().allMatch(position -> position.y() == Terrain.SURFACE);
    }

    @Test
    @DisplayName("a stroke reads before it writes, so earlier writes cannot change which blocks are chosen")
    void readsBeforeWrites() {
        MemoryExtent extent = terrain.flat();

        // Underlay picks blocks with a solid block above. The palette is air, so every write makes the
        // block above the next layer non-solid. The layers are visited from the bottom up: a brush that
        // read after writing would stop after the first layer it removed.
        new UnderlayBrush().apply(extent, stroke().blocks(terrain.air).size(8).thickness(1).build());

        Set<BlockVector3> expected = sphereOf(terrain.flat(), target, 8, terrain.stone).stream()
                .filter(position -> position.y() < Terrain.SURFACE)
                .collect(java.util.stream.Collectors.toSet());
        assertThat(expected).isNotEmpty();
        assertThat(extent.written()).isEqualTo(expected);
    }

    @Test
    @DisplayName("the same seed changes the same blocks no matter which thread runs the stroke")
    void sameResultOnAnyThread() throws Exception {
        MemoryExtent local = terrain.flat();
        MemoryExtent remote = terrain.flat();
        Brush brush = new SprayBrush();

        brush.apply(local, stroke().blocks(terrain.gold, terrain.andesite).size(10).chance(60).seed(42).build());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(() -> brush.apply(remote,
                    stroke().blocks(terrain.gold, terrain.andesite).size(10).chance(60).seed(42).build())).get(30, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }

        assertThat(remote.positionsOf(terrain.gold)).isNotEmpty().isEqualTo(local.positionsOf(terrain.gold));
        assertThat(remote.positionsOf(terrain.andesite)).isNotEmpty().isEqualTo(local.positionsOf(terrain.andesite));
    }

    @Test
    @DisplayName("every brush paints deterministically for a fixed seed")
    void everyBrushIsDeterministic() {
        for (Brush brush : new Brush[]{new SphereBrush(), new SprayBrush(), new SplatterBrush(), new DiscBrush(),
                new BucketBrush(), new AngleBrush(), new OverlayBrush(), new UnderlayBrush(), new FractureBrush(),
                new GradientBrush()}) {
            MemoryExtent first = terrain.step();
            MemoryExtent second = terrain.step();
            brush.apply(first, stroke().blocks(terrain.gold, terrain.andesite, terrain.dirt).size(10).falloff(50).mixing(40)
                    .chance(50).seed(11).angle(1, 30).build());
            brush.apply(second, stroke().blocks(terrain.gold, terrain.andesite, terrain.dirt).size(10).falloff(50).mixing(40)
                    .chance(50).seed(11).angle(1, 30).build());

            assertThat(second.written()).as(brush.getName()).isEqualTo(first.written());
            for (var type : new com.sk89q.worldedit.world.block.BlockType[]{terrain.gold, terrain.andesite, terrain.dirt}) {
                assertThat(second.positionsOf(type)).as(brush.getName()).isEqualTo(first.positionsOf(type));
            }
        }
    }
}
