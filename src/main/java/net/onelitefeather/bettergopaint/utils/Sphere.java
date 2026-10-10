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
        List<BlockVector3> blocks = new ArrayList<>();
        int minX = floor(middlePoint.x() + -radius / 2d);
        int minY = floor(middlePoint.y() + -radius / 2d);
        int minZ = floor(middlePoint.z() + -radius / 2d);
        int maxX = floor(middlePoint.x() + radius / 2d);
        int maxY = floor(middlePoint.y() + radius / 2d);
        int maxZ = floor(middlePoint.z() + radius / 2d);

        switch (axis) {
            case Y:
                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        add(blocks, BlockVector3.at(x, middlePoint.y(), z), middlePoint, radius);
                    }
                }
                break;
            case X:
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        add(blocks, BlockVector3.at(middlePoint.x(), y, z), middlePoint, radius);
                    }
                }
                break;
            case Z:
                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        add(blocks, BlockVector3.at(x, y, middlePoint.z()), middlePoint, radius);
                    }
                }
                break;
            case null:
                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            add(blocks, BlockVector3.at(x, y, z), middlePoint, radius);
                        }
                    }
                }
                break;
        }
        return blocks.stream()
                .filter(position -> Blocks.isInBounds(extent, position.y()))
                .filter(position -> air || !Blocks.isAir(extent, position));
    }

    private static int floor(double value) {
        return (int) Math.floor(value);
    }

    private static void add(@NotNull List<BlockVector3> blocks, @NotNull BlockVector3 position,
                            @NotNull BlockVector3 middlePoint, int radius) {
        if (position.distance(middlePoint) < radius / 2d) {
            blocks.add(position);
        }
    }
}
