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

public class Height {

    private Height() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Gets the height of the nearest non-empty block at a given position.
     *
     * @param extent   the extent to read from
     * @param position the position to check
     * @return the height of the nearest non-empty block at the position
     */
    public static int getNearestNonEmptyBlock(@NotNull Extent extent, @NotNull BlockVector3 position) {
        int minHeight = extent.getMinY();
        int maxHeight = extent.getMaxY() + 1;
        int x = position.x();
        int z = position.z();
        if (Blocks.isAir(extent, position)) {
            for (int y = position.y(); y >= minHeight; y--) {
                if (!Blocks.isAir(extent, x, y, z)) {
                    return y + 1;
                }
            }
            return minHeight;
        } else {
            for (int y = position.y(); y <= maxHeight; y++) {
                if (Blocks.isAir(extent, x, y, z)) {
                    return y;
                }
            }
            return maxHeight;
        }
    }

    /**
     * Calculates the average height difference of the surrounding blocks from a given position, within a specified distance.
     *
     * @param extent   the extent to read from
     * @param position the position to calculate the average height difference from
     * @param height   the reference height of the nearest non-empty block
     * @param distance the distance at which to calculate the average height difference
     * @return the average height difference of the surrounding blocks within the specified distance
     */
    public static double getAverageHeightDiffFracture(@NotNull Extent extent, @NotNull BlockVector3 position, int height,
                                                      int distance) {
        double totalHeight = 0;
        totalHeight += Math.abs(getNearestNonEmptyBlock(extent, position.add(distance, 0, -distance))) - height;
        totalHeight += Math.abs(getNearestNonEmptyBlock(extent, position.add(distance, 0, distance))) - height;
        totalHeight += Math.abs(getNearestNonEmptyBlock(extent, position.add(-distance, 0, distance))) - height;
        totalHeight += Math.abs(getNearestNonEmptyBlock(extent, position.add(-distance, 0, -distance))) - height;
        totalHeight += Math.abs(getNearestNonEmptyBlock(extent, position.add(0, 0, -distance))) - height;
        totalHeight += Math.abs(getNearestNonEmptyBlock(extent, position.add(0, 0, distance))) - height;
        totalHeight += Math.abs(getNearestNonEmptyBlock(extent, position.add(-distance, 0, 0))) - height;
        totalHeight += Math.abs(getNearestNonEmptyBlock(extent, position.add(distance, 0, 0))) - height;
        return (totalHeight / 8d) / distance;
    }

    /**
     * Calculates the average height difference angle of the surrounding blocks from a given position within a specified distance.
     *
     * @param extent   the extent to read from
     * @param position the position to calculate the average height difference angle from
     * @param distance the distance at which to calculate the average height difference angle
     * @return the average height difference angle of the surrounding blocks within the specified distance
     */
    public static double getAverageHeightDiffAngle(@NotNull Extent extent, @NotNull BlockVector3 position, int distance) {
        double maxHeightDiff = 0;
        double maxHeightDiff2 = 0;
        double diff = Math.abs(getNearestNonEmptyBlock(extent, position.add(distance, 0, -distance))
                - getNearestNonEmptyBlock(extent, position.add(-distance, 0, distance)));
        if (diff >= maxHeightDiff) {
            maxHeightDiff = diff;
            maxHeightDiff2 = maxHeightDiff;
        }
        diff = Math.abs(getNearestNonEmptyBlock(extent, position.add(distance, 0, distance))
                - getNearestNonEmptyBlock(extent, position.add(-distance, 0, -distance)));
        if (diff > maxHeightDiff) {
            maxHeightDiff = diff;
            maxHeightDiff2 = maxHeightDiff;
        }
        diff = Math.abs(getNearestNonEmptyBlock(extent, position.add(distance, 0, 0))
                - getNearestNonEmptyBlock(extent, position.add(-distance, 0, 0)));
        if (diff > maxHeightDiff) {
            maxHeightDiff = diff;
            maxHeightDiff2 = maxHeightDiff;
        }
        diff = Math.abs(getNearestNonEmptyBlock(extent, position.add(0, 0, -distance))
                - getNearestNonEmptyBlock(extent, position.add(0, 0, distance)));
        if (diff > maxHeightDiff) {
            maxHeightDiff = diff;
            maxHeightDiff2 = maxHeightDiff;
        }

        double height = (maxHeightDiff2 + maxHeightDiff) / 2.0;
        return height / (distance * 2d);
    }
}
