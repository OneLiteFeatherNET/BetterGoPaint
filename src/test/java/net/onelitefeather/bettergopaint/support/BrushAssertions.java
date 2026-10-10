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
import com.sk89q.worldedit.world.block.BlockType;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Helpers that describe the expected result of a brush without using the code under test.
 */
public final class BrushAssertions {

    private BrushAssertions() {
    }

    /**
     * Every position in the box around the middle whose distance to the middle is below half the size.
     */
    public static Set<BlockVector3> sphere(BlockVector3 middle, int size) {
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

    /**
     * The positions of the sphere that hold a block of the given type.
     */
    public static Set<BlockVector3> sphereOf(MemoryExtent extent, BlockVector3 middle, int size, BlockType type) {
        Set<BlockVector3> result = sphere(middle, size);
        result.removeIf(position -> !extent.typeAt(position).equals(type));
        return result;
    }
}
