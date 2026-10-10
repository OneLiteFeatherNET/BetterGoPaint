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

import com.github.zafarkhaja.semver.Version;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class UpdateServiceTest {

    private final Plugin plugin = mock(Plugin.class);
    private final ComponentLogger console = mock(ComponentLogger.class);

    private UpdateService service(Optional<String> answer) {
        return new UpdateService(plugin, console, SettingsHolder.of(Settings.defaults()), Version.parse("2.0.0"), () -> answer);
    }

    @Test
    @DisplayName("a newer release is remembered and reported to the console once")
    void newerReleaseIsReportedOnce() {
        UpdateService service = service(Optional.of("2.1.0\n"));

        service.check();
        service.check();

        assertThat(service.remoteVersion()).contains(Version.parse("2.1.0"));
        verify(console, times(1)).warn(any(Component.class));
    }

    @Test
    @DisplayName("the installed or an older release is not reported")
    void olderReleaseIsIgnored() {
        UpdateService service = service(Optional.of("1.9.9"));

        service.check();

        assertThat(service.remoteVersion()).isEmpty();
        verify(console, never()).warn(any(Component.class));
    }

    @Test
    @DisplayName("an answer that is not a version is ignored without an exception")
    void garbageIsIgnored() {
        UpdateService service = service(Optional.of("<html>Service Unavailable</html>"));

        service.check();

        assertThat(service.remoteVersion()).isEmpty();
        verify(console, never()).warn(any(Component.class));
    }

    @Test
    @DisplayName("an unreachable Hangar is ignored")
    void unreachableIsIgnored() {
        UpdateService service = service(Optional.empty());

        service.check();

        assertThat(service.remoteVersion()).isEmpty();
    }

    @Test
    @DisplayName("with update checks disabled the service never asks Hangar")
    @SuppressWarnings("unchecked")
    void disabledCheckNeverAsksHangar() {
        Settings defaults = Settings.defaults();
        Settings.Generic generic = defaults.generic();
        Settings disabled = new Settings(
                new Settings.Generic(generic.languages(), generic.defaultBrush(), generic.maxSize(), generic.defaultSize(),
                        generic.defaultChance(), generic.defaultAxis(), generic.defaultFalloffStrength(),
                        generic.defaultMixingStrength(), generic.prefix(), false, List.copyOf(generic.disabledWorlds()),
                        generic.enabledByDefault(), generic.maskEnabled(), generic.defaultMask(), generic.surfaceMode()),
                defaults.thickness(), defaults.angle(), defaults.fracture());
        Supplier<Optional<String>> source = mock(Supplier.class);
        UpdateService service = new UpdateService(plugin, console, SettingsHolder.of(disabled), Version.parse("2.0.0"), source);

        service.start();

        verify(source, never()).get();
    }
}
