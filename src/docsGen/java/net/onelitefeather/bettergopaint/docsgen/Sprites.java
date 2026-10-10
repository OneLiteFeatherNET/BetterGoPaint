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
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * The symbols of one picture: a block tile or an item icon at one pixel size, defined on first use. The symbols are
 * written in the order of their ids, so the picture does not depend on the order in which the cells are drawn.
 */
final class Sprites {

    private final Map<String, List<Rect>> symbols = new TreeMap<>();

    /**
     * @param material the Minecraft name of the block, for example {@code GRASS_BLOCK}
     * @param face     the face of the block the tile shows
     * @param pixel    the side of one pixel of the tile in the picture
     * @return the id of the symbol of the tile
     */
    String block(String material, BlockLooks.Face face, int pixel) {
        String id = "block-" + slug(material) + "-" + face.name().toLowerCase(Locale.ROOT) + "-" + pixel;
        symbols.computeIfAbsent(id, key -> tile(BlockLooks.look(material).pixels(face), pixel));
        return id;
    }

    /**
     * @param material the name of the item, for example {@code FEATHER}
     * @param pixel    the side of one pixel of the icon in the picture
     * @return the id of the symbol of the icon
     */
    String icon(String material, int pixel) {
        String id = "icon-" + slug(material) + "-" + pixel;
        symbols.computeIfAbsent(id, key -> ItemIcons.icon(material).pixels(pixel));
        return id;
    }

    /**
     * Defines every symbol used so far in the picture.
     *
     * @param document the picture the symbols belong to
     */
    void writeTo(SvgDocument document) {
        symbols.forEach((id, pixels) -> document.symbol(id, pixels));
    }

    private static List<Rect> tile(List<String> colours, int pixel) {
        List<Rect> rects = new ArrayList<>(colours.size());
        for (int index = 0; index < colours.size(); index++) {
            int column = index % BlockLooks.SIZE;
            int row = index / BlockLooks.SIZE;
            rects.add(new Rect(column * pixel, row * pixel, pixel, pixel, colours.get(index), "none", 0, 100));
        }
        return rects;
    }

    private static String slug(String material) {
        return material.toLowerCase(Locale.ROOT).replace('_', '-');
    }
}
