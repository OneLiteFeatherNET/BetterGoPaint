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

import com.fastasyncworldedit.core.nbt.FaweCompoundTag;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A WorldEdit {@link Extent} that keeps its blocks in a map. Every position that was not set is air.
 * Block types are stored, because the tests never look at block properties.
 */
public final class MemoryExtent implements Extent {

    private final Map<BlockVector3, BlockType> blocks = new LinkedHashMap<>();
    private final Map<BlockVector3, Integer> writes = new LinkedHashMap<>();
    private final BlockType air;
    private final int minY;
    private final int maxY;

    public MemoryExtent(BlockType air, int minY, int maxY) {
        this.air = air;
        this.minY = minY;
        this.maxY = maxY;
    }

    /**
     * Fills the box between the two corners, both inclusive, with the type.
     */
    public MemoryExtent fill(BlockVector3 from, BlockVector3 to, BlockType type) {
        for (int x = from.x(); x <= to.x(); x++) {
            for (int y = from.y(); y <= to.y(); y++) {
                for (int z = from.z(); z <= to.z(); z++) {
                    blocks.put(BlockVector3.at(x, y, z), type);
                }
            }
        }
        return this;
    }

    public MemoryExtent set(int x, int y, int z, BlockType type) {
        blocks.put(BlockVector3.at(x, y, z), type);
        return this;
    }

    public BlockType typeAt(BlockVector3 position) {
        return blocks.getOrDefault(position, air);
    }

    public BlockType typeAt(int x, int y, int z) {
        return typeAt(BlockVector3.at(x, y, z));
    }

    /**
     * All positions that hold the type, in insertion order.
     */
    public Set<BlockVector3> positionsOf(BlockType type) {
        Set<BlockVector3> result = new java.util.LinkedHashSet<>();
        blocks.forEach((position, value) -> {
            if (value.equals(type)) {
                result.add(position);
            }
        });
        return result;
    }

    /**
     * How often a position was written through {@link #setBlock}.
     */
    public int writeCount(BlockVector3 position) {
        return writes.getOrDefault(position, 0);
    }

    /**
     * The positions that were written through {@link #setBlock}.
     */
    public Set<BlockVector3> written() {
        return writes.keySet();
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return BlockVector3.at(-30000000, minY, -30000000);
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return BlockVector3.at(30000000, maxY, 30000000);
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        return typeAt(position).getDefaultState();
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        return typeAt(x, y, z).getDefaultState();
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        throw new UnsupportedOperationException("not needed by the brushes");
    }

    @Override
    public BiomeType getBiome(BlockVector3 position) {
        return null;
    }

    @Override
    public BiomeType getBiomeType(int x, int y, int z) {
        return null;
    }

    @Override
    public BiomeType getBiome(BlockVector2 position) {
        return null;
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 position, T block) throws WorldEditException {
        BlockType previous = typeAt(position);
        BlockType type = block.getBlockType();
        writes.merge(position.toImmutable(), 1, Integer::sum);
        blocks.put(position.toImmutable(), type);
        return !previous.equals(type);
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(int x, int y, int z, T block) throws WorldEditException {
        return setBlock(BlockVector3.at(x, y, z), block);
    }

    @Override
    public boolean tile(int x, int y, int z, FaweCompoundTag tile) throws WorldEditException {
        return false;
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        return false;
    }

    @Override
    public boolean setBiome(int x, int y, int z, BiomeType biome) {
        return false;
    }

    @Override
    public Operation commit() {
        return null;
    }
}
