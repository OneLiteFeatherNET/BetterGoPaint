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

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.AbstractPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockType;

import java.util.List;
import java.util.Random;

public class BetteGoPaintRandomPattern extends AbstractPattern  {

    private final List<BlockType> blockTypeList;
    private final Random random = new Random();

    public BetteGoPaintRandomPattern(List<BlockType> blockTypeList) {
        this.blockTypeList = blockTypeList;
    }

    @Override
    public BaseBlock applyBlock(BlockVector3 position) {
        return blockTypeList.get(random.nextInt(blockTypeList.size())).applyBlock(position);
    }

    @Override
    public boolean apply(Extent extent, BlockVector3 get, BlockVector3 set) throws WorldEditException {
        return blockTypeList.get(random.nextInt(blockTypeList.size())).apply(extent, get, set);
    }
}
