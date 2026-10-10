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
import net.onelitefeather.bettergopaint.utils.Sphere;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ReferenceSphereTest {

    private static final BlockVector3 MIDDLE = BlockVector3.at(0, 64, 0);

    @Test
    @DisplayName("a voxel exactly size / 2 away from the middle is not a member")
    void boundaryVoxelIsExcluded() {
        Set<BlockVector3> members = ReferenceSphere.members(MIDDLE, 6);

        assertThat(members).as("voxel at distance 3 of a size 6 sphere").doesNotContain(MIDDLE.add(3, 0, 0));
    }

    @Test
    @DisplayName("a voxel closer than size / 2 to the middle is a member")
    void interiorVoxelIsIncluded() {
        Set<BlockVector3> members = ReferenceSphere.members(MIDDLE, 5);

        assertThat(members).as("voxel at distance sqrt(5) of a size 5 sphere").contains(MIDDLE.add(2, 1, 0));
        assertThat(members).as("voxel at distance sqrt(8) of a size 5 sphere").doesNotContain(MIDDLE.add(2, 2, 0));
    }

    @Test
    @DisplayName("a size 6 sphere has the 93 voxels whose squared distance is at most 8")
    void countMatchesTheLatticeCount() {
        assertThat(ReferenceSphere.members(MIDDLE, 6)).as("members of a size 6 sphere").hasSize(93);
    }

    @Test
    @DisplayName("the reference agrees with the current sphere selection on empty ground")
    void agreesWithTheSphereOnEmptyGround() {
        Terrain terrain = new Terrain();
        for (int size = 1; size <= 21; size++) {
            Set<BlockVector3> current = Sphere.getBlocksInRadius(terrain.empty(), MIDDLE, size, null, true)
                    .collect(Collectors.toSet());

            assertThat(current).as("sphere of size %d", size).isEqualTo(ReferenceSphere.members(MIDDLE, size));
        }
    }
}
