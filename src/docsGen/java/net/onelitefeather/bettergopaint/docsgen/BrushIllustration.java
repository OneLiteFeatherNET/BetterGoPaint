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

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import net.onelitefeather.bettergopaint.support.MemoryExtent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Draws one brush scene as an SVG: a top view, a cross-section through the clicked block, and a legend. Changed blocks
 * take their palette colour. Blocks the stroke did not change are grey and air is white, so the painted part stands out
 * on the light card that is the same in both GitBook themes. Every position is a whole number of cells, and air is drawn
 * too, so both panels show the same grid.
 */
final class BrushIllustration {

    static final String CARD = "#f8fafc";
    private static final String AIR = "#ffffff";
    private static final String GRID = "#e5e7eb";
    private static final String INK = "#111827";
    private static final String UNCHANGED = "#9ca3af";
    private static final String MARKER = "#111827";
    private static final int CELL = 24;
    private static final int MARGIN = 2;
    private static final int PADDING = 16;
    private static final int PANEL_GAP = 40;
    private static final int TITLE_BASELINE = 32;
    private static final int LABEL_BASELINE = 60;
    private static final int PANEL_TOP = 72;
    private static final int LEGEND_GAP = 32;
    private static final int LEGEND_COLUMNS = 4;
    private static final int LEGEND_COLUMN_WIDTH = 150;
    private static final int LEGEND_ROW_HEIGHT = 22;

    private BrushIllustration() {
    }

    /**
     * @param scene   the scene to draw, with the stroke applied
     * @param palette the colours of the blocks of the scene
     * @return the picture, without any timestamp or generator version
     * @throws IllegalStateException if a block of the scene is not in the palette
     */
    static SvgDocument render(BrushScene scene, BlockPalette palette) {
        List<BrushScene.Change> changes = scene.changes();
        Box box = Box.around(changes, scene.target(), MARGIN);
        Set<BlockVector3> changed = new HashSet<>();
        changes.forEach(change -> changed.add(change.position()));
        List<Legend> legend = legend(palette, changes);

        int panelWidth = (box.maxX() - box.minX() + 1) * CELL;
        int panelHeight = Math.max(box.maxZ() - box.minZ() + 1, box.maxY() - box.minY() + 1) * CELL;
        int sideOrigin = PADDING + panelWidth + PANEL_GAP;
        int legendRows = (legend.size() + LEGEND_COLUMNS - 1) / LEGEND_COLUMNS;
        int legendWidth = Math.min(legend.size(), LEGEND_COLUMNS) * LEGEND_COLUMN_WIDTH;
        int legendTop = PANEL_TOP + panelHeight + LEGEND_GAP;
        int width = Math.max(sideOrigin + panelWidth + PADDING, PADDING + legendWidth + PADDING);
        int height = legendTop + legendRows * LEGEND_ROW_HEIGHT + PADDING;

        SvgDocument document = new SvgDocument(width, height)
                .rect(0, 0, width, height, CARD, "none", 0)
                .text(PADDING, TITLE_BASELINE, scene.brush().getName(), 18, INK, true)
                .text(PADDING, LABEL_BASELINE, "Top view", 13, INK, false)
                .text(sideOrigin, LABEL_BASELINE, "Side cross-section", 13, INK, false);
        drawTopView(document, scene, palette, box);
        drawSideView(document, scene, palette, box, changed, sideOrigin);
        drawLegend(document, legend, legendTop);
        return document;
    }

    /**
     * One cell per column of x and z. A column shows its highest changed block; a column the stroke did not change
     * shows its top block in grey.
     */
    private static void drawTopView(SvgDocument document, BrushScene scene, BlockPalette palette, Box box) {
        Map<Column, BrushScene.Change> highest = new HashMap<>();
        for (BrushScene.Change change : scene.changes()) {
            highest.merge(new Column(change.position().x(), change.position().z()), change,
                    (first, second) -> first.position().y() >= second.position().y() ? first : second);
        }
        for (int z = box.minZ(); z <= box.maxZ(); z++) {
            for (int x = box.minX(); x <= box.maxX(); x++) {
                String fill = topFill(scene, palette, highest.get(new Column(x, z)), x, z);
                cell(document, PADDING, x - box.minX(), z - box.minZ(), fill);
            }
        }
        BlockVector3 target = scene.target();
        marker(document, PADDING, target.x() - box.minX(), target.z() - box.minZ());
    }

