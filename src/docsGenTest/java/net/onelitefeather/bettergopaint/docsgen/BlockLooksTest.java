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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BlockLooksTest {

    @Test
    @DisplayName("no two materials share a base colour")
    void baseColoursAreDistinct() {
        List<String> colours = BlockLooks.materials().stream()
                .map(material -> BlockLooks.look(material).base())
                .toList();

        assertThat(Set.copyOf(colours)).as("the distinct base colours of the materials")
                .hasSize(BlockLooks.materials().size());
    }

    @Test
    @DisplayName("the legend name of a material is its Minecraft name")
    void labelIsTheMinecraftName() {
        assertThat(BlockLooks.look("GRASS_BLOCK").label()).as("the label of grass block").isEqualTo("Grass Block");
        assertThat(BlockLooks.look("GOLD_BLOCK").label()).as("the label of gold block").isEqualTo("Gold Block");
    }

    @Test
    @DisplayName("a tile has 64 pixels and every pixel is a six digit hex colour")
    void tileHasSixtyFourHexPixels() {
        List<String> pixels = BlockLooks.look("STONE").pixels(BlockLooks.Face.TOP);

        assertThat(pixels).as("the pixels of a stone tile").hasSize(64)
                .allMatch(colour -> colour.matches("#[0-9a-f]{6}"), "a six digit lower case hex colour");
    }

    @Test
    @DisplayName("the tile of a material is the same every time it is computed")
    void tileIsDeterministic() {
        assertThat(BlockLooks.look("STONE").pixels(BlockLooks.Face.TOP))
                .as("the tile of stone computed by a second look at the material")
                .isEqualTo(BlockLooks.look("STONE").pixels(BlockLooks.Face.TOP));
    }

    @Test
    @DisplayName("two materials never share a tile")
    void tilesDifferBetweenMaterials() {
        Set<List<String>> tiles = new HashSet<>();
        for (String material : BlockLooks.materials()) {
            tiles.add(BlockLooks.look(material).pixels(BlockLooks.Face.TOP));
        }

        assertThat(tiles).as("the distinct tiles of the materials").hasSize(BlockLooks.materials().size());
    }

    @Test
    @DisplayName("the top row of a grass side is green and the bottom row is dirt")
    void grassSideHasGreenStripOverDirt() {
        List<String> side = BlockLooks.look("GRASS_BLOCK").pixels(BlockLooks.Face.SIDE);

        for (int column = 0; column < 8; column++) {
            assertThat(BlockLooks.Rgb.of(side.get(column)).green())
                    .as("the green of pixel %d of the top row", column)
                    .isGreaterThan(BlockLooks.Rgb.of(side.get(column)).red());
            assertThat(BlockLooks.Rgb.of(side.get(56 + column)).red())
                    .as("the red of pixel %d of the bottom row", column)
                    .isGreaterThan(BlockLooks.Rgb.of(side.get(56 + column)).green());
        }
    }

    @Test
    @DisplayName("a material without a look fails and names the material")
    void unknownMaterialIsNamed() {
        assertThatThrownBy(() -> BlockLooks.look("MAGMA_BLOCK"))
                .as("the look of a material the generator does not know")
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MAGMA_BLOCK");
    }
}
