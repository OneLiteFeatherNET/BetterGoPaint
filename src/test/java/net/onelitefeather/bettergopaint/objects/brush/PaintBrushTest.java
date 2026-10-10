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
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static net.onelitefeather.bettergopaint.support.StrokeBuilder.stroke;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaintBrushTest {

    private final PaintBrush brush = new PaintBrush(SettingsHolder.of(Settings.defaults()));
    private final Audience feedback = mock(Audience.class);
    private final UUID player = UUID.randomUUID();
    private final UUID other = UUID.randomUUID();

    @Test
    @DisplayName("the points of a stroke are collected until the player sneaks to end it")
    void pointsAccumulateUntilSneaking() {
        assertThat(brush.prepare(player, false, feedback, stroke().target(BlockVector3.at(1, 64, 1)).build())).isNull();
        assertThat(brush.prepare(player, false, feedback, stroke().target(BlockVector3.at(2, 64, 2)).build())).isNull();

        StrokeContext finished = brush.prepare(player, true, feedback, stroke().target(BlockVector3.at(3, 64, 3)).build());

        assertThat(finished).isNotNull();
        assertThat(finished.points()).hasSize(3);
    }

    @Test
    @DisplayName("forgetting a player drops only that player's unfinished stroke")
    void forgetDropsOnlyThatPlayer() {
        brush.prepare(player, false, feedback, stroke().target(BlockVector3.at(1, 64, 1)).build());
        brush.prepare(other, false, feedback, stroke().target(BlockVector3.at(5, 64, 5)).build());

        brush.forget(player);

        StrokeContext ownStroke = brush.prepare(player, true, feedback, stroke().target(BlockVector3.at(9, 64, 9)).build());
        assertThat(ownStroke.points()).containsExactly(BlockVector3.at(9, 64, 9));

        StrokeContext otherStroke = brush.prepare(other, true, feedback, stroke().target(BlockVector3.at(6, 64, 6)).build());
        assertThat(otherStroke.points()).hasSize(2);
    }

    private void clickNonSneaking(int times) {
        for (int i = 0; i < times; i++) {
            brush.prepare(player, false, feedback, stroke().target(BlockVector3.at(i, 64, 0)).build());
        }
    }

    @Test
    @DisplayName("the point past the stroke limit is refused and the player gets the limit message")
    void pointPastTheLimitIsRefusedWithMessage() {
        clickNonSneaking(256);
        clearInvocations(feedback);

        StrokeContext refused = brush.prepare(player, false, feedback, stroke().target(BlockVector3.at(999, 64, 999)).build());

        assertThat(refused).as("a refused click does not finish the stroke").isNull();
        ArgumentCaptor<Component> sent = ArgumentCaptor.forClass(Component.class);
        verify(feedback).sendMessage(sent.capture());
        assertThat(sent.getValue()).as("the message of a refused point").isInstanceOf(TranslatableComponent.class);
        assertThat(((TranslatableComponent) sent.getValue()).key())
                .as("the key of the limit message")
                .isEqualTo("bettergopaint.brush.paint.limit");
    }

    @Test
    @DisplayName("sneaking at the stroke limit paints the 256 stored points and not the refused one")
    void sneakingAtTheLimitPaintsTheStoredPoints() {
        clickNonSneaking(256);

        StrokeContext finished = brush.prepare(player, true, feedback, stroke().target(BlockVector3.at(999, 64, 999)).build());

        assertThat(finished).isNotNull();
        assertThat(finished.points()).as("points of the finished stroke").hasSize(256);
        assertThat(finished.points()).as("the refused point").doesNotContain(BlockVector3.at(999, 64, 999));
    }
}
