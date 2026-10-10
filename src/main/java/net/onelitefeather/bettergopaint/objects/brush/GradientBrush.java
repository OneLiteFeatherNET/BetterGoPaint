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
import com.sk89q.worldedit.world.block.BlockType;
import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.utils.Sphere;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GradientBrush extends Brush {

    private static final String NAME = "Gradient Brush";
    private static final String DESCRIPTION = "Creates gradients";
    private static final String HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjA2MmRhM2QzYjhmMWZkMzUzNDNjYzI3OWZiMGZlNWNmNGE1N2I1YWJjNDMxZmJiNzhhNzNiZjJhZjY3NGYifX19";

    public GradientBrush() {
        super(NAME, DESCRIPTION, HEAD);
    }

    @Override
    public void apply(@NotNull Extent extent, @NotNull StrokeContext context) {
        double y = context.target().y() - (context.size() / 2d);
        List<BlockType> blocks = context.blocks();
        List<BlockVector3> positions = Sphere.getBlocksInRadius(extent, context.target(), context.size(), null, false)
                .filter(position -> {
                    double rate = (position.distance(context.target()) - (context.size() / 2d)
                            * ((100d - context.falloffStrength()) / 100d))
                            / ((context.size() / 2d) - (context.size() / 2d)
                            * ((100d - context.falloffStrength()) / 100d));

                    return context.random().nextDouble() > rate;
                })
                .filter(position -> passesDefaultChecks(extent, context, position))
                .toList();
        List<BlockType> types = new ArrayList<>(positions.size());
        for (BlockVector3 position : positions) {
            int random = (int) (((position.y() - y) / context.size()
                    * blocks.size()) + (context.random().nextDouble() * 2 - 1)
                    * (context.mixingStrength() / 100d));

            types.add(blocks.get(Math.clamp(random, 0, blocks.size() - 1)));
        }
        for (int i = 0; i < positions.size(); i++) {
            place(extent, types.get(i), positions.get(i));
        }
    }
}
