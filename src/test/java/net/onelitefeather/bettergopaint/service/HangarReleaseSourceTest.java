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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HangarReleaseSourceTest {

    private final HttpClient client = mock(HttpClient.class);
    private final AtomicInteger created = new AtomicInteger();
    private final HangarReleaseSource source = new HangarReleaseSource(() -> {
        created.incrementAndGet();
        return client;
    });

    @Test
    @DisplayName("creating the source creates no HTTP client")
    void constructionCreatesNoClient() {
        assertThat(created).as("clients created by the constructor").hasValue(0);
    }

    @Test
    @DisplayName("closing a source that never asked for a release creates no HTTP client")
    void closingUnusedSourceCreatesNoClient() {
        source.close();

        assertThat(created).as("clients created by close").hasValue(0);
    }

    @Test
    @DisplayName("the first release request creates one HTTP client and later requests reuse it")
    void firstRequestCreatesOneClient() throws Exception {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("2.1.0");
        doReturn(response).when(client).send(any(), any());

        Optional<String> first = source.get();
        Optional<String> second = source.get();

        assertThat(first).as("first answer").contains("2.1.0");
        assertThat(second).as("second answer").contains("2.1.0");
        assertThat(created).as("clients created for two requests").hasValue(1);
    }

    @Test
    @DisplayName("closing a source shuts down the HTTP client it created")
    void closingShutsDownTheCreatedClient() throws Exception {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(404);
        doReturn(response).when(client).send(any(), any());
        source.get();

        source.close();

        verify(client).shutdownNow();
    }
}
