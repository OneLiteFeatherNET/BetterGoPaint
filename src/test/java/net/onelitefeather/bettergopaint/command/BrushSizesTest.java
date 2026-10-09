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
package net.onelitefeather.bettergopaint.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BrushSizesTest {

    @Test
    @DisplayName("sizes from 1 up to the configured maximum are valid")
    void boundsComeFromTheMaximum() {
        assertThat(BrushSizes.isValid(1, 100)).isTrue();
        assertThat(BrushSizes.isValid(100, 100)).isTrue();
        assertThat(BrushSizes.isValid(0, 100)).isFalse();
        assertThat(BrushSizes.isValid(101, 100)).isFalse();
        assertThat(BrushSizes.isValid(101, 200)).isTrue();
    }

    @Test
    @DisplayName("suggestions stay inside the range and include the maximum")
    void suggestionsStayInRange() {
        assertThat(BrushSizes.suggestions("", 23))
                .contains("1", "10", "15", "20", "23")
                .doesNotContain("0", "25")
                .allSatisfy(size -> assertThat(Integer.parseInt(size)).isBetween(1, 23));
    }

    @Test
    @DisplayName("suggestions follow what the player typed")
    void suggestionsFilterByInput() {
        assertThat(BrushSizes.suggestions("2", 30)).containsExactly("2", "20", "25");
    }
}
