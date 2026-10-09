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
package net.onelitefeather.bettergopaint.service;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Registers the bundled English messages and every extra language from {@code lang/} with
 * Adventure's global translator.
 */
@Singleton
public final class TranslationService {

    public static final String LANG_FOLDER = "lang-folder";
    private static final String BUNDLE = "bettergopaint";

    private final Path langFolder;
    private final SettingsHolder settings;
    private final ClassLoader pluginClassLoader;

    @Inject
    public TranslationService(@Named(LANG_FOLDER) @NotNull Path langFolder, @NotNull SettingsHolder settings) {
        this.langFolder = langFolder;
        this.settings = settings;
        this.pluginClassLoader = TranslationService.class.getClassLoader();
    }

    public void register() {
        var store = MiniMessageTranslationStore.create(Key.key("bettergopaint", "translations"));
        store.defaultLocale(Locale.US);
        var languages = new HashSet<>(settings.get().generic().languages());
        languages.add("en-US");
        if (Files.exists(langFolder)) {
            try (var classLoader = new URLClassLoader(new URL[]{langFolder.toUri().toURL()}, pluginClassLoader)) {
                languages.stream().map(Locale::forLanguageTag).forEach(locale ->
                        store.registerAll(locale, ResourceBundle.getBundle(BUNDLE, locale, classLoader), false));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            languages.stream().map(Locale::forLanguageTag).forEach(locale ->
                    store.registerAll(locale, ResourceBundle.getBundle(BUNDLE, locale, pluginClassLoader), false));
        }
        GlobalTranslator.translator().addSource(store);
    }
}
