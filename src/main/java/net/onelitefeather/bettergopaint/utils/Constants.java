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
