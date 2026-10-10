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

import jakarta.inject.Inject;
import net.kyori.adventure.text.Component;
import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.service.UpdateService;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.utils.Constants;
import net.onelitefeather.bettergopaint.utils.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class ConnectListener implements Listener {

    private final PlayerBrushManager brushManager;
    private final UpdateService updateService;
    private final SettingsHolder settings;

    @Inject
    public ConnectListener(
            @NotNull PlayerBrushManager brushManager,
            @NotNull UpdateService updateService,
            @NotNull SettingsHolder settings
    ) {
        this.brushManager = brushManager;
        this.updateService = updateService;
        this.settings = settings;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(@NotNull PlayerQuitEvent event) {
        brushManager.removeBrush(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || (player.hasPermission(Constants.ADMIN_PERMISSION)
                && !player.hasPermission(Constants.DISABLE_DONATION_NOTIFY))) {
            player.sendMessage(Component.translatable("bettergopaint.notify.donation.player", Messages.prefix(settings.get())));
        }
        if (player.isOp() || player.hasPermission(Constants.PERMISSION_NOTIFY_UPDATE)) {
            updateService.notifyPlayer(player);
        }
    }
}
