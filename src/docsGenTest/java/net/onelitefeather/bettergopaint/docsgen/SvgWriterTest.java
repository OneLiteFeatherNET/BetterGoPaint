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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SvgWriterTest {

    private static final Pattern ATTRIBUTE = Pattern.compile("([\\w-]+)=\"([^\"]*)\"");
    private static final Set<String> GEOMETRY = Set.of("x", "y", "width", "height", "font-size", "stroke-width", "viewBox");

    @TempDir
    Path folder;

    private static SvgDocument sample() {
        return new SvgDocument(60, 30)
                .rect(1, 2, 10, 12, "#ca8a04", "#ffffff", 1)
                .text(4, 20, "Gold & <stone>", 13, "#111827", true);
    }

    @Test
    @DisplayName("an empty document is a root element with its size and a trailing newline")
    void emptyDocument() {
        String svg = SvgWriter.write(new SvgDocument(40, 20));

        assertThat(svg).as("the svg of an empty 40 by 20 document").isEqualTo(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"40\" height=\"20\" viewBox=\"0 0 40 20\">\n"
                        + "</svg>\n");
    }

    @Test
    @DisplayName("text content escapes the characters that are special in xml")
    void textIsEscaped() {
        String svg = SvgWriter.write(new SvgDocument(10, 10).text(1, 2, "<a & \"b\" 'c'>", 12, "#000000", false));

        assertThat(svg).as("the escaped text element").contains(
                ">&lt;a &amp; &quot;b&quot; &#39;c&#39;&gt;</text>");
    }

    @Test
    @DisplayName("the same model is written to the same string every time")
    void sameModelSameString() {
        assertThat(SvgWriter.write(sample())).as("the first write of the model")
                .isEqualTo(SvgWriter.write(sample()))
                .as("a second write of the same instance")
                .isEqualTo(SvgWriter.write(sample()));
    }

    @Test
    @DisplayName("the output uses line feeds only and ends with one")
    void lineFeedsOnly() {
        String svg = SvgWriter.write(sample());

        assertThat(svg).as("the svg text").doesNotContain("\r").endsWith("</svg>\n");
    }

    @Test
    @DisplayName("every coordinate and size is written as an integer")
    void integerCoordinates() {
        String svg = SvgWriter.write(sample());

        Matcher matcher = ATTRIBUTE.matcher(svg);
        int checked = 0;
        while (matcher.find()) {
            if (GEOMETRY.contains(matcher.group(1))) {
                checked++;
                assertThat(matcher.group(2)).as("the value of %s", matcher.group(1)).matches("-?\\d+( -?\\d+)*");
            }
        }
        assertThat(checked).as("the geometry attributes of the sample").isEqualTo(11);
    }

    @Test
    @DisplayName("the bytes of the document are its UTF-8 text")
    void utf8Bytes() {
        SvgDocument document = new SvgDocument(10, 10).text(1, 2, "°ä", 12, "#000000", false);

        assertThat(SvgWriter.bytes(document)).as("the bytes of a document with non-ASCII text")
                .isEqualTo(SvgWriter.write(document).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("a symbol is written once in defs, before the drawing")
    void symbolsAreWrittenInDefsBeforeTheDrawing() {
        SvgDocument document = new SvgDocument(10, 10)
                .symbol("tile", List.of(new SvgDocument.Rect(0, 0, 3, 3, "#7d7d7d", "none", 0, 100)))
                .rect(0, 0, 10, 10, "#ffffff", "none", 0);

        String svg = SvgWriter.write(document);

        assertThat(svg).as("the svg with one symbol and one rectangle")
                .contains("<defs>\n  <g id=\"tile\">\n    <rect x=\"0\" y=\"0\" width=\"3\" height=\"3\" fill=\"#7d7d7d\" stroke=\"none\" stroke-width=\"0\"/>\n  </g>\n</defs>\n")
                .containsOnlyOnce("<g id=\"tile\">");
        assertThat(svg.indexOf("<defs>")).as("the position of defs").isLessThan(svg.indexOf("<rect x=\"0\" y=\"0\" width=\"10\""));
    }

    @Test
    @DisplayName("a use element writes its target, its integer position and its opacity as a fraction")
    void useElementWritesTargetPositionAndOpacity() {
        String svg = SvgWriter.write(new SvgDocument(40, 40)
                .use("block-stone-side-3", 12, 24, 60)
                .use("block-stone-side-3", 0, 0, 100));

        assertThat(svg).as("the use elements of the svg")
                .contains("  <use href=\"#block-stone-side-3\" x=\"12\" y=\"24\" opacity=\"0.60\"/>\n")
                .contains("  <use href=\"#block-stone-side-3\" x=\"0\" y=\"0\"/>\n");
    }

    @Test
    @DisplayName("a rectangle with less than full opacity writes its opacity as a fraction")
    void fadedRectangleWritesOpacity() {
        String svg = SvgWriter.write(new SvgDocument(40, 40).rect(1, 2, 10, 12, "#866043", "none", 0, 35));

        assertThat(svg).as("the faded rectangle").contains("stroke-width=\"0\" opacity=\"0.35\"/>");
    }

    @Test
    @DisplayName("a polyline writes its points as integer pairs and draws no fill")
    void polylineWritesIntegerPoints() {
        String svg = SvgWriter.write(new SvgDocument(40, 40).polyline(
                List.of(new SvgDocument.Point(12, 12), new SvgDocument.Point(36, 24)), "#111827", 2));

        assertThat(svg).as("the polyline of the svg")
                .contains("<polyline points=\"12,12 36,24\" fill=\"none\" stroke=\"#111827\" stroke-width=\"2\""
                        + " stroke-linejoin=\"round\" stroke-linecap=\"round\"/>");
    }

    @Test
    @DisplayName("a circle writes its centre and radius as integers")
    void circleWritesIntegerGeometry() {
        String svg = SvgWriter.write(new SvgDocument(40, 40).circle(20, 16, 8, "#ffffff", "#111827", 2));

        assertThat(svg).as("the circle of the svg")
                .contains("<circle cx=\"20\" cy=\"16\" r=\"8\" fill=\"#ffffff\" stroke=\"#111827\" stroke-width=\"2\"/>");
    }

    @Test
    @DisplayName("centred text is anchored in the middle")
    void centredTextIsAnchoredInTheMiddle() {
        String svg = SvgWriter.write(new SvgDocument(40, 40).centredText(20, 30, "10", 11, "#111827", true));

        assertThat(svg).as("the centred text of the svg")
                .contains("<text x=\"20\" y=\"30\" text-anchor=\"middle\" font-family=\"sans-serif\" font-size=\"11\""
                        + " font-weight=\"bold\" fill=\"#111827\">10</text>");
    }

    @Test
    @DisplayName("the coordinates of the new elements are integers")
    void newElementsHaveIntegerGeometry() {
        String svg = SvgWriter.write(new SvgDocument(40, 40)
                .symbol("tile", List.of(new SvgDocument.Rect(1, 2, 3, 4, "#000000", "none", 0, 100)))
                .use("tile", 5, 6, 100)
                .polyline(List.of(new SvgDocument.Point(7, 8), new SvgDocument.Point(9, 10)), "#000000", 1)
                .circle(11, 12, 13, "#000000", "none", 0)
                .centredText(14, 15, "1", 11, "#000000", false));

        Matcher matcher = ATTRIBUTE.matcher(svg);
        int checked = 0;
        while (matcher.find()) {
            if (Set.of("x", "y", "width", "height", "cx", "cy", "r").contains(matcher.group(1))) {
                checked++;
                assertThat(matcher.group(2)).as("the value of %s", matcher.group(1)).matches("-?\\d+");
            } else if (matcher.group(1).equals("points")) {
                checked++;
                assertThat(matcher.group(2)).as("the points").matches("-?\\d+,-?\\d+( -?\\d+,-?\\d+)*");
            }
        }
        assertThat(checked).as("the geometry attributes of the new elements").isEqualTo(14);
    }

    @Test
    @DisplayName("writing a document creates no file in the folder")
    void writesNothingToDisk() throws IOException {
        SvgWriter.write(sample());
        SvgWriter.bytes(sample());

        try (Stream<Path> files = Files.list(folder)) {
            assertThat(files).as("the files written by the writer").isEmpty();
        }
    }
}
