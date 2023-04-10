package com.connexal.ecosim.world;

import com.connexal.ecosim.EcoSim;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VoidWorldGen extends ChunkGenerator {
    private final List<BlockPopulator> blockPopulators = new ArrayList<>();
    private SimplexOctaveGenerator generator = null;

    public VoidWorldGen() {
        this.blockPopulators.add(new PoolPopulator()); //Must be first
        this.blockPopulators.add(new TreePopulator());
    }

    public static boolean validChunkCoords(int chunkX, int chunkZ, boolean allowEdge) {
        int blockX = chunkX * 16;
        int blockZ = chunkZ * 16;
        if (allowEdge) {
            return blockX >= 0 && blockX < EcoSim.SIMULATION_WIDTH && blockZ >= 0 && blockZ < EcoSim.SIMULATION_WIDTH;
        } else {
            return blockX >= 16 && blockX < (EcoSim.SIMULATION_WIDTH - 16) && blockZ >= 16 && blockZ < (EcoSim.SIMULATION_WIDTH - 16);
        }
    }

    private void createGenerator(WorldInfo worldInfo) {
        if (generator == null) {
            generator = new SimplexOctaveGenerator(worldInfo.getSeed(), 10);
            generator.setScale(0.007D);
        }
    }

    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkGenerator.ChunkData chunkData) {
        if (!validChunkCoords(chunkX, chunkZ, true)) {
            return;
        }

        int chunkWorldX = chunkX * 16;
        int chunkWorldZ = chunkZ * 16;

        this.createGenerator(worldInfo);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkWorldX + x;
                int worldZ = chunkWorldZ + z;

                if (worldX >= EcoSim.SIMULATION_WIDTH || worldZ >= EcoSim.SIMULATION_WIDTH) {
                    continue;
                }

                double noise = (generator.noise(worldX, worldZ, 0.9D, 0.5D, false) * 2) + 100;

                int height = (int) Math.round(noise);
                EcoSim.HEIGHT_MAP[worldX][worldZ] = height;

                for (int y = 0; y < height; y++) {
                    if (y == 0) {
                        chunkData.setBlock(x, y, z, Material.BEDROCK);
                    } else if (y < height - 6) {
                        chunkData.setBlock(x, y, z, Material.STONE);
                    } else if (y < height - 1) {
                        chunkData.setBlock(x, y, z, Material.DIRT);
                    } else {
                        chunkData.setBlock(x, y, z, Material.GRASS_BLOCK);
                    }
                }
            }
        }
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return this.blockPopulators;
    }

    @Override
    public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return new VoidBiomeProvider();
    }

    @Override
    public boolean shouldGenerateSurface() {
        return false;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return false;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return false;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return false;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return false;
    }
}
