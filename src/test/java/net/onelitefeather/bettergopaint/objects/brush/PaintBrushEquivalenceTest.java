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
import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.support.MemoryExtent;
import net.onelitefeather.bettergopaint.support.StrokeBuilder;
import net.onelitefeather.bettergopaint.support.Terrain;
import net.onelitefeather.bettergopaint.utils.Blocks;
import net.onelitefeather.bettergopaint.utils.Height;
import net.onelitefeather.bettergopaint.utils.Sphere;
import net.onelitefeather.bettergopaint.utils.curve.BezierSpline;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import static net.onelitefeather.bettergopaint.support.StrokeBuilder.stroke;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pins the paint brush to the result of the original algorithm, which built one spline per candidate block.
 * The reference below is that algorithm with the curve corrected to one control point per clicked point (see the
 * comment in {@code perBlockCurves}), so the optimised brush must select the same positions and write the same
 * blocks for a fixed seed.
 */
class PaintBrushEquivalenceTest {

    private final Terrain terrain = new Terrain();
    private final PaintBrush brush = new PaintBrush(SettingsHolder.of(Settings.defaults()));

    /**
     * The original paint algorithm: one spline per candidate block, translated by the block position.
     */
    private void perBlockCurves(MemoryExtent extent, StrokeContext context) {
        List<BlockVector3> points = context.points();
        BlockVector3 first = points.getFirst();
        List<BlockVector3> positions = new ArrayList<>();

        Sphere.getBlocksInRadius(extent, first, context.size(), null, false).forEach(block -> {
            if (Height.getAverageHeightDiffAngle(extent, block, 1) >= 0.1
                    && Height.getAverageHeightDiffAngle(extent, block, context.angleDistance())
                    >= Math.tan(Math.toRadians(context.angleHeightDifference()))) {
                return;
            }

            double rate = (block.distance(first) - (context.size() / 2.0)
                    * ((100.0 - context.falloffStrength()) / 100.0)) / ((context.size() / 2.0)
                    - (context.size() / 2.0) * ((100.0 - context.falloffStrength()) / 100.0));

            if (context.random().nextDouble() <= rate) {
                return;
            }

            // One control point per clicked point. The first one is the candidate block itself; adding the first
            // clicked point again as a control point would make the curve one segment too long, and the parameter
            // range below would then stop one segment short of the last clicked point. This reference was corrected
            // for that bug together with the brush. The original per-block algorithm had the same duplicate.
            LinkedList<Vector3> newCurve = new LinkedList<>();
            newCurve.add(block.toVector3());
            for (BlockVector3 point : points.subList(1, points.size())) {
                newCurve.add(block.add(point.subtract(first)).toVector3());
            }
            BezierSpline spline = new BezierSpline(newCurve);
            double maxCount = (spline.getCurveLength() * 2.5) + 1;
            for (int y = 0; y <= maxCount; y++) {
                Vector3 curvePoint = spline.getPoint((y / maxCount) * (points.size() - 1));
                BlockVector3 point = BlockVector3.at(curvePoint.x(), curvePoint.y(), curvePoint.z());

                if (Blocks.isAir(extent, point) || !brush.passesDefaultChecks(extent, context, point)) {
                    continue;
                }

                positions.add(point);
            }
        });

        for (BlockVector3 position : positions) {
            extent.setBlock(position, context.palette().applyBlock(position));
        }
    }

    private void assertSameBlocksAsPerBlockCurves(Supplier<MemoryExtent> world, StrokeBuilder stroke) {
        MemoryExtent expected = world.get();
        perBlockCurves(expected, stroke.build());
        MemoryExtent actual = world.get();
        brush.apply(actual, stroke.build());

        assertThat(actual.written()).as("written positions").isEqualTo(expected.written());
        for (BlockVector3 position : expected.written()) {
            assertThat(actual.typeAt(position)).as("block at %s", position).isSameAs(expected.typeAt(position));
        }
    }

    @Test
    @DisplayName("a curve with positive offsets places the blocks of the per-block curves")
    void positiveOffsets() {
        assertSameBlocksAsPerBlockCurves(terrain::flat, stroke().blocks(terrain.gold).size(8)
                .points(BlockVector3.at(0, 63, 0), BlockVector3.at(6, 63, 0), BlockVector3.at(6, 63, 6)));
    }

    @Test
    @DisplayName("a curve with negative offsets places the blocks of the per-block curves")
    void negativeOffsets() {
        assertSameBlocksAsPerBlockCurves(terrain::flat, stroke().blocks(terrain.gold).size(8)
                .points(BlockVector3.at(-3, 63, -2), BlockVector3.at(-9, 63, -4), BlockVector3.at(-6, 63, -10)));
    }

    @Test
    @DisplayName("a curve that mixes positive and negative offsets places the blocks of the per-block curves")
    void mixedOffsets() {
        assertSameBlocksAsPerBlockCurves(terrain::flat, stroke().blocks(terrain.gold).size(7)
                .points(BlockVector3.at(1, 63, -4), BlockVector3.at(-5, 63, 3), BlockVector3.at(4, 63, 5)));
    }

    @Test
    @DisplayName("a curve across a cliff with a palette of several blocks keeps the palette draws in order")
    void paletteDrawsStayInOrder() {
        assertSameBlocksAsPerBlockCurves(terrain::step, stroke().blocks(terrain.gold, terrain.andesite, terrain.dirt)
                .size(8).seed(7)
                .points(BlockVector3.at(-2, 63, -1), BlockVector3.at(-6, 63, 4), BlockVector3.at(-3, 63, 9)));
    }
}
