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

import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A decorator that counts how often each position is read through {@link #getBlock(BlockVector3)} or
 * {@link #getBlock(int, int, int)}. Writes and every other call pass straight through. Each test creates its
 * own instance, so the counts never leak between tests.
 */
public final class CountingExtent extends AbstractDelegateExtent {

    private final Map<BlockVector3, Integer> reads = new LinkedHashMap<>();

    public CountingExtent(Extent extent) {
        super(extent);
    }

    /**
     * How often the position was read.
     */
    public int readCount(BlockVector3 position) {
        return reads.getOrDefault(position, 0);
    }

    /**
     * The largest number of reads of any single position.
     */
    public int maxReadCount() {
        return reads.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    /**
     * The number of reads of all positions together.
     */
    public int totalReads() {
        return reads.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * The positions that were read at least once.
     */
    public Set<BlockVector3> readPositions() {
        return reads.keySet();
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        record(position);
        return getExtent().getBlock(position);
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        record(BlockVector3.at(x, y, z));
        return getExtent().getBlock(x, y, z);
    }

    private void record(BlockVector3 position) {
        reads.merge(position.toImmutable(), 1, Integer::sum);
    }
}
