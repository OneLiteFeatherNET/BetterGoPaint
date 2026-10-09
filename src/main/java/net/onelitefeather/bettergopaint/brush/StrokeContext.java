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
package net.onelitefeather.bettergopaint.brush;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.AbstractExtentMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.block.BlockType;
import net.onelitefeather.bettergopaint.objects.other.SurfaceMode;
import org.bukkit.Axis;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

/**
 * Everything a brush needs to compute a stroke, captured on the player's thread before the stroke is
 * queued. The context only holds WorldEdit types and plain values, so a brush never has to reach
 * back into Bukkit while the stroke runs.
 *
 * @param target               the position the stroke is centred on
 * @param points               the points of a multi-click stroke in click order, the last one is the {@code target}
 * @param eye                  the position of the player's eyes, used by the relative surface mode
 * @param palette              the pattern that picks the block to place
 * @param blocks               the palette blocks in the order they were configured, used by the gradient brush
 * @param mask                 the BetterGoPaint mask, or null if the mask is disabled
 * @param axis                 the axis of the disc brush
 * @param surfaceMode          the surface mode of the brush
 * @param size                 the diameter of the brush
 * @param chance               the chance in percent of the spray brush
 * @param falloffStrength      the falloff strength in percent
 * @param mixingStrength       the mixing strength in percent of the gradient brush
 * @param thickness            the thickness of the overlay and underlay brush
 * @param fractureDistance     the distance of the fracture brush
 * @param angleDistance        the distance of the angle brush
 * @param angleHeightDifference the height difference in degrees of the angle brush
 * @param random               the source of randomness of the stroke
 */
public record StrokeContext(
        @NotNull BlockVector3 target,
        @NotNull List<BlockVector3> points,
        @NotNull Vector3 eye,
        @NotNull Pattern palette,
        @NotNull List<BlockType> blocks,
        @Nullable Mask mask,
        @NotNull Axis axis,
        @NotNull SurfaceMode surfaceMode,
        int size,
        int chance,
        int falloffStrength,
        int mixingStrength,
        int thickness,
        int fractureDistance,
        int angleDistance,
        double angleHeightDifference,
        @NotNull Random random
) {

    public StrokeContext {
        points = List.copyOf(points);
        blocks = List.copyOf(blocks);
    }

    /**
     * Creates a copy of this context for another target, palette and size. This is how the arguments of
     * {@link com.sk89q.worldedit.command.tool.brush.Brush#build} reach the brush.
     *
     * @param target  the new target
     * @param palette the new palette
     * @param size    the new size
     * @return the new context
     */
    public @NotNull StrokeContext with(@NotNull BlockVector3 target, @NotNull Pattern palette, int size) {
        List<BlockVector3> newPoints = this.target.equals(target) ? points : List.of(target);
        return new StrokeContext(target, newPoints, eye, palette, blocks, mask, axis, surfaceMode, size, chance,
                falloffStrength, mixingStrength, thickness, fractureDistance, angleDistance, angleHeightDifference,
                random);
    }

    /**
     * Creates a copy of this context with the points of a multi-click stroke.
     *
     * @param points the points in click order
     * @return the new context
     */
    public @NotNull StrokeContext withPoints(@NotNull List<BlockVector3> points) {
        return new StrokeContext(target, points, eye, palette, blocks, mask, axis, surfaceMode, size, chance,
                falloffStrength, mixingStrength, thickness, fractureDistance, angleDistance, angleHeightDifference,
                random);
    }

    /**
     * Checks if the BetterGoPaint mask lets the block at the position pass.
     *
     * @param extent   the extent to read the block from
     * @param position the position to check
     * @return true if the mask is disabled or accepts the block
     */
    public boolean passesMask(@NotNull Extent extent, @NotNull BlockVector3 position) {
        if (mask == null) {
            return true;
        }
        if (mask instanceof AbstractExtentMask extentMask) {
            return extentMask.test(extent, position);
        }
        return mask.test(position);
    }
}
