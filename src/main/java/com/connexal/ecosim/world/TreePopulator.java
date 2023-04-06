package com.connexal.ecosim.world;

import com.connexal.ecosim.EcoSim;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class TreePopulator extends BlockPopulator {
    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        if (!VoidWorldGen.validChunkCoords(chunkX, chunkZ, false)) {
            return;
        }

        if (random.nextBoolean()) {
            int amount = random.nextInt(4) + 1;

            for (int i = 1; i < amount; i++) {
                int x = random.nextInt(15) + (chunkX * 16);
                int z = random.nextInt(15) + (chunkZ * 16);
                int y = EcoSim.HEIGHT_MAP[x][z];

                Material type = limitedRegion.getType(x, y - 1, z);
                if (type == Material.GRASS_BLOCK || type == Material.DIRT) {
                    limitedRegion.generateTree(new Location(limitedRegion.getWorld(), x, y, z), random, TreeType.TREE);
                }
            }
        }
    }
}
