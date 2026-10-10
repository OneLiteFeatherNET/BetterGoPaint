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

import static org.assertj.core.api.Assertions.assertThat;

class ItemIconsTest {

    @Test
    @DisplayName("every pixel icon is an eight by eight grid whose colours are all defined")
    void everyIconIsAnEightByEightGrid() {
        for (String material : ItemIcons.materials()) {
            ItemIcons.Icon icon = ItemIcons.icon(material);

            assertThat(icon.rows()).as("the rows of the icon of %s", material).hasSize(8)
                    .allMatch(row -> row.length() == 8, "eight pixels per row");
            for (String row : icon.rows()) {
                for (char pixel : row.toCharArray()) {
                    boolean known = pixel == '.' || icon.colours().containsKey(pixel);
                    assertThat(known).as("the pixel %s of the icon of %s has a colour", pixel, material).isTrue();
                }
            }
            assertThat(icon.colours().values()).as("the colours of the icon of %s", material)
                    .allMatch(colour -> colour.matches("#[0-9a-f]{6}"));
        }
    }

    @Test
    @DisplayName("the icons cover the non-cube items of the brush menu")
    void iconsCoverTheMenuItems() {
        assertThat(ItemIcons.materials()).as("the items with a pixel icon")
                .containsAll(List.of("FEATHER", "PLAYER_HEAD", "GOLD_NUGGET", "BROWN_MUSHROOM", "CARVED_PUMPKIN",
                        "JACK_O_LANTERN", "LIGHT_WEIGHTED_PRESSURE_PLATE", "BARRIER", "DAYLIGHT_DETECTOR", "BLAZE_ROD"));
    }

    @Test
    @DisplayName("every stained glass pane of the menu has a tint and no block look")
    void glassPanesAreTintedNotBlocks() {
        for (String pane : List.of("WHITE_STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE", "LIME_STAINED_GLASS_PANE")) {
            assertThat(ItemIcons.isGlassPane(pane)).as("%s is a glass pane", pane).isTrue();
            assertThat(ItemIcons.glassTint(pane)).as("the tint of %s", pane).matches("#[0-9a-f]{6}");
            assertThat(BlockLooks.has(pane)).as("%s has a block look", pane).isFalse();
        }
    }
}
