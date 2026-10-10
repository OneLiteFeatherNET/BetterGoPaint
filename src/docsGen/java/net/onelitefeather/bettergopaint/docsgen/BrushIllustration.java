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
import net.onelitefeather.bettergopaint.support.MemoryExtent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Draws one brush scene as an SVG: a top view, a cross-section through the clicked block, and a legend. Blocks are
 * drawn with the tile of their Minecraft material. A block the brush changed is drawn at full opacity with a highlight
 * border; a block it did not change is faded, so the painted part stands out and the ground stays recognisable. Air
 * is the sky tint. Every position is a whole number of cells, and air is drawn too, so both panels share one grid.
 */
final class BrushIllustration {

    static final String CARD = "#f8fafc";
    private static final String SKY = "#dbeafe";
    private static final String GRID = "#0f172a";
    private static final int GRID_OPACITY = 22;
    private static final String INK = "#111827";
    private static final String HIGHLIGHT = "#ffffff";
    private static final int CELL = 24;
    private static final int PIXEL = CELL / BlockLooks.SIZE;
    private static final int LEGEND_PIXEL = 2;
    private static final int SWATCH = 16;
    private static final int FADED = 55;
    private static final int MARGIN = 2;
    private static final int PADDING = 16;
    private static final int PANEL_GAP = 40;
    private static final int TITLE_BASELINE = 32;
    private static final int LABEL_BASELINE = 60;
    private static final int PANEL_TOP = 72;
    private static final int LEGEND_GAP = 32;
    private static final int LEGEND_COLUMNS = 4;
    private static final int LEGEND_COLUMN_WIDTH = 180;
    private static final int LEGEND_ROW_HEIGHT = 26;
    private static final int POINT_RADIUS = 8;

    private BrushIllustration() {
    }

    /**
     * @param scene   the scene to draw, with the stroke applied
     * @param palette the materials of the blocks of the scene
     * @return the picture, without any timestamp or generator version
     * @throws IllegalStateException if a block of the scene is not in the palette or has no look
     */
    static SvgDocument render(BrushScene scene, BlockPalette palette) {
        List<BrushScene.Change> changes = scene.changes();
        Box box = Box.around(changes, scene.points(), scene.target(), MARGIN);
        Set<BlockVector3> changed = new HashSet<>();
        changes.forEach(change -> changed.add(change.position()));
        List<Cell> top = topCells(scene, palette, box);
        List<Cell> side = sideCells(scene, palette, box, changed);
        List<Legend> legend = legend(palette, top, side, scene.points().size() > 1);

        int columns = box.maxX() - box.minX() + 1;
        int panelWidth = columns * CELL;
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
        Sprites sprites = new Sprites();
        drawPanel(document, sprites, top, columns, PADDING, BlockLooks.Face.TOP);
        drawPanel(document, sprites, side, columns, sideOrigin, BlockLooks.Face.SIDE);
        BlockVector3 target = scene.target();
        marker(document, PADDING, target.x() - box.minX(), target.z() - box.minZ());
        marker(document, sideOrigin, target.x() - box.minX(), box.maxY() - target.y());
        if (scene.points().size() > 1) {
            drawPoints(document, scene, box);
        }
        drawLegend(document, sprites, legend, legendTop);
        sprites.writeTo(document);
        return document;
    }

    /**
     * One cell per column of x and z, row by row from the lowest z. A column the brush changed shows its highest
     * changed block at full opacity; any other column shows its top block, faded, or the sky when it has none.
     */
    private static List<Cell> topCells(BrushScene scene, BlockPalette palette, Box box) {
        Map<Column, BrushScene.Change> highest = new HashMap<>();
        for (BrushScene.Change change : scene.changes()) {
            highest.merge(new Column(change.position().x(), change.position().z()), change,
                    (first, second) -> first.position().y() >= second.position().y() ? first : second);
        }
        List<Cell> cells = new ArrayList<>();
        for (int z = box.minZ(); z <= box.maxZ(); z++) {
            for (int x = box.minX(); x <= box.maxX(); x++) {
                BrushScene.Change change = highest.get(new Column(x, z));
                if (change != null) {
                    cells.add(cellOf(palette.entry(change.after(), change.position()), true));
                } else {
                    cells.add(topmost(scene.after(), palette, x, z));
                }
            }
        }
        return cells;
    }

