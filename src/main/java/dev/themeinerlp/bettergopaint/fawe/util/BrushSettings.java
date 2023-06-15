/*
 * BetterGoPaint is designed to simplify painting inside of Minecraft.
 * Copyright (C) TheMeinerLP
 * Copyright (C) OneLiteFeather
 * Copyright (C) OneLiteFeather team and contributors
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
package dev.themeinerlp.bettergopaint.fawe.util;

import org.bukkit.Axis;

public class BrushSettings {
    public boolean surfaceEnabled;
    public boolean maskEnabled;
    public int chance;
    public int thickness;
    public int fractureDistance;
    public int angleDistance;
    public int falloffStrength;
    public int mixingStrength;
    public Axis axis;

    public double angleHeightDifference;

    public BrushSettings(boolean surfaceEnabled, boolean maskEnabled, int chance, int thickness, int fractureDistance, int angleDistance, int falloffStrength, int mixingStrength, Axis axis, double angleHeightDifference) {
        this.surfaceEnabled = surfaceEnabled;
        this.maskEnabled = maskEnabled;
        this.chance = chance;
        this.thickness = thickness;
        this.fractureDistance = fractureDistance;
        this.angleDistance = angleDistance;
        this.falloffStrength = falloffStrength;
        this.mixingStrength = mixingStrength;
        this.axis = axis;
        this.angleHeightDifference = angleHeightDifference;
    }
}
