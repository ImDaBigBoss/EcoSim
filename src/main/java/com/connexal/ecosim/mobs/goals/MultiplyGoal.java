package com.connexal.ecosim.mobs.goals;

import com.connexal.ecosim.EcoSim;
import com.connexal.ecosim.mobs.SimGoal;
import com.connexal.ecosim.mobs.SimMob;
import com.connexal.ecosim.mobs.SimMobs;
import net.kyori.adventure.util.TriState;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.List;
import java.util.Map;

public class MultiplyGoal extends SimGoal {
    private static final double MATE_DISTANCE = 1;
    private static final int TIME_OF_ACT = 5;

    private SimMob femaleMate = null;
    private boolean isMating = false;

    public MultiplyGoal(SimMob mob) {
        super(mob);
    }

    @Override
    public boolean shouldActivate() {
        return !this.isMating && this.mob.canMate() && this.isNearMate();
    }

    @Override
    public void start() {
        //Nothing
    }

    @Override
    public void stop() {
        //Nothing
    }

    private void heartParticles(SimMob parent1, SimMob parent2) {
        parent1.getEntity().lookAt(parent2.getEntity());
        parent1.getLocation().getWorld().spawnParticle(Particle.HEART, parent1.getEntity().getEyeLocation(), 1);
    }

    @Override
    public void tick() {
        if (this.isMating) {
            return;
        }

        double distance = this.femaleMate.getLocation().distance(this.mob.getLocation());

        if (distance <= MATE_DISTANCE) {
            this.isMating = true;

            this.mob.setBlocked(true);
            this.femaleMate.setBlocked(true);

            this.mob.setGestate(true);
            this.femaleMate.setGestate(true);

            int hearts = EcoSim.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(EcoSim.getInstance(), () -> {
                heartParticles(this.mob, this.femaleMate);
                heartParticles(this.femaleMate, this.mob);
            }, 0L, 10L);

            EcoSim.getInstance().getServer().getScheduler().runTaskLater(EcoSim.getInstance(), () -> {
                EcoSim.getInstance().getServer().getScheduler().cancelTask(hearts);

                this.mob.setBlocked(false);
                this.femaleMate.setBlocked(false);

                EcoSim.getInstance().getServer().getScheduler().runTaskLater(EcoSim.getInstance(), () -> {
                    SimMobs.spawnChildMob(this.mob, this.femaleMate);

                    this.femaleMate.setGestate(false);
                    this.mob.setGestate(false);
                }, (long) ((20L / EcoSim.TICKS_PER_SECOND) * this.femaleMate.getGestationTime()));
            }, (20L / EcoSim.TICKS_PER_SECOND) * TIME_OF_ACT);
        } else {
            this.mob.move(this.femaleMate.getLocation());
        }
    }

    private boolean isNearMate() {
        Map<SimMob, Location> nearbyEntities = this.mob.getSurroundingMobs();

        Location closestLocation = null;
        SimMob closestEntity = null;

        for (Map.Entry<SimMob, Location> entry : nearbyEntities.entrySet()) {
            if (entry.getKey().getType() != this.mob.getType() || entry.getKey().getSex() == this.mob.getSex() || !entry.getKey().canMate()) {
                continue;
            }

            if (closestLocation == null || entry.getValue().distance(this.mob.getLocation()) < closestLocation.distance(this.mob.getLocation())) {
                closestLocation = entry.getValue();
                closestEntity = entry.getKey();
            }
        }

        this.femaleMate = closestEntity;
        return closestEntity != null;
    }
}
