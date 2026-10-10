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

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.utils.BlockReadCache;
import net.onelitefeather.bettergopaint.utils.Blocks;
import net.onelitefeather.bettergopaint.utils.HeightCache;
import net.onelitefeather.bettergopaint.utils.Messages;
import net.onelitefeather.bettergopaint.utils.Sphere;
import net.onelitefeather.bettergopaint.utils.curve.BezierSpline;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PaintBrush extends Brush {

    private static final String NAME = "Paint Brush";
    private static final String DESCRIPTION = "Paints strokes\n§8hold shift to end";
    /**
     * The most points one unfinished stroke can hold. A fixed limit, not a config key, so no server owner has to set it.
     */
    public static final int MAX_STROKE_POINTS = 256;
    private static final String HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODBiM2E5ZGZhYmVmYmRkOTQ5YjIxN2JiZDRmYTlhNDg2YmQwYzNmMGNhYjBkMGI5ZGZhMjRjMzMyZGQzZTM0MiJ9fX0=";

    /**
     * Points of the stroke each player is currently drawing. Owned by this brush instance, so it is dropped with
     * the player's brush state through {@link #forget(UUID)}.
     */
    private final Map<UUID, List<BlockVector3>> selectedPoints = new ConcurrentHashMap<>();

    private final SettingsHolder settings;

    /**
     * Builds the spline of a curve from its control points. The production brush uses the constructor of
     * {@link BezierSpline}; tests inject a counting function to observe how often a curve is built.
     */
    private final Function<LinkedList<Vector3>, BezierSpline> splines;

    public PaintBrush(@NotNull SettingsHolder settings) {
        this(settings, BezierSpline::new);
    }

    PaintBrush(@NotNull SettingsHolder settings, @NotNull Function<LinkedList<Vector3>, BezierSpline> splines) {
        super(NAME, DESCRIPTION, HEAD);
        this.settings = settings;
        this.splines = splines;
    }

    /**
     * Drops the unfinished stroke of a player, for example when the player quits.
     *
     * @param playerId the player whose stroke points should be discarded
     */
    public void forget(@NotNull UUID playerId) {
        selectedPoints.remove(playerId);
    }

    @Override
    public @Nullable StrokeContext prepare(
            @NotNull UUID playerId,
            boolean sneaking,
            @NotNull Audience feedback,
            @NotNull StrokeContext context
    ) {
        List<BlockVector3> points = selectedPoints.computeIfAbsent(playerId, ignored -> new ArrayList<>());
        if (points.size() < MAX_STROKE_POINTS) {
            points.add(context.target());
        } else if (!sneaking) {
            feedback.sendMessage(Component.translatable(
                    "bettergopaint.brush.paint.limit",
                    Messages.prefix(settings.get()),
                    Component.text(MAX_STROKE_POINTS)
            ));
            return null;
        }

        if (!sneaking) {
            feedback.sendMessage(Component.translatable(
                    "bettergopaint.brush.paint.point",
                    Messages.prefix(settings.get()),
                    Component.text(points.size())
            ));
            return null;
        }

        selectedPoints.remove(playerId);
        return context.withPoints(points);
    }

    @Override
    public void apply(@NotNull Extent extent, @NotNull StrokeContext context) {
        List<BlockVector3> points = context.points();
        BlockVector3 first = points.getFirst();
        List<Vector3> offsets = sampleOffsets(points, first);
        Extent reads = new BlockReadCache(extent);
        HeightCache heights = new HeightCache(reads);
        double tangent = Math.tan(Math.toRadians(context.angleHeightDifference()));
        double halfSize = context.size() / 2.0;
        double falloffFactor = (100.0 - context.falloffStrength()) / 100.0;
        List<BlockVector3> positions = new ArrayList<>();

        Sphere.getBlocksInRadius(reads, first, context.size(), null, false).forEach(block -> {
            if (heights.averageHeightDiffAngle(block, 1) >= 0.1
                    && heights.averageHeightDiffAngle(block, context.angleDistance()) >= tangent) {
                return;
            }

            double rate = (block.distance(first) - halfSize * falloffFactor)
                    / (halfSize - halfSize * falloffFactor);

            if (context.random().nextDouble() <= rate) {
                return;
            }

            // Translate in double space first and convert to a block position second, as the per-block curve did.
            double blockX = block.x();
            double blockY = block.y();
            double blockZ = block.z();
            for (Vector3 offset : offsets) {
                BlockVector3 point = BlockVector3.at(
                        blockX + offset.x(),
                        blockY + offset.y(),
                        blockZ + offset.z()
                );

                if (Blocks.isAir(reads, point) || !passesDefaultChecks(reads, context, point)) {
                    continue;
                }

                positions.add(point);
            }
        });

        placeOnce(extent, context, positions);
    }

    /**
     * Samples the curve through the points once per stroke. The curve is the same for every candidate block,
     * only translated by the block position, so the samples are kept as offsets from the first point.
     *
     * @param points the points of the stroke in click order
     * @param first  the first point, the origin of the relative curve
     * @return the sample offsets from the first point, in curve order
     */
    private List<Vector3> sampleOffsets(@NotNull List<BlockVector3> points, @NotNull BlockVector3 first) {
        LinkedList<Vector3> relativeCurve = new LinkedList<>();
        relativeCurve.add(Vector3.ZERO);
        for (BlockVector3 point : points) {
            relativeCurve.add(point.subtract(first).toVector3());
        }
        BezierSpline spline = splines.apply(relativeCurve);
        double maxCount = (spline.getCurveLength() * 2.5) + 1;
        List<Vector3> offsets = new ArrayList<>();
        for (int y = 0; y <= maxCount; y++) {
            offsets.add(spline.getPoint((y / maxCount) * (points.size() - 1)));
        }
        return offsets;
    }

    /**
     * Writes every position once. The palette is drawn for every occurrence in order, as it always was, so the
     * random sequence does not change, and each position gets the block of its last occurrence, which is the one
     * that stayed in the world before.
     *
     * @param extent      the extent to write to
     * @param context     the context of the stroke
     * @param occurrences the positions in the order they were found, with repeats
     */
    private void placeOnce(@NotNull Extent extent, @NotNull StrokeContext context,
                           @NotNull List<BlockVector3> occurrences) {
        Map<BlockVector3, BaseBlock> blocks = new LinkedHashMap<>();
        for (BlockVector3 position : occurrences) {
            blocks.put(position, context.palette().applyBlock(position));
        }
        for (Map.Entry<BlockVector3, BaseBlock> entry : blocks.entrySet()) {
            extent.setBlock(entry.getKey(), entry.getValue());
        }
    }

}