    /**
     * @return the fill of one column of the top view; air is drawn as an empty cell, so the grid keeps its scale
     */
    private static String topFill(BrushScene scene, BlockPalette palette, BrushScene.Change changed, int x, int z) {
        if (changed != null) {
            BlockVector3 position = BlockVector3.at(x, changed.position().y(), z);
            return colourOrAir(palette.entry(changed.after(), position));
        }
        MemoryExtent after = scene.after();
        for (int y = after.getMaximumPoint().y(); y >= after.getMinimumPoint().y(); y--) {
            BlockVector3 position = BlockVector3.at(x, y, z);
            if (palette.entry(after.typeAt(position), position).colour() != null) {
                return UNCHANGED;
            }
        }
        return AIR;
    }

    private static String colourOrAir(BlockPalette.Entry entry) {
        return entry.colour() == null ? AIR : entry.colour();
    }

    /**
     * One cell per column of x and y, in the plane of the clicked block. The highest y is drawn first.
     */
    private static void drawSideView(SvgDocument document, BrushScene scene, BlockPalette palette, Box box,
                                     Set<BlockVector3> changed, int originX) {
        int planeZ = scene.target().z();
        for (int y = box.maxY(); y >= box.minY(); y--) {
            for (int x = box.minX(); x <= box.maxX(); x++) {
                BlockVector3 position = BlockVector3.at(x, y, planeZ);
                BlockPalette.Entry entry = palette.entry(scene.after().typeAt(position), position);
                String fill;
                if (entry.colour() == null) {
                    fill = AIR;
                } else {
                    fill = changed.contains(position) ? entry.colour() : UNCHANGED;
                }
                cell(document, originX, x - box.minX(), box.maxY() - y, fill);
            }
        }
        BlockVector3 target = scene.target();
        marker(document, originX, target.x() - box.minX(), box.maxY() - target.y());
    }

    private static void drawLegend(SvgDocument document, List<Legend> legend, int top) {
        for (int index = 0; index < legend.size(); index++) {
            Legend item = legend.get(index);
            int x = PADDING + (index % LEGEND_COLUMNS) * LEGEND_COLUMN_WIDTH;
            int baseline = top + (index / LEGEND_COLUMNS) * LEGEND_ROW_HEIGHT;
            if (item.marker()) {
                document.rect(x, baseline - 11, 14, 14, item.fill(), MARKER, 2);
            } else {
                document.rect(x, baseline - 11, 14, 14, item.fill(), "none", 0);
            }
            document.text(x + 22, baseline, item.label(), 13, INK, false);
        }
    }

    /**
     * The legend: the unchanged grey, each palette block that the picture shows, and the marker of the clicked block.
     * Palette blocks follow the palette order, so the legend does not depend on the order of the changes.
     */
    private static List<Legend> legend(BlockPalette palette, List<BrushScene.Change> changes) {
        Set<BlockType> used = Collections.newSetFromMap(new IdentityHashMap<>());
        changes.forEach(change -> used.add(change.after()));

        List<Legend> items = new ArrayList<>();
        items.add(new Legend("unchanged block", UNCHANGED, false));
        items.add(new Legend("air", AIR, false));
        for (BlockPalette.Entry entry : palette.entries()) {
            if (entry.colour() != null && used.contains(entry.type())) {
                items.add(new Legend(entry.label() + " block", entry.colour(), false));
            }
        }
        items.add(new Legend("clicked block", CARD, true));
        return items;
    }

    private static void cell(SvgDocument document, int originX, int column, int row, String fill) {
        document.rect(originX + column * CELL, PANEL_TOP + row * CELL, CELL, CELL, fill, GRID, 1);
    }

    private static void marker(SvgDocument document, int originX, int column, int row) {
        document.rect(originX + column * CELL, PANEL_TOP + row * CELL, CELL, CELL, "none", MARKER, 2);
    }

    /**
     * An x and z column of the top view.
     */
    private record Column(int x, int z) {
    }

    /**
     * One legend line.
     *
     * @param label  the text of the line
     * @param fill   the fill of the swatch
     * @param marker whether the swatch is the outline of the clicked block
     */
    private record Legend(String label, String fill, boolean marker) {
    }

    /**
     * The smallest box of whole blocks that holds every change and the clicked block, grown by a margin.
     */
    private record Box(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

        static Box around(List<BrushScene.Change> changes, BlockVector3 target, int margin) {
            int minX = target.x();
            int minY = target.y();
            int minZ = target.z();
            int maxX = target.x();
            int maxY = target.y();
            int maxZ = target.z();
            for (BrushScene.Change change : changes) {
                BlockVector3 position = change.position();
                minX = Math.min(minX, position.x());
                minY = Math.min(minY, position.y());
                minZ = Math.min(minZ, position.z());
                maxX = Math.max(maxX, position.x());
                maxY = Math.max(maxY, position.y());
                maxZ = Math.max(maxZ, position.z());
            }
            return new Box(minX - margin, minY - margin, minZ - margin, maxX + margin, maxY + margin, maxZ + margin);
        }
    }
}
