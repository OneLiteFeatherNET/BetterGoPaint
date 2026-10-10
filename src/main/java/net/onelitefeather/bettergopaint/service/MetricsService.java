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

import com.fastasyncworldedit.core.Fawe;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Sends anonymous usage statistics to bStats.
 */
@Singleton
public final class MetricsService {

    private static final int BSTATS_ID = 18734;

    private final JavaPlugin plugin;
    private @Nullable Metrics metrics;

    @Inject
    public MetricsService(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        metrics = new Metrics(plugin, BSTATS_ID);
        metrics.addCustomChart(new SimplePie(
                "faweVersion",
                () -> Objects.requireNonNull(Fawe.instance().getVersion()).toString()
        ));
    }

    public void shutdown() {
        if (metrics != null) {
            metrics.shutdown();
        }
    }
}
