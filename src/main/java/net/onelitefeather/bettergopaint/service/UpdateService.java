package net.onelitefeather.bettergopaint.service;

import com.github.zafarkhaja.semver.Version;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.onelitefeather.bettergopaint.BetterGoPaint;
import net.onelitefeather.bettergopaint.objects.other.Settings;
import net.onelitefeather.bettergopaint.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public final class UpdateService implements Runnable {
    private final HttpClient hangarClient = HttpClient.newBuilder().build();
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateService.class);
    private final Version localVersion;
    private Version remoteVersion;
    private final BukkitTask scheduler;
    private final String DOWNLOAD_URL = "https://hangar.papermc.io/OneLiteFeather/BetterGoPaint/versions/%s";

    public UpdateService(BetterGoPaint betterGoPaint) {
        this.localVersion = Version.parse(betterGoPaint.getPluginMeta().getVersion());
        this.scheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(betterGoPaint, this, 0, 20 * 60 * 60 * 3);
    }


    @Override
    public void run() {
        var remoteVersion = getNewerVersion();
        if (remoteVersion != null) {
            this.remoteVersion = remoteVersion;
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.isOp() || onlinePlayer.hasPermission(Constants.PERMISSION_NOTIFY_UPDATE)) {
                    notifyPlayer(localVersion, remoteVersion, onlinePlayer);
                }
            }
        }
    }

    public void notifyConsole(ComponentLogger logger) {
        if (this.remoteVersion != null && this.remoteVersion.isHigherThan(this.localVersion)) {
            logger.warn(Component.translatable("bettergopaint.notify.update.console")
                    .arguments(Component.text(localVersion.toString()),
                            Component.text(remoteVersion.toString()),
                            Component.text(DOWNLOAD_URL.formatted(remoteVersion.toString()))
                    ));
        }
    }


    public void notifyPlayer(Player player) {
        if (this.remoteVersion != null && this.remoteVersion.isHigherThan(this.localVersion)) {
            notifyPlayer(this.localVersion, this.remoteVersion, player);
        }
    }


    private void notifyPlayer(Version localVersion, Version remoteVersion, Player player) {
        player.sendMessage(Component.translatable("bettergopaint.notify.update.player")
                .arguments(
                        MiniMessage.miniMessage().deserialize(Settings.settings().generic.PREFIX),
                        Component.text(localVersion.toString()),
                        Component.text(remoteVersion.toString())
                ));
    }


    @Nullable
    private Version getNewerVersion() {
        try {
            HttpResponse<String> httpResponse = hangarClient.send(Constants.LATEST_RELEASE_VERSION_REQUEST, HttpResponse.BodyHandlers.ofString());
            Version remoteVersion = Version.parse(httpResponse.body());
            if (remoteVersion.isHigherThan(this.localVersion)) {
                return remoteVersion;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Something went wrong to check updates", e);
        }
        return null;
    }

    public void shutdown() {
        this.hangarClient.shutdownNow();
        this.scheduler.cancel();
    }
}
