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

import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import net.onelitefeather.bettergopaint.brush.SingleTypeMask;
import net.onelitefeather.bettergopaint.objects.other.SurfaceMode;
import net.onelitefeather.bettergopaint.support.CountingExtent;
import net.onelitefeather.bettergopaint.support.CountingMask;
import net.onelitefeather.bettergopaint.support.MemoryExtent;
import net.onelitefeather.bettergopaint.support.ReferenceSphere;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static net.onelitefeather.bettergopaint.support.BrushAssertions.sphereOf;
import static net.onelitefeather.bettergopaint.support.StrokeBuilder.stroke;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spray, splatter and gradient decide with a random number whether a candidate is chosen. That decision needs no
 * world data, so it runs before the mask and the surface check, and only the candidates it selects are tested.
 */
class RandomFilterFirstTest {

    private final Terrain terrain = new Terrain();
    private final BlockVector3 target = Terrain.surface();

    /**
     * The chance used by the gradient and splatter falloff, the same expression as the brushes use.
     */
    private static double falloffRate(double distance, int size, int falloffStrength) {
        return (distance - (size / 2d) * ((100d - falloffStrength) / 100d))
                / ((size / 2d) - (size / 2d) * ((100d - falloffStrength) / 100d));
    }

    /**
     * Half the ground is grass, the other half dirt, both at the surface of the flat world.
     */
    private MemoryExtent mixedGround() {
        return terrain.flat()
                .fill(BlockVector3.at(-12, Terrain.SURFACE, -12), BlockVector3.at(-1, Terrain.SURFACE, 12), terrain.grass)
                .fill(BlockVector3.at(0, Terrain.SURFACE, -12), BlockVector3.at(12, Terrain.SURFACE, 12), terrain.dirt);
    }

    @Test
    @DisplayName("spray with chance 0 never asks the mask")
    void sprayWithChanceZeroNeverTestsTheMask() {
        CountingMask mask = new CountingMask(position -> true);

        new SprayBrush().apply(terrain.flat(), stroke().blocks(terrain.gold).size(8).chance(0).mask(mask).build());

        assertThat(mask.tests()).as("mask tests").isZero();
    }

    @Test
    @DisplayName("spray with chance 0 never reads the surface")
    void sprayWithChanceZeroNeverReadsTheSurface() {
        CountingExtent withSurface = new CountingExtent(terrain.flat());
        CountingExtent withoutSurface = new CountingExtent(terrain.flat());

        new SprayBrush().apply(withSurface, stroke().blocks(terrain.gold).size(8).chance(0)
                .surfaceMode(SurfaceMode.DIRECT).build());
        new SprayBrush().apply(withoutSurface, stroke().blocks(terrain.gold).size(8).chance(0)
                .surfaceMode(SurfaceMode.DISABLED).build());

        assertThat(withSurface.totalReads()).as("reads with the surface check").isEqualTo(withoutSurface.totalReads());
    }

    @Test
    @DisplayName("spray tests the mask only for the candidates its chance selects")
    void sprayTestsOnlySelectedCandidates() {
        MemoryExtent world = terrain.flat();
        Random random = new Random(9);
        int expected = 0;
        for (BlockVector3 position : ReferenceSphere.members(target, 8)) {
            if (!world.typeAt(position).equals(terrain.air) && random.nextInt(100) < 30) {
                expected++;
            }
        }
        CountingMask mask = new CountingMask(position -> true);

        new SprayBrush().apply(terrain.flat(), stroke().blocks(terrain.gold).size(8).chance(30).seed(9).mask(mask).build());

        assertThat(mask.tests()).as("mask tests").isEqualTo(expected);
    }

    @Test
    @DisplayName("splatter tests the mask only for the candidates its distance filter selects")
    void splatterTestsOnlySelectedCandidates() {
        MemoryExtent world = terrain.flat();
        Random random = new Random(9);
        int expected = 0;
        for (BlockVector3 position : ReferenceSphere.members(target, 8)) {
            if (!world.typeAt(position).equals(terrain.air)
                    && random.nextDouble() > falloffRate(position.distance(target), 8, 50)) {
                expected++;
            }
        }
        CountingMask mask = new CountingMask(position -> true);

        new SplatterBrush().apply(terrain.flat(), stroke().blocks(terrain.gold).size(8).falloff(50).seed(9).mask(mask).build());

        assertThat(mask.tests()).as("mask tests").isEqualTo(expected);
    }

    @Test
    @DisplayName("gradient tests the mask only for the candidates its distance filter selects")
    void gradientTestsOnlySelectedCandidates() {
        MemoryExtent world = terrain.flat();
        Random random = new Random(9);
        int expected = 0;
        for (BlockVector3 position : ReferenceSphere.members(target, 8)) {
            if (!world.typeAt(position).equals(terrain.air)
                    && random.nextDouble() > falloffRate(position.distance(target), 8, 50)) {
                expected++;
            }
        }
        CountingMask mask = new CountingMask(position -> true);

        new GradientBrush().apply(terrain.flat(), stroke().blocks(terrain.gold).size(8).falloff(50).seed(9).mask(mask).build());

        assertThat(mask.tests()).as("mask tests").isEqualTo(expected);
    }

    @Test
    @DisplayName("spray with chance 100 selects every candidate that passes the surface check")
    void sprayWithChanceHundredSelectsEveryCandidateThatPassesTheChecks() {
        MemoryExtent extent = terrain.flat();

        new SprayBrush().apply(extent, stroke().blocks(terrain.gold).size(8).chance(100)
                .surfaceMode(SurfaceMode.DIRECT).build());

        Set<BlockVector3> expected = sphereOf(terrain.flat(), target, 8, terrain.stone).stream()
                .filter(position -> position.y() == Terrain.SURFACE)
                .collect(Collectors.toSet());
        assertThat(extent.positionsOf(terrain.gold)).as("painted blocks").isNotEmpty().isEqualTo(expected);
    }

    @Test
    @DisplayName("every position a random brush selects passes the mask and lies inside the sphere")
    void selectedPositionsPassTheMaskAndLieInside() {
        Set<BlockVector3> sphere = ReferenceSphere.members(target, 10);
        for (Brush brush : new Brush[]{new SprayBrush(), new SplatterBrush(), new GradientBrush()}) {
            MemoryExtent before = mixedGround();
            MemoryExtent extent = mixedGround();
            Mask mask = new SingleTypeMask(extent, terrain.grass);

            brush.apply(extent, stroke().blocks(terrain.gold, terrain.andesite).size(10).chance(60).falloff(40)
                    .seed(3).mask(mask).build());

            assertThat(extent.written()).as(brush.getName()).isNotEmpty();
            for (BlockVector3 position : extent.written()) {
                assertThat(before.typeAt(position)).as("%s wrote %s", brush.getName(), position)
                        .isSameAs(terrain.grass);
                assertThat(sphere).as("%s wrote %s", brush.getName(), position).contains(position);
            }
        }
    }
}
