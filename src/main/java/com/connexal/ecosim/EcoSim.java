package com.connexal.ecosim;

import com.connexal.ecosim.command.GetEggsCommand;
import com.connexal.ecosim.command.MobInfoCommand;
import com.connexal.ecosim.command.TpCentreCommand;
import com.connexal.ecosim.mobs.SimMobs;
import com.connexal.ecosim.utils.VoidWorldGen;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public final class EcoSim extends JavaPlugin {
    public static final int TICKS_PER_SECOND = 2;

    private static EcoSim instance;

    @Override
    public void onEnable() {
        instance = this;

        SimMobs.init();

        this.getCommand("geteggs").setExecutor(new GetEggsCommand());
        this.getCommand("tpcentre").setExecutor(new TpCentreCommand());
        this.getCommand("mobinfo").setExecutor(new MobInfoCommand());
    }

    @Override
    public void onDisable() {
        SimMobs.stop();
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidWorldGen();
    }

    public static EcoSim getInstance() {
        return instance;
    }
}
