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

import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.objects.brush.PaintBrush;
import net.onelitefeather.bettergopaint.objects.brush.SphereBrush;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BrushIllustrationTest {

    private final SettingsHolder settings = GeneratorFixtures.settings();
    private final PlayerBrushManager manager = GeneratorFixtures.brushManager(settings);

    private SvgDocument render(Class<? extends Brush> type) {
        Brush brush = manager.getBrushes().stream().filter(type::isInstance).findFirst().orElseThrow();
        Terrain terrain = new Terrain();
        return BrushIllustration.render(BrushScenes.sceneOf(brush, terrain), BlockPalette.of(terrain));
    }

    private static List<String> texts(SvgDocument document) {
        return document.elements().stream()
                .filter(SvgDocument.Text.class::isInstance)
                .map(element -> ((SvgDocument.Text) element).content())
                .toList();
    }

    private static List<SvgDocument.Use> uses(SvgDocument document) {
        return document.elements().stream()
                .filter(SvgDocument.Use.class::isInstance)
                .map(SvgDocument.Use.class::cast)
                .toList();
    }

    @Test
    @DisplayName("the legend of the sphere picture names the Minecraft materials it shows")
    void legendNamesTheRealMaterials() {
        assertThat(texts(render(SphereBrush.class))).as("the texts of the sphere picture")
                .contains("Grass Block", "Dirt", "Stone", "Andesite", "Cobblestone");
    }

    @Test
    @DisplayName("the legend explains the changed, unchanged, air and clicked markers")
    void legendExplainsTheMarkers() {
        assertThat(texts(render(SphereBrush.class))).as("the texts of the sphere picture")
                .contains("changed by the brush", "unchanged (faded)", "air", "clicked block");
    }

    @Test
    @DisplayName("an unchanged grass block is drawn with the grass tile at reduced opacity")
    void unchangedBlocksAreFaded() {
        assertThat(uses(render(SphereBrush.class))).as("the tiles of the sphere picture")
                .anyMatch(use -> use.target().startsWith("block-grass-block-top") && use.opacity() < 100);
    }

    @Test
    @DisplayName("a block placed by the brush is drawn at full opacity")
    void changedBlocksAreNotFaded() {
        assertThat(uses(render(SphereBrush.class))).as("the tiles of the sphere picture")
                .anyMatch(use -> use.target().startsWith("block-stone-") && use.opacity() == 100);
    }

    @Test
    @DisplayName("air is drawn in the sky tint")
    void airIsSkyTint() {
        assertThat(render(SphereBrush.class).elements()).as("the rectangles of the sphere picture")
                .anyMatch(element -> element instanceof SvgDocument.Rect rect && rect.fill().equals("#dbeafe"));
    }

    @Test
    @DisplayName("the clicked block is outlined in the picture")
    void clickedBlockIsOutlined() {
        assertThat(render(SphereBrush.class).elements()).as("the rectangles of the sphere picture")
                .anyMatch(element -> element instanceof SvgDocument.Rect rect
                        && rect.stroke().equals("#111827") && rect.strokeWidth() == 2);
    }

    @Test
    @DisplayName("the paint picture numbers every control point from 1 to 6")
    void paintPictureNumbersEveryControlPoint() {
        assertThat(texts(render(PaintBrush.class))).as("the texts of the paint picture")
                .contains("1", "2", "3", "4", "5", "6");
    }

    @Test
    @DisplayName("the paint picture draws the stroke as a path through the control points")
    void paintPictureDrawsThePath() {
        assertThat(render(PaintBrush.class).elements()).as("the elements of the paint picture")
                .anyMatch(element -> element instanceof SvgDocument.Polyline line && line.points().size() == 6);
    }
}
