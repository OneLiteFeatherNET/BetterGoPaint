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
package net.onelitefeather.bettergopaint.docsgen;

import net.onelitefeather.bettergopaint.docsgen.SvgDocument.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The pictures of the menu items that are not cubes: small pixel icons drawn in code, and the dye colours of the
 * stained glass panes. Each icon is a grid of characters, one character per pixel, where {@code .} is transparent and
 * every other character is a colour of the icon. The shapes are simple and hand-drawn; no item texture is used.
 */
final class ItemIcons {

    private static final Map<String, Icon> ICONS = Map.ofEntries(
            Map.entry("FEATHER", new Icon(List.of(
                    ".......w",
                    "......wg",
                    ".....wgg",
                    "....wgd.",
                    "...wgd..",
                    "..wgd...",
                    ".wgd....",
                    "wd......"), Map.of('w', "#f4f4f4", 'g', "#bdbdbd", 'd', "#7d7d7d"))),
            Map.entry("PLAYER_HEAD", new Icon(List.of(
                    "oooooooo",
                    "ohhhhhho",
                    "osssssso",
                    "osesseso",
                    "osssssso",
                    "osssssso",
                    "ossmmsso",
                    "oooooooo"), Map.of('o', "#3b2a1a", 'h', "#5b3a1e", 's', "#e8b98a", 'e', "#2b2b2b",
                    'm', "#b5654a"))),
            Map.entry("GOLD_NUGGET", new Icon(List.of(
                    "........",
                    "...oo...",
                    "..oYYo..",
                    ".oYyyyo.",
                    "oYyyyyyo",
                    ".oyyyyo.",
                    "..oooo..",
                    "........"), Map.of('o', "#b8860b", 'y', "#f9d849", 'Y', "#fff2a8"))),
            Map.entry("BROWN_MUSHROOM", new Icon(List.of(
                    "........",
                    "..bbbb..",
                    ".bbbbbb.",
                    "bdbbbdbb",
                    "bbbbbbbb",
                    "...ss...",
                    "..ssss..",
                    "........"), Map.of('b', "#8f5f35", 'd', "#5c3b1f", 's', "#e8dcc0"))),
            Map.entry("CARVED_PUMPKIN", pumpkin("#2a1404")),
            Map.entry("JACK_O_LANTERN", pumpkin("#ffe066")),
            Map.entry("LIGHT_WEIGHTED_PRESSURE_PLATE", new Icon(List.of(
                    "........",
                    "........",
                    "........",
                    "yyyyyyyy",
                    "yYYYYYYy",
                    "oooooooo",
                    "........",
                    "........"), Map.of('y', "#f9d849", 'Y', "#fff2a8", 'o', "#b8860b"))),
            Map.entry("BARRIER", new Icon(List.of(
                    "..rrrr..",
                    ".rr..rr.",
                    "rr.r..rr",
                    "r..r...r",
                    "r...r..r",
                    "rr..r.rr",
                    ".rr..rr.",
                    "..rrrr.."), Map.of('r', "#d62424"))),
            Map.entry("COMPASS", new Icon(List.of(
                    "..gggg..",
                    ".g....g.",
                    "g..rr..g",
                    "g..rr..g",
                    "g..ww..g",
                    "g..ww..g",
                    ".g....g.",
                    "..gggg.."), Map.of('g', "#8c8c8c", 'r', "#d62424", 'w', "#e0e0e0"))),
            Map.entry("BOOK", new Icon(List.of(
                    "........",
                    ".bbbbbb.",
                    ".bppppb.",
                    ".bppppb.",
                    ".bppppb.",
                    ".bbbbbb.",
                    "........",
                    "........"), Map.of('b', "#6b4423", 'p', "#f4ecd8"))),
            Map.entry("DAYLIGHT_DETECTOR", new Icon(List.of(
                    "........",
                    "..yyyy..",
                    ".yyyyyy.",
                    "bbbbbbbb",
                    "dddddddd",
                    "bbbbbbbb",
                    "dddddddd",
                    "........"), Map.of('y', "#f9d849", 'b', "#a8824f", 'd', "#6d4f2b"))),
            Map.entry("BLAZE_ROD", new Icon(List.of(
                    "......yo",
                    ".....yyo",
                    "....yyo.",
                    "...yyo..",
                    "..yyo...",
                    ".yyo....",
                    "yyo.....",
                    "oo......"), Map.of('y', "#f5b50a", 'o', "#c96a0a"))),
            Map.entry("MAGMA_CREAM", new Icon(List.of(
                    "........",
                    "...cc...",
                    "..cccc..",
                    ".ccdccc.",
                    ".cccccc.",
                    "..cccd..",
                    "...cc...",
                    "........"), Map.of('c', "#f7b264", 'd', "#b35a1f"))),
            Map.entry("BLAZE_POWDER", new Icon(List.of(
                    "........",
                    "..y..y..",
                    "...yy...",
                    ".yyyyyy.",
                    "..yyyy..",
                    ".y.yy.y.",
                    "..y..y..",
                    "........"), Map.of('y', "#f5c12e"))));

