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
import com.sk89q.worldedit.world.block.BlockType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

public final class ConnectedBlocks {

    private static final BlockVector3[] FACES = new BlockVector3[]{
            BlockVector3.at(0, 0, -1),
            BlockVector3.at(1, 0, 0),
            BlockVector3.at(0, 0, 1),
            BlockVector3.at(-1, 0, 0),
            BlockVector3.at(0, 1, 0),
            BlockVector3.at(0, -1, 0),
    };

    private ConnectedBlocks() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Returns a stream of connected positions starting from a given position, based on a list of positions.
     * Only blocks of the same type as the start block are considered.
     *
     * @param extent the extent to read from
     * @param start  the starting position
     * @param blocks the list of positions to check for connectivity
     * @return a stream of connected positions
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull Stream<BlockVector3> getConnectedBlocks(@NotNull Extent extent, @NotNull BlockVector3 start,
                                                                   @NotNull List<BlockVector3> blocks) {
        if (blocks.isEmpty()) return Stream.empty();
        BlockType startType = extent.getBlock(start).getBlockType();
        Set<BlockVector3> candidates = new HashSet<>(blocks);
        Set<BlockVector3> connected = new LinkedHashSet<>();
        Queue<BlockVector3> toCheck = new LinkedList<>();

        toCheck.add(start);
        connected.add(start);

        while (!toCheck.isEmpty() && connected.size() < blocks.size()) {
            BlockVector3 current = toCheck.poll();
            for (BlockVector3 face : FACES) {
                BlockVector3 relative = current.add(face);
                if (candidates.contains(relative)
                        && !connected.contains(relative)
                        && extent.getBlock(relative).getBlockType().equals(startType)) {
                    connected.add(relative);
                    toCheck.add(relative);
                }
            }
        }

        return connected.stream();
    }
}
