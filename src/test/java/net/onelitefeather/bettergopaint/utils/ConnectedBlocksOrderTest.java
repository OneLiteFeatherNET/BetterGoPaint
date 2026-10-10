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
import com.sk89q.worldedit.world.block.BlockType;
import net.onelitefeather.bettergopaint.support.MemoryExtent;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The bucket brush places its blocks in the order the flood discovers them, and the palette draws follow that order.
 * The reference below is the original breadth-first flood, so the order must stay the same.
 */
class ConnectedBlocksOrderTest {

    private static final BlockVector3[] FACES = new BlockVector3[]{
            BlockVector3.at(0, 0, -1),
            BlockVector3.at(1, 0, 0),
            BlockVector3.at(0, 0, 1),
            BlockVector3.at(-1, 0, 0),
            BlockVector3.at(0, 1, 0),
            BlockVector3.at(0, -1, 0),
    };

    private final Terrain terrain = new Terrain();

    private List<BlockVector3> originalFlood(MemoryExtent extent, BlockVector3 start, List<BlockVector3> blocks) {
        BlockType startType = extent.getBlock(start).getBlockType();
        Set<BlockVector3> candidates = new HashSet<>(blocks);
        Set<BlockVector3> connected = new LinkedHashSet<>();
        Queue<BlockVector3> toCheck = new LinkedList<>();
        toCheck.add(start);
        connected.add(start);
        while (!toCheck.isEmpty() && connected.size() < blocks.size()) {
            BlockVector3 current = toCheck.poll();
            for (BlockVector3 face : FACES) {
                BlockVector3 relative = current.add(face);
                if (candidates.contains(relative)
                        && !connected.contains(relative)
                        && extent.getBlock(relative).getBlockType().equals(startType)) {
                    connected.add(relative);
                    toCheck.add(relative);
                }
            }
        }
        return List.copyOf(connected);
    }

    private void assertSameOrderAsOriginal(MemoryExtent extent, BlockVector3 start, int size) {
        List<BlockVector3> candidates = Sphere.getBlocksInRadius(extent, start, size, null, false).toList();

        List<BlockVector3> flood = ConnectedBlocks.getConnectedBlocks(extent, start, candidates).toList();

        assertThat(flood).as("flood from %s with size %d", start, size)
                .containsExactlyElementsOf(originalFlood(extent, start, candidates));
    }

    @Test
    @DisplayName("a flood over separate islands keeps the discovery order of the original flood")
    void islandsKeepTheOrder() {
        MemoryExtent extent = terrain.flat()
                .set(0, Terrain.SURFACE, 0, terrain.dirt)
                .set(1, Terrain.SURFACE, 0, terrain.dirt)
                .set(3, Terrain.SURFACE, 0, terrain.dirt);

        assertSameOrderAsOriginal(extent, Terrain.surface(), 10);
    }

    @Test
    @DisplayName("a flood over plain ground keeps the discovery order of the original flood")
    void groundKeepsTheOrder() {
        for (int size = 2; size <= 12; size++) {
            assertSameOrderAsOriginal(terrain.flat(), Terrain.surface(), size);
        }
    }

    @Test
    @DisplayName("a flood that has to walk around a wall keeps the discovery order of the original flood")
    void wallKeepsTheOrder() {
        MemoryExtent extent = terrain.flat();
        for (int z = -6; z <= 5; z++) {
            extent.set(2, Terrain.SURFACE, z, terrain.air);
        }

        assertSameOrderAsOriginal(extent, Terrain.surface(), 14);
    }

    @Test
    @DisplayName("a flood from a start that is not a candidate keeps the discovery order of the original flood")
    void startOutsideTheCandidates() {
        MemoryExtent extent = terrain.flat();
        BlockVector3 start = Terrain.surface();
        List<BlockVector3> candidates = List.of(start.add(1, 0, 0), start.add(2, 0, 0), start.add(2, 0, 1));

        List<BlockVector3> flood = ConnectedBlocks.getConnectedBlocks(extent, start, candidates).toList();

        assertThat(flood).containsExactlyElementsOf(originalFlood(extent, start, candidates));
    }
}
