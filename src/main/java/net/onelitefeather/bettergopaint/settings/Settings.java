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

import net.onelitefeather.bettergopaint.objects.other.SurfaceMode;
import org.bukkit.Axis;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An immutable snapshot of {@code config.yml}. A reload publishes a new snapshot; consumers read
 * the current one through {@link SettingsHolder#get()} each time they need a value.
 */
public record Settings(
        @NotNull Generic generic,
        @NotNull Thickness thickness,
        @NotNull Angle angle,
        @NotNull Fracture fracture
) {

    public static final String PAPER_MATERIALS = "https://jd.papermc.io/paper/org/bukkit/Material.html";

    public static @NotNull Settings defaults() {
        return new Settings(
                new Generic(List.of(), Material.FEATHER, 100, 10, 50, Axis.Y, 50, 50,
                        "<aqua>BetterGoPaint > </aqua>", true, List.of(), true, true, Material.SPONGE, SurfaceMode.DIRECT),
                new Thickness(1, 5),
                new Angle(2, 5, 10.0, 40.0, 85.0),
                new Fracture(2, 5)
        );
    }

    public record Generic(
            @NotNull List<String> languages,
            @NotNull Material defaultBrush,
            int maxSize,
            int defaultSize,
            int defaultChance,
            @NotNull Axis defaultAxis,
            int defaultFalloffStrength,
            int defaultMixingStrength,
            @NotNull String prefix,
            boolean checkForUpdates,
            @NotNull List<String> disabledWorlds,
            boolean enabledByDefault,
            boolean maskEnabled,
            @NotNull Material defaultMask,
            @NotNull SurfaceMode surfaceMode
    ) {

        public Generic {
            languages = List.copyOf(languages);
            disabledWorlds = List.copyOf(disabledWorlds);
        }
    }

    public record Thickness(int defaultThickness, int maxThickness) {
    }

    public record Angle(
            int defaultAngleDistance,
            int maxAngleDistance,
            double minAngleHeightDifference,
            double defaultAngleHeightDifference,
            double maxAngleHeightDifference
    ) {
    }

    public record Fracture(int defaultFractureDistance, int maxFractureDistance) {
    }
}
