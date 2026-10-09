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

import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.objects.other.SurfaceMode;
import net.onelitefeather.bettergopaint.settings.Settings;
import org.bukkit.Axis;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Turns brush settings into the versioned text stored on exported items and back. Decoding never
 * throws: a missing or unreadable value falls back to the configured default, an unknown brush
 * yields an empty result.
 */
public final class ExportedBrushCodec {

    public static final int FORMAT_VERSION = 1;

    private static final String ENTRY_SEPARATOR = ";";
    private static final String VALUE_SEPARATOR = "=";
    private static final String LIST_SEPARATOR = ",";

    private ExportedBrushCodec() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static @NotNull String encode(@NotNull BrushSettings settings) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("version", Integer.toString(FORMAT_VERSION));
        values.put("brush", settings.brush().getName());
        values.put("size", Integer.toString(settings.size()));
        values.put("chance", Integer.toString(settings.chance()));
        values.put("thickness", Integer.toString(settings.thickness()));
        values.put("axis", settings.axis().name());
        values.put("angleDistance", Integer.toString(settings.angleDistance()));
        values.put("angleHeightDifference", Double.toString(settings.angleHeightDifference()));
        values.put("fractureDistance", Integer.toString(settings.fractureDistance()));
        values.put("falloff", Integer.toString(settings.falloffStrength()));
        values.put("mixing", Integer.toString(settings.mixingStrength()));
        values.put("blocks", settings.blocks().stream()
                .map(material -> material.getKey().asString())
                .collect(Collectors.joining(LIST_SEPARATOR)));
        if (settings.maskEnabled()) {
            values.put("mask", settings.mask().getKey().asString());
        }
        values.put("surface", settings.surfaceMode().name());
        return values.entrySet().stream()
                .map(entry -> entry.getKey() + VALUE_SEPARATOR + entry.getValue())
                .collect(Collectors.joining(ENTRY_SEPARATOR));
    }

    public static @NotNull Optional<ExportedPlayerBrush> decode(
            @NotNull String encoded,
            @NotNull Function<String, Optional<Brush>> brushByName,
            @NotNull Settings settings
    ) {
        Map<String, String> values = new LinkedHashMap<>();
        for (String entry : encoded.split(ENTRY_SEPARATOR)) {
            int separator = entry.indexOf(VALUE_SEPARATOR);
            if (separator > 0) {
                values.put(entry.substring(0, separator), entry.substring(separator + 1));
            }
        }
        String brushName = values.get("brush");
        if (brushName == null) {
            return Optional.empty();
        }
        return brushByName.apply(brushName).map(brush -> {
            ExportedPlayerBrush.Builder builder = ExportedPlayerBrush.builder(brush, settings);
            integer(values.get("size")).ifPresent(builder::size);
            integer(values.get("chance")).ifPresent(builder::chance);
            integer(values.get("thickness")).ifPresent(builder::thickness);
            enumValue(Axis.class, values.get("axis")).ifPresent(builder::axis);
            integer(values.get("angleDistance")).ifPresent(builder::angleDistance);
            decimal(values.get("angleHeightDifference")).ifPresent(builder::angleHeightDifference);
            integer(values.get("fractureDistance")).ifPresent(builder::fractureDistance);
            integer(values.get("falloff")).ifPresent(builder::falloffStrength);
            integer(values.get("mixing")).ifPresent(builder::mixingStrength);
            builder.blocks(materials(values.get("blocks")));
            builder.mask(values.containsKey("mask") ? material(values.get("mask")).orElse(null) : null);
            enumValue(SurfaceMode.class, values.get("surface")).ifPresent(builder::surfaceMode);
            return builder.build();
        });
    }

    /**
     * Reads the lore written by BetterGoPaint 1.x, for example {@code Size: 10}. Every line is
     * parsed on its own; a line that cannot be read leaves the default in place.
     */
    public static @NotNull ExportedPlayerBrush decodeLegacy(
            @NotNull Brush brush,
            @NotNull List<String> loreLines,
            @NotNull Settings settings
    ) {
        ExportedPlayerBrush.Builder builder = ExportedPlayerBrush.builder(brush, settings);
        for (String line : loreLines) {
            String value = line.contains(": ") ? line.substring(line.indexOf(": ") + 2).trim() : "";
            if (line.startsWith("Size: ")) {
                integer(value).ifPresent(builder::size);
            } else if (line.startsWith("Chance: ")) {
                integer(value.replace("%", "")).ifPresent(builder::chance);
            } else if (line.startsWith("Thickness: ")) {
                integer(value).ifPresent(builder::thickness);
            } else if (line.startsWith("Axis: ")) {
                enumValue(Axis.class, value).ifPresent(builder::axis);
            } else if (line.startsWith("FractureDistance: ")) {
                integer(value).ifPresent(builder::fractureDistance);
            } else if (line.startsWith("AngleDistance: ")) {
                integer(value).ifPresent(builder::angleDistance);
            } else if (line.startsWith("AngleHeightDifference: ")) {
                decimal(value).ifPresent(builder::angleHeightDifference);
            } else if (line.startsWith("Mixing: ")) {
                integer(value).ifPresent(builder::mixingStrength);
            } else if (line.startsWith("Falloff: ")) {
                integer(value).ifPresent(builder::falloffStrength);
            } else if (line.startsWith("Blocks: ")) {
                builder.blocks(materials(value.replace(", ", LIST_SEPARATOR)));
            } else if (line.startsWith("Mask: ")) {
                material(value).ifPresent(builder::mask);
            } else if (line.startsWith("Surface Mode: ")) {
                SurfaceMode.byName(value).ifPresent(builder::surfaceMode);
            }
        }
        return builder.build();
    }

    private static Optional<Integer> integer(String value) {
        try {
            return value == null ? Optional.empty() : Optional.of(Integer.parseInt(value.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static Optional<Double> decimal(String value) {
        try {
            return value == null ? Optional.empty() : Optional.of(Double.parseDouble(value.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static <E extends Enum<E>> Optional<E> enumValue(Class<E> type, String value) {
        try {
            return value == null ? Optional.empty() : Optional.of(Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static Optional<Material> material(String value) {
        return value == null || value.isBlank() ? Optional.empty() : Optional.ofNullable(Material.matchMaterial(value.trim()));
    }

    private static List<Material> materials(String value) {
        if (value == null || value.isBlank() || value.equals("none")) {
            return List.of();
        }
        return Arrays.stream(value.split(LIST_SEPARATOR))
                .map(ExportedBrushCodec::material)
                .flatMap(Optional::stream)
                .filter(Objects::nonNull)
                .toList();
    }
}
