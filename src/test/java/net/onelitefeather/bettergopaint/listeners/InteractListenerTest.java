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
package net.onelitefeather.bettergopaint.listeners;

import net.onelitefeather.bettergopaint.brush.PlayerBrush;
import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.settings.Settings;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.utils.Constants;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InteractListenerTest {

    private final PlayerBrushManager brushManager = mock(PlayerBrushManager.class);
    private final Player player = mock(Player.class);
    private final ItemStack feather = mock(ItemStack.class);
    private final World world = mock(World.class);
    private final Block clicked = mock(Block.class);
    private InteractListener listener;

    @BeforeEach
    void setUp() {
        Settings defaults = Settings.defaults();
        Settings settings = new Settings(
                new Settings.Generic(List.of(), Material.FEATHER, 100, 10, 50, defaults.generic().defaultAxis(), 50, 50,
                        "", false, List.of("world_nether"), true, true, Material.SPONGE, defaults.generic().surfaceMode()),
                defaults.thickness(), defaults.angle(), defaults.fracture());
        listener = new InteractListener(brushManager, SettingsHolder.of(settings));
        when(feather.getType()).thenReturn(Material.FEATHER);
        when(clicked.getLocation()).thenAnswer(invocation -> new Location(world, 0, 64, 0));
        PlayerBrush brush = mock(PlayerBrush.class);
        when(brushManager.getBrush(player)).thenReturn(brush);
    }

    private PlayerInteractEvent rightClick() {
        return new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, feather, clicked, BlockFace.UP);
    }

    @Test
    @DisplayName("players without bettergopaint.use cannot paint")
    void permissionIsRequired() {
        listener.onClick(rightClick());

        verify(brushManager, never()).getBrush(any());
        verify(feather, never()).hasItemMeta();
    }

    @Test
    @DisplayName("painting in a disabled world is ignored")
    void disabledWorldIsIgnored() {
        when(player.hasPermission(Constants.USE_PERMISSION)).thenReturn(true);
        when(world.getName()).thenReturn("world_nether");

        listener.onClick(rightClick());

        verify(brushManager, never()).getBrush(any());
    }

    @Test
    @DisplayName("the bypass permission allows painting in a disabled world")
    void bypassAllowsDisabledWorld() {
        when(player.hasPermission(Constants.USE_PERMISSION)).thenReturn(true);
        when(player.hasPermission(Constants.WORLD_BYPASS_PERMISSION)).thenReturn(true);
        when(world.getName()).thenReturn("world_nether");

        listener.onClick(rightClick());

        verify(brushManager).getBrush(player);
    }

    @Test
    @DisplayName("painting in a normal world uses the player's brush")
    void normalWorldUsesPlayerBrush() {
        when(player.hasPermission(Constants.USE_PERMISSION)).thenReturn(true);
        when(world.getName()).thenReturn("world");

        listener.onClick(rightClick());

        verify(brushManager).getBrush(player);
    }
}
