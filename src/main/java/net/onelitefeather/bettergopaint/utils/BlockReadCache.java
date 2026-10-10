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

import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Remembers the block of every position that the read phase of a stroke asked for, so that the sphere, the masks,
 * the surface checks and the height queries share one read per position. Only reads are cached. A stroke places its
 * blocks through the plain extent after the read phase, so a cache never has to be invalidated by a write.
 * <p>
 * A cache belongs to one stroke and is dropped with it.
 */
public final class BlockReadCache extends AbstractDelegateExtent {

    private final PositionMap<BlockState> states = new PositionMap<>();

    public BlockReadCache(@NotNull Extent extent) {
        super(extent);
    }

    @Override
    public BlockState getBlock(@NotNull BlockVector3 position) {
        return getBlock(position.x(), position.y(), position.z());
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        BlockState known = states.get(x, y, z);
        if (known != null) {
            return known;
        }
        BlockState state = getExtent().getBlock(x, y, z);
        states.put(x, y, z, state);
        return state;
    }
}
