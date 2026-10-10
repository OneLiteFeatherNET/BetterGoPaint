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

import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;

import java.util.function.Predicate;

/**
 * A mask that answers with a predicate and counts how often it was tested. It is not an extent mask, so the stroke
 * asks it through {@link Mask#test(BlockVector3)} and the test can see every candidate the stroke tests.
 */
public final class CountingMask implements Mask {

    private final Predicate<BlockVector3> accepts;
    private int tests;

    public CountingMask(Predicate<BlockVector3> accepts) {
        this.accepts = accepts;
    }

    /**
     * How often the mask was tested.
     */
    public int tests() {
        return tests;
    }

    @Override
    public boolean test(BlockVector3 position) {
        tests++;
        return accepts.test(position);
    }

    /**
     * The copy shares the predicate but counts on its own, so a copy never changes the count of the original.
     */
    @Override
    public Mask copy() {
        return new CountingMask(accepts);
    }
}
