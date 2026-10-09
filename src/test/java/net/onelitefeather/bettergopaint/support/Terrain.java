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
package net.onelitefeather.bettergopaint.support;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;

/**
 * Builds the small worlds the brush tests paint on. All of them are flat or have a feature at the origin.
 */
public final class Terrain {

    public static final int MIN_Y = 0;
    public static final int MAX_Y = 255;
    /**
     * Height of the top layer of {@link #flat}.
     */
    public static final int SURFACE = 63;
    private static final int RADIUS = 12;

    public final BlockType air = TestBlocks.air();
    public final BlockType stone = TestBlocks.solid("stone");
    public final BlockType grass = TestBlocks.solid("grass");
    public final BlockType dirt = TestBlocks.solid("dirt");
    public final BlockType andesite = TestBlocks.solid("andesite");
    public final BlockType gold = TestBlocks.solid("gold");

    public MemoryExtent empty() {
        return new MemoryExtent(air, MIN_Y, MAX_Y);
    }

    /**
     * A world of stone up to and including {@link #SURFACE}.
     */
    public MemoryExtent flat() {
        return empty().fill(BlockVector3.at(-RADIUS, MIN_Y, -RADIUS), BlockVector3.at(RADIUS, SURFACE, RADIUS), stone);
    }

    /**
     * A world of stone with a top layer of the given type at {@link #SURFACE}.
     */
    public MemoryExtent flatWithTop(BlockType top) {
        return flat().fill(BlockVector3.at(-RADIUS, SURFACE, -RADIUS), BlockVector3.at(RADIUS, SURFACE, RADIUS), top);
    }

    /**
     * Flat ground that is four blocks higher for every x greater than zero.
     */
    public MemoryExtent step() {
        return flat().fill(BlockVector3.at(1, SURFACE + 1, -RADIUS), BlockVector3.at(RADIUS, SURFACE + 4, RADIUS), stone);
    }

    /**
     * Flat ground with a one block wide trench along z at x = 0. The trench is three blocks deep.
     */
    public MemoryExtent trench() {
        MemoryExtent extent = flat();
        for (int y = SURFACE - 2; y <= SURFACE; y++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                extent.set(0, y, z, air);
            }
        }
        return extent;
    }

    public static BlockVector3 surface() {
        return BlockVector3.at(0, SURFACE, 0);
    }
}
