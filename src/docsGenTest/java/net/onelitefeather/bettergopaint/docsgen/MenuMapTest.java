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
package net.onelitefeather.bettergopaint.docsgen;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuMapTest {

    @Test
    @DisplayName("the menu map has one cell for each of the 54 slots, in slot order")
    void menuMapHasFiftyFourCells() {
        List<MenuMap.MenuCell> cells = MenuMap.cells(MenuStates.sphereDefault(), EnglishText.fromClasspath());

        assertThat(cells).as("the cells of the default sphere menu").hasSize(54);
        assertThat(cells).as("the slot numbers in order")
                .extracting(MenuMap.MenuCell::slot)
                .containsExactlyElementsOf(IntStream.range(0, 54).boxed().toList());
    }

    @Test
    @DisplayName("slot 52 shows the mask block of the brush")
    void slotFiftyTwoShowsTheMaskBlock() {
        MenuMap.MenuCell cell = MenuMap.cells(MenuStates.sprayDisabled(), EnglishText.fromClasspath()).get(52);

        assertThat(cell.material()).as("the material of slot 52 of the spray menu").isEqualTo("DIRT");
        assertThat(cell.purpose()).as("the purpose of slot 52").isEqualTo("Current Mask");
    }

    @Test
    @DisplayName("the mask toggle label resolves to its English text")
    void maskToggleLabelResolves() {
        MenuMap.MenuCell cell = MenuMap.cells(MenuStates.sphereDefault(), EnglishText.fromClasspath()).get(6);

        assertThat(cell.purpose()).as("the purpose of the mask toggle in slot 6").isEqualTo("Mask");
    }

    @Test
    @DisplayName("a translation key without English text fails and names the key")
    void unknownKeyFailsWithItsName() {
        EnglishText empty = EnglishText.of(Map.of());

        assertThatThrownBy(() -> MenuMap.cells(MenuStates.sphereDefault(), empty))
                .as("the cells with an empty bundle")
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("bettergopaint.menu.brush.name");
    }
}
