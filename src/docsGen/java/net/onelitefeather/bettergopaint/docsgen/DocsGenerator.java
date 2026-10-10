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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Command line entry point of the documentation tooling. The generator is run by Gradle, see the tasks
 * {@code generateDocsImages}, {@code checkDocsImages} and {@code checkDocsLinks} in {@code build.gradle.kts}.
 * <p>
 * Usage: {@code DocsGenerator generate <docs directory>}.
 */
public final class DocsGenerator {

    private static final String IMAGES = "images";

    private DocsGenerator() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2 || !"generate".equals(args[0])) {
            System.err.println("usage: DocsGenerator generate <docs directory>");
            System.exit(2);
            return;
        }
        Path docs = Path.of(args[1]).toAbsolutePath().normalize();
        Map<String, byte[]> images = expectedImages();
        write(docs.resolve(IMAGES), images);
        System.out.println("wrote " + images.size() + " generated images to " + docs.resolve(IMAGES));
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

    private static void write(Path imagesDirectory, Map<String, byte[]> images) throws IOException {
        for (Map.Entry<String, byte[]> image : images.entrySet()) {
            Path file = imagesDirectory.resolve(image.getKey());
            Files.createDirectories(file.getParent());
            Files.write(file, image.getValue());
        }
    }
}
