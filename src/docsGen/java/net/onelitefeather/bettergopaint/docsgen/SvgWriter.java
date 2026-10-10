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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * Turns an {@link SvgDocument} into SVG text. The output is UTF-8 with line feeds, has no timestamp and no generator
 * version, and formats every number with {@link Locale#ROOT}, so two runs on any platform write the same bytes.
 */
public final class SvgWriter {

    private SvgWriter() {
    }

    /**
     * @param document the picture to write
     * @return the SVG text, with line feeds and a trailing line feed
     */
    public static String write(SvgDocument document) {
        StringBuilder svg = new StringBuilder();
        svg.append(format("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\" viewBox=\"0 0 %d %d\">\n",
                document.width(), document.height(), document.width(), document.height()));
        if (!document.symbols().isEmpty()) {
            svg.append("<defs>\n");
            for (SvgDocument.Symbol symbol : document.symbols()) {
                svg.append(format("  <g id=\"%s\">\n", escape(symbol.id())));
                for (SvgDocument.Rect pixel : symbol.pixels()) {
                    svg.append("    ").append(rect(pixel)).append('\n');
                }
                svg.append("  </g>\n");
            }
            svg.append("</defs>\n");
        }
        for (SvgDocument.Element element : document.elements()) {
            svg.append("  ").append(switch (element) {
                case SvgDocument.Rect rect -> rect(rect);
                case SvgDocument.Text text -> text(text);
                case SvgDocument.Use use -> format("<use href=\"#%s\" x=\"%d\" y=\"%d\"%s/>",
                        escape(use.target()), use.x(), use.y(), opacity(use.opacity()));
                case SvgDocument.Polyline line -> format(
                        "<polyline points=\"%s\" fill=\"none\" stroke=\"%s\" stroke-width=\"%d\""
                                + " stroke-linejoin=\"round\" stroke-linecap=\"round\"/>",
                        points(line.points()), escape(line.stroke()), line.strokeWidth());
                case SvgDocument.Circle circle -> format(
                        "<circle cx=\"%d\" cy=\"%d\" r=\"%d\" fill=\"%s\" stroke=\"%s\" stroke-width=\"%d\"/>",
                        circle.centreX(), circle.centreY(), circle.radius(), escape(circle.fill()),
                        escape(circle.stroke()), circle.strokeWidth());
            }).append('\n');
        }
        svg.append("</svg>\n");
        return svg.toString();
    }

    private static String rect(SvgDocument.Rect rect) {
        return format("<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" fill=\"%s\" stroke=\"%s\" stroke-width=\"%d\"%s/>",
                rect.x(), rect.y(), rect.width(), rect.height(), escape(rect.fill()), escape(rect.stroke()),
                rect.strokeWidth(), opacity(rect.opacity()));
    }

    private static String text(SvgDocument.Text text) {
        return format("<text x=\"%d\" y=\"%d\"%s font-family=\"sans-serif\" font-size=\"%d\" font-weight=\"%s\" fill=\"%s\">%s</text>",
                text.x(), text.y(), text.centred() ? " text-anchor=\"middle\"" : "", text.fontSize(),
                text.bold() ? "bold" : "normal", escape(text.fill()), escape(text.content()));
    }

    /**
     * @return the opacity attribute, empty for a fully opaque element; the fraction has two digits, as {@code 0.60}
     */
    private static String opacity(int percent) {
        return percent == 100 ? "" : format(" opacity=\"%s\"", format("%.2f", percent / 100.0));
    }

    private static String points(List<SvgDocument.Point> points) {
        StringBuilder joined = new StringBuilder();
        for (SvgDocument.Point point : points) {
            if (!joined.isEmpty()) {
                joined.append(' ');
            }
            joined.append(format("%d,%d", point.x(), point.y()));
        }
        return joined.toString();
    }

    /**
     * @param document the picture to write
     * @return the UTF-8 bytes of {@link #write(SvgDocument)}
     */
    public static byte[] bytes(SvgDocument document) {
        return write(document).getBytes(StandardCharsets.UTF_8);
    }

    private static String format(String pattern, Object... arguments) {
        return String.format(Locale.ROOT, pattern, arguments);
    }

    private static String escape(String text) {
        StringBuilder escaped = new StringBuilder(text.length());
        for (char character : text.toCharArray()) {
            switch (character) {
                case '&' -> escaped.append("&amp;");
                case '<' -> escaped.append("&lt;");
                case '>' -> escaped.append("&gt;");
                case '"' -> escaped.append("&quot;");
                case '\'' -> escaped.append("&#39;");
                default -> escaped.append(character);
            }
        }
        return escaped.toString();
    }
}