    private static Cell topmost(MemoryExtent after, BlockPalette palette, int x, int z) {
        for (int y = after.getMaximumPoint().y(); y >= after.getMinimumPoint().y(); y--) {
            BlockVector3 position = BlockVector3.at(x, y, z);
            BlockPalette.Entry entry = palette.entry(after.typeAt(position), position);
            if (!entry.air()) {
                return cellOf(entry, false);
            }
        }
        return Cell.AIR;
    }

    /**
     * One cell per x and y in the plane of the clicked block, the highest y first.
     */
    private static List<Cell> sideCells(BrushScene scene, BlockPalette palette, Box box, Set<BlockVector3> changed) {
        int planeZ = scene.target().z();
        List<Cell> cells = new ArrayList<>();
        for (int y = box.maxY(); y >= box.minY(); y--) {
            for (int x = box.minX(); x <= box.maxX(); x++) {
                BlockVector3 position = BlockVector3.at(x, y, planeZ);
                BlockPalette.Entry entry = palette.entry(scene.after().typeAt(position), position);
                cells.add(cellOf(entry, changed.contains(position)));
            }
        }
        return cells;
    }

    private static Cell cellOf(BlockPalette.Entry entry, boolean changed) {
        return entry.air() ? Cell.AIR : new Cell(entry.material(), changed);
    }

    private static void drawPanel(SvgDocument document, Sprites sprites, List<Cell> cells, int columns, int originX,
                                  BlockLooks.Face face) {
        for (int index = 0; index < cells.size(); index++) {
            drawCell(document, sprites, originX + (index % columns) * CELL, PANEL_TOP + (index / columns) * CELL,
                    cells.get(index), face);
        }
    }

    private static void drawCell(SvgDocument document, Sprites sprites, int x, int y, Cell cell, BlockLooks.Face face) {
        if (cell.air()) {
            document.rect(x, y, CELL, CELL, SKY, "none", 0);
        } else {
            document.use(sprites.block(cell.material(), face, PIXEL), x, y, cell.changed() ? 100 : FADED);
            if (cell.changed()) {
                highlight(document, x, y, CELL, 70);
            }
        }
        document.rect(x, y, CELL, CELL, "none", GRID, 1, GRID_OPACITY);
    }

    /**
     * The thin highlight border of a changed block: a white line one pixel inside the edge of the cell.
     */
    private static void highlight(SvgDocument document, int x, int y, int size, int opacity) {
        document.rect(x + 1, y + 1, size - 2, size - 2, "none", HIGHLIGHT, 1, opacity);
    }

    /**
     * The outline of the clicked block.
     */
    private static void marker(SvgDocument document, int originX, int column, int row) {
        document.rect(originX + column * CELL, PANEL_TOP + row * CELL, CELL, CELL, "none", INK, 2);
    }

    /**
     * The stroke as a path through its clicked points, with each point numbered in click order.
     */
    private static void drawPoints(SvgDocument document, BrushScene scene, Box box) {
        List<SvgDocument.Point> centres = new ArrayList<>();
        for (BlockVector3 point : scene.points()) {
            centres.add(new SvgDocument.Point(PADDING + (point.x() - box.minX()) * CELL + CELL / 2,
                    PANEL_TOP + (point.z() - box.minZ()) * CELL + CELL / 2));
        }
        document.polyline(centres, INK, 2);
        for (int index = 0; index < centres.size(); index++) {
            SvgDocument.Point centre = centres.get(index);
            document.circle(centre.x(), centre.y(), POINT_RADIUS, HIGHLIGHT, INK, 2);
            document.centredText(centre.x(), centre.y() + 4, String.valueOf(index + 1), 11, INK, true);
        }
    }

