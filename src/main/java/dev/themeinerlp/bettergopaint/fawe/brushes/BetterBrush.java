package dev.themeinerlp.bettergopaint.fawe.brushes;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.block.BlockState;
import dev.themeinerlp.bettergopaint.fawe.util.BrushSettings;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface BetterBrush extends Brush {

    Player brushOwner();

    BukkitPlayer actor();

    @Override
    void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size) throws MaxChangedBlocksException;

    BrushSettings settings();

    String getName();

    ItemStack getDisplayItem();

    default boolean isOnSurface(EditSession editSession, Vector3 blockPos, Vector3 playerPos) {
        Vector3 finalPlayerPos = playerPos.add(0, 1.5, 0);
        double distanceX = finalPlayerPos.getX() - blockPos.getX();
        double distanceY = finalPlayerPos.getY() - blockPos.getY();
        double distanceZ = finalPlayerPos.getZ() - blockPos.getZ();
        if (distanceX > 1) {
            blockPos = blockPos.add(1, 0, 0);
        } else if (distanceX > 0) {
            blockPos = blockPos.add(0.5, 0, 0);
        }

        if (distanceY > 1) {
            blockPos = blockPos.add(0, 1, 0);
        } else if (distanceY > 0) {
            blockPos = blockPos.add(0, 0.5, 0);
        }

        if (distanceZ > 1) {
            blockPos = blockPos.add(0, 0, 1);
        } else if (distanceZ > 0) {
            blockPos = blockPos.add(0, 0, 0.5);
        }
        double distance = blockPos.distance(playerPos);
        for (int i = 1; i < distance; i++) {
            double moveX = distanceX * (i / distance);
            double moveY = distanceY * (i / distance);
            double moveZ = distanceZ * (i / distance);
            Vector3 checkLocation = blockPos.add(moveX, moveY, moveZ);
            if (!editSession.getBlock(checkLocation.toBlockPoint()).isAir()) {
                return false;
            }
        }
        return true;
    }

    default double getAverageHeightDiffAngle(EditSession editSession, Vector3 vector3, int distance) {
        double maxHeightDiff = 0;
        double maxHeightDiff2 = 0;
        double diff = Math.abs(getHeight(editSession, vector3.add(distance, 0, -distance)) - getHeight(editSession, vector3.add(-distance, 0, distance)));
        if (diff >= maxHeightDiff) {
            maxHeightDiff = diff;
            maxHeightDiff2 = maxHeightDiff;
        }
        diff = Math.abs(getHeight(editSession, vector3.add(distance, 0, distance)) - getHeight(editSession, vector3.add(-distance, 0, -distance)));
        if (diff > maxHeightDiff) {
            maxHeightDiff = diff;
            maxHeightDiff2 = maxHeightDiff;
        }
        diff = Math.abs(getHeight(editSession, vector3.add(distance, 0, 0)) - getHeight(editSession, vector3.add(-distance, 0, 0)));
        if (diff > maxHeightDiff) {
            maxHeightDiff = diff;
            maxHeightDiff2 = maxHeightDiff;
        }
        diff = Math.abs(getHeight(editSession, vector3.add(0, 0, -distance)) - getHeight(editSession, vector3.add(0, 0, distance)));
        if (diff > maxHeightDiff) {
            maxHeightDiff = diff;
            maxHeightDiff2 = maxHeightDiff;
        }
        double height = (maxHeightDiff2 + maxHeightDiff) / 2.0;
        return height / (double) (distance * 2);
    }

    default int getHeight(EditSession editSession, Vector3 vector3) {
        if (editSession.getBlock(vector3.toBlockPoint()).isAir()) {
            for (int i = vector3.getBlockX(); i <= editSession.getWorld().getMaxY() && i >= editSession.getWorld().getMinY(); i--) {
                var vec = vector3.toBlockPoint().add(0, i, 0);
                BlockState blockState = editSession.getBlock(vec);
                if (!blockState.isAir()) {
                    return vec.getY() + 1;
                }
            }
            return 1;
        } else {
            for (int i = vector3.getBlockX(); i <= editSession.getWorld().getMaxY() && i >= editSession.getWorld().getMinY(); i++) {
                var vec = vector3.toBlockPoint().add(0, i, 0);
                BlockState blockState = editSession.getBlock(vec);
                if (blockState.isAir()) {
                    return vec.getY();
                }
            }
            return editSession.getWorld().getMaxY();
        }
    }

    default double getAverageHeightDiffFracture(EditSession editSession, Vector3 vector3, int height, int distance) {
        double totalHeight = 0;
        totalHeight += Math.abs(getHeight(editSession, vector3.add(distance, 0, -distance))) - height;
        totalHeight += Math.abs(getHeight(editSession, vector3.add(distance, 0, distance))) - height;
        totalHeight += Math.abs(getHeight(editSession, vector3.add(-distance, 0, distance))) - height;
        totalHeight += Math.abs(getHeight(editSession, vector3.add(-distance, 0, -distance))) - height;
        totalHeight += Math.abs(getHeight(editSession, vector3.add(0, 0, -distance))) - height;
        totalHeight += Math.abs(getHeight(editSession, vector3.add(0, 0, distance))) - height;
        totalHeight += Math.abs(getHeight(editSession, vector3.add(-distance, 0, 0))) - height;
        totalHeight += Math.abs(getHeight(editSession, vector3.add(distance, 0, 0))) - height;
        return (totalHeight / (double) 8) / (double) distance;
    }
}
