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
import net.onelitefeather.bettergopaint.utils.Blocks;
import net.onelitefeather.bettergopaint.utils.Sphere;
import org.jetbrains.annotations.NotNull;

public class OverlayBrush extends Brush {

    private static final String NAME = "Overlay Brush";
    private static final String DESCRIPTION = "Only paints blocks\n§8that have air above it";
    private static final String HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGYzMWQ2Zjk2NTRmODc0ZWE5MDk3YWRlZWEwYzk2OTk2ZTc4ZTNmZDM3NTRmYmY5ZWJlOTYzYWRhZDliZTRjIn19fQ==";

    public OverlayBrush() {
        super(NAME, DESCRIPTION, HEAD);
    }

    @Override
    public void apply(@NotNull Extent extent, @NotNull StrokeContext context) {
        Extent reads = new BlockReadCache(extent);
        placeAll(extent, context, Sphere.getBlocksInRadius(reads, context.target(), context.size(), null, false)
                .filter(position -> context.passesMask(reads, position))
                .filter(position -> isOverlay(reads, position, context.thickness()))
                .toList());
    }

    private boolean isOverlay(Extent extent, BlockVector3 position, int thickness) {
        if (!Blocks.isSolid(extent, position)) {
            return false;
        }
        for (int i = 1; i <= thickness; i++) {
            if (!Blocks.isSolid(extent, position.x(), position.y() + i, position.z())) {
                return true;
            }
        }
        return false;
    }
}
