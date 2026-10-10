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
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.utils.Blocks;
import net.onelitefeather.bettergopaint.utils.Height;
import net.onelitefeather.bettergopaint.utils.Messages;
import net.onelitefeather.bettergopaint.utils.Sphere;
import net.onelitefeather.bettergopaint.utils.curve.BezierSpline;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PaintBrush extends Brush {

    private static final String NAME = "Paint Brush";
    private static final String DESCRIPTION = "Paints strokes\n§8hold shift to end";
    private static final String HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODBiM2E5ZGZhYmVmYmRkOTQ5YjIxN2JiZDRmYTlhNDg2YmQwYzNmMGNhYjBkMGI5ZGZhMjRjMzMyZGQzZTM0MiJ9fX0=";

    /**
     * Points of the stroke each player is currently drawing. Owned by this brush instance, so it is dropped with
     * the player's brush state through {@link #forget(UUID)}.
     */
    private final Map<UUID, List<BlockVector3>> selectedPoints = new ConcurrentHashMap<>();

    private final SettingsHolder settings;

    public PaintBrush(@NotNull SettingsHolder settings) {
        super(NAME, DESCRIPTION, HEAD);
        this.settings = settings;
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
        points.add(context.target());

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

            LinkedList<Vector3> newCurve = new LinkedList<>();
            newCurve.add(block.toVector3());
            for (BlockVector3 point : points) {
                newCurve.add(block.add(point.subtract(first)).toVector3());
            }
            BezierSpline spline = new BezierSpline(newCurve);
            double maxCount = (spline.getCurveLength() * 2.5) + 1;
            for (int y = 0; y <= maxCount; y++) {
                Vector3 curvePoint = spline.getPoint((y / maxCount) * (points.size() - 1));
                BlockVector3 point = BlockVector3.at(curvePoint.x(), curvePoint.y(), curvePoint.z());

                if (Blocks.isAir(extent, point) || !passesDefaultChecks(extent, context, point)) {
                    continue;
                }

                positions.add(point);
            }
        });

        placeAll(extent, context, positions);
    }

}
