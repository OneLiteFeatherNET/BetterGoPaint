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
        // GUI puts the frame of column 6 in slot 6 and the mask toggle one row below it, in slot 15.
        MenuMap.MenuCell cell = MenuMap.cells(MenuStates.sphereDefault(), EnglishText.fromClasspath()).get(15);

        assertThat(cell.purpose()).as("the purpose of the mask toggle in slot 15").isEqualTo("Mask");
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

    @Test
    @DisplayName("the callouts are numbered in slot order and cover exactly the functional slots")
    void calloutsCoverTheFunctionalSlots() {
        List<MenuMap.MenuCell> cells = MenuMap.cells(MenuStates.sphereDefault(), EnglishText.fromClasspath());
        List<Integer> functional = cells.stream()
                .filter(cell -> !cell.purpose().isEmpty())
                .map(MenuMap.MenuCell::slot)
                .toList();

        List<MenuMap.Callout> callouts = MenuMap.callouts(cells);

        assertThat(callouts).as("the slots of the callouts").extracting(MenuMap.Callout::slot)
                .containsExactlyElementsOf(functional);
        assertThat(callouts).as("the numbers of the callouts")
                .extracting(MenuMap.Callout::number)
                .containsExactlyElementsOf(IntStream.rangeClosed(1, functional.size()).boxed().toList());
    }

    @Test
    @DisplayName("a filler pane gets no callout")
    void fillerPaneHasNoCallout() {
        List<MenuMap.Callout> callouts = MenuMap.callouts(MenuMap.cells(MenuStates.sphereDefault(), EnglishText.fromClasspath()));

        assertThat(callouts).as("the slots of the callouts").extracting(MenuMap.Callout::slot).doesNotContain(0, 1, 3);
    }

    @Test
    @DisplayName("a block item is drawn with its block tile")
    void blockItemIsDrawnWithItsTile() {
        SvgDocument menu = MenuMap.render("title", MenuMap.cells(MenuStates.sphereDefault(), EnglishText.fromClasspath()));

        assertThat(menu.elements()).as("the tiles of the sphere menu")
                .anyMatch(element -> element instanceof SvgDocument.Use use && use.target().startsWith("block-stone-"));
    }

    @Test
    @DisplayName("a non-block item is drawn with its pixel icon")
    void nonBlockItemIsDrawnWithItsIcon() {
        SvgDocument menu = MenuMap.render("title", MenuMap.cells(MenuStates.sphereDefault(), EnglishText.fromClasspath()));

        assertThat(menu.elements()).as("the tiles of the sphere menu")
                .anyMatch(element -> element instanceof SvgDocument.Use use && use.target().startsWith("icon-feather"));
    }

    @Test
    @DisplayName("a filler glass pane is a tinted translucent tile")
    void fillerGlassPaneIsTranslucent() {
        SvgDocument menu = MenuMap.render("title", MenuMap.cells(MenuStates.sphereDefault(), EnglishText.fromClasspath()));
        String tint = ItemIcons.glassTint("GRAY_STAINED_GLASS_PANE");

        assertThat(menu.elements()).as("the rectangles of the sphere menu")
                .anyMatch(element -> element instanceof SvgDocument.Rect rect
                        && rect.fill().equals(tint) && rect.opacity() < 100);
    }

    @Test
    @DisplayName("a menu item that is neither a block tile nor an icon fails and names its material")
    void itemWithoutTileOrIconIsNamed() {
        List<MenuMap.MenuCell> cells = List.of(new MenuMap.MenuCell(0, "MAGMA_BLOCK", "Magma"));

        assertThatThrownBy(() -> MenuMap.render("title", cells))
                .as("the rendering of an item the generator cannot draw")
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MAGMA_BLOCK");
    }
}
