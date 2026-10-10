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
import net.onelitefeather.bettergopaint.brush.SingleTypeMask;
import net.onelitefeather.bettergopaint.objects.other.SurfaceMode;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.support.CountingExtent;
import net.onelitefeather.bettergopaint.support.MemoryExtent;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static net.onelitefeather.bettergopaint.support.StrokeBuilder.stroke;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * A stroke reads each world position at most once while it selects its candidates, however many filters look at it.
 */
class ReadOnceTest {

    private final Terrain terrain = new Terrain();
    private final BlockVector3 target = Terrain.surface();

    @Test
    @DisplayName("spray reads each position at most once with a mask and the direct surface check")
    void sprayReadsOnce() {
        CountingExtent extent = new CountingExtent(terrain.flat());

        new SprayBrush().apply(extent, stroke().blocks(terrain.gold).size(8).chance(50)
                .surfaceMode(SurfaceMode.DIRECT)
                .mask(new SingleTypeMask(extent, terrain.stone))
                .build());

        assertThat(extent.maxReadCount()).as("most reads of one position").isLessThanOrEqualTo(1);
    }

    @Test
    @DisplayName("overlay reads each position at most once with a thickness of 3")
    void overlayReadsOnce() {
        CountingExtent extent = new CountingExtent(terrain.flat());

        new OverlayBrush().apply(extent, stroke().blocks(terrain.gold).size(8).thickness(3).build());

        assertThat(extent.maxReadCount()).as("most reads of one position").isLessThanOrEqualTo(1);
    }

    @Test
    @DisplayName("underlay reads each position at most once with a thickness of 3")
    void underlayReadsOnce() {
        CountingExtent extent = new CountingExtent(terrain.flat());

        new UnderlayBrush().apply(extent, stroke().blocks(terrain.gold).size(8).thickness(3).build());

        assertThat(extent.maxReadCount()).as("most reads of one position").isLessThanOrEqualTo(1);
    }

    @Test
    @DisplayName("bucket reads each position at most once while it floods a connected region")
    void bucketReadsOnce() {
        MemoryExtent world = terrain.flatWithTop(terrain.dirt);
        CountingExtent extent = new CountingExtent(world);

        new BucketBrush().apply(extent, stroke().blocks(terrain.gold).size(10).build());

        assertThat(extent.maxReadCount()).as("most reads of one position").isLessThanOrEqualTo(1);
    }

    @Test
    @DisplayName("every brush reads each position at most once on a cliff with a mask")
    void everyBrushReadsOnce() {
        for (Brush brush : new Brush[]{new SphereBrush(), new SprayBrush(), new SplatterBrush(), new DiscBrush(),
                new BucketBrush(), new AngleBrush(), new OverlayBrush(), new UnderlayBrush(), new FractureBrush(),
                new GradientBrush(), new PaintBrush(SettingsHolder.of(Settings.defaults()))}) {
            CountingExtent extent = new CountingExtent(terrain.step());
            BlockVector3 end = target.add(4, 0, 0);

            brush.apply(extent, stroke().blocks(terrain.gold, terrain.andesite).size(8).chance(70).thickness(3)
                    .fractureDistance(2).angle(1, 30).falloff(50).mixing(40).seed(4)
                    .surfaceMode(SurfaceMode.DIRECT)
                    .mask(new SingleTypeMask(extent, terrain.stone))
                    .points(target, end).target(end)
                    .build());

            assertThat(extent.maxReadCount()).as("most reads of one position by %s", brush.getName())
                    .isLessThanOrEqualTo(1);
        }
    }
}
