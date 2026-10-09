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

import net.onelitefeather.bettergopaint.objects.brush.Brush;
import net.onelitefeather.bettergopaint.objects.brush.PaintBrush;
import net.onelitefeather.bettergopaint.objects.brush.SphereBrush;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerBrushManagerTest {

    private PlayerBrushManager manager;
    private List<Brush> brushes;

    @BeforeEach
    void setUp() {
        manager = new PlayerBrushManager();
        brushes = manager.getBrushes();
    }

    @Test
    @DisplayName("a player without a brush starts on the first one")
    void cycleForwardWithoutBrushStartsAtFirst() {
        assertThat(manager.cycleForward(null)).isInstanceOf(SphereBrush.class);
        assertThat(manager.cycleBack(null)).isInstanceOf(SphereBrush.class);
    }

    @Test
    @DisplayName("cycling forward walks the list and wraps to the first brush")
    void cycleForwardWraps() {
        assertThat(manager.cycleForward(brushes.getFirst())).isSameAs(brushes.get(1));
        assertThat(manager.cycleForward(brushes.getLast())).isSameAs(brushes.getFirst());
    }

    @Test
    @DisplayName("cycling backwards walks the list and wraps to the last brush")
    void cycleBackWraps() {
        assertThat(manager.cycleBack(brushes.get(1))).isSameAs(brushes.getFirst());
        assertThat(manager.cycleBack(brushes.getFirst())).isSameAs(brushes.getLast());
    }

    @Test
    @DisplayName("every brush can be found again by its name")
    void everyBrushIsFoundByName() {
        for (Brush brush : brushes) {
            assertThat(manager.getBrushHandler(brush.getName()))
                    .as("lookup of %s", brush.getName())
                    .containsSame(brush);
        }
        assertThat(manager.getBrushHandler("Paint Brush")).containsInstanceOf(PaintBrush.class);
    }

    @Test
    @DisplayName("an unknown name finds no brush")
    void unknownNameFindsNothing() {
        assertThat(manager.getBrushHandler("Teleport Brush")).isEmpty();
    }

    @Test
    @DisplayName("an exact name finds exactly that brush")
    void exactNameLookup() {
        for (Brush brush : brushes) {
            assertThat(manager.getBrushByName(brush.getName())).containsSame(brush);
        }
        assertThat(manager.getBrushByName(" ♦ Sphere Brush ♦ ")).isEmpty();
    }
}
