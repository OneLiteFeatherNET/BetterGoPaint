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
package net.onelitefeather.bettergopaint.support;

import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BlockMaterial;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Block types for tests without a running server.
 * <p>
 * WorldEdit builds {@link BlockType}s from the block registry of the platform, which needs a Minecraft
 * server, so {@code BlockTypes.STONE} cannot be initialised in a unit test. The brushes only ask a type
 * for its default state and whether its material is air or solid, so a mock that answers those is enough.
 * Mocks compare by identity, which makes two types equal exactly when they are the same instance.
 */
public final class TestBlocks {

    private TestBlocks() {
    }

    /**
     * Creates a block type.
     *
     * @param id    the name used in assertion messages
     * @param air   whether the material is air
     * @param solid whether the material is solid
     */
    public static BlockType type(String id, boolean air, boolean solid) {
        BlockType type = mock(BlockType.class, id);
        BlockState state = mock(BlockState.class, id + " (state)");
        BlockMaterial material = mock(BlockMaterial.class, id + " (material)");
        when(material.isAir()).thenReturn(air);
        when(material.isSolid()).thenReturn(solid);
        when(type.getMaterial()).thenReturn(material);
        when(type.getDefaultState()).thenReturn(state);
        when(state.getBlockType()).thenReturn(type);
        BaseBlock block = block(type);
        when(type.applyBlock(any(BlockVector3.class))).thenReturn(block);
        return type;
    }

    public static BlockType air() {
        return type("air", true, false);
    }

    public static BlockType solid(String id) {
        return type(id, false, true);
    }

    /**
     * Creates a pattern that always answers with the type.
     */
    public static Pattern pattern(BlockType type) {
        BaseBlock block = block(type);
        return position -> block;
    }

    private static BaseBlock block(BlockType type) {
        BaseBlock block = mock(BaseBlock.class);
        when(block.getBlockType()).thenReturn(type);
        return block;
    }
}
