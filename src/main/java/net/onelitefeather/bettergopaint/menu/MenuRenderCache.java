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
package net.onelitefeather.bettergopaint.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;

/**
 * Keeps the rendered labels of the menus, one set per locale. A label is a translation without arguments, so it
 * depends only on the translation store. The set is dropped when the translation generation changes, which is
 * how a reload of the bundles reaches the menus.
 *
 * <p>An instance is owned by the {@link BrushMenuFactory}; nothing here is static, so every factory has its own cache.
 */
public final class MenuRenderCache {

    private final IntSupplier translationGeneration;
    private final BiFunction<Locale, Component, Component> renderer;
    private final Map<Locale, Map<String, Component>> labels = new HashMap<>();
    private int labelGeneration;

    public MenuRenderCache(
            @NotNull IntSupplier translationGeneration,
            @NotNull BiFunction<Locale, Component, Component> renderer
    ) {
        this.translationGeneration = translationGeneration;
        this.renderer = renderer;
        this.labelGeneration = translationGeneration.getAsInt();
    }

    /**
     * @return the current translation generation; a menu rendered under another generation is stale
     */
    public int generation() {
        return translationGeneration.getAsInt();
    }

    /**
     * @param locale the locale of the viewer
     * @param key the translation key, without arguments
     * @return the rendered label, rendered once per locale and translation generation
     */
    public synchronized @NotNull Component label(@NotNull Locale locale, @NotNull String key) {
        int generation = translationGeneration.getAsInt();
        if (generation != labelGeneration) {
            labels.clear();
            labelGeneration = generation;
        }
        return labels.computeIfAbsent(locale, ignored -> new HashMap<>())
                .computeIfAbsent(key, ignored -> renderer.apply(locale, Component.translatable(key)));
    }

    /**
     * Renders a text with arguments. These depend on the state of the player, so they are not cached.
     *
     * @param locale the locale of the viewer
     * @param key the translation key
     * @param arguments the arguments of the translation
     * @return the rendered text
     */
    public @NotNull Component render(@NotNull Locale locale, @NotNull String key, @NotNull ComponentLike... arguments) {
        return renderer.apply(locale, Component.translatable(key, arguments));
    }
}
