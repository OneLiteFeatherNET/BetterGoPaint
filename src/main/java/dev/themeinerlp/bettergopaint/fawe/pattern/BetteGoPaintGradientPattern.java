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
