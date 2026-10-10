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

import net.onelitefeather.bettergopaint.menu.BrushMenuHolder;
import net.onelitefeather.bettergopaint.menu.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Draws the brush menu as a grid of six rows of nine cells. Every cell shows its slot number, the material of the
 * item and the English text of the item name. The slots come from {@code GUI.layout()}, so the picture cannot drift
 * from the menu the plugin builds.
 */
final class MenuMap {

    private static final int COLUMNS = 9;
    private static final int CELL_WIDTH = 128;
    private static final int CELL_HEIGHT = 104;
    private static final int PADDING = 16;
    private static final int TITLE_BASELINE = 36;
    private static final int GRID_TOP = 56;
    private static final int LINE_HEIGHT = 14;
    private static final int TEXT_CHARACTERS = 18;
    private static final int MATERIAL_LINES = 2;
    private static final int PURPOSE_LINES = 3;
    private static final String CARD = "#f8fafc";
    private static final String CELL = "#ffffff";
    private static final String BORDER = "#d1d5db";
    private static final String INK = "#111827";
    private static final String MUTED = "#4b5563";

    private MenuMap() {
    }

    /**
     * One slot of the menu.
     *
     * @param slot     the slot, counted from 0 like the inventory
     * @param material the name of the material, for example {@code DIRT}
     * @param purpose  the English text of the item name, empty for a filler pane
     */
    record MenuCell(int slot, String material, String purpose) {
    }

    /**
     * @param layout the items of the menu, one per slot
     * @param text   the English text of the translation keys
     * @return one cell per slot, in slot order
     * @throws IllegalArgumentException if the layout is not a full menu
     * @throws IllegalStateException    if a translation key has no English text; the message names the key
     */
    static List<MenuCell> cells(MenuItem[] layout, EnglishText text) {
        if (layout.length != BrushMenuHolder.SIZE) {
            throw new IllegalArgumentException("a brush menu has " + BrushMenuHolder.SIZE + " slots, got " + layout.length);
        }
        List<MenuCell> cells = new ArrayList<>(layout.length);
        for (int slot = 0; slot < layout.length; slot++) {
            MenuItem item = layout[slot];
            cells.add(new MenuCell(slot, item.material().name(), text.plain(item.name()).trim()));
        }
        return cells;
    }

    /**
     * @param title the title shown above the grid
     * @param cells the cells of the menu, in slot order
     * @return the picture of the menu
     */
    static SvgDocument render(String title, List<MenuCell> cells) {
        int width = 2 * PADDING + COLUMNS * CELL_WIDTH;
        int height = GRID_TOP + (cells.size() / COLUMNS) * CELL_HEIGHT + PADDING;
        SvgDocument document = new SvgDocument(width, height)
                .rect(0, 0, width, height, CARD, "none", 0)
                .text(PADDING, TITLE_BASELINE, title, 18, INK, true);
        for (MenuCell cell : cells) {
            int x = PADDING + (cell.slot() % COLUMNS) * CELL_WIDTH + 2;
            int y = GRID_TOP + (cell.slot() / COLUMNS) * CELL_HEIGHT + 2;
            document.rect(x, y, CELL_WIDTH - 4, CELL_HEIGHT - 4, CELL, BORDER, 1);
            drawCell(document, cell, x + 8, y + 18);
        }
        return document;
    }

    private static void drawCell(SvgDocument document, MenuCell cell, int x, int slotBaseline) {
        document.text(x, slotBaseline, "Slot " + cell.slot(), 11, MUTED, true);
        int line = slotBaseline;
        for (String part : wrap(cell.material().toLowerCase(Locale.ROOT).replace('_', ' '), MATERIAL_LINES)) {
            line += LINE_HEIGHT;
            document.text(x, line, part, 11, MUTED, false);
        }
        String purpose = cell.purpose().isEmpty() ? "filler" : cell.purpose();
        for (String part : wrap(purpose, PURPOSE_LINES)) {
            line += LINE_HEIGHT;
            document.text(x, line, part, 11, INK, !cell.purpose().isEmpty());
        }
    }

    /**
     * Breaks the text at spaces into at most {@code maxLines} lines of {@link #TEXT_CHARACTERS} characters. Text that
     * does not fit ends with {@code ...}.
     */
    static List<String> wrap(String text, int maxLines) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            if (word.isEmpty()) {
                continue;
            }
            if (line.isEmpty()) {
                line.append(word);
            } else if (line.length() + 1 + word.length() <= TEXT_CHARACTERS) {
                line.append(' ').append(word);
            } else {
                lines.add(line.toString());
                line.setLength(0);
                line.append(word);
            }
        }
        if (!line.isEmpty()) {
            lines.add(line.toString());
        }
        if (lines.size() > maxLines) {
            List<String> kept = new ArrayList<>(lines.subList(0, maxLines));
            String last = kept.get(maxLines - 1);
            kept.set(maxLines - 1, last.substring(0, Math.min(last.length(), TEXT_CHARACTERS - 3)) + "...");
            return kept;
        }
        return lines;
    }
}
