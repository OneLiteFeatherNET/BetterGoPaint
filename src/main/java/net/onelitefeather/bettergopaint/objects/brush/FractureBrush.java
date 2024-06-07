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
package net.onelitefeather.bettergopaint.objects.brush;

import com.cryptomorin.xseries.XMaterial;
import net.onelitefeather.bettergopaint.BetterGoPaint;
import net.onelitefeather.bettergopaint.objects.other.BlockPlace;
import net.onelitefeather.bettergopaint.objects.other.BlockPlacer;
import net.onelitefeather.bettergopaint.objects.other.BlockType;
import net.onelitefeather.bettergopaint.objects.player.ExportedPlayerBrush;
import net.onelitefeather.bettergopaint.utils.Height;
import net.onelitefeather.bettergopaint.utils.Sphere;
import net.onelitefeather.bettergopaint.utils.Surface;
import net.onelitefeather.bettergopaint.objects.player.PlayerBrush;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FractureBrush extends Brush {

    @SuppressWarnings({"deprecation"})
    @Override
    public void paint(Location loc, Player p) {
        PlayerBrush pb = BetterGoPaint.getBrushManager().getPlayerBrush(p);
        int size = pb.getBrushSize();
        List<BlockType> pbBlocks = pb.getBlocks();
        if (pbBlocks.isEmpty()) {
            return;
        }
        List<Block> blocks = Sphere.getBlocksInRadius(loc, size);
        List<BlockPlace> placedBlocks = new ArrayList<>();
        for (Block b : blocks) {
            if ((!pb.isSurfaceModeEnabled()) || Surface.isOnSurface(b.getLocation(), p.getLocation())) {
                if ((!pb.isMaskEnabled()) || (b.getType().equals(pb
                        .getMask()
                        .getMaterial()) && (XMaterial.supports(13) || b.getData() == pb.getMask().getData()))) {
                    if (Height.getAverageHeightDiffFracture(b.getLocation(), Height.getHeight( b.getLocation()), 1) >= 0.1) {
                        if (Height.getAverageHeightDiffFracture(
                                b.getLocation(),
                                Height.getHeight( b.getLocation()),
                                pb.getFractureDistance()
                        ) >= 0.1) {
                            Random r = new Random();
                            int random = r.nextInt(pbBlocks.size());
                            placedBlocks.add(
                                    new BlockPlace(
                                            b.getLocation(),
                                            new BlockType(pbBlocks.get(random).getMaterial(), pbBlocks.get(random).getData())
                                    ));
                        }
                    }
                }
            }
        }
        BlockPlacer.placeBlocks(placedBlocks, p);
    }

    @Override
    public String getName() {
        return "Fracture Brush";
    }

    @SuppressWarnings("deprecation")
    @Override
    public void paint(Location loc, Player p, ExportedPlayerBrush epb) {
        int size = epb.getBrushSize();
        List<BlockType> epbBlocks = epb.getBlocks();
        if (epbBlocks.isEmpty()) {
            return;
        }
        List<Block> blocks = Sphere.getBlocksInRadius(loc, size);
        List<BlockPlace> placedBlocks = new ArrayList<BlockPlace>();
        for (Block b : blocks) {
            if ((!epb.isSurfaceModeEnabled()) || Surface.isOnSurface(b.getLocation(), p.getLocation())) {
                if ((!epb.isMaskEnabled()) || (b.getType().equals(epb
                        .getMask()
                        .getMaterial()) && (XMaterial.supports(13) || b.getData() == epb.getMask().getData()))) {
                    if (Height.getAverageHeightDiffFracture(b.getLocation(), Height.getHeight( b.getLocation()), 1) >= 0.1) {
                        if (Height.getAverageHeightDiffFracture(
                                b.getLocation(),
                                Height.getHeight( b.getLocation()),
                                epb.getFractureDistance()
                        ) >= 0.1) {
                            Random r = new Random();
                            int random = r.nextInt(epbBlocks.size());
                            placedBlocks.add(
                                    new BlockPlace(
                                            b.getLocation(),
                                            new BlockType(
                                                    epb.getBlocks().get(random).getMaterial(),
                                                    epb.getBlocks().get(random).getData()
                                            )
                                    ));
                        }
                    }

                }
            }
        }
        BlockPlacer.placeBlocks(placedBlocks, p);

    }

}
