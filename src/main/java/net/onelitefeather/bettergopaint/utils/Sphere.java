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
import org.bukkit.Axis;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class Sphere {

    private Sphere() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Returns a stream of positions within a specified radius from a given middle point. Positions
     * outside the build limits of the extent are never returned.
     *
     * @param extent      The extent the blocks are read from.
     * @param middlePoint The middle point from which to calculate the radius.
     * @param radius      The radius value.
     * @param axis        The axis along which to calculate the radius (optional).
     * @param air         Whether air blocks should be included.
     * @return A stream of positions within the specified radius.
     */
    @Contract(value = "_, _, _, _, _ -> new", pure = true)
    public static @NotNull Stream<BlockVector3> getBlocksInRadius(@NotNull Extent extent, @NotNull BlockVector3 middlePoint,
                                                                  int radius, @Nullable Axis axis, boolean air) {
        int centerX = middlePoint.x();
        int centerY = middlePoint.y();
        int centerZ = middlePoint.z();
        int minX = floor(centerX + -radius / 2d);
        int minY = floor(centerY + -radius / 2d);
        int minZ = floor(centerZ + -radius / 2d);
        int maxX = floor(centerX + radius / 2d);
        int maxY = floor(centerY + radius / 2d);
        int maxZ = floor(centerZ + radius / 2d);

        // A disc fixes one coordinate to the middle; the other two range over the sphere's box.
        int fromX = axis == Axis.X ? centerX : minX;
        int toX = axis == Axis.X ? centerX : maxX;
        int fromY = axis == Axis.Y ? centerY : minY;
        int toY = axis == Axis.Y ? centerY : maxY;
        int fromZ = axis == Axis.Z ? centerZ : minZ;
        int toZ = axis == Axis.Z ? centerZ : maxZ;

        // Both sides of the membership test are non-negative, so comparing squares keeps the strict ordering of
        // the distance test: distance < radius / 2 exactly when the squared distance < (radius / 2)^2.
        double halfRadius = radius / 2d;
        double limit = halfRadius * halfRadius;

        // The loop order (x, then y, then z) is part of the contract: seeded brushes draw random numbers per position
        // in this order.
        List<BlockVector3> blocks = new ArrayList<>();
        for (int x = fromX; x <= toX; x++) {
            int dx = x - centerX;
            for (int y = fromY; y <= toY; y++) {
                if (!Blocks.isInBounds(extent, y)) {
                    continue;
                }
                int dy = y - centerY;
                for (int z = fromZ; z <= toZ; z++) {
                    int dz = z - centerZ;
                    if (dx * dx + dy * dy + dz * dz >= limit) {
                        continue;
                    }
                    if (!air && Blocks.isAir(extent, x, y, z)) {
                        continue;
                    }
                    blocks.add(BlockVector3.at(x, y, z));
                }
            }
        }
        return blocks.stream();
    }

    private static int floor(double value) {
        return (int) Math.floor(value);
    }
}
