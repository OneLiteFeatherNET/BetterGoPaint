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
import com.sk89q.worldedit.math.Vector3;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.support.MemoryExtent;
import net.onelitefeather.bettergopaint.support.Terrain;
import net.onelitefeather.bettergopaint.utils.curve.BezierSpline;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static net.onelitefeather.bettergopaint.support.StrokeBuilder.stroke;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The paint brush builds its relative curve once per stroke and places every position at most once.
 */
class PaintBrushCurveTest {

    private final Terrain terrain = new Terrain();
    private final BlockVector3 target = Terrain.surface();
    private final BlockVector3 second = target.add(6, 0, 0);
    private final List<List<Vector3>> builtCurves = new ArrayList<>();
    private final PaintBrush brush = new PaintBrush(SettingsHolder.of(Settings.defaults()), knots -> {
        builtCurves.add(List.copyOf(knots));
        return new BezierSpline(knots);
    });

    @Test
    @DisplayName("the curve of a stroke is built once, not once per candidate block")
    void curveIsBuiltOncePerStroke() {
        MemoryExtent extent = terrain.flat();

        brush.apply(extent, stroke().blocks(terrain.gold).size(8).points(target, second).target(second).build());

        assertThat(builtCurves).as("curves built for a stroke with many candidate blocks").hasSize(1);
    }

    @Test
    @DisplayName("every position of a stroke is written at most once")
    void positionsAreWrittenOnce() {
        MemoryExtent extent = terrain.flat();

        brush.apply(extent, stroke().blocks(terrain.gold).size(8).points(target, second).target(second).build());

        assertThat(extent.written()).isNotEmpty()
                .allSatisfy(position -> assertThat(extent.writeCount(position)).as("writes of %s", position).isEqualTo(1));
    }
}
