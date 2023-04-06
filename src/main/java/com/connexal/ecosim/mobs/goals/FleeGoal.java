package com.connexal.ecosim.mobs.goals;

import com.connexal.ecosim.mobs.SimGoal;
import com.connexal.ecosim.mobs.SimMob;
import com.connexal.ecosim.mobs.SimMobType;
import com.connexal.ecosim.mobs.SimMobs;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;

public class FleeGoal extends SimGoal {
    private static final double FLEE_SPEED_MULTIPLIER = 2.0;

    private SimMob closestPredator = null;

    public FleeGoal(SimMob mob) {
        super(mob);
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

        Location targetLocation = this.closestPredator.getLocation().clone().subtract(this.mob.getLocation().toVector()).toLocation(this.mob.getLocation().getWorld());
        while (targetLocation.getBlock().getType().isSolid() && targetLocation.getBlockY() < targetLocation.getWorld().getMaxHeight()) {
            targetLocation.add(0, 1, 0);
        }

        this.mob.move(targetLocation, FLEE_SPEED_MULTIPLIER);
    }

    private boolean isNearDanger() {
        List<SimMobType> dangerTypes = SimMobs.predators.get(this.mob.getType());
        Map<SimMob, Location> nearbyEntities = this.mob.getSurroundingMobs();

        Location closestLocation = null;
        SimMob closestEntity = null;

        for (Map.Entry<SimMob, Location> entry : nearbyEntities.entrySet()) {
            if (!dangerTypes.contains(entry.getKey().getType())) {
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
