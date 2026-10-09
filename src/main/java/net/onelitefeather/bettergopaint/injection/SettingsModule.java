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
package net.onelitefeather.bettergopaint.injection;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import net.onelitefeather.bettergopaint.service.TranslationService;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Binds the files the plugin reads from its data folder.
 */
public final class SettingsModule extends AbstractModule {

    private final Path dataFolder;

    public SettingsModule(@NotNull Path dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    protected void configure() {
        bind(Path.class).annotatedWith(Names.named(SettingsHolder.CONFIG_FILE)).toInstance(dataFolder.resolve("config.yml"));
        bind(Path.class).annotatedWith(Names.named(TranslationService.LANG_FOLDER)).toInstance(dataFolder.resolve("lang"));
        bind(SettingsHolder.class);
    }
}