    private static final Map<String, String> GLASS_TINTS = Map.of(
            "WHITE_STAINED_GLASS_PANE", "#f9fffe",
            "ORANGE_STAINED_GLASS_PANE", "#f9801d",
            "YELLOW_STAINED_GLASS_PANE", "#fed83d",
            "LIME_STAINED_GLASS_PANE", "#80c71f",
            "RED_STAINED_GLASS_PANE", "#b02e26",
            "GRAY_STAINED_GLASS_PANE", "#474f52");

    private ItemIcons() {
    }

    /**
     * @param material the name of the item, for example {@code FEATHER}
     * @return whether the item has a pixel icon
     */
    static boolean has(String material) {
        return ICONS.containsKey(material);
    }

    /**
     * @return the names of the items with a pixel icon, sorted
     */
    static List<String> materials() {
        return ICONS.keySet().stream().sorted().toList();
    }

    /**
     * @param material the name of the item
     * @return the icon of the item
     * @throws IllegalStateException if the item has no icon; the message names the item
     */
    static Icon icon(String material) {
        Icon icon = ICONS.get(material);
        if (icon == null) {
            throw new IllegalStateException("no pixel icon for the menu item " + material + "; add it to ItemIcons");
        }
        return icon;
    }

    /**
     * @param material the name of the item
     * @return whether the item is a stained glass pane, which the menu draws as a tinted tile
     */
    static boolean isGlassPane(String material) {
        return GLASS_TINTS.containsKey(material);
    }

    /**
     * @param material the name of a stained glass pane
     * @return the tint of the pane as {@code #rrggbb}
     * @throws IllegalStateException if the item is not a stained glass pane the menu knows
     */
    static String glassTint(String material) {
        String tint = GLASS_TINTS.get(material);
        if (tint == null) {
            throw new IllegalStateException("no glass tint for the menu item " + material + "; add it to ItemIcons");
        }
        return tint;
    }

    private static Icon pumpkin(String face) {
        return new Icon(List.of(
                "...gg...",
                ".oooooo.",
                "ookooook",
                "oookkooo",
                "ookoook.",
                "oookkkoo",
                ".oooooo.",
                "........"), Map.of('o', "#e0802b", 'g', "#4f7a2a", 'k', face));
    }

    /**
     * A pixel icon.
     *
     * @param rows    the rows of the icon, each as long as the others; {@code .} is transparent
     * @param colours the colour of each character other than {@code .}, as {@code #rrggbb}
     */
    record Icon(List<String> rows, Map<Character, String> colours) {

        /**
         * @param pixel the side of one pixel in the picture
         * @return one rectangle per opaque pixel, in icon coordinates
         */
        List<Rect> pixels(int pixel) {
            List<Rect> rects = new ArrayList<>();
            for (int row = 0; row < rows.size(); row++) {
                String line = rows.get(row);
                for (int column = 0; column < line.length(); column++) {
                    char character = line.charAt(column);
                    if (character != '.') {
                        rects.add(new Rect(column * pixel, row * pixel, pixel, pixel, colours.get(character), "none", 0, 100));
                    }
                }
            }
            return rects;
        }
    }
}
