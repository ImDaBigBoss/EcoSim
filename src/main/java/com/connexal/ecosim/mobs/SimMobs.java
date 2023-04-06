package com.connexal.ecosim.mobs;

import com.connexal.ecosim.EcoSim;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.*;

public class SimMobs implements Listener {
    private static final Particle.DustOptions FAILED_DUST = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 2);

    private static boolean initialised = false;
    private static final EcoSim plugin = EcoSim.getInstance();

    public static final Map<SimMobType, List<SimMobType>> predators = new HashMap<>();
    public static final Map<UUID, SimMob> spawnedMobs = new HashMap<>();

    public static void init() {
        if (initialised) {
            throw new IllegalStateException("SimMobs has already been initialized");
        }

        initialised = true;

        plugin.getServer().getPluginManager().registerEvents(new SimMobs(), plugin);

        for (SimMobType mob : SimMobType.values()) {
            //Any characteristics that need to be set for the mobs

            if (mob.getPrey() != null) {
                for (SimMobType prey : mob.getPrey()) {
                    predators.computeIfAbsent(prey, k -> new ArrayList<>()).add(mob);
                }
            }
        }

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            List<SimMob> tmp = new ArrayList<>(spawnedMobs.values());
            for (SimMob mob : tmp) {
                mob.tick();
            }
        }, 0, 20L / EcoSim.TICKS_PER_SECOND); //Twice a second
    }

    public static void stop() {
        if (!initialised) {
            throw new IllegalStateException("SimMobs has not been initialized");
        }

        initialised = false;

        for (SimMob mob : spawnedMobs.values()) {
            mob.kill();
        }
    }

    public static SimMob spawnChildMob(SimMob male, SimMob female) {
        Location location = female.getLocation();
        Mob entity = (Mob) location.getWorld().spawnEntity(location, female.getType().getEntityType());

        SimMob child = spawnMob(entity, female.getType());
        child.init(male, female);

        return child;
    }

    public static SimMob spawnMob(Location location, SimMobType simMob) {
        Mob entity = (Mob) location.getWorld().spawnEntity(location, simMob.getEntityType());
        return spawnMob(entity, simMob);
    }

    public static SimMob spawnMob(Mob entity, SimMobType simMob) {
        SimMob mob = new SimMob(simMob, entity);
        spawnedMobs.put(entity.getUniqueId(), mob);

        entity.setPersistent(true);
        entity.clearLootTable();
        mob.updateName();

        if (entity instanceof Breedable breedable) {
            breedable.setAgeLock(true);
            breedable.setAdult();
        }

        plugin.getServer().getMobGoals().removeAllGoals(entity);

        for (SimGoalType goal : SimGoalType.values()) {
            if (predators.get(simMob) == null && goal.isFlee()) {
                continue;
            }
            if (goal.getTargetSex() != null && goal.getTargetSex() != mob.getSex()) {
                continue;
            }

            SimGoal goalWrapper;
            try {
                goalWrapper = goal.getGoal().getConstructor(SimMob.class).newInstance(mob);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            mob.registerGoal(goal, goalWrapper);
        }

        return mob;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            event.setCancelled(true);
            return;
        }

        if (!SimMobType.isRegistered(event.getEntityType())) {
            event.setCancelled(true);
            event.getLocation().getWorld().spawnParticle(Particle.REDSTONE, event.getLocation(), 10, FAILED_DUST);
            return;
        }

        spawnMob((Mob) event.getEntity(), SimMobType.valueOf(event.getEntityType().name()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityRemove(EntityRemoveFromWorldEvent event) {
        if (!(event.getEntity() instanceof Mob)) {
            return;
        }

        SimMob mob = spawnedMobs.remove(event.getEntity().getUniqueId());
        if (mob == null) {
            return;
        }
    }
}
