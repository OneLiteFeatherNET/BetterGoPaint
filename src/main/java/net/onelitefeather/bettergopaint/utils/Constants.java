package net.onelitefeather.bettergopaint.utils;

import java.net.URI;
import java.net.http.HttpRequest;

public final class Constants {

    public static final String USE_PERMISSION = "bettergopaint.use";
    public static final String ADMIN_PERMISSION = "bettergopaint.admin";
    public static final String PERMISSION_NOTIFY_UPDATE = "antiredstoneclockremastered.notify.admin.update";
    public static final String DISABLE_DONATION_NOTIFY = "bettergopaint.notify.disable.donation";
    public static final String RELOAD_PERMISSION = "bettergopaint.command.admin.reload";
    public static final String WORLD_BYPASS_PERMISSION = "bettergopaint.world.bypass";

    public static final URI LATEST_RELEASE_VERSION_URI = URI.create("https://hangar.papermc.io/api/v1/projects/BetterGoPaint/latestrelease");
    public static final HttpRequest LATEST_RELEASE_VERSION_REQUEST = HttpRequest.newBuilder().GET().uri(LATEST_RELEASE_VERSION_URI).build();

}
