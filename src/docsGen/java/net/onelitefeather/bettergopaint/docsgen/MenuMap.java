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

/**
 * The picture of a brush menu: the slots of {@code GUI.layout()} drawn as the Minecraft 6x9 chest container, at twice the
 * scale of the inventory. Block items are block tiles, stained glass panes are tinted translucent tiles and the other
 * items are pixel icons. Each functional slot has a numbered badge, and a legend beside the container maps the numbers
 * to the English text of the slot. A filler pane has no badge, so the picture shows what a builder can use.
 */
final class MenuMap {

    private static final int COLUMNS = 9;
    private static final int ROWS = 6;
    private static final int SLOT = 36;
    private static final int BEVEL = 2;
    private static final int ITEM = SLOT - 2 * BEVEL;
    private static final int ITEM_PIXEL = ITEM / BlockLooks.SIZE;
    private static final int FRAME = 14;
    private static final int PANEL_WIDTH = COLUMNS * SLOT + 2 * FRAME;
    private static final int PANEL_HEIGHT = ROWS * SLOT + 2 * FRAME;
    private static final int PADDING = 16;
    private static final int TITLE_BASELINE = 36;
    private static final int PANEL_TOP = 56;
    private static final int LEGEND_GAP = 28;
    private static final int LEGEND_WIDTH = 380;
    private static final int LEGEND_LINE = 24;
    private static final int BADGE_RADIUS = 9;
    private static final int BADGE_OFFSET = 10;
    private static final int GLASS_OPACITY = 80;
    private static final String CARD = "#f8fafc";
    private static final String CONTAINER = "#c6c6c6";
    private static final String CONTAINER_DARK = "#555555";
    private static final String SLOT_DARK = "#373737";
    private static final String SLOT_LIGHT = "#ffffff";
    private static final String SLOT_FILL = "#8b8b8b";
    private static final String INK = "#111827";
    private static final String WHITE = "#ffffff";

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
     * The numbered badge of a functional slot.
     *
     * @param number the number of the badge, from 1 in slot order
     * @param slot   the slot the badge marks
     * @param label  the English text of the slot
     */
    record Callout(int number, int slot, String label) {
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
     * @param cells the cells of the menu, in slot order
     * @return one callout per functional slot, numbered from 1 in slot order; filler panes get none
     */
    static List<Callout> callouts(List<MenuCell> cells) {
        List<Callout> callouts = new ArrayList<>();
        for (MenuCell cell : cells) {
            if (!cell.purpose().isEmpty()) {
                callouts.add(new Callout(callouts.size() + 1, cell.slot(), cell.purpose()));
            }
        }
        return callouts;
    }

    /**
     * @param title the title shown above the container
     * @param cells the cells of the menu, in slot order
     * @return the picture of the menu
     * @throws IllegalStateException if an item is neither a block with a look, a glass pane nor an item with an icon
     */
    static SvgDocument render(String title, List<MenuCell> cells) {
        List<Callout> callouts = callouts(cells);
        int legendHeight = callouts.size() * LEGEND_LINE;
        int width = PADDING + PANEL_WIDTH + LEGEND_GAP + LEGEND_WIDTH + PADDING;
        int height = PANEL_TOP + Math.max(PANEL_HEIGHT, legendHeight) + PADDING;
        SvgDocument document = new SvgDocument(width, height)
                .rect(0, 0, width, height, CARD, "none", 0)
                .text(PADDING, TITLE_BASELINE, title, 18, INK, true);
        drawContainer(document, PADDING, PANEL_TOP);
        Sprites sprites = new Sprites();
        for (MenuCell cell : cells) {
            int x = slotX(cell.slot());
            int y = slotY(cell.slot());
            drawSlot(document, sprites, cell, x, y);
        }
        for (Callout callout : callouts) {
            badge(document, slotX(callout.slot()) + BADGE_OFFSET, slotY(callout.slot()) + BADGE_OFFSET, callout.number());
        }
        int legendX = PADDING + PANEL_WIDTH + LEGEND_GAP;
        for (Callout callout : callouts) {
            int baseline = PANEL_TOP + LEGEND_LINE / 2 + (callout.number() - 1) * LEGEND_LINE + 4;
            badge(document, legendX + BADGE_RADIUS, baseline - 4, callout.number());
            document.text(legendX + 2 * BADGE_RADIUS + 8, baseline, "Slot " + callout.slot() + ": " + callout.label(),
                    13, INK, false);
        }
        sprites.writeTo(document);
        return document;
    }

    private static int slotX(int slot) {
        return PADDING + FRAME + (slot % COLUMNS) * SLOT;
    }

    private static int slotY(int slot) {
        return PANEL_TOP + FRAME + (slot / COLUMNS) * SLOT;
    }

    /**
     * The grey container with its light top and left edge and its dark bottom and right edge.
     */
    private static void drawContainer(SvgDocument document, int x, int y) {
        document.rect(x, y, PANEL_WIDTH, PANEL_HEIGHT, CONTAINER_DARK, "none", 0);
        document.rect(x, y, PANEL_WIDTH - BEVEL, PANEL_HEIGHT - BEVEL, SLOT_LIGHT, "none", 0);
        document.rect(x + BEVEL, y + BEVEL, PANEL_WIDTH - 2 * BEVEL, PANEL_HEIGHT - 2 * BEVEL, CONTAINER, "none", 0);
    }

    /**
     * An inventory slot: a dark inset with a light bottom and right edge, and the item in its middle.
     */
    private static void drawSlot(SvgDocument document, Sprites sprites, MenuCell cell, int x, int y) {
        document.rect(x, y, SLOT, SLOT, SLOT_LIGHT, "none", 0);
        document.rect(x, y, SLOT - BEVEL, SLOT - BEVEL, SLOT_DARK, "none", 0);
        document.rect(x + BEVEL, y + BEVEL, ITEM, ITEM, SLOT_FILL, "none", 0);
        int itemX = x + BEVEL;
        int itemY = y + BEVEL;
        if (BlockLooks.has(cell.material())) {
            document.use(sprites.block(cell.material(), BlockLooks.Face.TOP, ITEM_PIXEL), itemX, itemY, 100);
        } else if (ItemIcons.isGlassPane(cell.material())) {
            document.rect(itemX, itemY, ITEM, ITEM, ItemIcons.glassTint(cell.material()), "none", 0, GLASS_OPACITY);
        } else {
            document.use(sprites.icon(cell.material(), ITEM_PIXEL), itemX, itemY, 100);
        }
    }

    /**
     * A numbered badge: a dark disc with a white number.
     */
    private static void badge(SvgDocument document, int centreX, int centreY, int number) {
        document.circle(centreX, centreY, BADGE_RADIUS, INK, WHITE, 1);
        document.centredText(centreX, centreY + 4, String.valueOf(number), 11, WHITE, true);
    }
}
