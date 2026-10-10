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
package net.onelitefeather.bettergopaint.brush;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.objects.brush.AngleBrush;
import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.objects.brush.BucketBrush;
import net.onelitefeather.bettergopaint.objects.brush.DiscBrush;
import net.onelitefeather.bettergopaint.objects.brush.FractureBrush;
import net.onelitefeather.bettergopaint.objects.brush.GradientBrush;
import net.onelitefeather.bettergopaint.objects.brush.OverlayBrush;
import net.onelitefeather.bettergopaint.objects.brush.PaintBrush;
import net.onelitefeather.bettergopaint.objects.brush.SphereBrush;
import net.onelitefeather.bettergopaint.objects.brush.SplatterBrush;
import net.onelitefeather.bettergopaint.objects.brush.SprayBrush;
import net.onelitefeather.bettergopaint.menu.BrushMenuFactory;
import net.onelitefeather.bettergopaint.objects.brush.UnderlayBrush;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * The PlayerBrushManager class manages the brush selection for each player.
 */
@Singleton
public class PlayerBrushManager {

    /**
     * Written from the region thread of each player and read from commands and the FAWE worker, so it must be
     * safe for concurrent use.
     */
    private final Map<UUID, PlayerBrush> playerBrushes;
    private final BiFunction<PlayerBrushManager, Locale, PlayerBrush> brushFactory;
    private final List<Brush> brushes;
    private final PaintBrush paintBrush;

    private final SettingsHolder settings;

    @Inject
    public PlayerBrushManager(@NotNull SettingsHolder settings, @NotNull BrushMenuFactory menuFactory) {
        this(settings, (manager, locale) -> new PlayerBrush(manager, settings, locale, menuFactory));
    }

    PlayerBrushManager(
            @NotNull SettingsHolder settings,
            @NotNull BiFunction<PlayerBrushManager, Locale, PlayerBrush> brushFactory
    ) {
        this.settings = settings;
        this.brushFactory = brushFactory;
        this.playerBrushes = new ConcurrentHashMap<>();
        this.paintBrush = new PaintBrush(settings);
        this.brushes = List.of(
                new SphereBrush(),
                new SprayBrush(),
                new SplatterBrush(),
                new DiscBrush(),
                new BucketBrush(),
                new AngleBrush(),
                new OverlayBrush(),
                new UnderlayBrush(),
                new FractureBrush(),
                new GradientBrush(),
                paintBrush
        );
    }

    /**
     * Retrieves the brush for the given player.
     *
     * @param player The player for which to retrieve the brush.
     * @return The brush for the specified player.
     */
    public @NotNull PlayerBrush getBrush(@NotNull Player player) {
        return playerBrushes.computeIfAbsent(player.getUniqueId(), ignored -> brushFactory.apply(this, player.locale()));
    }

    /**
     * Retrieves the brush handler for the given name.
     *
     * @param name The name of the brush to look for.
     * @return An optional containing the brush handler, or empty if not found.
     */
    public @NotNull Optional<Brush> getBrushHandler(String name) {
        return brushes.stream()
                .filter(brush -> name.contains(brush.getName()))
                .findAny();
    }

    /**
     * Retrieves the brush with exactly the given name.
     *
     * @param name The name of the brush.
     * @return An optional containing the brush, or empty if no brush has that name.
     */
    public @NotNull Optional<Brush> getBrushByName(@NotNull String name) {
        return brushes.stream()
                .filter(brush -> brush.getName().equals(name))
                .findFirst();
    }

    /**
     * Retrieves the list of available brushes.
     *
     * @return The list of available brushes.
     */
    public @NotNull List<Brush> getBrushes() {
        return brushes;
    }

    /**
     * Removes the player's brush settings and any unfinished Paint Brush stroke.
     *
     * @param player The player who should be removed.
     */
    public void removeBrush(@NotNull Player player) {
        playerBrushes.remove(player.getUniqueId());
        paintBrush.forget(player.getUniqueId());
    }

    /**
     * Retrieves the next brush in the list of available brushes.
     *
     * @param brush The current brush, if null returns the first brush in the list.
     * @return The next brush in the list, or the first brush if the current brush is null.
     */
    public @NotNull Brush cycleForward(@Nullable Brush brush) {
        if (brush == null) {
            return brushes.getFirst();
        }
        int next = brushes.indexOf(brush) + 1;
        if (next < brushes.size()) {
            return brushes.get(next);
        }
        return brushes.getFirst();
    }

    /**
     * Retrieves the previous brush in the list of available brushes.
     *
     * @param brush The current brush.
     * @return The previous brush in the list, or the first brush if the current brush is null.
     */
    public @NotNull Brush cycleBack(@Nullable Brush brush) {
        if (brush == null) {
            return brushes.getFirst();
        }
        int back = brushes.indexOf(brush) - 1;
        if (back >= 0) {
            return brushes.get(back);
        }
        return brushes.getLast();
    }
}
