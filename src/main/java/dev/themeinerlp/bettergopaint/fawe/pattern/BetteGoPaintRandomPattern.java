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
