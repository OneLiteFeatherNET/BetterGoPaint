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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemsTest {

    private static final String TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0In19fQ==";

    @Test
    @DisplayName("heads with the same texture get the same profile id")
    void sameTextureGivesSameProfileId() {
        assertThat(Items.profileId(TEXTURE))
                .as("profile id of the same texture twice")
                .isEqualTo(Items.profileId(new String(TEXTURE)));
    }

    @Test
    @DisplayName("heads with different textures get different profile ids")
    void differentTexturesGiveDifferentProfileIds() {
        assertThat(Items.profileId(TEXTURE))
                .as("profile id of another texture")
                .isNotEqualTo(Items.profileId(TEXTURE + "A"));
    }
}
