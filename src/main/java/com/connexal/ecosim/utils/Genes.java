package com.connexal.ecosim.utils;

public class Genes {
    public static final int DEFAULT_SENSE_RADIUS = 5;
    public static final int DEFAULT_GESTATION_COOLDOWN = 20;

    public static double getGeneValue(double parent1, double parent2) {
        double chosen = Math.random() < 0.5 ? parent1 : parent2;
        double mutation = (Math.random() / 5) - 0.1; //random percentage between -10% and 10%
        return chosen * (1 + mutation);
    }
}
