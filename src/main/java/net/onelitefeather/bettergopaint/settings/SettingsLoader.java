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
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Reads {@code config.yml} into {@link Settings} and writes it back with every key and its comment,
 * so new keys appear after an update. A missing or invalid value is replaced by its default and
 * reported once in the log.
 */
public final class SettingsLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsLoader.class);

    private SettingsLoader() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static @NotNull Settings load(@NotNull Path file) {
        YamlConfiguration yaml = Files.exists(file) ? YamlConfiguration.loadConfiguration(file.toFile()) : new YamlConfiguration();
        Settings defaults = Settings.defaults();
        Reader reader = new Reader(yaml);

        Settings.Generic g = defaults.generic();
        Settings.Generic generic = new Settings.Generic(
                reader.list("generic.languages", g.languages(), "Extra languages to load from lang/, for example de-DE"),
                reader.material("generic.default-brush", g.defaultBrush(), false, "Item that acts as the brush. Must be an item: " + Settings.PAPER_MATERIALS),
                reader.integer("generic.max-size", g.maxSize(), 1, "Largest brush size a player can set"),
                reader.integer("generic.default-size", g.defaultSize(), 1, "Brush size of a new player"),
                reader.integer("generic.default-chance", g.defaultChance(), 0, "Starting chance of the spray brush in percent"),
                reader.enumValue("generic.default-axis", Axis.class, g.defaultAxis(), "Starting axis of the disc brush: X, Y or Z"),
                reader.integer("generic.default-falloff-strength", g.defaultFalloffStrength(), 0, "Starting falloff strength"),
                reader.integer("generic.default-mixing-strength", g.defaultMixingStrength(), 0, "Starting mixing strength of the gradient brush"),
                reader.string("generic.prefix", g.prefix(), "MiniMessage prefix of chat messages"),
                reader.bool("generic.check-for-updates", g.checkForUpdates(), "Check Hangar every three hours for a newer release and tell admins about it"),
                reader.list("generic.disabled-worlds", g.disabledWorlds(), "Worlds in which painting is blocked"),
                reader.bool("generic.enabled-by-default", g.enabledByDefault(), "Whether the brush of a new player starts enabled"),
                reader.bool("generic.mask-enabled", g.maskEnabled(), "Whether the mask starts enabled"),
                reader.material("generic.default-mask", g.defaultMask(), false, "Starting mask block: " + Settings.PAPER_MATERIALS),
                reader.enumValue("generic.surface-mode", SurfaceMode.class, g.surfaceMode(), "Starting surface mode: DIRECT, RELATIVE or DISABLED")
        );
        Settings.Thickness t = defaults.thickness();
        Settings.Thickness thickness = new Settings.Thickness(
                reader.integer("thickness.default-thickness", t.defaultThickness(), 1, "Starting thickness of the overlay and underlay brush"),
                reader.integer("thickness.max-thickness", t.maxThickness(), 1, "Largest thickness")
        );
        Settings.Angle a = defaults.angle();
        Settings.Angle angle = new Settings.Angle(
                reader.integer("angle.default-angle-distance", a.defaultAngleDistance(), 1, "Starting angle check distance"),
                reader.integer("angle.max-angle-distance", a.maxAngleDistance(), 1, "Largest angle check distance"),
                reader.decimal("angle.min-angle-height-difference", a.minAngleHeightDifference(), "Smallest angle height difference"),
                reader.decimal("angle.default-angle-height-difference", a.defaultAngleHeightDifference(), "Starting angle height difference"),
                reader.decimal("angle.max-angle-height-difference", a.maxAngleHeightDifference(), "Largest angle height difference")
        );
        Settings.Fracture f = defaults.fracture();
        Settings.Fracture fracture = new Settings.Fracture(
                reader.integer("fracture.default-fracture-distance", f.defaultFractureDistance(), 1, "Starting fracture check distance"),
                reader.integer("fracture.max-fracture-distance", f.maxFractureDistance(), 1, "Largest fracture check distance")
        );

        try {
            Files.createDirectories(file.toAbsolutePath().getParent());
            yaml.save(file.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write " + file, e);
        }
        return new Settings(generic, thickness, angle, fracture);
    }

    /**
     * Reads one key at a time, writes the effective value back and attaches the comment.
     */
    private record Reader(YamlConfiguration yaml) {

        private <T> T read(String path, T fallback, String comment, Function<Object, T> parse, Function<T, Object> write) {
            T value = fallback;
            Object raw = yaml.get(path);
            if (raw != null) {
                T parsed = null;
                try {
                    parsed = parse.apply(raw);
                } catch (RuntimeException ignored) {
                    // reported below
                }
                if (parsed == null) {
                    LOGGER.warn("config.yml: {} has the invalid value '{}', using {}", path, raw, fallback);
                } else {
                    value = parsed;
                }
            }
            yaml.set(path, write.apply(value));
            yaml.setComments(path, List.of(comment));
            return value;
        }

        int integer(String path, int fallback, int min, String comment) {
            return read(path, fallback, comment, raw -> {
                int value = raw instanceof Number number ? number.intValue() : Integer.parseInt(raw.toString().trim());
                return value >= min ? value : null;
            }, value -> value);
        }

        double decimal(String path, double fallback, String comment) {
            return read(path, fallback, comment, raw -> raw instanceof Number number
                    ? number.doubleValue()
                    : Double.parseDouble(raw.toString().trim()), value -> value);
        }

        boolean bool(String path, boolean fallback, String comment) {
            return read(path, fallback, comment, raw -> switch (raw.toString().trim().toLowerCase(Locale.ROOT)) {
                case "true" -> Boolean.TRUE;
                case "false" -> Boolean.FALSE;
                default -> null;
            }, value -> value);
        }

        String string(String path, String fallback, String comment) {
            return read(path, fallback, comment, Object::toString, value -> value);
        }

        List<String> list(String path, List<String> fallback, String comment) {
            return read(path, fallback, comment, raw -> raw instanceof List<?> list
                    ? list.stream().map(Object::toString).toList()
                    : null, value -> value);
        }

        <E extends Enum<E>> E enumValue(String path, Class<E> type, E fallback, String comment) {
            return read(path, fallback, comment,
                    raw -> Enum.valueOf(type, raw.toString().trim().toUpperCase(Locale.ROOT)), Enum::name);
        }

        Material material(String path, Material fallback, boolean item, String comment) {
            return read(path, fallback, comment, raw -> {
                Material material = Material.matchMaterial(raw.toString().trim());
                return material == null || (item && !material.isItem()) ? null : material;
            }, Enum::name);
        }
    }
}
