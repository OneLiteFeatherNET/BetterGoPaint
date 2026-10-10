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

import com.sk89q.worldedit.math.BlockVector3;
import net.onelitefeather.bettergopaint.support.CountingExtent;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BlockReadCacheTest {

    private final Terrain terrain = new Terrain();

    @Test
    @DisplayName("a position read many times reaches the extent once")
    void repeatedReadsReachTheExtentOnce() {
        CountingExtent extent = new CountingExtent(terrain.flat());
        BlockReadCache cache = new BlockReadCache(extent);
        BlockVector3 position = Terrain.surface();

        cache.getBlock(position);
        cache.getBlock(position);
        cache.getBlock(position.x(), position.y(), position.z());

        assertThat(extent.readCount(position)).as("reads of %s through the extent", position).isEqualTo(1);
    }

    @Test
    @DisplayName("the cache answers with the block the extent holds")
    void answersWithTheExtentsBlock() {
        BlockReadCache cache = new BlockReadCache(terrain.flat());

        assertThat(cache.getBlock(Terrain.surface()).getBlockType()).isSameAs(terrain.stone);
        assertThat(cache.getBlock(Terrain.surface().add(0, 1, 0)).getBlockType()).isSameAs(terrain.air);
    }

    @Test
    @DisplayName("positions outside the packable range are read through without failing")
    void unpackablePositionsStillRead() {
        BlockReadCache cache = new BlockReadCache(terrain.empty());

        assertThat(cache.getBlock(BlockVector3.at(Integer.MAX_VALUE, 64, 0)).getBlockType()).isSameAs(terrain.air);
    }
}
