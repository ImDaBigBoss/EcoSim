package com.connexal.ecosim.mobs;

import org.bukkit.entity.EntityType;

public enum SimMobType {
    RABBIT("Rabbit", EntityType.RABBIT, 500, 500, 5, 1, null),
    FOX("Fox", EntityType.FOX, 500, 500, 10, 1, new SimMobType[] {SimMobType.RABBIT});

    public static boolean isRegistered(EntityType entityType) {
        for (SimMobType type : SimMobType.values()) {
            if (type.getEntityType() == entityType) {
                return true;
            }
        }

        return false;
    }

    private final String name;
    private final EntityType entityType;
    private final int maxSatiation;
    private final int maxHydration;
    private final int speed;
    private final int nourishment;
    private final SimMobType[] prey;

    SimMobType(String name, EntityType entityType, int maxSatiation, int maxHydration, int nourishment, int speed, SimMobType[] prey) {
        this.name = name;
        this.entityType = entityType;
        this.maxSatiation = maxSatiation;
        this.maxHydration = maxHydration;
        this.speed = speed;
        this.nourishment = nourishment;
        this.prey = prey;
    }

    public String getName() {
        return this.name;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public int getMaxSatiation() {
        return this.maxSatiation;
    }

    public int getMaxHydration() {
        return this.maxHydration;
    }

    public int getNourishment() {
        return this.nourishment;
    }

    public int getSpeed() {
        return this.speed;
    }

    public SimMobType[] getPrey() {
        return this.prey;
    }
}
