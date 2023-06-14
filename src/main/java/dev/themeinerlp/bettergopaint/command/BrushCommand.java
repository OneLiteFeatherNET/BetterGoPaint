package dev.themeinerlp.bettergopaint.command;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.item.ItemTypes;
import dev.themeinerlp.bettergopaint.BetterGoPaint;
import dev.themeinerlp.bettergopaint.fawe.brushes.BucketBrush;
import dev.themeinerlp.bettergopaint.fawe.pattern.BetteGoPaintRandomPattern;
import dev.themeinerlp.bettergopaint.fawe.util.BrushSettings;
import org.bukkit.Axis;
import org.bukkit.entity.Player;

import java.util.List;

public final class BrushCommand {

    private final BetterGoPaint betterGoPaint;

    public BrushCommand(final BetterGoPaint betterGoPaint) {
        this.betterGoPaint = betterGoPaint;
    }

    @CommandMethod("bgp|gp brush")
    @CommandPermission("bettergopaint.command.brush")
    public void onBrush(Player player) {
        BukkitPlayer act = BukkitAdapter.adapt(player);
        LocalSession session = act.getSession();
        BrushTool brushTool = session.getBrushTool(ItemTypes.STONE_SHOVEL.getDefaultState(), act, true);
        BrushSettings brushSettings = new BrushSettings(
                true,
                false,
                0,
                0,
                0,
                0,
                0,
                0,
                Axis.X,
                1.0
        );
        BucketBrush angleBrush = new BucketBrush(player, brushSettings, betterGoPaint);
        brushTool.setSize(20);
        brushTool.setBrush(angleBrush, "");
        brushTool.setFill(new BetteGoPaintRandomPattern(List.of(BlockTypes.STONE)));
    }

}
