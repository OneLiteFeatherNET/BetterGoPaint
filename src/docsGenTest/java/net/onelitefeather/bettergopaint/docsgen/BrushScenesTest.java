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

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.objects.brush.GradientBrush;
import net.onelitefeather.bettergopaint.objects.brush.PaintBrush;
import net.onelitefeather.bettergopaint.objects.brush.SphereBrush;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BrushScenesTest {

    private final SettingsHolder settings = GeneratorFixtures.settings();
    private final PlayerBrushManager manager = GeneratorFixtures.brushManager(settings);
    private final Terrain terrain = new Terrain();

    private Brush brush(Class<? extends Brush> type) {
        return manager.getBrushes().stream().filter(type::isInstance).findFirst().orElseThrow();
    }

    @Test
    @DisplayName("every brush of the registry has a scene, in registry order")
    void everyRegisteredBrushHasAScene() {
        List<BrushScene> scenes = BrushScenes.all(manager.getBrushes(), terrain);

        assertThat(scenes).as("one scene per registered brush").hasSize(manager.getBrushes().size());
        assertThat(scenes).as("the scenes in registry order")
                .extracting(scene -> scene.brush().getName())
                .containsExactlyElementsOf(manager.getBrushes().stream().map(Brush::getName).toList());
    }

    @Test
    @DisplayName("a brush without a scene fails and names the brush")
    void brushWithoutSceneIsNamed() {
        Brush unknown = new Brush("Unknown Brush", "No scene yet", "head") {
            @Override
            public void apply(Extent extent, StrokeContext context) {
            }
        };

        assertThatThrownBy(() -> BrushScenes.all(List.of(unknown), terrain))
                .as("the scene of a brush that has none")
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unknown Brush");
    }

    @Test
    @DisplayName("the sphere scene places at least one stone block")
    void sphereSceneMakesStone() {
        BrushScene scene = BrushScenes.sceneOf(brush(SphereBrush.class), terrain);

        assertThat(scene.changes()).as("the changes of the sphere scene")
                .anyMatch(change -> change.after() == terrain.stone);
    }

    @Test
    @DisplayName("two renders of the same scene produce the same svg")
    void sceneRendersAreEqual() {
        Terrain first = new Terrain();
        Terrain second = new Terrain();

        String firstSvg = SvgWriter.write(BrushIllustration.render(
                BrushScenes.sceneOf(brush(SphereBrush.class), first), BlockPalette.of(first)));
        String secondSvg = SvgWriter.write(BrushIllustration.render(
                BrushScenes.sceneOf(brush(SphereBrush.class), second), BlockPalette.of(second)));

        assertThat(secondSvg).as("the svg of the sphere scene rendered twice").isEqualTo(firstSvg);
    }

    @Test
    @DisplayName("the sphere scene places stone, andesite and cobblestone")
    void sphereSceneMixesThreeStoneFamilyBlocks() {
        BrushScene scene = BrushScenes.sceneOf(brush(SphereBrush.class), terrain);

        assertThat(scene.changes()).as("the changes of the sphere scene")
                .extracting(BrushScene.Change::after)
                .contains(terrain.stone, terrain.andesite, terrain.cobblestone);
    }

    @Test
    @DisplayName("the gradient scene places stone, andesite and cobblestone")
    void gradientSceneMixesThreeStoneFamilyBlocks() {
        BrushScene scene = BrushScenes.sceneOf(brush(GradientBrush.class), terrain);

        assertThat(scene.changes()).as("the changes of the gradient scene")
                .extracting(BrushScene.Change::after)
                .contains(terrain.stone, terrain.andesite, terrain.cobblestone);
    }

    @Test
    @DisplayName("the paint scene has six control points and its last point is the clicked block")
    void paintSceneHasSixControlPoints() {
        BrushScene scene = BrushScenes.sceneOf(brush(PaintBrush.class), terrain);

        assertThat(scene.points()).as("the control points of the paint scene").hasSize(6);
        assertThat(scene.points().getLast()).as("the last control point").isEqualTo(scene.target());
    }

    @Test
    @DisplayName("the paint stroke fills less than half of its bounding box in the top view")
    void paintStrokeIsAPathNotABlob() {
        BrushScene scene = BrushScenes.sceneOf(brush(PaintBrush.class), terrain);
        Set<BlockVector3> columns = scene.changes().stream()
                .map(change -> BlockVector3.at(change.position().x(), 0, change.position().z()))
                .collect(Collectors.toSet());
        int minX = columns.stream().mapToInt(BlockVector3::x).min().orElseThrow();
        int maxX = columns.stream().mapToInt(BlockVector3::x).max().orElseThrow();
        int minZ = columns.stream().mapToInt(BlockVector3::z).min().orElseThrow();
        int maxZ = columns.stream().mapToInt(BlockVector3::z).max().orElseThrow();
        double area = (maxX - minX + 1.0) * (maxZ - minZ + 1.0);

        assertThat(columns.size() / area).as("the share of the bounding box the paint stroke fills")
                .isLessThan(0.5);
    }
}
