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
package net.onelitefeather.bettergopaint.settings;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Holds the current {@link Settings} snapshot and swaps it atomically on reload.
 */
@Singleton
public final class SettingsHolder implements Provider<Settings> {

    public static final String CONFIG_FILE = "config-file";

    private final Path file;
    private final AtomicReference<Settings> current;

    @Inject
    public SettingsHolder(@Named(CONFIG_FILE) @NotNull Path file) {
        this.file = file;
        this.current = new AtomicReference<>(SettingsLoader.load(file));
    }

    private SettingsHolder(@NotNull Settings settings) {
        this.file = Path.of("config.yml");
        this.current = new AtomicReference<>(settings);
    }

    /**
     * A holder that never touches the disk, for tests.
     */
    public static @NotNull SettingsHolder of(@NotNull Settings settings) {
        return new SettingsHolder(settings);
    }

    @Override
    public @NotNull Settings get() {
        return current.get();
    }

    /**
     * Re-reads {@code config.yml} and publishes the new snapshot.
     */
    public @NotNull Settings reload() {
        Settings settings = SettingsLoader.load(file);
        current.set(settings);
        return settings;
    }
}
