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
package net.onelitefeather.bettergopaint.support;

import com.sk89q.worldedit.math.BlockVector3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CountingExtentTest {

    private final Terrain terrain = new Terrain();

    @Test
    @DisplayName("two reads of one position are counted as two reads")
    void countsRepeatedReadsOfOnePosition() {
        CountingExtent extent = new CountingExtent(terrain.flat());
        BlockVector3 position = Terrain.surface();

        extent.getBlock(position);
        extent.getBlock(position);

        assertThat(extent.readCount(position)).as("reads of %s", position).isEqualTo(2);
    }

    @Test
    @DisplayName("reads through the coordinate overload count for the same position")
    void countsBothOverloadsTogether() {
        CountingExtent extent = new CountingExtent(terrain.flat());
        BlockVector3 position = Terrain.surface();

        extent.getBlock(position);
        extent.getBlock(position.x(), position.y(), position.z());

        assertThat(extent.readCount(position)).as("reads of %s", position).isEqualTo(2);
    }

    @Test
    @DisplayName("a position that was never read reports zero reads")
    void unreadPositionHasNoReads() {
        CountingExtent extent = new CountingExtent(terrain.flat());

        extent.getBlock(Terrain.surface());

        assertThat(extent.readCount(Terrain.surface().add(1, 0, 0))).isZero();
    }

    @Test
    @DisplayName("reads return the block of the wrapped extent")
    void returnsTheWrappedBlock() {
        CountingExtent extent = new CountingExtent(terrain.flat());

        assertThat(extent.getBlock(Terrain.surface()).getBlockType()).isSameAs(terrain.stone);
    }

    @Test
    @DisplayName("writes pass through and are not counted as reads")
    void writesAreNotReads() throws Exception {
        MemoryExtent inner = terrain.empty();
        CountingExtent extent = new CountingExtent(inner);
        BlockVector3 position = Terrain.surface();

        extent.setBlock(position, terrain.gold.getDefaultState());

        assertThat(inner.typeAt(position)).isSameAs(terrain.gold);
        assertThat(extent.maxReadCount()).isZero();
    }

    @Test
    @DisplayName("a new counting extent starts without reads")
    void instancesDoNotShareCounts() {
        CountingExtent first = new CountingExtent(terrain.flat());
        first.getBlock(Terrain.surface());

        CountingExtent second = new CountingExtent(terrain.flat());

        assertThat(second.maxReadCount()).isZero();
        assertThat(second.totalReads()).isZero();
    }
}
