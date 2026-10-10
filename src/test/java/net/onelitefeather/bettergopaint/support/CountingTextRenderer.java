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
package net.onelitefeather.bettergopaint.support;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Counts the translation renders of a menu. A label is a translatable without arguments, the part of the
 * menu the render cache is meant to keep. Returns the component unchanged, so no translation bundle is needed.
 */
public final class CountingTextRenderer implements BiFunction<Locale, Component, Component> {

    private final Map<Locale, Integer> labelsByLocale = new HashMap<>();
    private int calls;
    private int labels;

    @Override
    public Component apply(Locale locale, Component component) {
        calls++;
        if (component instanceof TranslatableComponent translatable && translatable.arguments().isEmpty()) {
            labels++;
            labelsByLocale.merge(locale, 1, Integer::sum);
        }
        return component;
    }

    public int calls() {
        return calls;
    }

    public int labels() {
        return labels;
    }

    public int labelsFor(Locale locale) {
        return labelsByLocale.getOrDefault(locale, 0);
    }
}
