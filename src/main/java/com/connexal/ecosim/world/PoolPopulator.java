package com.connexal.ecosim.world;

import com.connexal.ecosim.EcoSim;
import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class PoolPopulator extends BlockPopulator {
    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        if (!VoidWorldGen.validChunkCoords(chunkX, chunkZ, false)) {
            return;
        }

        if (random.nextInt(10) == 0) {
            int x = random.nextInt(15) + (chunkX * 16);
            int z = random.nextInt(15) + (chunkZ * 16);
            int y = worldInfo.getMaxHeight();

            int radius = random.nextInt(2) + 3;
            for (int i = -radius; i <= radius; i++) {
                for (int j = -radius; j <= radius; j++) {
                    int tmpY = EcoSim.HEIGHT_MAP[x + i][z + j] - 1;
                    if (tmpY < y) {
                        y = tmpY;
                    }
                }
            }

            for (int i = -radius; i <= radius; i++) {
                for (int j = -radius; j <= radius; j++) {
                    if (Math.sqrt(i*i + j*j) <= radius) {
                        limitedRegion.setType(x + i, y + 1, z + j, Material.AIR);
                        limitedRegion.setType(x + i, y, z + j, Material.WATER);
                        limitedRegion.setType(x + i, y - 1, z + j, Material.WATER);
                    }
                }
            }
        }
    }
}
