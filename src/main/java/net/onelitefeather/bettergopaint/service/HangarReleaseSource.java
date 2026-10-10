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
package net.onelitefeather.bettergopaint.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Asks Hangar for the version string of the latest BetterGoPaint release.
 * Every failure is reported as an empty result, never as an exception.
 */
public final class HangarReleaseSource implements Supplier<Optional<String>>, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HangarReleaseSource.class);
    private static final URI LATEST_RELEASE_URI =
            URI.create("https://hangar.papermc.io/api/v1/projects/BetterGoPaint/latestrelease");
    private static final HttpRequest LATEST_RELEASE_REQUEST = HttpRequest.newBuilder(LATEST_RELEASE_URI)
            .timeout(Duration.ofSeconds(10))
            .GET()
            .build();

    private final Supplier<HttpClient> clientFactory;
    private @Nullable HttpClient client;

    public HangarReleaseSource() {
        this(() -> HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build());
    }

    HangarReleaseSource(@NotNull Supplier<HttpClient> clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public Optional<String> get() {
        try {
            HttpResponse<String> response = client().send(LATEST_RELEASE_REQUEST, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                LOGGER.debug("Update check: Hangar answered with status {}", response.statusCode());
                return Optional.empty();
            }
            return Optional.of(response.body());
        } catch (IOException e) {
            LOGGER.debug("Update check: Hangar is not reachable ({})", e.getMessage());
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    /**
     * Creates the client on the first request, so a plugin that never checks for updates never allocates one.
     */
    private synchronized HttpClient client() {
        if (client == null) {
            client = clientFactory.get();
        }
        return client;
    }

    @Override
    public synchronized void close() {
        if (client != null) {
            client.shutdownNow();
            client = null;
        }
    }
}
