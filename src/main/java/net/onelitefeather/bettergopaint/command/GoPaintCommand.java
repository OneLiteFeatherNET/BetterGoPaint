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
package net.onelitefeather.bettergopaint.command;

import net.kyori.adventure.text.Component;
import net.onelitefeather.bettergopaint.BetterGoPaint;
import net.onelitefeather.bettergopaint.brush.PlayerBrush;
import net.onelitefeather.bettergopaint.objects.other.Settings;
import net.onelitefeather.bettergopaint.utils.Constants;
import net.onelitefeather.bettergopaint.utils.Messages;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The {@code /gopaint} command tree. Every subcommand needs {@link Constants#USE_PERMISSION} and its own node.
 */
public final class GoPaintCommand {

    private final BetterGoPaint plugin;
    private final MinecraftHelp<Source> help;

    public GoPaintCommand(@NotNull BetterGoPaint plugin, @NotNull MinecraftHelp<Source> help) {
        this.plugin = plugin;
        this.help = help;
    }

    @Command("gopaint|gp|bgp")
    @CommandDescription("Shows the BetterGoPaint commands")
    @Permission(value = {Constants.USE_PERMISSION, Constants.HELP_PERMISSION}, mode = Permission.Mode.ALL_OF)
    public void root(Source sender) {
        help.queryCommands("", sender);
    }

    @Command("gopaint|gp|bgp help [query]")
    @CommandDescription("Shows the BetterGoPaint commands")
    @Permission(value = {Constants.USE_PERMISSION, Constants.HELP_PERMISSION}, mode = Permission.Mode.ALL_OF)
    public void help(Source sender, @Argument("query") String query) {
        help.queryCommands(query == null ? "" : query, sender);
    }

    @Command("gopaint|gp|bgp size <size>")
    @CommandDescription("Sets the size of your brush")
    @Permission(value = {Constants.USE_PERMISSION, Constants.SIZE_PERMISSION}, mode = Permission.Mode.ALL_OF)
    public void size(PlayerSource sender, @Argument(value = "size", suggestions = "brush-sizes") int size) {
        Player player = sender.source();
        int maxSize = Settings.settings().generic.MAX_SIZE;
        if (!BrushSizes.isValid(size, maxSize)) {
            player.sendMessage(Component.translatable(
                    "bettergopaint.command.size.out_of_range",
                    Messages.prefix(),
                    Component.text(BrushSizes.MIN_SIZE),
                    Component.text(maxSize)
            ));
            return;
        }
        PlayerBrush brush = plugin.getBrushManager().getBrush(player);
        brush.setSize(size);
        player.sendMessage(Component.translatable(
                "bettergopaint.command.size.set",
                Messages.prefix(),
                Component.text(brush.size())
        ));
    }

    @Suggestions("brush-sizes")
    public List<String> brushSizes(CommandContext<Source> context, CommandInput input) {
        return BrushSizes.suggestions(input.lastRemainingToken(), Settings.settings().generic.MAX_SIZE);
    }

    @Command("gopaint|gp|bgp toggle")
    @CommandDescription("Enables or disables your brush")
    @Permission(value = {Constants.USE_PERMISSION, Constants.TOGGLE_PERMISSION}, mode = Permission.Mode.ALL_OF)
    public void toggle(PlayerSource sender) {
        Player player = sender.source();
        PlayerBrush brush = plugin.getBrushManager().getBrush(player);
        brush.toggle();
        player.sendMessage(brush.enabled()
                ? Component.translatable("bettergopaint.command.toggle.enabled", Messages.prefix())
                : Component.translatable("bettergopaint.command.toggle.disabled", Messages.prefix()));
    }

    @Command("gopaint|gp|bgp info")
    @CommandDescription("Shows who made BetterGoPaint")
    @Permission(value = {Constants.USE_PERMISSION, Constants.INFO_PERMISSION}, mode = Permission.Mode.ALL_OF)
    public void info(Source sender) {
        sender.source().sendMessage(Component.translatable(
                "bettergopaint.command.info",
                Messages.prefix(),
                Component.text(plugin.getPluginMeta().getVersion())
        ));
    }

    @Command("gopaint|gp|bgp reload")
    @CommandDescription("Reloads config.yml")
    @Permission(value = {Constants.USE_PERMISSION, Constants.RELOAD_PERMISSION}, mode = Permission.Mode.ALL_OF)
    public void reload(Source sender) {
        plugin.reloadConfig();
        sender.source().sendMessage(Component.translatable("bettergopaint.command.reload.done", Messages.prefix()));
    }
}
