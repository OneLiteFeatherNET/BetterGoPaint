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
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Checks Hangar for a newer release off the main thread and tells the console and joining admins about it.
 */
@Singleton
public final class UpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateService.class);
    private static final long CHECK_INTERVAL_TICKS = 20L * 60 * 60 * 3;
    private static final String DOWNLOAD_URL = "https://hangar.papermc.io/OneLiteFeather/BetterGoPaint/versions/%s";

    private final Plugin plugin;
    private final SettingsHolder settings;
    private final ComponentLogger console;
    private final Version localVersion;
    private final Supplier<Optional<String>> latestReleaseSource;

    private volatile @Nullable Version remoteVersion;
    private @Nullable BukkitTask task;

    @Inject
    public UpdateService(@NotNull Plugin plugin, @NotNull ComponentLogger console, @NotNull SettingsHolder settings) {
        this(plugin, console, settings, Version.parse(plugin.getPluginMeta().getVersion()), new HangarReleaseSource());
    }

    UpdateService(
            @NotNull Plugin plugin,
            @NotNull ComponentLogger console,
            @NotNull SettingsHolder settings,
            @NotNull Version localVersion,
            @NotNull Supplier<Optional<String>> latestReleaseSource
    ) {
        this.plugin = plugin;
        this.console = console;
        this.settings = settings;
        this.localVersion = localVersion;
        this.latestReleaseSource = latestReleaseSource;
    }

    /**
     * Schedules the periodic check, starting immediately but asynchronously. Does nothing when
     * {@code generic.check-for-updates} is disabled.
     */
    public void start() {
        if (!settings.get().generic().checkForUpdates()) {
            return;
        }
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::check, 0, CHECK_INTERVAL_TICKS);
    }

    /**
     * Runs one check. The console is told once per newly discovered version.
     */
    void check() {
        Optional<Version> newer = latestReleaseSource.get().flatMap(this::parseNewer);
        if (newer.isEmpty() || newer.get().equals(remoteVersion)) {
            return;
        }
        Version version = newer.get();
        this.remoteVersion = version;
        console.warn(Component.translatable("bettergopaint.notify.update.console").arguments(
                Component.text(localVersion.toString()),
                Component.text(version.toString()),
                Component.text(DOWNLOAD_URL.formatted(version))
        ));
    }

    private Optional<Version> parseNewer(String body) {
        try {
            Version version = Version.parse(body.trim());
            return version.isHigherThan(localVersion) ? Optional.of(version) : Optional.empty();
        } catch (RuntimeException e) {
            LOGGER.debug("Update check: Hangar answered with something that is not a version: {}", body);
            return Optional.empty();
        }
    }

    /**
     * @return the newest release known to be higher than the installed version
     */
    public Optional<Version> remoteVersion() {
        return Optional.ofNullable(remoteVersion);
    }

    public void notifyPlayer(@NotNull Player player) {
        Version version = this.remoteVersion;
        if (version == null) {
            return;
        }
        player.sendMessage(Component.translatable("bettergopaint.notify.update.player").arguments(
                Messages.prefix(settings.get()),
                Component.text(localVersion.toString()),
                Component.text(version.toString())
        ));
    }

    public void shutdown() {
        if (task != null) {
            task.cancel();
        }
        if (latestReleaseSource instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                LOGGER.debug("Update check: closing the HTTP client failed", e);
            }
        }
    }
}
