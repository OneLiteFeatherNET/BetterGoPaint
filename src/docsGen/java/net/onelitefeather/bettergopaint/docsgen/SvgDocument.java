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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A picture as plain data: its size and its elements in drawing order. Every coordinate and size is a whole number,
 * so {@link SvgWriter} never has to format a fraction, and the same model always produces the same bytes.
 */
public final class SvgDocument {

    private final int width;
    private final int height;
    private final List<Element> elements = new ArrayList<>();

    public SvgDocument(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("an svg needs a positive size, got " + width + " by " + height);
        }
        this.width = width;
        this.height = height;
    }

    /**
     * Adds a rectangle. Pass {@code "none"} as the fill or the stroke to leave it out.
     */
    public SvgDocument rect(int x, int y, int width, int height, String fill, String stroke, int strokeWidth) {
        elements.add(new Rect(x, y, width, height, Objects.requireNonNull(fill), Objects.requireNonNull(stroke), strokeWidth));
        return this;
    }

    /**
     * Adds a line of text whose start is at {@code x} and whose baseline is at {@code y}.
     */
    public SvgDocument text(int x, int y, String content, int fontSize, String fill, boolean bold) {
        elements.add(new Text(x, y, Objects.requireNonNull(content), fontSize, Objects.requireNonNull(fill), bold));
        return this;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    /**
     * @return the elements in drawing order
     */
    public List<Element> elements() {
        return List.copyOf(elements);
    }

    /**
     * One drawn element.
     */
    public sealed interface Element permits Rect, Text {
    }

    /**
     * A rectangle. A zero stroke width draws no outline.
     */
    public record Rect(int x, int y, int width, int height, String fill, String stroke, int strokeWidth) implements Element {
    }

    /**
     * A line of sans-serif text.
     */
    public record Text(int x, int y, String content, int fontSize, String fill, boolean bold) implements Element {
    }
}
