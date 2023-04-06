package com.connexal.ecosim.mobs;

import com.connexal.ecosim.EcoSim;
import com.connexal.ecosim.utils.Genes;
import com.connexal.ecosim.utils.MobSex;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimMob {
    private static final int MATE_COOLDOWN = 60 * 1000; //1 minute
    private static final long ADULT_AGE = 20;

    private final SimMobType type;
    private final Mob entity;
    private final MobSex sex;
    private final String displayName;
    private final Map<SimGoalType, SimGoal> goals = new HashMap<>();

    //Life
    private double satiation;
    private double hydration;
    private double age;

    //Genes
    private double speed;
    private double gestationTime;
    private double senseRadius;

    private double staminaUsageMultiplier;
    private double growthRate;

    //Goal coordination
    private SimGoalType currentGoalType = null;
    private SimGoal currentGoal = null;
    private boolean isBlocked = false;
    private boolean isGestating = false;
    private long lastMateTime = 0;

    private Map<SimMob, Location> surroundingMobs = new HashMap<>();
    private List<Block> surroundingBlocks = new ArrayList<>();

    public SimMob(SimMobType type, Mob entity) {
        this.type = type;
        this.entity = entity;

        this.sex = Math.random() < 0.5 ? MobSex.FEMALE : MobSex.MALE;

        this.displayName = ChatColor.BOLD + "[" + (this.sex == MobSex.MALE ? "M" : "F") + "] " + ChatColor.WHITE + type.getName();

        this.satiation = type.getMaxSatiation();
        this.hydration = type.getMaxHydration();
        this.age = ADULT_AGE;

        this.init(type.getSpeed(), Genes.DEFAULT_SENSE_RADIUS, Genes.DEFAULT_GESTATION_COOLDOWN);
    }

    public void init(double speed, double gestationTime, double senseRadius) {
        this.speed = speed;
        this.gestationTime = gestationTime;
        this.senseRadius = senseRadius;

        //Correlated with speed, more speed = more stamina usage
        this.staminaUsageMultiplier = (this.speed / 2) + (this.senseRadius / 4);
        //Correlated with gestation time, longer gestation time = slower growth
        this.growthRate = this.gestationTime / ADULT_AGE;
    }

    public void init(SimMob parent1, SimMob parent2) {
        this.age = 0;

        this.speed = Genes.getGeneValue(parent1.getSpeed(), parent2.getSpeed());
        this.gestationTime = Genes.getGeneValue(parent1.getGestationTime(), parent2.getGestationTime());
        this.senseRadius = Genes.getGeneValue(parent1.getSenseRadius(), parent2.getSenseRadius());
    }

    public SimMobType getType() {
        return this.type;
    }

    public Mob getEntity() {
        return this.entity;
    }

    public MobSex getSex() {
        return this.sex;
    }

    public double getHunger() {
        return 1 - (this.satiation / this.type.getMaxSatiation());
    }

    public double getThirst() {
        return 1 - (this.hydration / this.type.getMaxHydration());
    }

    public double getAge() {
        return this.age;
    }

    public boolean isAdult() {
        return this.age >= ADULT_AGE;
    }

    public double getSpeed() {
        return this.speed;
    }

    public double getGestationTime() {
        return this.gestationTime;
    }

    public double getSenseRadius() {
        return this.senseRadius;
    }

    public double getStaminaUsageMultiplier() {
        return this.staminaUsageMultiplier;
    }

    public void setGestate(boolean gestating) {
        if (!gestating) {
            this.lastMateTime = System.currentTimeMillis();
        }

        this.isGestating = gestating;
    }

    public boolean isGestating() {
        return this.isGestating;
    }

    public long getLastMateTime() {
        return this.lastMateTime;
    }

    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
        this.updateName();

        if (blocked) {
            this.stop();
        }
    }

    public boolean isBlocked() {
        return this.isBlocked;
    }

    public boolean canMate() {
        return this.isAdult() && (System.currentTimeMillis() - this.lastMateTime > MATE_COOLDOWN) && !this.isGestating;
    }

    public Map<SimMob, Location> getSurroundingMobs() {
        return new HashMap<>(this.surroundingMobs);
    }

    public List<Block> getSurroundingBlocks() {
        return new ArrayList<>(this.surroundingBlocks);
    }

    public Location getLocation() {
        return this.entity.getLocation();
    }

    public void registerGoal(SimGoalType type, SimGoal goal) {
        this.goals.put(type, goal);
    }

    public void move(Location location, double speedMultiplier) {
        double speed = (this.speed * speedMultiplier) * ((double) EcoSim.TICKS_PER_SECOND / 2);
        if (!this.isAdult()) {
            speed *= 0.5;
        }

        this.entity.getPathfinder().moveTo(location, speed);
    }

    public void move(Location location) {
        this.move(location, 1);
    }

    public void stop() {
        this.entity.getPathfinder().stopPathfinding();
    }

    public void tick() {
        //Life cycle
        this.satiation = Math.max(0, this.satiation - this.staminaUsageMultiplier);
        this.hydration = Math.max(0, this.hydration - this.staminaUsageMultiplier);

        if (this.satiation == 0 || this.hydration == 0) {
            this.kill();
        }

        if (this.isAdult()) {
            this.age++;
        } else {
            this.age += this.growthRate;
        }

        //Variables
        this.surroundingMobs = this.entity.getNearbyEntities(this.senseRadius, this.senseRadius, this.senseRadius).stream()
                .filter(entity -> SimMobType.isRegistered(entity.getType()))
                .collect(Collectors.toMap(entity -> SimMobs.spawnedMobs.get(entity.getUniqueId()), Entity::getLocation));
        this.surroundingBlocks = null; //TODO

        //Goals
        SimGoal oldGoal = this.currentGoal;
        SimGoalType oldGoalType = this.currentGoalType;

        if (!this.isBlocked) {
            boolean foundGoal = false;

            for (SimGoalType type : SimGoalType.values()) {
                SimGoal currentGoal = this.goals.get(type);
                if (currentGoal == null) {
                    continue;
                }

                if (currentGoal.shouldActivate()) {
                    this.currentGoalType = type;
                    this.currentGoal = currentGoal;
                    foundGoal = true;
                    break;
                }
            }

            if (!foundGoal) {
                this.currentGoalType = null;
                this.currentGoal = null;
            }
        }

        if (oldGoalType != this.currentGoalType) {
            if (oldGoal != null) {
                oldGoal.stop();
            }

            this.stop();

            if (this.currentGoal != null) {
                this.currentGoal.start();
            }
            this.updateName();
        }

        if (this.currentGoal != null) {
            this.currentGoal.tick();
        }
    }

    public void updateName() {
        String prefix = "";

        if (this.isBlocked) {
            prefix = ChatColor.BOLD + "[BLOCKED] ";
        } else if (this.currentGoal != null) {
            prefix = ChatColor.BOLD + "[" + this.currentGoalType.name() + "] ";
        }

        this.entity.customName(Component.text(prefix + this.displayName));
    }

    public void kill() {
        this.entity.damage(this.entity.getHealth() + 1);
    }
}
