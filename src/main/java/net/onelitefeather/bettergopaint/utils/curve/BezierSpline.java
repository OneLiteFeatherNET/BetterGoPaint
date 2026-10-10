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

import com.fastasyncworldedit.core.math.MutableVector3;
import com.sk89q.worldedit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class BezierSpline {

    private final LinkedList<Vector3> knotsList;
    private MutableVector3[] knots;
    private BezierSplineSegment[] segments;
    private double length = 0;
    private Vector3 anchorPoint;

    public BezierSpline() {
        knotsList = new LinkedList<>();
        segments = new BezierSplineSegment[0];
    }

    public BezierSpline(@NotNull LinkedList<Vector3> knotsList) {
        this.knotsList = knotsList;
        recalculate();
    }

    private void recalculate() {
        knots = knotsList.stream().map(MutableVector3::new).toArray(MutableVector3[]::new);
        segments = new BezierSplineSegment[knots.length - 1];
        for (int i = 0; i < knots.length - 1; i++) {
            segments[i] = new BezierSplineSegment(knots[i], knots[i + 1]);
        }
        calculateControlPoints();
        calculateLength();
    }

    public void addKnot(@NotNull Vector3 knot) {
        knotsList.add(knot);
        recalculate();
    }

    public void removeKnot(int n) {
        if (n < knots.length) {
            knotsList.remove(n);
            recalculate();
        }
    }

    public double getCurveLength() {
        return length;
    }

    public void calculateLength() {
        length = 0;
        for (BezierSplineSegment segment : segments) {
            segment.calculateCurveLength();
            length += segment.getCurveLength();
        }
    }

    public int getSegmentsCount() {
        return segments.length;
    }

    public BezierSplineSegment getCurveSegment(int n) {
        assert (n < segments.length);
        return segments[n];
    }

    public double getT(double blocks) {
        if (length == 0) {
            calculateLength();
        }

        assert (blocks >= 0);
        assert (blocks <= length);
        if (blocks == 0) {
            return 0;
        }
        if (blocks == length) {
            return segments.length;
        }
        double current = 0;
        int i = 0;

        for (i = 0; i < segments.length; ++i) {
            current += segments[i].getCurveLength();
            if (current > blocks) {
                current -= segments[i].getCurveLength();
                break;
            }
        }
        if (i >= segments.length) {
            return segments.length;
        }
        return i + segments[i].getT(blocks - current);
    }

    public @NotNull Vector3 getPoint(double point) {
        if (point >= segments.length) {
            return getPoint(segments.length - 1, 1);
        } else {
            return getPoint((int) Math.floor(point), point - Math.floor(point));
        }
    }

    public @NotNull Vector3 getPoint(int n, double f) {
        assert (n < segments.length);
        assert (0 <= f && f <= 1);
        BezierSplineSegment segment = segments[n];
        return segment.getPoint(f);
    }

    public double getdTdS(double f) {
        if (f >= segments.length) {
            return segments[segments.length - 1].getdTdS(1);
        } else {
            return segments[(int) Math.floor(f)].getdTdS(f - Math.floor(f));
        }
    }

    public double getHAngle(double f) {
        assert (f <= segments.length);
        if (f >= segments.length) {
            return segments[segments.length - 1].getHAngle(1);
        } else {
            return segments[(int) Math.floor(f)].getHAngle(f - Math.floor(f));
        }
    }

    public void calculateControlPoints() {
        /*
         * Do not touch.
         */
        if (segments == null) {
            return;
        }

        if (segments.length == 0) {
            return;
        }

        Double xflat, yflat, zflat;
        xflat = knots[0].x();
        yflat = knots[0].y();
        zflat = knots[0].z();
        for (MutableVector3 l : knots) {
            if (floor(l.x()) != xflat) {
                xflat = null;
                break;
            }
        }
        for (MutableVector3 l : knots) {
            if (floor(l.y()) != yflat) {
                yflat = null;
                break;
            }
        }
        for (MutableVector3 l : knots) {
            if (floor(l.z()) != zflat) {
                zflat = null;
                break;
            }
        }

        if (segments.length == 1) {
            MutableVector3 midpoint = new MutableVector3(
                    (segments[0].getP0().x() + segments[0].getP3().x()) / 2,
                    (segments[0].getP0().y() + segments[0].getP3().y()) / 2,
                    (segments[0].getP0().z() + segments[0].getP3().z()) / 2
            );
            segments[0].setP1(midpoint);
            segments[0].setP2(new MutableVector3(midpoint));
        } else {
            segments[0].setA(0);
            segments[0].setB(2);
            segments[0].setC(1);
            segments[0].getR().mutX(knots[0].x() + 2 * knots[1].x());
            segments[0].getR().mutY(knots[0].y() + 2 * knots[1].y());
            segments[0].getR().mutZ(knots[0].z() + 2 * knots[1].z());
            int n = knots.length - 1;
            int i;
            float m;

            for (i = 1; i < n - 1; i++) {
                segments[i].setA(1);
                segments[i].setB(4);
                segments[i].setC(1);
                segments[i].getR().mutX(4 * knots[i].x() + 2 * knots[i + 1].x());
                segments[i].getR().mutY(4 * knots[i].y() + 2 * knots[i + 1].y());
                segments[i].getR().mutZ(4 * knots[i].z() + 2 * knots[i + 1].z());
            }

            segments[n - 1].setA(2);
            segments[n - 1].setB(7);
            segments[n - 1].setC(0);
            segments[n - 1].getR().mutX(8 * knots[n - 1].x() + knots[n].x());
            segments[n - 1].getR().mutY(8 * knots[n - 1].y() + knots[n].y());
            segments[n - 1].getR().mutZ(8 * knots[n - 1].z() + knots[n].z());

            for (i = 1; i < n; i++) {
                m = segments[i].getA() / segments[i - 1].getB();
                segments[i].setB(segments[i].getB() - m * segments[i - 1].getC());
                segments[i].getR().mutX(
                        segments[i].getR().x() - m * segments[i - 1].getR().x());
                segments[i].getR().mutY(
                        segments[i].getR().y() - m * segments[i - 1].getR().y());
                segments[i].getR().mutZ(
                        segments[i].getR().z() - m * segments[i - 1].getR().z());
            }
            segments[n - 1].getP1().mutX(
                    segments[n - 1].getR().x() / segments[n - 1].getB());
            segments[n - 1].getP1().mutY(
                    segments[n - 1].getR().y() / segments[n - 1].getB());
            segments[n - 1].getP1().mutZ(
                    segments[n - 1].getR().z() / segments[n - 1].getB());

            for (i = n - 2; i >= 0; i--) {
                segments[i].getP1().mutX(
                        (segments[i].getR().x() - segments[i].getC()
                                * segments[i + 1].getP1().x())
                                / segments[i].getB());
                segments[i].getP1().mutY(
                        (segments[i].getR().y() - segments[i].getC()
                                * segments[i + 1].getP1().y())
                                / segments[i].getB());
                segments[i].getP1().mutZ(
                        (segments[i].getR().z() - segments[i].getC()
                                * segments[i + 1].getP1().z())
                                / segments[i].getB());
            }

            for (i = 0; i < n - 1; i++) {
                segments[i].getP2().mutX(
                        2 * knots[i + 1].x() - segments[i + 1].getP1().x());
                segments[i].getP2().mutY(
                        2 * knots[i + 1].y() - segments[i + 1].getP1().y());
                segments[i].getP2().mutZ(
                        2 * knots[i + 1].z() - segments[i + 1].getP1().z());
            }
            segments[n - 1].getP2().mutX(
                    0.5 * (knots[n].x() + segments[n - 1].getP1().x()));
            segments[n - 1].getP2().mutY(
                    0.5 * (knots[n].y() + segments[n - 1].getP1().y()));
            segments[n - 1].getP2().mutZ(
                    0.5 * (knots[n].z() + segments[n - 1].getP1().z()));
        }

        if (xflat != null) {
            for (BezierSplineSegment cs : segments) {
                cs.setX(xflat);
            }
        }
        if (yflat != null) {
            for (BezierSplineSegment cs : segments) {
                cs.setY(yflat);
            }
        }
        if (zflat != null) {
            for (BezierSplineSegment cs : segments) {
                cs.setZ(zflat);
            }
        }
    }

    public void shift(@NotNull Vector3 vector) {
        knotsList.replaceAll(knot -> knot.add(vector));
        recalculate();
    }

    public void scale(double d) {
        knotsList.replaceAll(knot -> knot.subtract(anchorPoint).multiply(d).add(anchorPoint));
        recalculate();
    }

    public void scale(@NotNull Vector3 vector) {
        knotsList.replaceAll(knot -> knot.subtract(anchorPoint).multiply(vector).add(anchorPoint));
        recalculate();
    }

    private static int floor(double value) {
        return (int) Math.floor(value);
    }

    @Override
    public String toString() {
        if (knots == null) {
            return "0 points.";
        }
        return knots.length + " points.";
    }

    public @NotNull String toName() {
        return "Curve";
    }

    public @NotNull BezierSpline emptySystem() {
        return new BezierSpline();
    }

    public @NotNull String toShorthand() {
        return "curve";
    }

}
