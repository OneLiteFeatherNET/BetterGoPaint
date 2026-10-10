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
package net.onelitefeather.bettergopaint.objects.brush;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards the rule that a stroke is computed from WorldEdit types only. The brushes and the geometry
 * helpers must not call Bukkit world, block or location methods, because FAWE runs them on a worker thread.
 */
class NoBukkitWorldAccessTest {

    private static final List<Path> ROOTS = List.of(
            Path.of("src/main/java/net/onelitefeather/bettergopaint/objects"),
            Path.of("src/main/java/net/onelitefeather/bettergopaint/utils/Sphere.java"),
            Path.of("src/main/java/net/onelitefeather/bettergopaint/utils/Height.java"),
            Path.of("src/main/java/net/onelitefeather/bettergopaint/utils/Surface.java"),
            Path.of("src/main/java/net/onelitefeather/bettergopaint/utils/ConnectedBlocks.java"),
            Path.of("src/main/java/net/onelitefeather/bettergopaint/utils/Blocks.java"),
            Path.of("src/main/java/net/onelitefeather/bettergopaint/utils/curve")
    );

    private static final Pattern FORBIDDEN = Pattern.compile(
            "getBlock\\(\\)|getBlockAt|org\\.bukkit\\.block\\.|org\\.bukkit\\.Location|org\\.bukkit\\.World|"
                    + "org\\.bukkit\\.entity\\.|org\\.bukkit\\.Bukkit|\\.getWorld\\(\\)"
    );

    @Test
    @DisplayName("brush and geometry code does not use Bukkit worlds, blocks, locations or players")
    void brushCodeIsFreeOfBukkitWorldAccess() throws IOException {
        List<String> offences = new java.util.ArrayList<>();
        for (Path root : ROOTS) {
            assertThat(root).exists();
            try (Stream<Path> files = Files.walk(root)) {
                for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                    List<String> lines = Files.readAllLines(file);
                    for (int i = 0; i < lines.size(); i++) {
                        if (FORBIDDEN.matcher(lines.get(i)).find()) {
                            offences.add(file + ":" + (i + 1) + " " + lines.get(i).strip());
                        }
                    }
                }
            }
        }

        assertThat(offences).isEmpty();
    }
}
