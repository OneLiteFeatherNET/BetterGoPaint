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
package net.onelitefeather.bettergopaint.docsgen;

import com.sk89q.worldedit.math.BlockVector3;
import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.objects.brush.AngleBrush;
import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.objects.brush.BucketBrush;
import net.onelitefeather.bettergopaint.objects.brush.DiscBrush;
import net.onelitefeather.bettergopaint.objects.brush.FractureBrush;
import net.onelitefeather.bettergopaint.objects.brush.GradientBrush;
import net.onelitefeather.bettergopaint.objects.brush.OverlayBrush;
import net.onelitefeather.bettergopaint.objects.brush.PaintBrush;
import net.onelitefeather.bettergopaint.objects.brush.SphereBrush;
import net.onelitefeather.bettergopaint.objects.brush.SplatterBrush;
import net.onelitefeather.bettergopaint.objects.brush.SprayBrush;
import net.onelitefeather.bettergopaint.objects.brush.UnderlayBrush;
import net.onelitefeather.bettergopaint.support.MemoryExtent;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.bukkit.Axis;

import java.util.List;
import java.util.function.Function;

import static net.onelitefeather.bettergopaint.support.StrokeBuilder.stroke;

/**
 * The fixed scene of every brush: the terrain it paints on and the settings of its stroke. The brush itself runs its
 * own {@link Brush#apply}; a scene only describes the input.
 */
final class BrushScenes {

    private BrushScenes() {
    }

    /**
     * @param registry the brushes in the order of the registry
     * @param terrain  the block fixtures the scenes are built from
     * @return one scene per brush, in the same order
     * @throws IllegalStateException if a brush has no scene, the message names the brush
     */
    static List<BrushScene> all(List<Brush> registry, Terrain terrain) {
        return registry.stream().map(brush -> sceneOf(brush, terrain)).toList();
    }

    /**
     * @param brush   the brush to run
     * @param terrain the block fixtures the scene is built from
     * @return the scene of the brush, with the stroke already applied to {@code after}
     * @throws IllegalStateException if the brush has no scene or the stroke changes no block
     */
    static BrushScene sceneOf(Brush brush, Terrain terrain) {
        Recipe recipe = recipeOf(brush);
        MemoryExtent before = recipe.world().apply(terrain);
        MemoryExtent after = recipe.world().apply(terrain);
        StrokeContext context = recipe.stroke().apply(terrain);

        brush.apply(after, context);

        BrushScene scene = new BrushScene(brush, before, after, context.target(), context.blocks());
        if (scene.changes().isEmpty()) {
            throw new IllegalStateException("the illustration scene of " + brush.getName() + " changes no block");
        }
        return scene;
    }

    private static Recipe recipeOf(Brush brush) {
        return switch (brush) {
            case SphereBrush _ -> new Recipe(BrushScenes::dirtGround,
                    terrain -> stroke().blocks(terrain.stone, terrain.andesite, terrain.gold).size(5).build());
            case SprayBrush _ -> new Recipe(Terrain::flat,
                    terrain -> stroke().blocks(terrain.gold).size(5).chance(50).build());
            case SplatterBrush _ -> new Recipe(Terrain::flat,
                    terrain -> stroke().blocks(terrain.gold).size(5).falloff(2).build());
            case DiscBrush _ -> new Recipe(Terrain::flat,
                    terrain -> stroke().blocks(terrain.gold).size(5).axis(Axis.X).build());
            case BucketBrush _ -> new Recipe(terrain -> terrain.flatWithTop(terrain.grass),
                    terrain -> stroke().blocks(terrain.gold).size(5).build());
            case AngleBrush _ -> new Recipe(Terrain::step,
                    terrain -> stroke().blocks(terrain.gold).size(6).angle(2, 40).build());
            case OverlayBrush _ -> new Recipe(terrain -> terrain.flatWithTop(terrain.grass),
                    terrain -> stroke().blocks(terrain.gold).size(5).thickness(2).build());
            case UnderlayBrush _ -> new Recipe(terrain -> terrain.flatWithTop(terrain.grass),
                    terrain -> stroke().blocks(terrain.gold).size(5).thickness(2).build());
            case FractureBrush _ -> new Recipe(Terrain::trench,
                    terrain -> stroke().target(BlockVector3.at(0, Terrain.SURFACE - 3, 0)).blocks(terrain.gold).size(5)
                            .fractureDistance(2).build());
            case GradientBrush _ -> new Recipe(BrushScenes::dirtGround,
                    terrain -> stroke().blocks(terrain.stone, terrain.andesite).size(5).mixing(2).falloff(2).build());
            case PaintBrush _ -> new Recipe(Terrain::flat, BrushScenes::paintStroke);
            default -> throw new IllegalStateException("no illustration scene for the brush " + brush.getName());
        };
    }

    /**
     * Dirt instead of stone, as deep as the flat terrain. The brushes that place stone or andesite need a block they
     * can replace: stone placed on stone changes nothing. Unchanged blocks are drawn grey whatever their type, so
     * the picture only shows the difference this makes to the stone palette.
     */
    private static MemoryExtent dirtGround(Terrain terrain) {
        return terrain.empty().fill(BlockVector3.at(-12, Terrain.MIN_Y, -12), BlockVector3.at(12, Terrain.SURFACE, 12),
                terrain.dirt);
    }

    /**
     * A paint stroke of six clicks along a fixed curve on the top layer of the flat terrain. The last point is the
     * target.
     */
    private static StrokeContext paintStroke(Terrain terrain) {
        List<BlockVector3> points = List.of(
                BlockVector3.at(-6, Terrain.SURFACE, -4),
                BlockVector3.at(-3, Terrain.SURFACE, -1),
                BlockVector3.at(-1, Terrain.SURFACE, 3),
                BlockVector3.at(2, Terrain.SURFACE, 2),
                BlockVector3.at(4, Terrain.SURFACE, -2),
                BlockVector3.at(6, Terrain.SURFACE, 0));
        return stroke().points(points.toArray(BlockVector3[]::new)).target(points.getLast())
                .blocks(terrain.gold).size(5).falloff(2).build();
    }

    /**
     * The terrain of a scene and the stroke that runs on it.
     */
    private record Recipe(Function<Terrain, MemoryExtent> world, Function<Terrain, StrokeContext> stroke) {
    }
}
