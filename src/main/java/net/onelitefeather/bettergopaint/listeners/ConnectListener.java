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
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.onelitefeather.bettergopaint.BetterGoPaint;
import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.objects.other.Settings;
import net.onelitefeather.bettergopaint.utils.Constants;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class ConnectListener implements Listener {

    private final PlayerBrushManager brushManager;
    private final BetterGoPaint betterGoPaint;

    public ConnectListener(@NotNull PlayerBrushManager brushManager, final BetterGoPaint paint) {
        this.brushManager = brushManager;
        this.betterGoPaint = paint;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(@NotNull PlayerQuitEvent event) {
        brushManager.removeBrush(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        var player = event.getPlayer();
        if (player.isOp() || (player.hasPermission(Constants.ADMIN_PERMISSION) && !player.hasPermission(Constants.DISABLE_DONATION_NOTIFY))) {
            player.sendMessage(Component.translatable("bettergopaint.notify.donation.player").arguments(MiniMessage.miniMessage()
                    .deserialize(Settings.settings().generic.PREFIX)));
        }
        if (player.isOp() || player.hasPermission(Constants.PERMISSION_NOTIFY_UPDATE)) {
            this.betterGoPaint.getUpdateService().notifyPlayer(player);
        }
    }
}
