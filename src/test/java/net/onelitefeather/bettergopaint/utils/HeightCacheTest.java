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
import net.onelitefeather.bettergopaint.support.CountingExtent;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HeightCacheTest {

    private final Terrain terrain = new Terrain();

    @Test
    @DisplayName("a repeated height query does not read the column again")
    void repeatedQueryDoesNotReadAgain() {
        CountingExtent extent = new CountingExtent(terrain.trench());
        HeightCache heights = new HeightCache(extent);
        BlockVector3 position = BlockVector3.at(0, 60, 0);

        heights.nearestNonEmptyBlock(position);
        int readsAfterFirstQuery = extent.totalReads();
        heights.nearestNonEmptyBlock(position);

        assertThat(extent.totalReads()).as("reads made by the second query").isEqualTo(readsAfterFirstQuery);
    }

    @Test
    @DisplayName("cached heights equal the heights of an uncached scan, from air and from solid blocks")
    void cachedHeightsMatchTheScan() {
        HeightCache heights = new HeightCache(terrain.trench());

        for (BlockVector3 position : new BlockVector3[]{
                BlockVector3.at(0, 70, 0), BlockVector3.at(0, 62, 0), BlockVector3.at(0, 60, 0),
                BlockVector3.at(3, 63, -2), BlockVector3.at(-7, 40, 5), BlockVector3.at(1, 64, 1)}) {
            assertThat(heights.nearestNonEmptyBlock(position))
                    .as("nearest non-empty block at %s", position)
                    .isEqualTo(Height.getNearestNonEmptyBlock(terrain.trench(), position));
        }
    }

    @Test
    @DisplayName("cached angles equal the angles of an uncached computation")
    void cachedAnglesMatchTheComputation() {
        HeightCache heights = new HeightCache(terrain.step());

        for (int distance = 1; distance <= 3; distance++) {
            BlockVector3 position = BlockVector3.at(0, Terrain.SURFACE, 0);
            assertThat(heights.averageHeightDiffAngle(position, distance))
                    .as("angle at %s with distance %d", position, distance)
                    .isEqualTo(Height.getAverageHeightDiffAngle(terrain.step(), position, distance));
        }
    }

    @Test
    @DisplayName("cached fracture differences equal the differences of an uncached computation")
    void cachedFractureMatchesTheComputation() {
        HeightCache heights = new HeightCache(terrain.trench());
        BlockVector3 position = BlockVector3.at(0, 60, 0);
        int self = Height.getNearestNonEmptyBlock(terrain.trench(), position);

        for (int distance = 1; distance <= 3; distance++) {
            assertThat(heights.averageHeightDiffFracture(position, self, distance))
                    .as("fracture difference with distance %d", distance)
                    .isEqualTo(Height.getAverageHeightDiffFracture(terrain.trench(), position, self, distance));
        }
    }
}
