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

import java.util.Objects;

public class BezierSplineSegment {

    private final double[] lengths = new double[20];
    private MutableVector3 p0, p1, p2, p3;
    private float a, b, c;
    private Double xFlat, yFlat, zFlat;
    private MutableVector3 r;
    private double curveLength;

    public BezierSplineSegment(@NotNull MutableVector3 p0, @NotNull MutableVector3 p3) {
        this.p0 = p0;
        this.p3 = p3;
        p1 = new MutableVector3(0, 0, 0);
        p2 = new MutableVector3(0, 0, 0);
        r = new MutableVector3(0, 0, 0);
    }

    public double getCurveLength() {
        return curveLength;
    }

    public void setX(double xflat2) {
        p0.mutX(xflat2);
        p1.mutX(xflat2);
        p2.mutX(xflat2);
        p3.mutX(xflat2);
        xFlat = xflat2;
    }

    public void setY(double yflat2) {
        p0.mutY(yflat2);
        p1.mutY(yflat2);
        p2.mutY(yflat2);
        p3.mutY(yflat2);
        yFlat = yflat2;
    }

    public void setZ(double zflat2) {
        p0.mutZ(zflat2);
        p1.mutZ(zflat2);
        p2.mutZ(zflat2);
        p3.mutZ(zflat2);
        zFlat = zflat2;
    }

    public void calculateCurveLength() {
        Vector3 current = Vector3.at(p0.x(), p0.y(), p0.z());
        double step = 0.05;
        lengths[0] = 0;
        Vector3 temp;
        for (int i = 1; i < 20; i++) {
            temp = getPoint(i * step);
            lengths[i] = lengths[i - 1] + temp.distance(current);
            current = temp;
        }
        curveLength = lengths[19];
    }

    public double getdXdT(double t) {
        assert (t >= 0);
        assert (t <= 1);
        return 3 * (1 - t) * (1 - t) * (p1.x() - p0.x()) + 6 * (1 - t) * t
                * (p2.x() - p1.x()) + 3 * t * t * (p3.x() - p2.x());
    }

    public double getdYdT(double t) {
        assert (t <= 1);
        return 3 * (1 - t) * (1 - t) * (p1.y() - p0.y()) + 6 * (1 - t) * t
                * (p2.y() - p1.y()) + 3 * t * t * (p3.y() - p2.y());
    }

    public double getdZdT(double t) {
        assert (t >= 0);
        assert (t <= 1);
        return 3 * (1 - t) * (1 - t) * (p1.z() - p0.z()) + 6 * (1 - t) * t
                * (p2.z() - p1.z()) + 3 * t * t * (p3.z() - p2.z());
    }

    public double getdTdS(double t) {
        double dZdT = getdZdT(t);
        double dXdT = getdXdT(t);
        double dYdT = getdYdT(t);
        return 1 / Math.sqrt(dZdT * dZdT + dXdT * dXdT + dYdT * dYdT);
    }

    public double getHAngle(double t) {
        // Positive x is 0, positive z is pi/2, negative x is pi, negative z is
        // 3*pi/2
        double dZdT = getdZdT(t);
        double dXdT = getdXdT(t);
        if (dXdT == 0) {
            if (dZdT < 0) {
                return Math.PI / 2;
            } else {
                return -Math.PI / 2;
            }
        }

        if (dXdT < 0) {
            return Math.PI + Math.atan(dZdT / dXdT);
        }
        return Math.atan(dZdT / dXdT);
    }

    public double getT(double d) {
        assert (d >= 0);
        assert (d <= curveLength);
        if (d == 0) {
            return 0;
        }
        if (d == curveLength) {
            return 1;
        }
        int i = 0;
        for (i = 0; i < 20; i++) {
            if (d == lengths[i]) {
                return i / 19d;
            }
            if (d < lengths[i]) {
                break;
            }
        }
        return (i + (d - lengths[i - 1]) / (lengths[i] - lengths[i - 1])) / 20;
    }

    public @NotNull Vector3 getPoint(double f) {
        double x = Objects.requireNonNullElseGet(xFlat, () -> (Math.pow(1 - f, 3) * p0.x())
                + (3 * Math.pow(1 - f, 2) * f * p1.x())
                + (3 * (1 - f) * f * f * p2.x()) + (Math.pow(f, 3) * p3.x()));
        double y = Objects.requireNonNullElseGet(yFlat, () -> (Math.pow(1 - f, 3) * p0.y())
                + (3 * Math.pow(1 - f, 2) * f * p1.y())
                + (3 * (1 - f) * f * f * p2.y()) + (Math.pow(f, 3) * p3.y()));
        double z = Objects.requireNonNullElseGet(zFlat, () -> (Math.pow(1 - f, 3) * p0.z())
                + (3 * Math.pow(1 - f, 2) * f * p1.z())
                + (3 * (1 - f) * f * f * p2.z()) + (Math.pow(f, 3) * p3.z()));
        return Vector3.at(x, y, z);
    }

    public double getLinearLength() {
        return p0.distance(p3);
    }

    public @NotNull MutableVector3 getP0() {
        return p0;
    }

    public void setP0(@NotNull MutableVector3 p0) {
        this.p0 = p0;
    }

    public @NotNull MutableVector3 getP1() {
        return p1;
    }

    public void setP1(@NotNull MutableVector3 p1) {
        this.p1 = p1;
    }

    public @NotNull MutableVector3 getP2() {
        return p2;
    }

    public void setP2(@NotNull MutableVector3 p2) {
        this.p2 = p2;
    }

    public @NotNull MutableVector3 getP3() {
        return p3;
    }

    public void setP3(@NotNull MutableVector3 p3) {
        this.p3 = p3;
    }

    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getC() {
        return c;
    }

    public void setC(float c) {
        this.c = c;
    }

    public @NotNull MutableVector3 getR() {
        return r;
    }

    public void setR(@NotNull MutableVector3 r) {
        this.r = r;
    }

}
