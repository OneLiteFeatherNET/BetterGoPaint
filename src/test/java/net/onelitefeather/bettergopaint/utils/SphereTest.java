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
import org.bukkit.Axis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SphereTest {

    private final Terrain terrain = new Terrain();
    private final BlockVector3 middle = Terrain.surface();

    private Set<BlockVector3> reference(MemoryExtent extent, int radius, boolean air) {
        Set<BlockVector3> result = new HashSet<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockVector3 position = middle.add(x, y, z);
                    boolean inside = Math.sqrt(x * x + y * y + z * z) < radius / 2d;
                    if (inside && (air || !extent.typeAt(position).equals(terrain.air))) {
                        result.add(position);
                    }
                }
            }
        }
        return result;
    }

    @Test
    @DisplayName("a sphere of size 6 holds every solid block closer than 3 blocks")
    void solidBlocksInsideTheRadius() {
        MemoryExtent extent = terrain.flat();

        Set<BlockVector3> blocks = Sphere.getBlocksInRadius(extent, middle, 6, null, false).collect(Collectors.toSet());

        assertThat(blocks).isEqualTo(reference(extent, 6, false)).isNotEmpty();
        assertThat(blocks).allMatch(position -> position.y() <= Terrain.SURFACE);
    }

    @Test
    @DisplayName("air blocks are only part of the sphere when asked for")
    void airIsOptional() {
        MemoryExtent extent = terrain.flat();

        Set<BlockVector3> withAir = Sphere.getBlocksInRadius(extent, middle, 6, null, true).collect(Collectors.toSet());

        assertThat(withAir).isEqualTo(reference(extent, 6, true));
        assertThat(withAir).anyMatch(position -> position.y() > Terrain.SURFACE);
    }

    @Test
    @DisplayName("a disc on the y axis stays on the height of the middle")
    void yAxisDisc() {
        Set<BlockVector3> blocks = Sphere.getBlocksInRadius(terrain.flat(), middle, 8, Axis.Y, false)
                .collect(Collectors.toSet());

        assertThat(blocks).isNotEmpty().allMatch(position -> position.y() == middle.y());
        assertThat(blocks).contains(middle, middle.add(3, 0, 0), middle.add(0, 0, -3)).doesNotContain(middle.add(4, 0, 0));
    }

    @Test
    @DisplayName("a disc on the x axis stays on the x of the middle")
    void xAxisDisc() {
        Set<BlockVector3> blocks = Sphere.getBlocksInRadius(terrain.flat(), middle, 8, Axis.X, false)
                .collect(Collectors.toSet());

        assertThat(blocks).isNotEmpty().allMatch(position -> position.x() == middle.x());
        assertThat(blocks).contains(middle.add(0, -3, 0), middle.add(0, 0, 3));
    }

    @Test
    @DisplayName("a disc on the z axis stays on the z of the middle")
    void zAxisDisc() {
        Set<BlockVector3> blocks = Sphere.getBlocksInRadius(terrain.flat(), middle, 8, Axis.Z, false)
                .collect(Collectors.toSet());

        assertThat(blocks).isNotEmpty().allMatch(position -> position.z() == middle.z());
        assertThat(blocks).contains(middle.add(3, 0, 0), middle.add(0, -3, 0));
    }

    @Test
    @DisplayName("positions outside the build limits are never returned")
    void respectsBuildLimits() {
        MemoryExtent extent = terrain.empty();
        BlockVector3 bottom = BlockVector3.at(0, Terrain.MIN_Y, 0);

        Set<BlockVector3> blocks = Sphere.getBlocksInRadius(extent, bottom, 8, null, true).collect(Collectors.toSet());

        assertThat(blocks).isNotEmpty().allMatch(position -> position.y() >= Terrain.MIN_Y);
    }

    @Test
    @DisplayName("the sphere reads from the extent it is given and does not change it")
    void doesNotWrite() {
        MemoryExtent extent = terrain.flat();

        Sphere.getBlocksInRadius(extent, middle, 6, null, false).toList();

        assertThat(extent.written()).isEmpty();
    }
}
