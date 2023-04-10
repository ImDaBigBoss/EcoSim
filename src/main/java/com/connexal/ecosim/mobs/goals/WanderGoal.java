package com.connexal.ecosim.mobs.goals;

import com.connexal.ecosim.mobs.SimGoal;
import com.connexal.ecosim.mobs.SimMob;
import com.connexal.ecosim.utils.Locations;
import org.bukkit.Location;

public class WanderGoal extends SimGoal {
    private Location targetLocation = null;

    public WanderGoal(SimMob mob) {
        super(mob);
    }

    @Override
    public boolean shouldActivate() {
        return true;
    }

    @Override
    public void start() {
        this.targetLocation = this.mob.getLocation().clone().add(Math.random() * 10 - 5, 0, Math.random() * 10 - 5);
        Locations.fix(targetLocation);

        this.mob.move(this.targetLocation, 1);
    }

    @Override
    public void stop() {
        this.targetLocation = null;
    }

    @Override
    public void tick() {
        if (this.targetLocation.distance(this.mob.getLocation()) < 1) {
            this.start();
        }
    }
}
