package dev.themeinerlp.bettergopaint.command;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import dev.themeinerlp.bettergopaint.BetterGoPaint;
import dev.themeinerlp.bettergopaint.objects.other.Settings;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public final class ReloadCommand {

    private final BetterGoPaint betterGoPaint;

    public ReloadCommand(final BetterGoPaint betterGoPaint) {
        this.betterGoPaint = betterGoPaint;
    }

    @CommandMethod("bgp|gp reload")
    @CommandPermission("bettergopaint.command.admin.reload")
    public void onReload(Player player) {
        betterGoPaint.reload();
        player.sendMessage(MiniMessage.miniMessage().deserialize(Settings.settings().GENERIC.PREFIX + "<red>Reloaded</red>"));
    }

}
