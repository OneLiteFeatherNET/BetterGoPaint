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
import com.sk89q.worldedit.math.Vector3;
import net.onelitefeather.bettergopaint.objects.other.SurfaceMode;
import org.jetbrains.annotations.NotNull;

public final class Surface {

    private Surface() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Checks if the given block is on the surface.
     *
     * @param extent   the extent to read from
     * @param position the position of the block to check
     * @return true if the block is on the surface, false otherwise
     */
    public static boolean isDirectlyOnSurface(@NotNull Extent extent, @NotNull BlockVector3 position) {
        return Blocks.isSolid(extent, position) && !Blocks.isSolid(extent, position.add(0, 1, 0));
    }

    /**
     * Checks if a block is on the surface, taking into account the player's position.
     *
     * @param extent   the extent to read from
     * @param position the position of the block to check
     * @param eye      the position of the player's eyes
     * @return true if the block is on the surface from the player's position, false otherwise
     */
    public static boolean isRelativelyOnSurface(@NotNull Extent extent, @NotNull BlockVector3 position,
                                                @NotNull Vector3 eye) {
        double distanceX = eye.x() - position.x();
        double distanceY = eye.y() - position.y();
        double distanceZ = eye.z() - position.z();

        double x = position.x();
        double y = position.y();
        double z = position.z();
        if (distanceX > 1) {
            x += 1;
        } else if (distanceX > 0) {
            x += 0.5;
        }
        if (distanceY > 1) {
            y += 1;
        } else if (distanceY > 0) {
            y += 0.5;
        }
        if (distanceZ > 1) {
            z += 1;
        } else if (distanceZ > 0) {
            z += 0.5;
        }

        double dx = x - eye.x();
        double dy = y - eye.y();
        double dz = z - eye.z();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        for (int step = 1; step < distance; step++) {
            x += distanceX * (step / distance);
            y += distanceY * (step / distance);
            z += distanceZ * (step / distance);
            if (!Blocks.isAir(extent, (int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a given block is on the surface based on the specified surface mode and eye position.
     *
     * @param extent      the extent to read from
     * @param position    the position of the block to check
     * @param surfaceMode the surface mode to use for the check
     * @param eye         the position of the player's eyes
     * @return true if the block is on the surface based on the surface mode and position, false otherwise
     */
    public static boolean isOnSurface(@NotNull Extent extent, @NotNull BlockVector3 position,
                                      @NotNull SurfaceMode surfaceMode, @NotNull Vector3 eye) {
        return switch (surfaceMode) {
            case RELATIVE -> isRelativelyOnSurface(extent, position, eye);
            case DIRECT -> isDirectlyOnSurface(extent, position);
            case DISABLED -> true;
        };
    }
}
