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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryExtentTest {

    private final Terrain terrain = new Terrain();

    @Test
    @DisplayName("a block that was set can be read back")
    void roundTripsABlock() {
        MemoryExtent extent = terrain.empty();
        BlockVector3 position = BlockVector3.at(3, 70, -4);

        boolean changed = extent.setBlock(position, terrain.gold.getDefaultState());

        assertThat(changed).isTrue();
        assertThat(extent.getBlock(position).getBlockType()).isSameAs(terrain.gold);
        assertThat(extent.getBlock(3, 70, -4).getBlockType()).isSameAs(terrain.gold);
        assertThat(extent.writeCount(position)).isEqualTo(1);
    }

    @Test
    @DisplayName("positions that were never set are air")
    void unsetPositionsAreAir() {
        MemoryExtent extent = terrain.empty();

        BlockType type = extent.getBlock(BlockVector3.at(1, 2, 3)).getBlockType();

        assertThat(type).isSameAs(terrain.air);
        assertThat(type.getMaterial().isAir()).isTrue();
    }

    @Test
    @DisplayName("setting the same type again reports no change")
    void sameTypeIsNoChange() {
        MemoryExtent extent = terrain.empty().set(0, 64, 0, terrain.stone);

        boolean changed = extent.setBlock(BlockVector3.at(0, 64, 0), terrain.stone.getDefaultState());

        assertThat(changed).isFalse();
    }

    @Test
    @DisplayName("the build limits are the ones given")
    void reportsBuildLimits() {
        MemoryExtent extent = terrain.empty();

        assertThat(extent.getMinY()).isEqualTo(Terrain.MIN_Y);
        assertThat(extent.getMaxY()).isEqualTo(Terrain.MAX_Y);
    }
}
