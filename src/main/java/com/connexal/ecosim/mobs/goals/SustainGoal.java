package com.connexal.ecosim.mobs.goals;

import com.connexal.ecosim.mobs.SimGoal;
import com.connexal.ecosim.mobs.SimMob;
import net.kyori.adventure.util.TriState;

public class SustainGoal extends SimGoal {
    private static final double DANGER_THRESHOLD = 0.2;

    private TriState eating = TriState.NOT_SET;

    public SustainGoal(SimMob mob) {
        super(mob);
    }

    @Override
    public boolean shouldActivate() {
        return this.mob.getHunger() > DANGER_THRESHOLD || this.mob.getThirst() > DANGER_THRESHOLD;
    }

    @Override
    public void start() {
        this.eating = this.mob.getHunger() > this.mob.getThirst() ? TriState.TRUE : TriState.FALSE;
    }

    @Override
    public void stop() {
        this.eating = TriState.NOT_SET;
    }

    @Override
    public void tick() {
        //TODO: Implement
    }
}
