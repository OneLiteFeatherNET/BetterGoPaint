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
import com.sk89q.worldedit.world.block.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Block queries on a WorldEdit {@link Extent}. Positions outside the build limits of the extent read
 * as air, like they did when the Bukkit world was asked.
 */
public final class Blocks {

    private Blocks() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Checks if the given height is inside the build limits of the extent.
     *
     * @param extent the extent to check
     * @param y      the height to check
     * @return true if the height can be read from the extent
     */
    public static boolean isInBounds(@NotNull Extent extent, int y) {
        return y >= extent.getMinY() && y <= extent.getMaxY();
    }

    /**
     * Checks if the block at the given position is air. Positions outside the build limits are air.
     *
     * @param extent   the extent to read from
     * @param position the position to check
     * @return true if the block is air
     */
    public static boolean isAir(@NotNull Extent extent, @NotNull BlockVector3 position) {
        return isAir(extent, position.x(), position.y(), position.z());
    }

    /**
     * Checks if the block at the given coordinates is air. Positions outside the build limits are air.
     *
     * @param extent the extent to read from
     * @param x      the x coordinate
     * @param y      the y coordinate
     * @param z      the z coordinate
     * @return true if the block is air
     */
    public static boolean isAir(@NotNull Extent extent, int x, int y, int z) {
        if (!isInBounds(extent, y)) {
            return true;
        }
        BlockState state = extent.getBlock(x, y, z);
        return state.getBlockType().getMaterial().isAir();
    }

    /**
     * Checks if the block at the given position is solid. Positions outside the build limits are not solid.
     *
     * @param extent   the extent to read from
     * @param position the position to check
     * @return true if the block is solid
     */
    public static boolean isSolid(@NotNull Extent extent, @NotNull BlockVector3 position) {
        if (!isInBounds(extent, position.y())) {
            return false;
        }
        return extent.getBlock(position).getBlockType().getMaterial().isSolid();
    }
}
