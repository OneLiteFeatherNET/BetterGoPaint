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
package net.onelitefeather.bettergopaint.docs;

import net.onelitefeather.bettergopaint.settings.SettingsLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Keeps the hand-written reference pages in step with the sources the plugin itself reads, so a
 * renamed key, a new permission or a changed default fails the build instead of the docs drifting.
 */
class ReferenceDocsTest {

    private static final Path DOCS = Path.of("docs");
    private static final Path BUILD_SCRIPT = Path.of("build.gradle.kts");
    private static final Path COMMANDS = Path.of("src", "main", "java", "net", "onelitefeather", "bettergopaint",
            "command", "GoPaintCommand.java");

    @TempDir
    Path folder;

    private static String read(Path path) throws IOException {
        return Files.readString(path);
    }

    private static List<String> matches(Pattern pattern, String text) {
        List<String> found = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            found.add(matcher.group(1));
        }
        return found;
    }

    @Test
    @DisplayName("every config key and its default is documented")
    void configurationIsDocumented() throws IOException {
        Path file = folder.resolve("config.yml");
        SettingsLoader.load(file);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file.toFile());
        String page = read(DOCS.resolve("reference/configuration.md"));

        for (String path : yaml.getKeys(true)) {
            if (yaml.isConfigurationSection(path)) {
                assertThat(page).as("section %s", path).contains("## " + path);
                continue;
            }
            String key = path.substring(path.indexOf('.') + 1);
            Object value = yaml.get(path);
            String shown = value instanceof List<?> list && list.isEmpty() ? "[]" : String.valueOf(value);
            assertThat(page).as("row for %s with default %s", path, shown)
                    .containsPattern("\\| `" + Pattern.quote(key) + "` \\| `" + Pattern.quote(shown) + "` \\|");
        }
    }

    @Test
    @DisplayName("every permission declared in the build is documented")
    void permissionsAreDocumented() throws IOException {
        List<String> permissions = matches(Pattern.compile("register\\(\"(bettergopaint\\.[^\"]+)\"\\)"), read(BUILD_SCRIPT));
        String page = read(DOCS.resolve("reference/permissions.md"));

        assertThat(permissions).isNotEmpty();
        permissions.forEach(permission -> assertThat(page).as(permission).contains("| `" + permission + "` |"));
    }

    @Test
    @DisplayName("every subcommand is documented")
    void commandsAreDocumented() throws IOException {
        List<String> subcommands = matches(Pattern.compile("@Command\\(\"gopaint\\|gp\\|bgp ([a-z]+)"), read(COMMANDS));
        String page = read(DOCS.resolve("reference/commands.md"));

        assertThat(subcommands).isNotEmpty();
        subcommands.forEach(subcommand -> assertThat(page).as(subcommand).contains("/gopaint " + subcommand));
    }

    @Test
    @DisplayName("every supported Minecraft version is documented")
    void supportedVersionsAreDocumented() throws IOException {
        String script = read(BUILD_SCRIPT);
        String list = script.substring(script.indexOf("val supportedMinecraftVersions"), script.indexOf(")", script.indexOf("val supportedMinecraftVersions")));
        List<String> versions = matches(Pattern.compile("\"([0-9.]+)\""), list);
        String page = read(DOCS.resolve("reference/supported-versions.md"));

        assertThat(versions).isNotEmpty();
        versions.forEach(version -> assertThat(page).as(version).contains(version));
    }

    @Test
    @DisplayName("every user page is listed in the navigation")
    void everyPageIsInTheNavigation() throws IOException {
        String summary = read(DOCS.resolve("SUMMARY.md"));
        try (var pages = Files.walk(DOCS)) {
            pages.filter(path -> path.toString().endsWith(".md"))
                    .map(path -> DOCS.relativize(path).toString().replace('\\', '/'))
                    .filter(page -> !page.equals("SUMMARY.md") && !page.startsWith("development/"))
                    .forEach(page -> assertThat(summary).as(page).contains("(" + page + ")"));
        }
    }
}
