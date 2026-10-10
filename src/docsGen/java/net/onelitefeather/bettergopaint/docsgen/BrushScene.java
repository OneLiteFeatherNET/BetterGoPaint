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

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.support.MemoryExtent;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * One brush run on its fixed terrain. {@code before} and {@code after} are two copies of the same terrain; the brush
 * wrote into {@code after} only.
 *
 * @param brush   the brush that ran
 * @param before  the terrain before the stroke
 * @param after   the terrain after the stroke
 * @param target  the position the stroke is centred on
 * @param palette the blocks the stroke could place, in the order they were configured
 * @param points  the clicked control points in click order; a single point for every brush but the paint brush
 */
record BrushScene(Brush brush, MemoryExtent before, MemoryExtent after, BlockVector3 target, List<BlockType> palette,
                  List<BlockVector3> points) {

    private static final Comparator<BlockVector3> ORDER = Comparator
            .comparingInt(BlockVector3::x)
            .thenComparingInt(BlockVector3::y)
            .thenComparingInt(BlockVector3::z);

    /**
     * @return every position whose block differs after the stroke, sorted by x, then y, then z
     */
    List<Change> changes() {
        return after.written().stream()
                .sorted(ORDER)
                .map(position -> new Change(position, before.typeAt(position), after.typeAt(position)))
                .filter(change -> !Objects.equals(change.before(), change.after()))
                .toList();
    }

    /**
     * One block the stroke changed.
     *
     * @param position the position of the block
     * @param before   the block type before the stroke
     * @param after    the block type after the stroke
     */
    record Change(BlockVector3 position, BlockType before, BlockType after) {
    }
}
