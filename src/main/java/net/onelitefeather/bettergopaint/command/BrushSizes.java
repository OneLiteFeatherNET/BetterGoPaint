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
package net.onelitefeather.bettergopaint.command;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Validation and tab completion for brush sizes. The upper bound is passed in on every call so a
 * config reload takes effect without registering the command again.
 */
public final class BrushSizes {

    public static final int MIN_SIZE = 1;

    private BrushSizes() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static boolean isValid(int size, int maxSize) {
        return size >= MIN_SIZE && size <= maxSize;
    }

    /**
     * Suggests sizes that start with what the player typed so far: the first ten sizes, every
     * multiple of five and the maximum.
     */
    public static List<String> suggestions(String input, int maxSize) {
        return IntStream.rangeClosed(MIN_SIZE, maxSize)
                .filter(size -> size <= 10 || size % 5 == 0 || size == maxSize)
                .mapToObj(Integer::toString)
                .filter(size -> size.startsWith(input))
                .toList();
    }
}
