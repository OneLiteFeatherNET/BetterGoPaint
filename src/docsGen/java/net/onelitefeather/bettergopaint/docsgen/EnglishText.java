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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Turns translated components into the English text of {@code bettergopaint.properties}. The values there are
 * MiniMessage, so the arguments are put in place of {@code <arg:N>} and the tags are removed. A key without an English
 * value fails with its name, so the generator never draws a raw key into an image.
 */
final class EnglishText {

    private static final String BUNDLE = "bettergopaint.properties";
    private static final Pattern ARGUMENT = Pattern.compile("<arg:(\\d+)>");
    private static final Pattern TAG = Pattern.compile("<[^<>]*>");

    private final Map<String, String> values;

    private EnglishText(Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    /**
     * @return the English values of the bundle that ships in the plugin's resources
     */
    static EnglishText fromClasspath() {
        Properties properties = new Properties();
        try (InputStream stream = EnglishText.class.getClassLoader().getResourceAsStream(BUNDLE)) {
            if (stream == null) {
                throw new IllegalStateException(BUNDLE + " is not on the classpath of the generator");
            }
            properties.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new UncheckedIOException("could not read " + BUNDLE, exception);
        }
        return new EnglishText(properties.stringPropertyNames().stream()
                .collect(Collectors.toMap(key -> key, properties::getProperty)));
    }

    /**
     * @param values the English values by translation key
     * @return the English text of the given values
     */
    static EnglishText of(Map<String, String> values) {
        return new EnglishText(values);
    }

    /**
     * @param component a component built with {@code Component.translatable}, possibly with arguments
     * @return the English text of the component, without MiniMessage tags
     * @throws IllegalStateException if a translation key has no English value; the message names the key
     */
    String plain(Component component) {
        StringBuilder text = new StringBuilder();
        if (component instanceof TranslatableComponent translatable) {
            text.append(translate(translatable));
        } else if (component instanceof TextComponent textComponent) {
            text.append(textComponent.content());
        }
        for (Component child : component.children()) {
            text.append(plain(child));
        }
        return text.toString();
    }

    private String translate(TranslatableComponent translatable) {
        String pattern = values.get(translatable.key());
        if (pattern == null) {
            throw new IllegalStateException("no English text for the translation key " + translatable.key()
                    + " in " + BUNDLE);
        }
        Matcher matcher = ARGUMENT.matcher(pattern);
        StringBuilder filled = new StringBuilder();
        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            matcher.appendReplacement(filled, Matcher.quoteReplacement(argument(translatable, index)));
        }
        matcher.appendTail(filled);
        return TAG.matcher(filled).replaceAll("");
    }

    private String argument(TranslatableComponent translatable, int index) {
        if (index >= translatable.arguments().size()) {
            throw new IllegalStateException("the translation key " + translatable.key() + " has no argument " + index);
        }
        Object value = translatable.arguments().get(index).value();
        return value instanceof Component component ? plain(component) : String.valueOf(value);
    }
}