    /**
     * The legend: each material of the picture by its Minecraft name, in palette order, then the markers.
     */
    private static List<Legend> legend(BlockPalette palette, List<Cell> top, List<Cell> side, boolean numbered) {
        Set<String> materials = new HashSet<>();
        boolean air = false;
        Cell changedSample = null;
        Cell fadedSample = null;
        List<Cell> all = new ArrayList<>(top);
        all.addAll(side);
        for (Cell cell : all) {
            if (cell.air()) {
                air = true;
            } else {
                materials.add(cell.material());
                if (cell.changed() && changedSample == null) {
                    changedSample = cell;
                }
                if (!cell.changed() && fadedSample == null) {
                    fadedSample = cell;
                }
            }
        }
        List<Legend> items = new ArrayList<>();
        for (BlockPalette.Entry entry : palette.entries()) {
            if (!entry.air() && materials.contains(entry.material())) {
                items.add(new Legend(BlockLooks.look(entry.material()).label(), Marker.MATERIAL, entry.material()));
            }
        }
        if (air) {
            items.add(new Legend("air", Marker.AIR, null));
        }
        if (changedSample != null) {
            items.add(new Legend("changed by the brush", Marker.CHANGED, changedSample.material()));
        }
        if (fadedSample != null) {
            items.add(new Legend("unchanged (faded)", Marker.FADED, fadedSample.material()));
        }
        items.add(new Legend("clicked block", Marker.CLICKED, null));
        if (numbered) {
            items.add(new Legend("numbered: click order", Marker.POINT, null));
        }
        return items;
    }

    private static void drawLegend(SvgDocument document, Sprites sprites, List<Legend> legend, int top) {
        for (int index = 0; index < legend.size(); index++) {
            Legend item = legend.get(index);
            int x = PADDING + (index % LEGEND_COLUMNS) * LEGEND_COLUMN_WIDTH;
            int y = top + (index / LEGEND_COLUMNS) * LEGEND_ROW_HEIGHT;
            drawSwatch(document, sprites, item, x, y);
            document.text(x + SWATCH + 6, y + 12, item.label(), 13, INK, false);
        }
    }

    private static void drawSwatch(SvgDocument document, Sprites sprites, Legend item, int x, int y) {
        switch (item.marker()) {
            case MATERIAL -> document.use(sprites.block(item.material(), BlockLooks.Face.TOP, LEGEND_PIXEL), x, y, 100);
            case AIR -> document.rect(x, y, SWATCH, SWATCH, SKY, GRID, 1);
            case CHANGED -> {
                document.use(sprites.block(item.material(), BlockLooks.Face.TOP, LEGEND_PIXEL), x, y, 100);
                highlight(document, x, y, SWATCH, 70);
            }
            case FADED -> document.use(sprites.block(item.material(), BlockLooks.Face.TOP, LEGEND_PIXEL), x, y, FADED);
            case CLICKED -> document.rect(x, y, SWATCH, SWATCH, "none", INK, 2);
            case POINT -> document.circle(x + SWATCH / 2, y + SWATCH / 2, 6, HIGHLIGHT, INK, 2);
        }
    }

    /**
     * One cell of a panel.
     *
     * @param material the Minecraft name of the block, or {@code null} for air
     * @param changed  whether the brush changed the block
     */
    private record Cell(String material, boolean changed) {

        static final Cell AIR = new Cell(null, false);

        boolean air() {
            return material == null;
        }
    }

    /**
     * An x and z column of the top view.
     */
    private record Column(int x, int z) {
    }

    /**
     * One legend line.
     *
     * @param label    the text of the line
     * @param marker   what the swatch shows
     * @param material the material of the swatch, or {@code null} when the marker needs none
     */
    private record Legend(String label, Marker marker, String material) {
    }

    /**
     * What a legend swatch shows.
     */
    private enum Marker {
        MATERIAL, AIR, CHANGED, FADED, CLICKED, POINT
    }

    /**
     * The smallest box of whole blocks that holds every change, the clicked block and every control point, grown by a
     * margin.
     */
    private record Box(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

        static Box around(List<BrushScene.Change> changes, List<BlockVector3> points, BlockVector3 target, int margin) {
            List<BlockVector3> positions = new ArrayList<>(points);
            positions.add(target);
            changes.forEach(change -> positions.add(change.position()));
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            int maxZ = Integer.MIN_VALUE;
            for (BlockVector3 position : positions) {
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
