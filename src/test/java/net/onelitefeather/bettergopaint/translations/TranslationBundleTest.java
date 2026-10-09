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
package net.onelitefeather.bettergopaint.translations;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Guards the shipped language files: every key the code asks for has to exist, every language has
 * to ship the same keys, and every message has to be valid MiniMessage.
 */
class TranslationBundleTest {

    private static final Path RESOURCES = Paths.get("src", "main", "resources");
    private static final Path SOURCES = Paths.get("src", "main", "java");
    private static final String BUNDLE = "bettergopaint";
    private static final Pattern TRANSLATABLE = Pattern.compile("translatable\\(\"(bettergopaint\\.[a-z0-9_.]+)\"");

    static List<Path> bundles() throws IOException {
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(RESOURCES, BUNDLE + "*.properties")) {
            stream.forEach(files::add);
        }
        Collections.sort(files);
        return files;
    }

    private static Properties read(Path file) throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    private static Set<String> keysUsedInCode() throws IOException {
        Set<String> keys = new TreeSet<>();
        try (var files = Files.walk(SOURCES)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                Matcher matcher = TRANSLATABLE.matcher(Files.readString(file));
                while (matcher.find()) {
                    keys.add(matcher.group(1));
                }
            }
        }
        return keys;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("bundles")
    @DisplayName("every language ships every key the code uses")
    void containsEveryKeyUsedInCode(Path bundle) throws IOException {
        assertThat(read(bundle).stringPropertyNames())
                .as("keys of %s", bundle.getFileName())
                .containsAll(keysUsedInCode());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("bundles")
    @DisplayName("every message is valid MiniMessage")
    void messagesParse(Path bundle) throws IOException {
        Properties properties = read(bundle);
        for (String key : properties.stringPropertyNames()) {
            assertThatCode(() -> MiniMessage.miniMessage().deserialize(properties.getProperty(key)))
                    .as("%s in %s", key, bundle.getFileName())
                    .doesNotThrowAnyException();
        }
    }
}
