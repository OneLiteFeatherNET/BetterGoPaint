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
package dev.themeinerlp.bettergopaint.command;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.item.ItemTypes;
import dev.themeinerlp.bettergopaint.BetterGoPaint;
import dev.themeinerlp.bettergopaint.fawe.brushes.FractureBrush;
import dev.themeinerlp.bettergopaint.fawe.brushes.GradientBrush;
import dev.themeinerlp.bettergopaint.fawe.pattern.BetteGoPaintGradientPattern;
import dev.themeinerlp.bettergopaint.fawe.pattern.BetteGoPaintRandomPattern;
import dev.themeinerlp.bettergopaint.fawe.util.BrushSettings;
import dev.themeinerlp.bettergopaint.objects.other.Settings;
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
                false,
                false,
                0,
                0,
                Settings.settings().FRACTURE.DEFAULT_FRACTURE_DISTANCE,
                2,
                50,
                50,
                Axis.X,
                40.0
        );
        GradientBrush angleBrush = new GradientBrush(player, brushSettings, betterGoPaint);
        brushTool.setSize(20);
        brushTool.setBrush(angleBrush, "");
        brushTool.setFill(new BetteGoPaintGradientPattern(List.of(BlockTypes.STONE, BlockTypes.CAKE, BlockTypes.CAMPFIRE)));
    }

}
