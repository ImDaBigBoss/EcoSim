package com.connexal.ecosim.utils;

import com.connexal.ecosim.EcoSim;
import org.bukkit.Location;

public class Locations {
    public static void fix(Location location) {
        location.setX(clamp(location.getX(), 0, EcoSim.SIMULATION_WIDTH - 1));
        location.setZ(clamp(location.getZ(), 0, EcoSim.SIMULATION_WIDTH - 1));
        location.setY(EcoSim.HEIGHT_MAP[location.getBlockX()][location.getBlockZ()]);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
