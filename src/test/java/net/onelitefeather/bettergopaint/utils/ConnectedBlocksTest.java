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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectedBlocksTest {

    private final Terrain terrain = new Terrain();
    private final BlockVector3 start = Terrain.surface();

    private MemoryExtent islands() {
        return terrain.flat()
                .set(0, Terrain.SURFACE, 0, terrain.dirt)
                .set(1, Terrain.SURFACE, 0, terrain.dirt)
                .set(3, Terrain.SURFACE, 0, terrain.dirt);
    }

    @Test
    @DisplayName("only blocks of the start type that touch each other are returned")
    void followsTheStartType() {
        MemoryExtent extent = islands();
        List<BlockVector3> candidates = Sphere.getBlocksInRadius(extent, start, 8, null, false).toList();

        List<BlockVector3> connected = ConnectedBlocks.getConnectedBlocks(extent, start, candidates).toList();

        assertThat(connected).containsExactlyInAnyOrder(start, BlockVector3.at(1, Terrain.SURFACE, 0));
    }

    @Test
    @DisplayName("blocks outside the candidate list are not reached")
    void staysInsideTheCandidates() {
        MemoryExtent extent = islands();
        List<BlockVector3> candidates = List.of(start);

        List<BlockVector3> connected = ConnectedBlocks.getConnectedBlocks(extent, start, candidates).toList();

        assertThat(connected).containsExactly(start);
    }

    @Test
    @DisplayName("a flood of stone spreads through the whole candidate list")
    void floodsAllCandidates() {
        MemoryExtent extent = terrain.flat();
        List<BlockVector3> candidates = Sphere.getBlocksInRadius(extent, start, 6, null, false).toList();

        List<BlockVector3> connected = ConnectedBlocks.getConnectedBlocks(extent, start, candidates).toList();

        assertThat(connected).containsExactlyInAnyOrderElementsOf(candidates);
    }

    @Test
    @DisplayName("no candidates means no result")
    void emptyCandidates() {
        assertThat(ConnectedBlocks.getConnectedBlocks(islands(), start, List.of())).isEmpty();
    }
}
