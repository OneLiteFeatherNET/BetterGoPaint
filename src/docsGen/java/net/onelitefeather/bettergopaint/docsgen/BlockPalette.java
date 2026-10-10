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
import net.onelitefeather.bettergopaint.support.Terrain;

import java.util.List;

/**
 * The colour and the name of every block the scenes contain. Blocks are matched by identity, because the test
 * fixtures are mocks that share no state. A block that is not listed fails the generator with its position; the
 * generator never guesses a colour.
 */
final class BlockPalette {

    private final List<Entry> entries;

    private BlockPalette(List<Entry> entries) {
        this.entries = entries;
    }

    /**
     * @param terrain the block fixtures of one generator run
     * @return the palette of the fixtures, in the order the legend lists them
     */
    static BlockPalette of(Terrain terrain) {
        return new BlockPalette(List.of(
                new Entry(terrain.air, "air", null),
                new Entry(terrain.stone, "stone", "#374151"),
                new Entry(terrain.andesite, "andesite", "#0d9488"),
                new Entry(terrain.gold, "gold", "#ca8a04"),
                new Entry(terrain.grass, "grass", "#16a34a"),
                new Entry(terrain.dirt, "dirt", "#92400e")));
    }

    /**
     * @param type     the block type to look up
     * @param position the position of the block, named in the failure
     * @return the entry of the type
     * @throws IllegalStateException if the type is not in the palette
     */
    Entry entry(BlockType type, BlockVector3 position) {
        for (Entry entry : entries) {
            if (entry.type() == type) {
                return entry;
            }
        }
        throw new IllegalStateException("no colour for the block " + type + " at " + position
                + "; add it to BlockPalette");
    }

    /**
     * @return the entries in legend order
     */
    List<Entry> entries() {
        return entries;
    }

    /**
     * One block of the palette.
     *
     * @param type   the block type, matched by identity
     * @param label  the name shown in the legend
     * @param colour the fill colour, or {@code null} for air, which is not drawn
     */
    record Entry(BlockType type, String label, String colour) {
    }
}
