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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class BrushDisabledMessageTest {

    private static final Path BUNDLE = Paths.get("src", "main", "resources", "bettergopaint.properties");

    @Test
    @DisplayName("disabled brush message explains the menu or /gopaint toggle and not a left click")
    void disabledMessageExplainsHowToEnableBrush() throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(BUNDLE, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        String message = properties.getProperty("bettergopaint.brush.disabled");

        assertThat(message).as("message must exist").isNotNull();
        assertThat(message).as("should point to /gopaint toggle or the menu")
                .containsAnyOf("/gopaint toggle", "menu");
        assertThat(message.toLowerCase(Locale.ROOT)).as("should not claim a left click enables the brush")
                .doesNotContain("left click to enable")
                .doesNotContain("left-click to enable");
    }
}
