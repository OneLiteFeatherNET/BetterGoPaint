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
import net.onelitefeather.bettergopaint.support.ReferenceSphere;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.bukkit.Axis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pins the sphere selection to the strict distance definition for every size the brushes use. These tests pass
 * before and after the sphere is rewritten to avoid per-voxel allocation.
 */
class SphereEquivalenceTest {

    private static final int MAX_SIZE = 21;

    private final Terrain terrain = new Terrain();

    private Set<BlockVector3> sphere(MemoryExtent extent, BlockVector3 middle, int size, Axis axis, boolean air) {
        return Sphere.getBlocksInRadius(extent, middle, size, axis, air).collect(Collectors.toSet());
    }

    @Test
    @DisplayName("sphere selection equals the strict distance definition for sizes 1 to 21, air included")
    void matchesTheDefinitionIncludingAir() {
        MemoryExtent extent = terrain.flat();
        for (BlockVector3 middle : new BlockVector3[]{Terrain.surface(), BlockVector3.at(3, 63, -5)}) {
            for (int size = 1; size <= MAX_SIZE; size++) {
                assertThat(sphere(extent, middle, size, null, true))
                        .as("sphere of size %d around %s", size, middle)
                        .isEqualTo(ReferenceSphere.members(middle, size));
            }
        }
    }

    @Test
    @DisplayName("sphere selection without air equals the definition restricted to solid blocks")
    void matchesTheDefinitionWithoutAir() {
        MemoryExtent extent = terrain.flat();
        for (BlockVector3 middle : new BlockVector3[]{Terrain.surface(), BlockVector3.at(3, 63, -5)}) {
            for (int size = 1; size <= MAX_SIZE; size++) {
                Set<BlockVector3> solid = ReferenceSphere.members(middle, size).stream()
                        .filter(position -> !extent.typeAt(position).equals(terrain.air))
                        .collect(Collectors.toSet());

                assertThat(sphere(extent, middle, size, null, false))
                        .as("solid sphere of size %d around %s", size, middle)
                        .isEqualTo(solid);
            }
        }
    }

    @Test
    @DisplayName("sphere positions come in x, then y, then z order, which the seeded random draws depend on")
    void yieldsPositionsInLoopOrder() {
        MemoryExtent extent = terrain.flat();
        for (int size = 1; size <= MAX_SIZE; size++) {
            assertThat(Sphere.getBlocksInRadius(extent, Terrain.surface(), size, null, true).toList())
                    .as("order of the sphere of size %d", size)
                    .containsExactlyElementsOf(ReferenceSphere.members(Terrain.surface(), size));
        }
    }

    @Test
    @DisplayName("a voxel exactly size / 2 away from the middle of an even sphere is not selected")
    void boundaryVoxelIsNotSelected() {
        MemoryExtent extent = terrain.empty();
        for (int size = 2; size <= MAX_SIZE; size += 2) {
            BlockVector3 boundary = Terrain.surface().add(size / 2, 0, 0);

            assertThat(sphere(extent, Terrain.surface(), size, null, true))
                    .as("boundary voxel of size %d", size)
                    .doesNotContain(boundary);
        }
    }

    @Test
    @DisplayName("each disc is the plane through the middle of the sphere")
    void discsAreThePlanesOfTheSphere() {
        MemoryExtent extent = terrain.empty();
        BlockVector3 middle = Terrain.surface();
        for (Axis axis : Axis.values()) {
            for (int size = 1; size <= MAX_SIZE; size++) {
                Set<BlockVector3> expected = ReferenceSphere.members(middle, size).stream()
                        .filter(position -> switch (axis) {
                            case X -> position.x() == middle.x();
                            case Y -> position.y() == middle.y();
                            case Z -> position.z() == middle.z();
                        })
                        .collect(Collectors.toSet());

                assertThat(sphere(extent, middle, size, axis, true))
                        .as("disc of size %d on %s", size, axis)
                        .isEqualTo(expected);
            }
        }
    }
}
