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
package net.onelitefeather.bettergopaint;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.onelitefeather.bettergopaint.command.GoPaintCommand;
import net.onelitefeather.bettergopaint.injection.BrushModule;
import net.onelitefeather.bettergopaint.injection.CommandModule;
import net.onelitefeather.bettergopaint.injection.ListenerModule;
import net.onelitefeather.bettergopaint.injection.PlatformModule;
import net.onelitefeather.bettergopaint.injection.ServiceModule;
import net.onelitefeather.bettergopaint.injection.SettingsModule;
import net.onelitefeather.bettergopaint.service.MetricsService;
import net.onelitefeather.bettergopaint.service.TranslationService;
import net.onelitefeather.bettergopaint.service.UpdateService;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.paper.util.sender.Source;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Entry point. Everything else is created by Guice; this class only decides the order things start in.
 */
public final class BetterGoPaint extends JavaPlugin {

    private @Nullable Injector injector;

    @Override
    public void onEnable() {
        // disable if goPaint and BetterGoPaint are installed simultaneously
        if (hasOriginalGoPaint()) {
            getComponentLogger().error("BetterGoPaint is a replacement for goPaint. Please use one instead of both");
            getComponentLogger().error("This plugin is now disabling to prevent future errors");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        injector = Guice.createInjector(Stage.PRODUCTION,
                new PlatformModule(this),
                new SettingsModule(getDataFolder().toPath()),
                new BrushModule(),
                new ServiceModule(),
                new ListenerModule(),
                new CommandModule()
        );

        Material brush = injector.getInstance(SettingsHolder.class).get().generic().defaultBrush();
        if (!brush.isItem()) {
            getComponentLogger().error("{} is not a valid default brush, it has to be an item", brush.name());
            getComponentLogger().error("For more information visit {}", Settings.PAPER_MATERIALS);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        injector.getInstance(TranslationService.class).register();
        getComponentLogger().info(Component.translatable("bettergopaint.notify.donation.console"));
        //noinspection UnnecessaryUnicodeEscape
        getComponentLogger().info(MiniMessage.miniMessage().deserialize(
                "<white>Made with <red>♥</red> <white>in <gradient:black:red:gold>Germany</gradient>"
        ));

        injector.getInstance(MetricsService.class).start();
        injector.getInstance(Key.get(new TypeLiteral<Set<Listener>>() {
        })).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
        registerCommands(injector);
        injector.getInstance(UpdateService.class).start();
    }

    @Override
    public void onDisable() {
        if (injector == null) {
            return;
        }
        injector.getInstance(UpdateService.class).shutdown();
        injector.getInstance(MetricsService.class).shutdown();
    }

    private void registerCommands(Injector injector) {
        CommandManager<Source> commandManager = injector.getInstance(Key.get(new TypeLiteral<CommandManager<Source>>() {
        }));
        new AnnotationParser<>(commandManager, Source.class).parse(injector.getInstance(GoPaintCommand.class));
    }

    private boolean hasOriginalGoPaint() {
        return getServer().getPluginManager().getPlugin("goPaint") != this;
    }
}
