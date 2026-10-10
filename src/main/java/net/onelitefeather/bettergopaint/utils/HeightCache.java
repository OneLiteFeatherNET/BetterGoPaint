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

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import org.jetbrains.annotations.NotNull;

/**
 * Height queries of one stroke. Neighbouring candidates ask for the same columns, so the nearest non-empty block of
 * every position is remembered for the rest of the stroke. The cache is owned by one stroke and is never shared, so
 * it cannot carry state from one stroke into the next.
 */
public final class HeightCache {

    private final Extent extent;
    private final PositionMap<Integer> nearest = new PositionMap<>();

    public HeightCache(@NotNull Extent extent) {
        this.extent = extent;
    }

    /**
     * @see Height#getNearestNonEmptyBlock(Extent, BlockVector3)
     */
    public int nearestNonEmptyBlock(@NotNull BlockVector3 position) {
        return nearestNonEmptyBlock(position.x(), position.y(), position.z());
    }

    /**
     * @see Height#getNearestNonEmptyBlock(Extent, int, int, int)
     */
    public int nearestNonEmptyBlock(int x, int y, int z) {
        Integer known = nearest.get(x, y, z);
        if (known != null) {
            return known;
        }
        int height = Height.getNearestNonEmptyBlock(extent, x, y, z);
        nearest.put(x, y, z, height);
        return height;
    }

    /**
     * @see Height#getAverageHeightDiffFracture(Extent, BlockVector3, int, int)
     */
    public double averageHeightDiffFracture(@NotNull BlockVector3 position, int height, int distance) {
        return Height.getAverageHeightDiffFracture(this::nearestNonEmptyBlock,
                position.x(), position.y(), position.z(), height, distance);
    }

    /**
     * @see Height#getAverageHeightDiffAngle(Extent, BlockVector3, int)
     */
    public double averageHeightDiffAngle(@NotNull BlockVector3 position, int distance) {
        return Height.getAverageHeightDiffAngle(this::nearestNonEmptyBlock,
                position.x(), position.y(), position.z(), distance);
    }
}
