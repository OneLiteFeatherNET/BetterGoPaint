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
    @DisplayName("writing a document creates no file in the folder")
    void writesNothingToDisk() throws IOException {
        SvgWriter.write(sample());
        SvgWriter.bytes(sample());

        try (Stream<Path> files = Files.list(folder)) {
            assertThat(files).as("the files written by the writer").isEmpty();
        }
    }
}
