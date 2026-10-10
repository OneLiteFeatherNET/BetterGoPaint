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
package net.onelitefeather.bettergopaint.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A hash map from block positions to values. A position is packed into one {@code long}, so a lookup allocates
 * no {@code BlockVector3}. Positions outside the packable range are never stored: a lookup for them misses and
 * the caller computes the value itself. Not thread safe, a map belongs to one stroke.
 *
 * @param <V> the value type, never null
 */
final class PositionMap<V> {

    private static final int INITIAL_CAPACITY = 64;
    private static final int X_BITS = 26;
    private static final int Y_BITS = 12;
    private static final int Z_BITS = 26;
    private static final long X_MASK = (1L << X_BITS) - 1;
    private static final long Y_MASK = (1L << Y_BITS) - 1;
    private static final long Z_MASK = (1L << Z_BITS) - 1;
    private static final int HORIZONTAL_LIMIT = 1 << (X_BITS - 1);
    private static final int VERTICAL_LIMIT = 1 << (Y_BITS - 1);

    private long[] keys = new long[INITIAL_CAPACITY];
    private Object[] values = new Object[INITIAL_CAPACITY];
    private int size;

    /**
     * Checks if a position can be stored. Every position of a Minecraft world can.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return true if the position can be packed
     */
    static boolean fits(int x, int y, int z) {
        return x >= -HORIZONTAL_LIMIT && x < HORIZONTAL_LIMIT
                && z >= -HORIZONTAL_LIMIT && z < HORIZONTAL_LIMIT
                && y >= -VERTICAL_LIMIT && y < VERTICAL_LIMIT;
    }

    /**
     * Returns the value stored for the position.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the value, or null if none was stored
     */
    @Nullable V get(int x, int y, int z) {
        if (!fits(x, y, z)) {
            return null;
        }
        long key = pack(x, y, z);
        int mask = keys.length - 1;
        for (int i = slot(key, mask); values[i] != null; i = (i + 1) & mask) {
            if (keys[i] == key) {
                return cast(values[i]);
            }
        }
        return null;
    }

    /**
     * Stores the value for the position, replacing an earlier one. Positions that do not fit are ignored.
     *
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     * @param value the value to store
     */
    void put(int x, int y, int z, @NotNull V value) {
        Objects.requireNonNull(value, "value");
        if (!fits(x, y, z)) {
            return;
        }
        if (2 * (size + 1) > keys.length) {
            grow();
        }
        long key = pack(x, y, z);
        int mask = keys.length - 1;
        int i = slot(key, mask);
        while (values[i] != null) {
            if (keys[i] == key) {
                values[i] = value;
                return;
            }
            i = (i + 1) & mask;
        }
        keys[i] = key;
        values[i] = value;
        size++;
    }

    private void grow() {
        long[] oldKeys = keys;
        Object[] oldValues = values;
        keys = new long[oldKeys.length * 2];
        values = new Object[oldKeys.length * 2];
        int mask = keys.length - 1;
        for (int j = 0; j < oldKeys.length; j++) {
            if (oldValues[j] != null) {
                int i = slot(oldKeys[j], mask);
                while (values[i] != null) {
                    i = (i + 1) & mask;
                }
                keys[i] = oldKeys[j];
                values[i] = oldValues[j];
            }
        }
    }

    private static long pack(int x, int y, int z) {
        return ((x & X_MASK) << (Y_BITS + Z_BITS)) | ((y & Y_MASK) << Z_BITS) | (z & Z_MASK);
    }

    private static int slot(long key, int mask) {
        long hash = key * 0x9E3779B97F4A7C15L;
        return (int) (hash >>> 32) & mask;
    }

    @SuppressWarnings("unchecked")
    private V cast(Object value) {
        return (V) value;
    }
}
