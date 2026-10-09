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
package net.onelitefeather.bettergopaint.utils.curve;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class BezierSplineTest {

    private final World world = Mockito.mock(World.class);

    private BezierSpline spline(Location... knots) {
        return new BezierSpline(new LinkedList<>(List.of(knots)));
    }

    @Test
    @DisplayName("a spline through two knots is the straight line between them")
    void twoKnotsFormAStraightLine() {
        BezierSpline spline = spline(new Location(world, 0, 64, 0), new Location(world, 10, 64, 0));

        assertThat(spline.getSegmentsCount()).isEqualTo(1);
        assertThat(spline.getCurveLength()).isCloseTo(10, within(1.0));
        assertThat(spline.getPoint(0.5).getX()).isCloseTo(5, within(0.01));
    }

    @Test
    @DisplayName("the curve starts on the first knot and ends on the last")
    void curvePassesThroughEndKnots() {
        BezierSpline spline = spline(
                new Location(world, 0, 64, 0),
                new Location(world, 5, 64, 8),
                new Location(world, 12, 64, 3)
        );

        Location start = spline.getPoint(0);
        Location end = spline.getPoint(spline.getSegmentsCount());

        assertThat(start.getX()).isCloseTo(0, within(1e-9));
        assertThat(start.getZ()).isCloseTo(0, within(1e-9));
        assertThat(end.getX()).isCloseTo(12, within(1e-9));
        assertThat(end.getZ()).isCloseTo(3, within(1e-9));
    }

    @Test
    @DisplayName("knots on one height keep the whole curve on that height")
    void flatKnotsStayFlat() {
        BezierSpline spline = spline(
                new Location(world, 0, 70, 0),
                new Location(world, 4, 70, 9),
                new Location(world, 9, 70, -2)
        );

        for (double t = 0; t <= spline.getSegmentsCount(); t += 0.25) {
            assertThat(spline.getPoint(t).getY()).as("y at t=%s", t).isEqualTo(70);
        }
    }

    @Test
    @DisplayName("getT maps walked distance back onto the curve parameter")
    void getTIsMonotonic() {
        BezierSpline spline = spline(
                new Location(world, 0, 64, 0),
                new Location(world, 6, 64, 6),
                new Location(world, 12, 64, 0)
        );

        double previous = -1;
        for (double blocks = 0; blocks < spline.getCurveLength(); blocks += 1) {
            double t = spline.getT(blocks);
            assertThat(t).as("t after %s blocks", blocks).isGreaterThan(previous);
            previous = t;
        }
        assertThat(spline.getT(spline.getCurveLength())).isEqualTo(spline.getSegmentsCount());
    }
}
