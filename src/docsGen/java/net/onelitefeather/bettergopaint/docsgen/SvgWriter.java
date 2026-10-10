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
        for (SvgDocument.Element element : document.elements()) {
            switch (element) {
                case SvgDocument.Rect rect -> svg.append(format(
                        "  <rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" fill=\"%s\" stroke=\"%s\" stroke-width=\"%d\"/>\n",
                        rect.x(), rect.y(), rect.width(), rect.height(), escape(rect.fill()), escape(rect.stroke()),
                        rect.strokeWidth()));
                case SvgDocument.Text text -> svg.append(format(
                        "  <text x=\"%d\" y=\"%d\" font-family=\"sans-serif\" font-size=\"%d\" font-weight=\"%s\" fill=\"%s\">%s</text>\n",
                        text.x(), text.y(), text.fontSize(), text.bold() ? "bold" : "normal", escape(text.fill()),
                        escape(text.content())));
            }
        }
        svg.append("</svg>\n");
        return svg.toString();
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
