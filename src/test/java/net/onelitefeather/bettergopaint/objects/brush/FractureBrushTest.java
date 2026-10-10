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
package net.onelitefeather.bettergopaint.objects.brush;

import com.sk89q.worldedit.math.BlockVector3;
import net.onelitefeather.bettergopaint.support.CountingExtent;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static net.onelitefeather.bettergopaint.support.StrokeBuilder.stroke;
import static org.assertj.core.api.Assertions.assertThat;

class FractureBrushTest {

    private final Terrain terrain = new Terrain();

    @Test
    @DisplayName("fracture brush scans the column above a candidate once, not once per filter")
    void selfColumnIsScannedOnce() {
        CountingExtent extent = new CountingExtent(terrain.trench());
        BlockVector3 candidate = BlockVector3.at(0, Terrain.SURFACE - 3, 0);

        new FractureBrush().apply(extent, stroke().blocks(terrain.gold).size(1).target(candidate)
                .fractureDistance(2).build());

        // The candidate is the only block of a size 1 sphere. Its nearest non-empty block is the air above it,
        // which a single scan of the column reads exactly once.
        assertThat(extent.readCount(candidate.add(0, 1, 0)))
                .as("reads of the air above the candidate")
                .isEqualTo(1);
    }
}
