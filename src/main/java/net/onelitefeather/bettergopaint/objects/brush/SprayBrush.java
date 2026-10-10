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
import net.onelitefeather.bettergopaint.utils.Sphere;
import org.jetbrains.annotations.NotNull;

public class SprayBrush extends Brush {

    private static final String NAME = "Spray Brush";
    private static final String DESCRIPTION = "Configurable random chance brush";
    private static final String HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg4MGY3NjVlYTgwZGVlMzcwODJkY2RmZDk4MTJlZTM2ZmRhODg0ODY5MmE4NDFiZWMxYmJkOWVkNTFiYTIyIn19fQ==";

    public SprayBrush() {
        super(NAME, DESCRIPTION, HEAD);
    }

    @Override
    public void apply(@NotNull Extent extent, @NotNull StrokeContext context) {
        Extent reads = new BlockReadCache(extent);
        placeAll(extent, context, Sphere.getBlocksInRadius(reads, context.target(), context.size(), null, false)
                .filter(position -> context.random().nextInt(100) < context.chance())
                .filter(position -> passesDefaultChecks(reads, context, position))
                .toList());
    }
}
