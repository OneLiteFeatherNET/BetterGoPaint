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
package net.onelitefeather.bettergopaint.brush;

import com.fastasyncworldedit.core.math.random.SimpleRandom;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 * Captures a {@link StrokeContext} on the player's thread. This is the one place where Bukkit values
 * are turned into WorldEdit values. It only reads coordinates from the locations and never asks
 * their world for anything.
 */
public final class StrokeContextFactory {

    /**
     * Height of the player's eyes above the feet that the relative surface mode has always used.
     */
    private static final double EYE_HEIGHT = 1.5;

    private StrokeContextFactory() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Captures the context of a stroke.
     *
     * @param target         the clicked block
     * @param playerLocation the location of the player's feet
     * @param settings       the brush settings to snapshot
     * @param blockTypes     converts a material to its WorldEdit block type
     * @param random         the source of randomness of the stroke
     * @return the context of the stroke
     */
    public static @NotNull StrokeContext capture(
            @NotNull Location target,
            @NotNull Location playerLocation,
            @NotNull BrushSettings settings,
            @NotNull Function<Material, BlockType> blockTypes,
            @NotNull Random random
    ) {
        BlockVector3 position = BlockVector3.at(target.getBlockX(), target.getBlockY(), target.getBlockZ());
        Vector3 eye = Vector3.at(playerLocation.getX(), playerLocation.getY() + EYE_HEIGHT, playerLocation.getZ());

        List<BlockType> blocks = settings.blocks().stream().map(blockTypes).toList();
        Mask mask = settings.maskEnabled() ? new SingleTypeMask(blockTypes.apply(settings.mask())) : null;

        return new StrokeContext(
                position,
                List.of(position),
                eye,
                palette(blocks, random),
                blocks,
                mask,
                settings.axis(),
                settings.surfaceMode(),
                settings.size(),
                settings.chance(),
                settings.falloffStrength(),
                settings.mixingStrength(),
                settings.thickness(),
                settings.fractureDistance(),
                settings.angleDistance(),
                settings.angleHeightDifference(),
                random
        );
    }

    /**
     * Builds the palette pattern. Every entry of the block list has the same weight, so a block that is
     * listed twice is twice as likely.
     *
     * @param blocks the palette blocks
     * @param random the source of randomness
     * @return the palette pattern
     */
    static @NotNull RandomPattern palette(@NotNull List<BlockType> blocks, @NotNull Random random) {
        SimpleRandom simpleRandom = (x, y, z) -> random.nextDouble();
        Map<BlockType, Integer> weights = new LinkedHashMap<>();
        for (BlockType block : blocks) {
            weights.merge(block, 1, Integer::sum);
        }
        RandomPattern pattern = new RandomPattern(simpleRandom);
        weights.forEach(pattern::add);
        return pattern;
    }
}
