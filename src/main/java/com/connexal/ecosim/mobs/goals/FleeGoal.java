package com.connexal.ecosim.mobs.goals;

import com.connexal.ecosim.EcoSim;
import com.connexal.ecosim.mobs.SimGoal;
import com.connexal.ecosim.mobs.SimMob;
import com.connexal.ecosim.mobs.SimMobType;
import com.connexal.ecosim.mobs.SimMobs;
import com.connexal.ecosim.utils.Locations;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

public class FleeGoal extends SimGoal {
    private static final double FLEE_SPEED_MULTIPLIER = 2.0;

    private final List<SimMobType> predators;
    private SimMob closestPredator = null;

    public FleeGoal(SimMob mob) {
        super(mob);

        this.predators = SimMobs.predators.get(mob.getType());
    }

    @Override
    public boolean shouldActivate() {
        return this.isNearDanger();
    }

    @Override
    public void start() {
        //Nothing
    }

    @Override
    public void stop() {
        //Nothing
    }

    @Override
    public void tick() {
        if (!this.isNearDanger()) {
            return;
        }

        Vector direction = this.mob.getLocation().toVector().subtract(this.closestPredator.getLocation().toVector()).normalize();
        Location targetLocation = this.mob.getLocation().add(direction.multiply(this.mob.getSenseRadius()));
        Locations.fix(targetLocation);

        this.mob.move(targetLocation, FLEE_SPEED_MULTIPLIER);
    }

    private boolean isNearDanger() {
        Map<SimMob, Location> nearbyEntities = this.mob.getSurroundingMobs();

        Location closestLocation = null;
        SimMob closestEntity = null;

        for (Map.Entry<SimMob, Location> entry : nearbyEntities.entrySet()) {
            if (!this.predators.contains(entry.getKey().getType())) {
                continue;
            }

            if (closestLocation == null || entry.getValue().distance(this.mob.getLocation()) < closestLocation.distance(this.mob.getLocation())) {
                closestLocation = entry.getValue();
                closestEntity = entry.getKey();
            }
        }

        this.closestPredator = closestEntity;
        return closestEntity != null;
    }
}
