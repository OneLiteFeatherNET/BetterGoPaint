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

import static org.assertj.core.api.Assertions.assertThat;

class DocsLinkCheckerTest {

    private static final String SHOTLIST = "development/screenshot-shotlist.md";

    @TempDir
    Path folder;

    private void page(String relative, String content) throws IOException {
        Path file = folder.resolve("docs").resolve(relative);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content, StandardCharsets.UTF_8);
    }

    private List<String> problems() {
        return new DocsLinkChecker(folder.resolve("docs"), Set.of()).problems();
    }

    @Test
    @DisplayName("a relative link to a missing page is reported with its page and target")
    void brokenRelativeLinkIsReported() throws IOException {
        page("how-to/a.md", "See [the reference](../reference/missing.md).");

        assertThat(problems()).as("the problems of a broken link")
                .singleElement().asString().contains("how-to/a.md").contains("../reference/missing.md");
    }

    @Test
    @DisplayName("an image that does not exist is reported with its page and target")
    void brokenImageIsReported() throws IOException {
        page("how-to/a.md", "![Cliff](../images/brushes/angle.svg)");

        assertThat(problems()).as("the problems of a broken image")
                .singleElement().asString().contains("how-to/a.md").contains("../images/brushes/angle.svg");
    }

    @Test
    @DisplayName("an anchor in the same page or in another page is not checked")
    void anchorIsIgnored() throws IOException {
        page("index.md", "[top](#top) and [part](other.md#section)");
        page("other.md", "Text.");

        assertThat(problems()).as("the problems of pages with anchors").isEmpty();
    }

    @Test
    @DisplayName("an https link is not fetched and does not fail the check")
    void externalLinkIsNotFetched() throws IOException {
        page("index.md", "[site](https://example.invalid/missing)");

        assertThat(problems()).as("the problems of an external link").isEmpty();
    }

    @Test
    @DisplayName("a builder page without an image or a shotlist link is reported by its page")
    void builderPageWithoutIllustrationIsReported() throws IOException {
        page("SUMMARY.md", "# Table of contents\n\n## How-to guides\n\n### For builders\n\n"
                + "* [Share a brush](how-to/share.md)\n\n## Reference\n");
        page("how-to/share.md", "Text without a picture.");

        assertThat(problems()).as("the problems of a builder page without illustration")
                .singleElement().asString().contains("how-to/share.md");
    }

    @Test
    @DisplayName("a builder page on the allow-list passes without an illustration")
    void builderPageOnAllowListPasses() throws IOException {
        page("SUMMARY.md", "### For builders\n\n* [Share a brush](how-to/share.md)\n");
        page("how-to/share.md", "Text without a picture.");

        assertThat(new DocsLinkChecker(folder.resolve("docs"), Set.of("how-to/share.md")).problems())
                .as("the problems of a builder page on the allow-list").isEmpty();
    }

    @Test
    @DisplayName("a builder page with an image passes")
    void builderPageWithImagePasses() throws IOException {
        page("SUMMARY.md", "### For builders\n\n* [Share a brush](how-to/share.md)\n");
        page("how-to/share.md", "![Sphere](../images/brushes/sphere-brush.svg)");
        page("images/brushes/sphere-brush.svg", "<svg/>");

        assertThat(problems()).as("the problems of a builder page with an image").isEmpty();
    }

    @Test
    @DisplayName("a hint file that the shotlist does not list is reported by its name")
    void hintFileMissingFromShotlistIsReported() throws IOException {
        page("how-to/a.md", "{% hint style=\"info\" %}\nPlanned: images/guides/cliff.gif\n{% endhint %}");
        page(SHOTLIST, "| Name |\n| --- |\n| images/guides/other.gif |\n");

        assertThat(problems()).as("the problems of a hint without shotlist entry")
                .singleElement().asString().contains("images/guides/cliff.gif");
    }

    @Test
    @DisplayName("a hint file that the shotlist lists passes")
    void hintFileListedInShotlistPasses() throws IOException {
        page("how-to/a.md", "{% hint style=\"info\" %}\nPlanned: images/guides/cliff.gif\n{% endhint %}");
        page(SHOTLIST, "| Name |\n| --- |\n| images/guides/cliff.gif |\n");

        assertThat(problems()).as("the problems of a hint with shotlist entry").isEmpty();
    }
}
