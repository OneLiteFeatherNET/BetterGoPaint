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

import com.fastasyncworldedit.core.Fawe;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.command.GoPaintCommand;
import net.onelitefeather.bettergopaint.command.ReloadCommand;
import net.onelitefeather.bettergopaint.listeners.ConnectListener;
import net.onelitefeather.bettergopaint.listeners.InteractListener;
import net.onelitefeather.bettergopaint.listeners.InventoryListener;
import net.onelitefeather.bettergopaint.objects.other.Settings;
import net.onelitefeather.bettergopaint.service.UpdateService;
import net.onelitefeather.bettergopaint.translations.PluginTranslationRegistry;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.serverlib.ServerLib;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class BetterGoPaint extends JavaPlugin implements Listener {

    public static final String PAPER_DOCS = "https://jd.papermc.io/paper/1.20.6/org/bukkit/Material.html#enum-constant-summary";

    private final PlayerBrushManager brushManager = new PlayerBrushManager();
    private final Metrics metrics = new Metrics(this, 18734);
    private UpdateService updateService;

    @Override
    public void onLoad() {
        metrics.addCustomChart(new SimplePie(
                "faweVersion",
                () -> Objects.requireNonNull(Fawe.instance().getVersion()).toString()
        ));
    }

    @Override
    public void onEnable() {
        // Check if we are in a safe environment
        ServerLib.checkUnsafeForks();

        // disable if goPaint and BetterGoPaint are installed simultaneously
        if (hasOriginalGoPaint()) {
            getComponentLogger().error("BetterGoPaint is a replacement for goPaint. Please use one instead of both");
            getComponentLogger().error("This plugin is now disabling to prevent future errors");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        reloadConfig();

        final TranslationRegistry translationRegistry = new PluginTranslationRegistry(TranslationRegistry.create(Key.key("bettergopaint", "translations")));
        translationRegistry.defaultLocale(Locale.US);
        Path langFolder = getDataFolder().toPath().resolve("lang");
        var languages = new HashSet<>(Settings.settings().generic.LANGUAGES);
        languages.add("en-US");
        if (Files.exists(langFolder)) {
            try (var urlClassLoader = new URLClassLoader(new URL[]{langFolder.toUri().toURL()})) {
                languages.stream().map(Locale::forLanguageTag).forEach(r -> {
                    var bundle = ResourceBundle.getBundle("bettergopaint", r, urlClassLoader, UTF8ResourceBundleControl.get());
                    translationRegistry.registerAll(r, bundle, false);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            languages.stream().map(Locale::forLanguageTag).forEach(r -> {
                var bundle = ResourceBundle.getBundle("bettergopaint", r, UTF8ResourceBundleControl.get());
                translationRegistry.registerAll(r, bundle, false);
            });
        }
        GlobalTranslator.translator().addSource(translationRegistry);
        donationInformation();


        Material brush = Settings.settings().generic.DEFAULT_BRUSH;
        if (!brush.isItem()) {
            getComponentLogger().error("{} is not a valid default brush, it has to be an item", brush.name());
            getComponentLogger().error("For more information visit {}", PAPER_DOCS);
            getServer().getPluginManager().disablePlugin(this);
        }

        //noinspection UnnecessaryUnicodeEscape
        getComponentLogger().info(MiniMessage.miniMessage().deserialize(
                "<white>Made with <red>\u2665</red> <white>in <gradient:black:red:gold>Germany</gradient>"
        ));

        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        metrics.shutdown();
        this.updateService.shutdown();
    }

    public void reloadConfig() {
        try {
            Files.createDirectories(getDataFolder().toPath());
            final Path resolve = getDataFolder().toPath().resolve("config.yml");
            Settings.settings().save(resolve.toFile());
            Settings.settings().load(resolve.toFile());
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Cannot init config", e);
        }

    }

    private void updateService() {
        this.updateService = new UpdateService(this);
        this.updateService.run();
        this.updateService.notifyConsole(getComponentLogger());
    }


    @SuppressWarnings("UnstableApiUsage")
    private void registerCommands() {
        Bukkit.getCommandMap().register("gopaint", getPluginMeta().getName(), new GoPaintCommand(this));

        var annotationParser = enableCommandSystem();
        if (annotationParser != null) {
            annotationParser.parse(new ReloadCommand(this));
            annotationParser.parse(new GoPaintCommand(this));
        }
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryListener(getBrushManager()), this);
        pm.registerEvents(new InteractListener(this), this);
        pm.registerEvents(new ConnectListener(getBrushManager(), this), this);
    }

    private boolean hasOriginalGoPaint() {
        return getServer().getPluginManager().getPlugin("goPaint") != this;
    }

    private void donationInformation() {
        getComponentLogger().info(Component.translatable("bettergopaint.notify.donation.console"));
    }

    private @Nullable AnnotationParser<CommandSender> enableCommandSystem() {
        try {
            LegacyPaperCommandManager<CommandSender> commandManager = LegacyPaperCommandManager.createNative(
                    this,
                    ExecutionCoordinator.simpleCoordinator()
            );
            if (commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
                commandManager.registerBrigadier();
                getLogger().info("Brigadier support enabled");
            }
            return new AnnotationParser<>(commandManager, CommandSender.class);

        } catch (Exception exception) {
            getLogger().log(Level.SEVERE, "Cannot init command manager", exception);
            return null;
        }
    }

    public @NotNull PlayerBrushManager getBrushManager() {
        return brushManager;
    }

    public UpdateService getUpdateService() {
        return updateService;
    }

}
