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
package net.onelitefeather.bettergopaint.docsgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * The look of the Minecraft blocks in the illustrations: a representative base colour per material and an 8x8 pixel
 * tile for each face, computed in code. The tiles are shades of the base colour with a seed taken from the material
 * name, so they are the same on every run. No texture file of Minecraft or of any other project is read, copied or
 * embedded; the colours are averages chosen by hand.
 */
final class BlockLooks {

    /**
     * The pixels of a tile along one side.
     */
    static final int SIZE = 8;

    private static final String DIRT = "DIRT";
    private static final int MORTAR_DARKEN = 28;
    private static final int RIM_DARKEN = 10;
    private static final List<BlockLook> LOOKS = List.of(
            new BlockLook("GRASS_BLOCK", "#7CBD6B", 14, 10, 8, true, Structure.NONE),
            new BlockLook(DIRT, "#866043", 12, 18, 8, false, Structure.NONE),
            new BlockLook("STONE", "#7D7D7D", 16, 26, 14, false, Structure.NONE),
            new BlockLook("ANDESITE", "#8A8B8C", 30, 6, 12, false, Structure.NONE),
            new BlockLook("COBBLESTONE", "#6E6E6E", 14, 20, 8, false, Structure.MORTAR),
            new BlockLook("GOLD_BLOCK", "#F9D849", 14, 4, 6, false, Structure.RIM),
            new BlockLook("SPONGE", "#C3C04A", 12, 26, 12, false, Structure.NONE));

    /**
     * A pattern drawn over the noise of a material, so that materials of similar colour read as different blocks.
     */
    enum Structure {
        /** Only the noise. */
        NONE,
        /** Dark mortar lines of a cobblestone wall. */
        MORTAR,
        /** A darker outline on the edge pixels of the block. */
        RIM
    }

    private BlockLooks() {
    }

    /**
     * @return the materials with a look, in the order of the legend
     */
    static List<String> materials() {
        return LOOKS.stream().map(BlockLook::material).toList();
    }

    /**
     * @param material the name of the material, for example {@code GRASS_BLOCK}
     * @return whether the material has a look
     */
    static boolean has(String material) {
        return LOOKS.stream().anyMatch(look -> look.material().equals(material));
    }

    /**
     * @param material the name of the material, for example {@code GRASS_BLOCK}
     * @return the look of the material
     * @throws IllegalStateException if the material has no look; the message names the material
     */
    static BlockLook look(String material) {
        return LOOKS.stream()
                .filter(look -> look.material().equals(material))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("no block look for " + material + "; add it to BlockLooks"));
    }

    /**
     * The face of a block that a tile shows.
     */
    enum Face {
        /** The top of a block, seen from above. */
        TOP,
        /** A side of a block. A grass block has its green strip on top of this face. */
        SIDE
    }

    /**
     * The look of one material.
     *
     * @param material     the Minecraft name of the material
     * @param base         the representative colour, {@code #rrggbb}
     * @param lightPercent the share of pixels that are lighter than the base colour, in percent
     * @param darkPercent  the share of pixels that are darker than the base colour, in percent
     * @param spread       how far a light or dark pixel differs from the base colour, in percent of the channel
     * @param grassStrip   whether the side is a grass side: a green strip over dirt
     * @param structure    the pattern drawn over the noise
     */
    record BlockLook(String material, String base, int lightPercent, int darkPercent, int spread,
                     boolean grassStrip, Structure structure) {

        /**
         * @return the name in the legend, the Minecraft name, for example {@code Grass Block}
         */
        String label() {
            StringBuilder label = new StringBuilder();
            for (String word : material.split("_")) {
                if (!label.isEmpty()) {
                    label.append(' ');
                }
                label.append(word.charAt(0)).append(word.substring(1).toLowerCase(Locale.ROOT));
            }
            return label.toString();
        }

        /**
         * @param face the face of the block
         * @return the {@link #SIZE} by {@link #SIZE} pixels of the tile, row by row, each {@code #rrggbb}
         */
        List<String> pixels(Face face) {
            Random random = new Random(seed(face));
            List<String> pixels = new ArrayList<>(SIZE * SIZE);
            for (int row = 0; row < SIZE; row++) {
                for (int column = 0; column < SIZE; column++) {
                    pixels.add(pixel(random, row, column, face));
                }
            }
            return List.copyOf(pixels);
        }

        private String pixel(Random random, int row, int column, Face face) {
            String colour;
            if (grassStrip && face == Face.SIDE && !(row < 2 || (row == 2 && random.nextBoolean()))) {
                colour = look(DIRT).shade(random, look(DIRT).base());
            } else {
                colour = shade(random, base);
            }
            return switch (structure) {
                case NONE -> colour;
                case MORTAR -> mortar(row, column) ? Rgb.of(colour).shifted(-MORTAR_DARKEN).hex() : colour;
                case RIM -> row == 0 || column == 0 || row == SIZE - 1 || column == SIZE - 1
                        ? Rgb.of(colour).shifted(-RIM_DARKEN).hex() : colour;
            };
        }

        /**
         * The mortar of a cobblestone wall: two full rows, and a staggered joint in each band between them.
         */
        private static boolean mortar(int row, int column) {
            return row == 3 || row == 7 || (row < 3 && column == 3) || (row > 3 && row < 7 && column == 6);
        }

        /**
         * One pixel: most pixels keep the base colour, the others are lighter or darker by the spread, and every pixel
         * varies a little.
         */
        private String shade(Random random, String colour) {
            int roll = random.nextInt(100);
            int step;
            if (roll < darkPercent) {
                step = -spread;
            } else if (roll < darkPercent + lightPercent) {
                step = spread;
            } else {
                step = random.nextInt(spread + 1) - spread / 2;
            }
            return Rgb.of(colour).shifted(step).hex();
        }

        private long seed(Face face) {
            long seed = 17;
            for (char character : (material + "/" + face.name()).toCharArray()) {
                seed = seed * 31 + character;
            }
            return seed;
        }
    }

    /**
     * An RGB colour with 8 bits per channel.
     *
     * @param red   the red channel
     * @param green the green channel
     * @param blue  the blue channel
     */
    record Rgb(int red, int green, int blue) {

        /**
         * @param hex a colour as {@code #rrggbb}, in any letter case
         * @return the colour
         */
        static Rgb of(String hex) {
            return new Rgb(
                    Integer.parseInt(hex.substring(1, 3), 16),
                    Integer.parseInt(hex.substring(3, 5), 16),
                    Integer.parseInt(hex.substring(5, 7), 16));
        }

        /**
         * @param percent the change in percent of the channel range; positive moves towards white, negative towards black
         * @return the colour changed by the percent in each channel
         */
        Rgb shifted(int percent) {
            return new Rgb(channel(red, percent), channel(green, percent), channel(blue, percent));
        }

        /**
         * @return the colour as lower case {@code #rrggbb}
         */
        String hex() {
            return String.format(Locale.ROOT, "#%02x%02x%02x", red, green, blue);
        }

        private static int channel(int value, int percent) {
            if (percent >= 0) {
                return value + (255 - value) * percent / 100;
            }
            return value + value * percent / 100;
        }
    }
}
