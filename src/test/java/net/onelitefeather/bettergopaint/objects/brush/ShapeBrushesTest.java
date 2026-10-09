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

import com.sk89q.worldedit.math.BlockVector3;
import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.support.MemoryExtent;
import net.onelitefeather.bettergopaint.support.Terrain;
import org.bukkit.Axis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.onelitefeather.bettergopaint.support.BrushAssertions.sphereOf;
import static net.onelitefeather.bettergopaint.support.StrokeBuilder.stroke;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The brushes that shape their result from a sphere around the target.
 */
class ShapeBrushesTest {

    private final Terrain terrain = new Terrain();
    private final BlockVector3 target = Terrain.surface();

    private Set<BlockVector3> painted(MemoryExtent extent) {
        return extent.positionsOf(terrain.gold);
    }

    @Test
    @DisplayName("sphere brush paints every solid block of the sphere and nothing else")
    void sphereBrush() {
        MemoryExtent extent = terrain.flat();
        StrokeContext context = stroke().blocks(terrain.gold).size(6).build();

        new SphereBrush().apply(extent, context);

        assertThat(painted(extent)).isEqualTo(sphereOf(extent, target, 6, terrain.gold));
        assertThat(painted(extent)).isNotEmpty().allMatch(position -> position.y() <= Terrain.SURFACE);
        assertThat(extent.written()).isEqualTo(painted(extent));
    }

    @Test
    @DisplayName("sphere brush skips air")
    void sphereBrushSkipsAir() {
        MemoryExtent extent = terrain.empty();

        new SphereBrush().apply(extent, stroke().blocks(terrain.gold).build());

        assertThat(extent.written()).isEmpty();
    }

    @Test
    @DisplayName("spray brush paints a share of the sphere that follows the chance")
    void sprayBrush() {
        Set<BlockVector3> sphere = sphereOf(terrain.flat(), target, 8, terrain.stone);

        MemoryExtent none = terrain.flat();
        new SprayBrush().apply(none, stroke().blocks(terrain.gold).size(8).chance(0).build());
        MemoryExtent all = terrain.flat();
        new SprayBrush().apply(all, stroke().blocks(terrain.gold).size(8).chance(100).build());
        MemoryExtent half = terrain.flat();
        new SprayBrush().apply(half, stroke().blocks(terrain.gold).size(8).chance(50).build());

        assertThat(painted(none)).isEmpty();
        assertThat(painted(all)).isEqualTo(sphere);
        assertThat(painted(half)).isNotEmpty().isSubsetOf(sphere).hasSizeBetween(sphere.size() / 4, sphere.size() * 3 / 4);
    }

    @Test
    @DisplayName("spray brush with the same seed paints the same blocks")
    void sprayBrushIsDeterministic() {
        MemoryExtent first = terrain.flat();
        MemoryExtent second = terrain.flat();
        MemoryExtent other = terrain.flat();

        new SprayBrush().apply(first, stroke().blocks(terrain.gold).size(8).seed(5).build());
        new SprayBrush().apply(second, stroke().blocks(terrain.gold).size(8).seed(5).build());
        new SprayBrush().apply(other, stroke().blocks(terrain.gold).size(8).seed(6).build());

        assertThat(painted(first)).isEqualTo(painted(second));
        assertThat(painted(first)).isNotEqualTo(painted(other));
    }

    @Test
    @DisplayName("splatter brush without falloff paints the whole sphere")
    void splatterWithoutFalloff() {
        MemoryExtent extent = terrain.flat();

        new SplatterBrush().apply(extent, stroke().blocks(terrain.gold).size(8).falloff(0).build());

        assertThat(painted(extent)).isEqualTo(sphereOf(extent, target, 8, terrain.gold)).isNotEmpty();
    }

    @Test
    @DisplayName("splatter brush with full falloff paints more blocks near the target")
    void splatterWithFalloff() {
        MemoryExtent extent = terrain.flat();
        Set<BlockVector3> sphere = sphereOf(extent, target, 12, terrain.stone);

        new SplatterBrush().apply(extent, stroke().blocks(terrain.gold).size(12).falloff(100).build());

        Set<BlockVector3> painted = painted(extent);
        assertThat(painted).isNotEmpty().isSubsetOf(sphere).hasSizeLessThan(sphere.size());
        double paintedDistance = painted.stream().mapToDouble(position -> position.distance(target)).average().orElseThrow();
        double sphereDistance = sphere.stream().mapToDouble(position -> position.distance(target)).average().orElseThrow();
        assertThat(paintedDistance).isLessThan(sphereDistance);
    }

    @Test
    @DisplayName("disc brush paints one layer of the sphere on the axis")
    void discBrush() {
        for (Axis axis : Axis.values()) {
            MemoryExtent extent = terrain.flat();
            StrokeContext context = stroke().blocks(terrain.gold).size(8).axis(axis).build();

            new DiscBrush().apply(extent, context);

            Set<BlockVector3> painted = painted(extent);
            assertThat(painted).as("disc on %s", axis).isNotEmpty()
                    .allMatch(position -> switch (axis) {
                        case X -> position.x() == target.x();
                        case Y -> position.y() == target.y();
                        case Z -> position.z() == target.z();
                    });
            assertThat(painted).isSubsetOf(sphereOf(extent, target, 8, terrain.gold));
        }
    }

    @Test
    @DisplayName("disc brush on the y axis covers the full circle of the surface layer")
    void discBrushCircle() {
        MemoryExtent extent = terrain.flat();

        new DiscBrush().apply(extent, stroke().blocks(terrain.gold).size(8).axis(Axis.Y).build());

        assertThat(painted(extent)).contains(target, target.add(3, 0, 0), target.add(0, 0, -3), target.add(-2, 0, 2))
                .doesNotContain(target.add(4, 0, 0), target.add(3, 0, 3));
    }

    @Test
    @DisplayName("bucket brush paints connected blocks of the target type only")
    void bucketBrush() {
        MemoryExtent extent = terrain.flat()
                .set(0, Terrain.SURFACE, 0, terrain.dirt)
                .set(1, Terrain.SURFACE, 0, terrain.dirt)
                .set(3, Terrain.SURFACE, 0, terrain.dirt);

        new BucketBrush().apply(extent, stroke().blocks(terrain.gold).size(10).build());

        assertThat(painted(extent)).containsExactlyInAnyOrder(target, target.add(1, 0, 0));
        assertThat(extent.typeAt(3, Terrain.SURFACE, 0)).isSameAs(terrain.dirt);
    }

    @Test
    @DisplayName("bucket brush on plain ground fills the connected part of the sphere")
    void bucketBrushOnGround() {
        MemoryExtent extent = terrain.flat();

        new BucketBrush().apply(extent, stroke().blocks(terrain.gold).size(6).build());

        assertThat(painted(extent)).isEqualTo(sphereOf(extent, target, 6, terrain.gold)).isNotEmpty();
    }

    @Test
    @DisplayName("the brushes keep their names")
    void names() {
        assertThat(new SphereBrush().getName()).isEqualTo("Sphere Brush");
        assertThat(new BucketBrush().getName()).isEqualTo("Bucket Brush");
        assertThat(new PaintBrush(SettingsHolder.of(net.onelitefeather.bettergopaint.settings.Settings.defaults())).getName())
                .isEqualTo("Paint Brush");
    }
}
