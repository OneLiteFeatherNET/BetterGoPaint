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
package net.onelitefeather.bettergopaint.objects.brush;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.utils.BlockReadCache;
import net.onelitefeather.bettergopaint.utils.HeightCache;
import net.onelitefeather.bettergopaint.utils.Sphere;
import org.jetbrains.annotations.NotNull;

public class AngleBrush extends Brush {

    private static final String NAME = "Angle Brush";
    private static final String DESCRIPTION = "Only works on cliffs";
    private static final String HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmRlNDQ4ZjBkYmU3NmJiOGE4MzJjOGYzYjJhMDNkMzViZDRlMjc4NWZhNWU4Mjk4YzI2MTU1MDNmNDdmZmEyIn19fQ==";

    public AngleBrush() {
        super(NAME, DESCRIPTION, HEAD);
    }

    @Override
    public void apply(@NotNull Extent extent, @NotNull StrokeContext context) {
        Extent reads = new BlockReadCache(extent);
        HeightCache heights = new HeightCache(reads);
        placeAll(extent, context, Sphere.getBlocksInRadius(reads, context.target(), context.size(), null, false)
                .filter(position -> passesDefaultChecks(reads, context, position))
                .filter(position -> heights.averageHeightDiffAngle(position, 1) >= 0.1
                        && heights.averageHeightDiffAngle(position, context.angleDistance())
                        >= Math.tan(Math.toRadians(context.angleHeightDifference())))
                .toList());
    }
}
