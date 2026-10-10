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

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.extension.platform.Actor;
import org.jetbrains.annotations.NotNull;

/**
 * Runs a stroke inside an edit session of the player's WorldEdit session. The edit session carries the
 * global mask and the limits of the player, and the stroke is remembered as one history entry.
 */
public final class StrokeRunner {

    private StrokeRunner() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Runs the stroke. The actor and the session must be looked up on the player's thread, this method
     * touches nothing from Bukkit and is meant to run in WorldEdit's action queue.
     *
     * @param actor        the actor that paints
     * @param localSession the WorldEdit session of the actor
     * @param stroke       the stroke to run
     * @param context      the context of the stroke
     */
    public static void run(
            @NotNull Actor actor,
            @NotNull LocalSession localSession,
            @NotNull Brush stroke,
            @NotNull StrokeContext context
    ) {
        try (EditSession editSession = localSession.createEditSession(actor)) {
            try {
                stroke.build(editSession, context.target(), context.palette(), context.size());
            } finally {
                localSession.remember(editSession);
            }
        }
    }
}
