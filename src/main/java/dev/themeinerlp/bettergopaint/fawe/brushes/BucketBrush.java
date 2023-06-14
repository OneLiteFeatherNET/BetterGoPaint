package dev.themeinerlp.bettergopaint.fawe.brushes;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import dev.themeinerlp.bettergopaint.fawe.util.BrushSettings;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BucketBrush implements BetterBrush {

    private final Player player;
    private final BrushSettings brushSettings;
    private final Plugin plugin;

    public BucketBrush(Player player, BrushSettings brushSettings, Plugin plugin) {
        this.player = player;
        this.brushSettings = brushSettings;
        this.plugin = plugin;
    }

    @Override
    public Player brushOwner() {
        return this.player;
    }

    @Override
    public BukkitPlayer actor() {
        return BukkitAdapter.adapt(this.player);
    }

    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size) throws
            MaxChangedBlocksException {
        BlockVector3 positionA = position.add((int) (-size / 2), (int) -size / 2, (int) -size / 2);
        BlockVector3 positionB = position.add((int) (size / 2), (int) size / 2, (int) size / 2);
        Vector3 playerPosAsVec3 = actor().getLocation().toVector();
        double distanceMath = size / 2;
        for (double x = positionA.getX(); x <= positionB.getX(); x++) {
            for (double z = positionA.getZ(); z <= positionB.getZ(); z++) {
                for (double y = positionA.getY(); y <= positionB.getY(); y++) {
                    BlockVector3 blockInRadius = BlockVector3.at(x, y, z);
                    BaseBlock block = editSession.getFullBlock(blockInRadius);
                    if (blockInRadius.distance(position) < distanceMath && !block.toBlockState().isAir()) {
                        if ((!settings().surfaceEnabled || isOnSurface(editSession, blockInRadius.toVector3(), playerPosAsVec3))) {
                            if (!settings().maskEnabled || (editSession.getMask() != null && editSession
                                    .getMask()
                                    .test(blockInRadius))) {
                                editSession.setBlock(blockInRadius, pattern);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public BrushSettings settings() {
        return this.brushSettings;
    }

}