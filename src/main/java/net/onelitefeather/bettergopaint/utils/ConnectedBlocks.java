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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class ConnectedBlocks {

    private static final int[][] FACES = new int[][]{
            {0, 0, -1},
            {1, 0, 0},
            {0, 0, 1},
            {-1, 0, 0},
            {0, 1, 0},
            {0, -1, 0},
    };

    private ConnectedBlocks() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Returns a stream of connected positions starting from a given position, based on a list of positions.
     * Only blocks of the same type as the start block are considered. The positions come in the order a
     * breadth-first flood from the start discovers them, which the palette draws of a stroke depend on.
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
        Grid grid = new Grid(start, blocks);
        List<BlockVector3> connected = new ArrayList<>();
        ArrayDeque<Integer> toCheck = new ArrayDeque<>();

        int startIndex = grid.index(start.x(), start.y(), start.z());
        grid.visited[startIndex] = true;
        connected.add(start);
        toCheck.add(startIndex);

        while (!toCheck.isEmpty() && connected.size() < blocks.size()) {
            int current = toCheck.poll();
            int x = grid.x(current);
            int y = grid.y(current);
            int z = grid.z(current);
            for (int[] face : FACES) {
                int relativeX = x + face[0];
                int relativeY = y + face[1];
                int relativeZ = z + face[2];
                if (!grid.isCandidate(relativeX, relativeY, relativeZ)) {
                    continue;
                }
                int next = grid.index(relativeX, relativeY, relativeZ);
                if (grid.visited[next]
                        || !extent.getBlock(relativeX, relativeY, relativeZ).getBlockType().equals(startType)) {
                    continue;
                }
                grid.visited[next] = true;
                connected.add(BlockVector3.at(relativeX, relativeY, relativeZ));
                toCheck.add(next);
            }
        }

        return connected.stream();
    }

    /**
     * A dense grid over the box that holds the start and every candidate. It records which positions are candidates
     * and which were already reached, so the flood never allocates a position to look one up.
     */
    private static final class Grid {

        private final int minX;
        private final int minY;
        private final int minZ;
        private final int sizeX;
        private final int sizeY;
        private final int sizeZ;
        private final boolean[] candidate;
        private final boolean[] visited;

        private Grid(BlockVector3 start, List<BlockVector3> blocks) {
            int lowX = start.x();
            int lowY = start.y();
            int lowZ = start.z();
            int highX = lowX;
            int highY = lowY;
            int highZ = lowZ;
            for (BlockVector3 block : blocks) {
                lowX = Math.min(lowX, block.x());
                lowY = Math.min(lowY, block.y());
                lowZ = Math.min(lowZ, block.z());
                highX = Math.max(highX, block.x());
                highY = Math.max(highY, block.y());
                highZ = Math.max(highZ, block.z());
            }
            this.minX = lowX;
            this.minY = lowY;
            this.minZ = lowZ;
            this.sizeX = highX - lowX + 1;
            this.sizeY = highY - lowY + 1;
            this.sizeZ = highZ - lowZ + 1;
            this.candidate = new boolean[sizeX * sizeY * sizeZ];
            this.visited = new boolean[sizeX * sizeY * sizeZ];
            for (BlockVector3 block : blocks) {
                candidate[index(block.x(), block.y(), block.z())] = true;
            }
        }

        private int index(int x, int y, int z) {
            return ((x - minX) * sizeY + (y - minY)) * sizeZ + (z - minZ);
        }

        private boolean isCandidate(int x, int y, int z) {
            int dx = x - minX;
            int dy = y - minY;
            int dz = z - minZ;
            if (dx < 0 || dy < 0 || dz < 0 || dx >= sizeX || dy >= sizeY || dz >= sizeZ) {
                return false;
            }
            return candidate[index(x, y, z)];
        }

        private int x(int index) {
            return minX + index / (sizeY * sizeZ);
        }

        private int y(int index) {
            return minY + (index / sizeZ) % sizeY;
        }

        private int z(int index) {
            return minZ + index % sizeZ;
        }
    }
}
