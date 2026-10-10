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
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Modules;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.command.GoPaintCommand;
import net.onelitefeather.bettergopaint.listeners.ConnectListener;
import net.onelitefeather.bettergopaint.listeners.InteractListener;
import net.onelitefeather.bettergopaint.listeners.InventoryListener;
import net.onelitefeather.bettergopaint.service.UpdateService;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.util.sender.Source;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Builds the real module graph with a mocked plugin, so a missing or broken binding fails here
 * instead of on server start. Only the command manager, which needs a running server, is replaced.
 */
class DIValidationTest {

    @TempDir
    Path dataFolder;

    private Injector injector;

    @BeforeEach
    void setUp() {
        JavaPlugin plugin = mock(JavaPlugin.class);
        PluginMeta meta = mock(PluginMeta.class);
        when(meta.getVersion()).thenReturn("2.0.0");
        when(plugin.getPluginMeta()).thenReturn(meta);
        when(plugin.getComponentLogger()).thenReturn(mock(ComponentLogger.class));

        @SuppressWarnings("unchecked")
        MinecraftHelp<Source> help = mock(MinecraftHelp.class);
        injector = Guice.createInjector(Stage.DEVELOPMENT,
                new PlatformModule(plugin),
                new SettingsModule(dataFolder),
                new BrushModule(),
                new ServiceModule(),
                new ListenerModule(),
                Modules.override(new CommandModule()).with(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(new TypeLiteral<MinecraftHelp<Source>>() {
                        }).toInstance(help);
                    }
                })
        );
    }

    @Test
    @DisplayName("every listener can be created and is registered")
    void listenersAreBound() {
        Set<Listener> listeners = injector.getInstance(Key.get(new TypeLiteral<Set<Listener>>() {
        }));

        assertThat(listeners).hasExactlyElementsOfTypes(ConnectListener.class, InteractListener.class, InventoryListener.class);
    }

    @Test
    @DisplayName("the command handler can be created")
    void commandIsBound() {
        assertThat(injector.getInstance(GoPaintCommand.class)).isNotNull();
    }

    @Test
    @DisplayName("shared state is a singleton")
    void singletons() {
        assertThat(injector.getInstance(SettingsHolder.class)).isSameAs(injector.getInstance(SettingsHolder.class));
        assertThat(injector.getInstance(PlayerBrushManager.class)).isSameAs(injector.getInstance(PlayerBrushManager.class));
        assertThat(injector.getInstance(UpdateService.class)).isSameAs(injector.getInstance(UpdateService.class));
    }

    @Test
    @DisplayName("the config file is created in the data folder")
    void configIsWritten() {
        injector.getInstance(SettingsHolder.class);

        assertThat(dataFolder.resolve("config.yml")).exists();
    }
}
