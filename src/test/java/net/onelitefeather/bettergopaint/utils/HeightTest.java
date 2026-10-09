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
package net.onelitefeather.bettergopaint.utils;

import com.sk89q.worldedit.math.BlockVector3;
import net.onelitefeather.bettergopaint.support.MemoryExtent;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class HeightTest {

    private final Terrain terrain = new Terrain();

    @Test
    @DisplayName("from inside the ground the nearest non-empty height is the first air above")
    void searchesUpFromSolid() {
        MemoryExtent extent = terrain.flat();

        assertThat(Height.getNearestNonEmptyBlock(extent, BlockVector3.at(0, 40, 0))).isEqualTo(Terrain.SURFACE + 1);
        assertThat(Height.getNearestNonEmptyBlock(extent, BlockVector3.at(0, Terrain.SURFACE, 0)))
                .isEqualTo(Terrain.SURFACE + 1);
    }

    @Test
    @DisplayName("from the air the nearest non-empty height is the block above the ground")
    void searchesDownFromAir() {
        MemoryExtent extent = terrain.flat();

        assertThat(Height.getNearestNonEmptyBlock(extent, BlockVector3.at(0, 90, 0))).isEqualTo(Terrain.SURFACE + 1);
    }

    @Test
    @DisplayName("an empty column ends on the minimum height")
    void emptyColumn() {
        assertThat(Height.getNearestNonEmptyBlock(terrain.empty(), BlockVector3.at(0, 90, 0))).isEqualTo(Terrain.MIN_Y);
    }

    @Test
    @DisplayName("a column that is solid up to the build limit ends above the maximum height")
    void solidColumn() {
        MemoryExtent extent = terrain.empty()
                .fill(BlockVector3.at(0, Terrain.MIN_Y, 0), BlockVector3.at(0, Terrain.MAX_Y, 0), terrain.stone);

        assertThat(Height.getNearestNonEmptyBlock(extent, BlockVector3.at(0, 100, 0))).isEqualTo(Terrain.MAX_Y + 1);
    }

    @Test
    @DisplayName("flat ground has no height difference")
    void flatGroundHasNoAngle() {
        MemoryExtent extent = terrain.flat();

        assertThat(Height.getAverageHeightDiffAngle(extent, Terrain.surface(), 1)).isZero();
        assertThat(Height.getAverageHeightDiffAngle(extent, Terrain.surface(), 3)).isZero();
        assertThat(Height.getAverageHeightDiffFracture(extent, Terrain.surface(), Terrain.SURFACE + 1, 2)).isZero();
    }

    @Test
    @DisplayName("a four block step has a height difference angle of 2 on its edge")
    void stepAngle() {
        MemoryExtent extent = terrain.step();

        assertThat(Height.getAverageHeightDiffAngle(extent, Terrain.surface(), 1)).isCloseTo(2.0, within(1e-9));
        assertThat(Height.getAverageHeightDiffAngle(extent, BlockVector3.at(-5, Terrain.SURFACE, 0), 1)).isZero();
    }

    @Test
    @DisplayName("the bottom of a trench is lower than the ground around it")
    void trenchFracture() {
        MemoryExtent extent = terrain.trench();
        BlockVector3 bottom = BlockVector3.at(0, Terrain.SURFACE - 3, 0);
        int height = Height.getNearestNonEmptyBlock(extent, bottom);

        assertThat(height).isEqualTo(Terrain.SURFACE - 2);
        assertThat(Height.getAverageHeightDiffFracture(extent, bottom, height, 1)).isCloseTo(2.25, within(1e-9));
        assertThat(Height.getAverageHeightDiffFracture(extent, BlockVector3.at(5, Terrain.SURFACE, 0), Terrain.SURFACE + 1, 1))
                .isZero();
    }
}
