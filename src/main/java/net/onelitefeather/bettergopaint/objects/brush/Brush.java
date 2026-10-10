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

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.MaskingExtent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import net.kyori.adventure.audience.Audience;
import net.onelitefeather.bettergopaint.brush.StrokeContext;
import net.onelitefeather.bettergopaint.utils.Surface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * A BetterGoPaint brush. A brush instance only describes the brush. The state of one stroke lives in a
 * {@link StrokeContext}, and the brush reads and writes blocks only through the {@link Extent} it is given.
 * <p>
 * {@link #bind(StrokeContext)} turns a brush and a context into WorldEdit's own
 * {@link com.sk89q.worldedit.command.tool.brush.Brush}.
 */
public abstract class Brush {

    private final String name;
    private final String description;
    private final String head;

    protected Brush(@NotNull String name, @NotNull String description, @NotNull String head) {
        this.description = description;
        this.name = name;
        this.head = head;
    }

    /**
     * Retrieves the description of the brush.
     *
     * @return The description of the brush.
     */
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * Retrieves the head of the brush.
     *
     * @return The head of the brush.
     */
    public @NotNull String getHead() {
        return head;
    }

    /**
     * Retrieves the name of the brush.
     *
     * @return The name of the brush.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Called on the player's thread for every click before a stroke is queued. Most brushes paint on
     * every click and return the context unchanged. A brush that needs several clicks collects them here.
     *
     * @param playerId the unique id of the player
     * @param sneaking whether the player is sneaking
     * @param feedback where messages for the player go
     * @param context  the context captured for this click
     * @return the context of the stroke to queue, or null if no stroke should run yet
     */
    public @Nullable StrokeContext prepare(
            @NotNull UUID playerId,
            boolean sneaking,
            @NotNull Audience feedback,
            @NotNull StrokeContext context
    ) {
        return context;
    }

    /**
     * Computes the stroke and writes it to the extent. Every read happens before the first block is
     * written, so the result does not depend on the order of the writes.
     *
     * @param extent  the extent to read from and write to
     * @param context the context of the stroke
     */
    public abstract void apply(@NotNull Extent extent, @NotNull StrokeContext context);

    /**
     * Binds this brush to the context of one stroke.
     *
     * @param context the context of the stroke
     * @return the stroke as a WorldEdit brush
     */
    public final @NotNull com.sk89q.worldedit.command.tool.brush.Brush bind(@NotNull StrokeContext context) {
        return new Stroke(this, context);
    }

    /**
     * Wraps the extent so that no block the mask rejects is written. A stroke runs against the edit session
     * of the player, which already carries the global mask. This makes the rule hold for any extent.
     *
     * @param extent the extent to write to
     * @param mask   the mask of the player's session, or null
     * @return the extent the brush should use
     */
    static @NotNull Extent masked(@NotNull Extent extent, @Nullable Mask mask) {
        return mask == null ? extent : new MaskingExtent(extent, mask);
    }

    /**
     * Writes a block of the palette to the position.
     *
     * @param extent   the extent to write to
     * @param context  the context of the stroke
     * @param position the position to change
     */
    protected void place(@NotNull Extent extent, @NotNull StrokeContext context, @NotNull BlockVector3 position) {
        extent.setBlock(position, context.palette().applyBlock(position));
    }

    /**
     * Writes a block of the palette to every position.
     *
     * @param extent    the extent to write to
     * @param context   the context of the stroke
     * @param positions the positions to change
     */
    protected void placeAll(@NotNull Extent extent, @NotNull StrokeContext context, @NotNull List<BlockVector3> positions) {
        for (BlockVector3 position : positions) {
            place(extent, context, position);
        }
    }

    /**
     * Writes the default state of a block type to the position.
     *
     * @param extent   the extent to write to
     * @param type     the block type to place
     * @param position the position to change
     */
    protected void place(@NotNull Extent extent, @NotNull BlockType type, @NotNull BlockVector3 position) {
        extent.setBlock(position, type.getDefaultState());
    }

    /**
     * Checks if a position passes the default checks defined by the stroke context.
     *
     * @param extent   the extent to read from
     * @param context  the context of the stroke
     * @param position the position being checked
     * @return true if the position passes all the default checks, false otherwise
     */
    protected boolean passesDefaultChecks(@NotNull Extent extent, @NotNull StrokeContext context,
                                          @NotNull BlockVector3 position) {
        return context.passesMask(extent, position) && passesSurfaceCheck(extent, context, position);
    }

    /**
     * Checks if a position passes the surface check defined by the stroke context.
     *
     * @param extent   the extent to read from
     * @param context  the context of the stroke
     * @param position the position being checked
     * @return true if the position passes the surface check, false otherwise
     */
    protected boolean passesSurfaceCheck(@NotNull Extent extent, @NotNull StrokeContext context,
                                         @NotNull BlockVector3 position) {
        return Surface.isOnSurface(extent, position, context.surfaceMode(), context.eye());
    }

    /**
     * The stroke of a brush as WorldEdit sees it.
     */
    private static final class Stroke implements com.sk89q.worldedit.command.tool.brush.Brush {

        private final Brush brush;
        private final StrokeContext context;

        private Stroke(@NotNull Brush brush, @NotNull StrokeContext context) {
            this.brush = brush;
            this.context = context;
        }

        @Override
        public void build(EditSession session, BlockVector3 position, Pattern pattern, double size)
                throws MaxChangedBlocksException {
            brush.apply(masked(session, session.getMask()), context.with(position, pattern, (int) Math.round(size)));
        }
    }

}
