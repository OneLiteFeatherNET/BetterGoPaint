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
