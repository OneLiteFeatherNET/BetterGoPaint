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
 * A picture as plain data: its size, its symbols and its elements in drawing order. Every coordinate and size is a
 * whole number, so {@link SvgWriter} never has to format a fraction, and the same model always produces the same
 * bytes. A symbol is a group of pixels that is written once in {@code defs} and drawn any number of times with
 * {@link #use}, which keeps a picture of hundreds of block faces small.
 */
public final class SvgDocument {

    private final int width;
    private final int height;
    private final List<Element> elements = new ArrayList<>();
    private final List<Symbol> symbols = new ArrayList<>();

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
        return rect(x, y, width, height, fill, stroke, strokeWidth, 100);
    }

    /**
     * Adds a rectangle that is drawn with the given opacity.
     *
     * @param opacity the opacity in percent, from 0 to 100
     */
    public SvgDocument rect(int x, int y, int width, int height, String fill, String stroke, int strokeWidth,
                            int opacity) {
        elements.add(new Rect(x, y, width, height, Objects.requireNonNull(fill), Objects.requireNonNull(stroke),
                strokeWidth, checkOpacity(opacity)));
        return this;
    }

    /**
     * Adds a line of text whose start is at {@code x} and whose baseline is at {@code y}.
     */
    public SvgDocument text(int x, int y, String content, int fontSize, String fill, boolean bold) {
        elements.add(new Text(x, y, Objects.requireNonNull(content), fontSize, Objects.requireNonNull(fill), bold,
                false));
        return this;
    }

    /**
     * Adds a line of text whose middle is at {@code x} and whose baseline is at {@code y}.
     */
    public SvgDocument centredText(int x, int y, String content, int fontSize, String fill, boolean bold) {
        elements.add(new Text(x, y, Objects.requireNonNull(content), fontSize, Objects.requireNonNull(fill), bold,
                true));
        return this;
    }

    /**
     * Adds a circle.
     */
    public SvgDocument circle(int centreX, int centreY, int radius, String fill, String stroke, int strokeWidth) {
        elements.add(new Circle(centreX, centreY, radius, Objects.requireNonNull(fill), Objects.requireNonNull(stroke),
                strokeWidth));
        return this;
    }

    /**
     * Adds an open line through the points, in the given order, without a fill.
     */
    public SvgDocument polyline(List<Point> points, String stroke, int strokeWidth) {
        elements.add(new Polyline(List.copyOf(points), Objects.requireNonNull(stroke), strokeWidth));
        return this;
    }

    /**
     * Defines a symbol: a group of pixels drawn at the origin of the symbol. The symbols are written in the order of
     * their ids.
     *
     * @param id     the id of the symbol, unique in the picture
     * @param pixels the rectangles of the symbol, in symbol coordinates
     * @throws IllegalStateException if a symbol with the same id exists
     */
    public SvgDocument symbol(String id, List<Rect> pixels) {
        for (Symbol existing : symbols) {
            if (existing.id().equals(id)) {
                throw new IllegalStateException("the picture already defines the symbol " + id);
            }
        }
        symbols.add(new Symbol(Objects.requireNonNull(id), List.copyOf(pixels)));
        return this;
    }

    /**
     * Draws a symbol at a position.
     *
     * @param target  the id of a symbol of this picture
     * @param opacity the opacity in percent, from 0 to 100
     */
    public SvgDocument use(String target, int x, int y, int opacity) {
        elements.add(new Use(Objects.requireNonNull(target), x, y, checkOpacity(opacity)));
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
     * @return the symbols in the order they were defined
     */
    public List<Symbol> symbols() {
        return List.copyOf(symbols);
    }

    private static int checkOpacity(int opacity) {
        if (opacity < 0 || opacity > 100) {
            throw new IllegalArgumentException("an opacity is a percentage from 0 to 100, got " + opacity);
        }
        return opacity;
    }

    /**
     * One drawn element.
     */
    public sealed interface Element permits Rect, Text, Use, Polyline, Circle {
    }

    /**
     * A rectangle. A zero stroke width draws no outline; an opacity of 100 is the default and is not written.
     */
    public record Rect(int x, int y, int width, int height, String fill, String stroke, int strokeWidth,
                       int opacity) implements Element {
    }

    /**
     * A line of sans-serif text, starting at {@code x} or centred on it.
     */
    public record Text(int x, int y, String content, int fontSize, String fill, boolean bold, boolean centred)
            implements Element {
    }

    /**
     * A drawn symbol.
     */
    public record Use(String target, int x, int y, int opacity) implements Element {
    }

    /**
     * An open line through whole-number points.
     */
    public record Polyline(List<Point> points, String stroke, int strokeWidth) implements Element {
    }

    /**
     * A circle.
     */
    public record Circle(int centreX, int centreY, int radius, String fill, String stroke, int strokeWidth)
            implements Element {
    }

    /**
     * A point with whole-number coordinates.
     */
    public record Point(int x, int y) {
    }

    /**
     * A symbol: the pixels that {@link SvgDocument#use} draws.
     *
     * @param id     the id of the symbol
     * @param pixels the rectangles of the symbol in symbol coordinates
     */
    public record Symbol(String id, List<Rect> pixels) {
    }
}
