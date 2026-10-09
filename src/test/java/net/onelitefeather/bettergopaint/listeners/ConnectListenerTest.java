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

import net.kyori.adventure.text.Component;
import net.onelitefeather.bettergopaint.BetterGoPaint;
import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.service.UpdateService;
import net.onelitefeather.bettergopaint.utils.Constants;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConnectListenerTest {

    private final UpdateService updateService = mock(UpdateService.class);
    private final Player player = mock(Player.class);
    private ConnectListener listener;

    @BeforeEach
    void setUp() {
        BetterGoPaint plugin = mock(BetterGoPaint.class);
        when(plugin.getUpdateService()).thenReturn(updateService);
        listener = new ConnectListener(mock(PlayerBrushManager.class), plugin);
    }

    private PlayerJoinEvent join() {
        return new PlayerJoinEvent(player, Component.empty());
    }

    @Test
    @DisplayName("players with the update permission are told about updates when they join")
    void adminIsNotifiedOnJoin() {
        when(player.hasPermission(Constants.PERMISSION_NOTIFY_UPDATE)).thenReturn(true);

        listener.onJoin(join());

        verify(updateService).notifyPlayer(player);
    }

    @Test
    @DisplayName("players without the update permission are not told about updates")
    void playerIsNotNotified() {
        listener.onJoin(join());

        verify(updateService, never()).notifyPlayer(any());
    }
}
