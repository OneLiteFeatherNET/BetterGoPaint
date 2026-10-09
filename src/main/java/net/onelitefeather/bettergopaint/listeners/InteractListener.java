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

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import net.kyori.adventure.text.Component;
import jakarta.inject.Inject;
import net.onelitefeather.bettergopaint.brush.BrushSettings;
import net.onelitefeather.bettergopaint.brush.ExportedBrushes;
import net.onelitefeather.bettergopaint.brush.PlayerBrush;
import net.onelitefeather.bettergopaint.brush.PlayerBrushManager;
import net.onelitefeather.bettergopaint.settings.SettingsHolder;
import net.onelitefeather.bettergopaint.utils.Constants;
import net.onelitefeather.bettergopaint.utils.Messages;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class InteractListener implements Listener {

    private final PlayerBrushManager brushManager;
    private final SettingsHolder settings;

    @Inject
    public InteractListener(@NotNull PlayerBrushManager brushManager, @NotNull SettingsHolder settings) {
        this.brushManager = brushManager;
        this.settings = settings;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission(Constants.USE_PERMISSION)) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        if (event.getAction().isLeftClick() && item.getType() == settings.get().generic().defaultBrush()) {
            PlayerBrush brush = brushManager.getBrush(player);
            player.openInventory(brush.getInventory());
            event.setCancelled(true);
            return;
        }

        if (!event.getAction().isRightClick()) {
            return;
        }

        Location location;
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            Block targetBlock = player.getTargetBlockExact(250, FluidCollisionMode.NEVER);
            if (targetBlock == null) {
                return;
            }
            location = targetBlock.getLocation().clone();
        } else if (event.getClickedBlock() != null) {
            location = event.getClickedBlock().getLocation();
        } else {
            return;
        }

        final boolean hasNotWorldByePassPermission = !player.hasPermission(Constants.WORLD_BYPASS_PERMISSION);

        if (hasNotWorldByePassPermission && settings.get().generic().disabledWorlds()
                .contains(location.getWorld().getName())) {
            return;
        }

        BrushSettings brushSettings;

        Optional<ExportedBrushes.Result> exported = ExportedBrushes.read(item, brushManager, settings.get());
        if (exported.isPresent()) {
            brushSettings = exported.get().brush();
            if (exported.get().converted() && event.getHand() != null) {
                player.getInventory().setItem(event.getHand(), item);
            }
        } else if (item.getType().equals(settings.get().generic().defaultBrush())) {
            brushSettings = brushManager.getBrush(player);
        } else {
            return;
        }

        if (brushSettings == null || brushSettings.blocks().isEmpty()) {
            return;
        }

        if (brushSettings.enabled()) {
            BukkitAdapter.adapt(player).runAction(
                    () -> brushSettings.brush().paint(location, player, brushSettings), false, true
            );
        } else {
            player.sendMessage(Component.translatable("bettergopaint.brush.disabled", Messages.prefix(settings.get())));
        }
    }

}
