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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PositionMapTest {

    @Test
    @DisplayName("a stored value is found again at the same position")
    void storedValueIsFound() {
        PositionMap<String> map = new PositionMap<>();

        map.put(4, 64, -9, "stone");

        assertThat(map.get(4, 64, -9)).isEqualTo("stone");
    }

    @Test
    @DisplayName("neighbouring positions with swapped or negated coordinates are distinct keys")
    void neighboursAreDistinct() {
        PositionMap<String> map = new PositionMap<>();

        map.put(1, -2, 3, "first");
        map.put(3, -2, 1, "swapped");
        map.put(-1, 2, -3, "negated");

        assertThat(map.get(1, -2, 3)).isEqualTo("first");
        assertThat(map.get(3, -2, 1)).isEqualTo("swapped");
        assertThat(map.get(-1, 2, -3)).isEqualTo("negated");
        assertThat(map.get(-1, -2, -3)).isNull();
    }

    @Test
    @DisplayName("every value survives the table growing")
    void valuesSurviveGrowth() {
        PositionMap<Integer> map = new PositionMap<>();

        for (int x = -20; x < 20; x++) {
            for (int z = -20; z < 20; z++) {
                map.put(x, 70, z, x * 100 + z);
            }
        }

        for (int x = -20; x < 20; x++) {
            for (int z = -20; z < 20; z++) {
                assertThat(map.get(x, 70, z)).as("value at %d, 70, %d", x, z).isEqualTo(x * 100 + z);
            }
        }
    }

    @Test
    @DisplayName("storing a position again replaces its value")
    void replacesValue() {
        PositionMap<String> map = new PositionMap<>();

        map.put(0, 0, 0, "old");
        map.put(0, 0, 0, "new");

        assertThat(map.get(0, 0, 0)).isEqualTo("new");
    }

    @Test
    @DisplayName("positions outside the packable range are not stored")
    void positionsOutsideTheRangeAreNotStored() {
        PositionMap<String> map = new PositionMap<>();

        map.put(Integer.MAX_VALUE, 0, 0, "far");

        assertThat(PositionMap.fits(Integer.MAX_VALUE, 0, 0)).isFalse();
        assertThat(map.get(Integer.MAX_VALUE, 0, 0)).isNull();
    }
}
