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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The strict distance definition of a sphere, written the slow and obvious way. A position is a member when its
 * Euclidean distance to the middle point is strictly less than {@code size / 2d}. The optimised sphere code is
 * compared against this helper, so it must never be tuned to match the implementation.
 */
public final class ReferenceSphere {

    private ReferenceSphere() {
    }

    /**
     * Every position whose distance to the middle is strictly less than {@code size / 2d}.
     *
     * @param middle the middle point
     * @param size   the diameter of the sphere
     * @return the members, in the order of the loops over x, y and z
     */
    public static Set<BlockVector3> members(BlockVector3 middle, int size) {
        Set<BlockVector3> result = new LinkedHashSet<>();
        int reach = size / 2 + 1;
        for (int x = -reach; x <= reach; x++) {
            for (int y = -reach; y <= reach; y++) {
                for (int z = -reach; z <= reach; z++) {
                    if (Math.sqrt(x * x + y * y + z * z) < size / 2d) {
                        result.add(middle.add(x, y, z));
                    }
                }
            }
        }
        return result;
    }
}
