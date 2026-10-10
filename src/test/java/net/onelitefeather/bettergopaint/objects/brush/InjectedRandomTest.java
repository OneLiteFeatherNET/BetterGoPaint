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

import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.support.MemoryExtent;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static net.onelitefeather.bettergopaint.support.StrokeBuilder.stroke;
import static org.assertj.core.api.Assertions.assertThat;

class InjectedRandomTest {

    private final Terrain terrain = new Terrain();

    @Test
    @DisplayName("a stroke keeps the random source it was built with")
    void strokeKeepsTheInjectedSource() {
        Random random = new Random(21);

        StrokeContext context = stroke().random(random).build();

        assertThat(context.random()).isSameAs(random);
    }

    @Test
    @DisplayName("two strokes with equally seeded random sources select the same positions")
    void equallySeededStrokesSelectTheSamePositions() {
        MemoryExtent first = terrain.flat();
        MemoryExtent second = terrain.flat();

        new SprayBrush().apply(first, stroke().blocks(terrain.gold).size(8).chance(40).random(new Random(21)).build());
        new SprayBrush().apply(second, stroke().blocks(terrain.gold).size(8).chance(40).random(new Random(21)).build());

        assertThat(first.positionsOf(terrain.gold)).isNotEmpty().isEqualTo(second.positionsOf(terrain.gold));
    }
}
