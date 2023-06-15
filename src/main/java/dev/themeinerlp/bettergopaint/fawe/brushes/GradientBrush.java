package dev.themeinerlp.bettergopaint.fawe.brushes;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import dev.themeinerlp.bettergopaint.fawe.pattern.BetteGoPaintGradientPattern;
import dev.themeinerlp.bettergopaint.fawe.util.BrushSettings;
import dev.themeinerlp.bettergopaint.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public final class GradientBrush implements BetterBrush {

    private final Player player;
    private final BrushSettings brushSettings;
    private final Plugin plugin;

    private final ItemStack itemStack;

    public GradientBrush(Player player, BrushSettings brushSettings, Plugin plugin) {
        this.player = player;
        this.brushSettings = brushSettings;
        this.plugin = plugin;

        itemStack = new ItemStack(Material.PLAYER_HEAD);
        if (itemStack.getItemMeta() instanceof SkullMeta skullMeta) {
            var profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", Constants.HEAD_GRADIENT));
            skullMeta.setPlayerProfile(profile);
            itemStack.setItemMeta(skullMeta);
        }
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
        Vector3 playerPosAsVec3 = actor().getBlockTrace(250);
        double distanceMath = size / 2;
        double yy = playerPosAsVec3.getBlockY() - distanceMath;
        for (double x = positionA.getX(); x <= positionB.getX(); x++) {
            for (double z = positionA.getZ(); z <= positionB.getZ(); z++) {
                for (double y = positionA.getY(); y <= positionB.getY(); y++) {
                    BlockVector3 blockInRadius = BlockVector3.at(x, y, z);
                    BaseBlock block = editSession.getFullBlock(blockInRadius);
                    if (blockInRadius.distance(position) >= distanceMath || block.toBlockState().isAir()) {
                        continue;
                    }

                    if (settings().surfaceEnabled && !isOnSurface(editSession, blockInRadius.toVector3(), playerPosAsVec3)) {
                        continue;
                    }

                    if (settings().maskEnabled && (editSession.getMask() != null || !editSession.getMask().test(blockInRadius))) {
                        continue;
                    }

                    if (pattern instanceof BetteGoPaintGradientPattern gradientPattern) {
                        var changes = editSession.getBlockChangeCount();
                        gradientPattern.apply(editSession, blockInRadius, playerPosAsVec3.toBlockPoint(), yy, size, brushSettings);
                        editSession.setSize((changes+1));
                    } else {
                        editSession.setBlock(blockInRadius, pattern);
                    }
                }
            }
        }
    }

    @Override
    public BrushSettings settings() {
        return this.brushSettings;
    }

    @Override
    public String getName() {
        return "BucketBrush";
    }

    @Override
    public ItemStack getDisplayItem() {
        return itemStack;
    }

}
