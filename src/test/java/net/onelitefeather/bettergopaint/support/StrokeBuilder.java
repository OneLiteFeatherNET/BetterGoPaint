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
package net.onelitefeather.bettergopaint.support;

import com.fastasyncworldedit.core.math.random.SimpleRandom;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.block.BlockType;
import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.objects.other.SurfaceMode;
import org.bukkit.Axis;

import java.util.List;
import java.util.Random;

/**
 * Builds {@link StrokeContext}s for tests. Every value has a plain default, the random source is seeded.
 */
public final class StrokeBuilder {

    private BlockVector3 target = Terrain.surface();
    private List<BlockVector3> points;
    private Vector3 eye;
    private List<BlockType> blocks = List.of();
    private Mask mask;
    private Axis axis = Axis.Y;
    private SurfaceMode surfaceMode = SurfaceMode.DISABLED;
    private int size = 6;
    private int chance = 50;
    private int falloffStrength = 0;
    private int mixingStrength = 0;
    private int thickness = 1;
    private int fractureDistance = 2;
    private int angleDistance = 1;
    private double angleHeightDifference = 45;
    private long seed = 1;

    public static StrokeBuilder stroke() {
        return new StrokeBuilder();
    }

    public StrokeBuilder target(BlockVector3 target) {
        this.target = target;
        return this;
    }

    public StrokeBuilder points(BlockVector3... points) {
        this.points = List.of(points);
        return this;
    }

    public StrokeBuilder eye(Vector3 eye) {
        this.eye = eye;
        return this;
    }

    public StrokeBuilder blocks(BlockType... blocks) {
        this.blocks = List.of(blocks);
        return this;
    }

    public StrokeBuilder mask(Mask mask) {
        this.mask = mask;
        return this;
    }

    public StrokeBuilder axis(Axis axis) {
        this.axis = axis;
        return this;
    }

    public StrokeBuilder surfaceMode(SurfaceMode surfaceMode) {
        this.surfaceMode = surfaceMode;
        return this;
    }

    public StrokeBuilder size(int size) {
        this.size = size;
        return this;
    }

    public StrokeBuilder chance(int chance) {
        this.chance = chance;
        return this;
    }

    public StrokeBuilder falloff(int falloffStrength) {
        this.falloffStrength = falloffStrength;
        return this;
    }

    public StrokeBuilder mixing(int mixingStrength) {
        this.mixingStrength = mixingStrength;
        return this;
    }

    public StrokeBuilder thickness(int thickness) {
        this.thickness = thickness;
        return this;
    }

    public StrokeBuilder fractureDistance(int fractureDistance) {
        this.fractureDistance = fractureDistance;
        return this;
    }

    public StrokeBuilder angle(int distance, double heightDifference) {
        this.angleDistance = distance;
        this.angleHeightDifference = heightDifference;
        return this;
    }

    public StrokeBuilder seed(long seed) {
        this.seed = seed;
        return this;
    }

    public StrokeContext build() {
        Random random = new Random(seed);
        SimpleRandom simpleRandom = (x, y, z) -> random.nextDouble();
        RandomPattern palette = new RandomPattern(simpleRandom);
        blocks.forEach(block -> palette.add(TestBlocks.pattern(block), 1));
        return new StrokeContext(
                target,
                points == null ? List.of(target) : points,
                eye == null ? Vector3.at(target.x(), target.y() + 1.5, target.z()) : eye,
                palette,
                blocks,
                mask,
                axis,
                surfaceMode,
                size,
                chance,
                falloffStrength,
                mixingStrength,
                thickness,
                fractureDistance,
                angleDistance,
                angleHeightDifference,
                random
        );
    }
}
