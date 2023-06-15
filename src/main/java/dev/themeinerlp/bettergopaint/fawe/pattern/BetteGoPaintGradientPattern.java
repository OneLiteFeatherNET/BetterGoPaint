/*
 * BetterGoPaint is designed to simplify painting inside of Minecraft.
 * Copyright (C) TheMeinerLP
 * Copyright (C) OneLiteFeather
 * Copyright (C) OneLiteFeather team and contributors
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
package dev.themeinerlp.bettergopaint.fawe.pattern;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.AbstractPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockType;
import dev.themeinerlp.bettergopaint.fawe.util.BrushSettings;

import java.util.List;
import java.util.Random;

public class BetteGoPaintGradientPattern extends AbstractPattern {

    private final List<BlockType> blockTypeList;
    private final Random random = new Random();

    public BetteGoPaintGradientPattern(List<BlockType> blockTypeList) {
        this.blockTypeList = blockTypeList;
    }

    @Override
    public BaseBlock applyBlock(BlockVector3 position) {
        throw new UnsupportedOperationException("This method is not allowed");
    }

    @Override
    public boolean apply(Extent extent, BlockVector3 get, BlockVector3 set) throws WorldEditException {
        throw new UnsupportedOperationException("This method is not allowed");
    }

    public void apply(EditSession extent, BlockVector3 blockLoc, BlockVector3 playerPos, double y, double size, BrushSettings settings) throws WorldEditException {
        double _y = (blockLoc.getBlockY() - y) / size * blockTypeList.size();
        int block = (int) (_y + (random.nextDouble() * 2 - 1) * ((double) settings.mixingStrength / 100.0));
        if (block == -1) {
            block = 0;
        }
        if (block == blockTypeList.size()) {
            block = blockTypeList.size() - 1;
        }
        double rate = (blockLoc.distance(playerPos) - (size / 2.0) * ((100.0 - (double) settings.falloffStrength) / 100.0)) / ((size / 2.0) - (size / 2.0) * ((100.0 - (double) settings.falloffStrength) / 100.0));
        if (!(random.nextDouble() <= rate)) {
            blockTypeList.get(block).apply(extent, blockLoc, blockLoc);
        }
    }


}
