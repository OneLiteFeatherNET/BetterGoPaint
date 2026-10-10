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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DocsImagesTest {

    @TempDir
    Path images;

    private void writeFile(String relative, String content) throws IOException {
        Path file = images.resolve(relative);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content, StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("a generated image that is not in the folder is reported by its name as missing")
    void missingFileIsReported() {
        List<String> problems = DocsImages.problems(images, Map.of("brushes/a.svg", bytes("x")));

        assertThat(problems).as("the problems of a missing image")
                .singleElement().asString().contains("brushes/a.svg").contains("missing");
    }

    @Test
    @DisplayName("a committed image that differs from the generated output is reported by its name")
    void differingFileIsReported() throws IOException {
        writeFile("brushes/a.svg", "edited by hand");

        List<String> problems = DocsImages.problems(images, Map.of("brushes/a.svg", bytes("generated")));

        assertThat(problems).as("the problems of a differing image")
                .singleElement().asString().contains("brushes/a.svg").contains("differs");
    }

    @Test
    @DisplayName("an extra image inside a generated folder is reported by its name")
    void extraFileIsReported() throws IOException {
        writeFile("brushes/a.svg", "same");
        writeFile("brushes/extra.svg", "not generated");

        List<String> problems = DocsImages.problems(images, Map.of("brushes/a.svg", bytes("same")));

        assertThat(problems).as("the problems of an extra image")
                .singleElement().asString().contains("brushes/extra.svg").contains("not generated");
    }

    @Test
    @DisplayName("a hand-added screenshot outside the generated folders is ignored")
    void handAddedScreenshotIsIgnored() throws IOException {
        writeFile("brushes/a.svg", "same");
        writeFile("screenshots/tour.gif", "captured in game");

        List<String> problems = DocsImages.problems(images, Map.of("brushes/a.svg", bytes("same")));

        assertThat(problems).as("the problems of a screenshot outside the generated folders").isEmpty();
    }

    @Test
    @DisplayName("a folder with exactly the generated files passes")
    void matchingFolderPasses() throws IOException {
        writeFile("brushes/a.svg", "same");
        writeFile("menu/b.svg", "also same");

        List<String> problems = DocsImages.problems(images, Map.of(
                "brushes/a.svg", bytes("same"),
                "menu/b.svg", bytes("also same")));

        assertThat(problems).as("the problems of a matching folder").isEmpty();
    }

    @Test
    @DisplayName("writing the images creates the folders and the files with the given bytes")
    void writeCreatesFiles() throws IOException {
        DocsImages.write(images, Map.of("brushes/a.svg", bytes("written")));

        assertThat(Files.readString(images.resolve("brushes/a.svg"), StandardCharsets.UTF_8))
                .as("the content of the written file").isEqualTo("written");
    }

    private static byte[] bytes(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }
}
