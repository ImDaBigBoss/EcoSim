package com.connexal.ecosim.mobs;

public abstract class SimGoal {
    protected final SimMob mob;

    public SimGoal(SimMob mob) {
        this.mob = mob;
    }

    public abstract boolean shouldActivate();

    public abstract void start();

    public abstract void stop();

    public abstract void tick();
}
