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
import com.sk89q.worldedit.extent.NullExtent;
import com.sk89q.worldedit.function.mask.AbstractExtentMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import org.jetbrains.annotations.NotNull;

/**
 * A WorldEdit mask that accepts blocks of exactly one type. It is the BetterGoPaint mask of a brush.
 * <p>
 * WorldEdit's own {@code BlockTypeMask} could do the same, but it sizes a lookup table from the block
 * registry when it is created, which only exists on a running server. This mask compares the type
 * directly, so it works against any extent and in unit tests.
 */
public final class SingleTypeMask extends AbstractExtentMask {

    private final BlockType type;

    /**
     * Creates a mask for the given block type. It is not bound to an extent, because the extent of a
     * stroke is only known once the stroke runs. Test it with {@link #test(Extent, BlockVector3)}.
     *
     * @param type the accepted block type
     */
    public SingleTypeMask(@NotNull BlockType type) {
        this(new NullExtent(), type);
    }

    /**
     * Creates a mask for the given block type that reads from the given extent when it is tested
     * through {@link #test(BlockVector3)}.
     *
     * @param extent the extent to read from
     * @param type   the accepted block type
     */
    public SingleTypeMask(@NotNull Extent extent, @NotNull BlockType type) {
        super(extent);
        this.type = type;
    }

    /**
     * Returns the accepted block type.
     *
     * @return the block type
     */
    public @NotNull BlockType type() {
        return type;
    }

    @Override
    public boolean test(@NotNull BlockVector3 position) {
        return test(getExtent(), position);
    }

    @Override
    public boolean test(@NotNull Extent extent, @NotNull BlockVector3 position) {
        return extent.getBlock(position).getBlockType().equals(type);
    }

    @Override
    public @NotNull Mask copy() {
        return new SingleTypeMask(getExtent(), type);
    }
}
