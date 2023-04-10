package com.connexal.ecosim;

import com.connexal.ecosim.command.GetEggsCommand;
import com.connexal.ecosim.command.MobInfoCommand;
import com.connexal.ecosim.command.SetTpsCommand;
import com.connexal.ecosim.command.TpCentreCommand;
import com.connexal.ecosim.mobs.SimMobs;
import com.connexal.ecosim.world.VoidWorldGen;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

public final class EcoSim extends JavaPlugin implements Listener {
    public static final int SIMULATION_WIDTH = 256;
    public static final int[][] HEIGHT_MAP = new int[SIMULATION_WIDTH][SIMULATION_WIDTH];

    private static EcoSim instance;
    public static int ticksPerSecond = 2;

    @Override
    public void onEnable() {
        instance = this;
        this.getServer().getPluginManager().registerEvents(this, this);

        SimMobs.init();

        this.getCommand("geteggs").setExecutor(new GetEggsCommand());
        this.getCommand("tpcentre").setExecutor(new TpCentreCommand());
        this.getCommand("mobinfo").setExecutor(new MobInfoCommand());
        this.getCommand("settps").setExecutor(new SetTpsCommand());
    }

    @Override
    public void onDisable() {
        SimMobs.stop();

        this.getServer().unloadWorld("world", false);
        try {
            FileUtils.deleteDirectory(new File("world"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidWorldGen();
    }

    //When the server has finished loading
    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        World world = this.getServer().getWorld("world");
        if (world == null) {
            this.getLogger().severe("World 'world' not found, couldn't set world border!");
            return;
        }

        int side = SIMULATION_WIDTH / 2;
        world.setSpawnLocation(side, HEIGHT_MAP[side][side], side);

        WorldBorder border = world.getWorldBorder();
        border.setSize(SIMULATION_WIDTH);
        border.setCenter((double) SIMULATION_WIDTH / 2, (double) SIMULATION_WIDTH / 2);
    }

    public static EcoSim getInstance() {
        return instance;
    }
}
