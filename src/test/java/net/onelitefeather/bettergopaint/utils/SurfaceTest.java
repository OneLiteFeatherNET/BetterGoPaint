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
import com.sk89q.worldedit.math.Vector3;
import net.onelitefeather.bettergopaint.objects.other.SurfaceMode;
import net.onelitefeather.bettergopaint.support.MemoryExtent;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SurfaceTest {

    private final Terrain terrain = new Terrain();
    private final MemoryExtent extent = terrain.flat();
    private final Vector3 eye = Vector3.at(0.5, Terrain.SURFACE + 3.5, 0.5);

    @Test
    @DisplayName("direct mode accepts a solid block with a free block above")
    void directTopBlock() {
        assertThat(Surface.isDirectlyOnSurface(extent, BlockVector3.at(2, Terrain.SURFACE, 0))).isTrue();
    }

    @Test
    @DisplayName("direct mode rejects a buried block and air")
    void directRejects() {
        assertThat(Surface.isDirectlyOnSurface(extent, BlockVector3.at(2, Terrain.SURFACE - 1, 0))).isFalse();
        assertThat(Surface.isDirectlyOnSurface(extent, BlockVector3.at(2, Terrain.SURFACE + 1, 0))).isFalse();
    }

    @Test
    @DisplayName("relative mode accepts a block the eye can see")
    void relativeVisible() {
        assertThat(Surface.isRelativelyOnSurface(extent, BlockVector3.at(2, Terrain.SURFACE, 0), eye)).isTrue();
    }

    @Test
    @DisplayName("relative mode rejects a block hidden below the ground")
    void relativeHidden() {
        assertThat(Surface.isRelativelyOnSurface(extent, BlockVector3.at(2, Terrain.SURFACE - 3, 0), eye)).isFalse();
    }

    @Test
    @DisplayName("the surface mode picks the check, and disabled accepts everything")
    void modeSelectsCheck() {
        BlockVector3 buried = BlockVector3.at(2, Terrain.SURFACE - 3, 0);

        assertThat(Surface.isOnSurface(extent, buried, SurfaceMode.DISABLED, eye)).isTrue();
        assertThat(Surface.isOnSurface(extent, buried, SurfaceMode.DIRECT, eye)).isFalse();
        assertThat(Surface.isOnSurface(extent, buried, SurfaceMode.RELATIVE, eye)).isFalse();
        assertThat(Surface.isOnSurface(extent, BlockVector3.at(2, Terrain.SURFACE, 0), SurfaceMode.DIRECT, eye)).isTrue();
    }
}
