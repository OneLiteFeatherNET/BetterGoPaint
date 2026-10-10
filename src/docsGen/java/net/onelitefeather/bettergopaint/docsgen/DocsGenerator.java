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

import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.support.Terrain;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Command line entry point of the documentation tooling. The generator is run by Gradle, see the tasks
 * {@code generateDocsImages}, {@code checkDocsImages} and {@code checkDocsLinks} in {@code build.gradle.kts}.
 * <p>
 * Usage: {@code DocsGenerator generate|check|links <docs directory>}. {@code generate} writes the images into
 * {@code docs/images}; {@code check} compares them with the generator; {@code links} checks the pages. Both checks exit
 * with status 1 and name every problem.
 */
public final class DocsGenerator {

    private static final String IMAGES = "images";

    private DocsGenerator() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2 || !List.of("generate", "check", "links").contains(args[0])) {
            System.err.println("usage: DocsGenerator generate|check|links <docs directory>");
            System.exit(2);
            return;
        }
        Path docs = Path.of(args[1]).toAbsolutePath().normalize();
        Path images = docs.resolve(IMAGES);
        switch (args[0]) {
            case "generate" -> {
                Map<String, byte[]> expected = expectedImages();
                DocsImages.write(images, expected);
                System.out.println("wrote " + expected.size() + " generated images to " + images);
            }
            case "check" -> {
                Map<String, byte[]> expected = expectedImages();
                report(DocsImages.problems(images, expected), expected.size() + " generated images match " + images);
            }
            default -> report(new DocsLinkChecker(docs).problems(),
                    "all links and illustrations in " + docs + " resolve");
        }
    }

    private static void report(List<String> problems, String success) {
        if (problems.isEmpty()) {
            System.out.println(success);
            return;
        }
        problems.forEach(System.err::println);
        System.err.println(problems.size() + " problem(s)");
        System.exit(1);
    }

    /**
     * Renders every generated image in memory.
     *
     * @return the images keyed by their path below {@code docs/images}, with {@code /} as separator, in sorted order
     */
    static Map<String, byte[]> expectedImages() {
        Terrain terrain = new Terrain();
        BlockPalette palette = BlockPalette.of(terrain);
        SettingsHolder settings = GeneratorFixtures.settings();
        PlayerBrushManager manager = GeneratorFixtures.brushManager(settings);

        Map<String, byte[]> images = new TreeMap<>();
        for (BrushScene scene : BrushScenes.all(manager.getBrushes(), terrain)) {
            images.put("brushes/" + fileName(scene.brush()) + ".svg", SvgWriter.bytes(BrushIllustration.render(scene, palette)));
        }
        EnglishText english = EnglishText.fromClasspath();
        for (MenuStates.State state : MenuStates.all()) {
            images.put("menu/" + state.fileName(),
                    SvgWriter.bytes(MenuMap.render(state.title(), MenuMap.cells(state.layout(), english))));
        }
        return images;
    }

    /**
     * @param brush the brush the picture belongs to
     * @return the name of the brush as a file name, for example {@code sphere-brush}
     */
    static String fileName(Brush brush) {
        return brush.getName().toLowerCase(Locale.ROOT).replace(' ', '-');
    }
}
