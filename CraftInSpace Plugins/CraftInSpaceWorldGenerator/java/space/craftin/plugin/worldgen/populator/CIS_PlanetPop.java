/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.worldgen.populator;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import space.craftin.plugin.worldgen.util.AsteroidUtil;
import space.craftin.plugin.worldgen.util.PlanetUtil;

import java.util.Optional;
import java.util.Random;

public class CIS_PlanetPop extends BlockPopulator {
	@Override
	public void populate(World world, Random random, Chunk chunk) {
		Optional<Location> seededLoc = PlanetUtil.getSeededLocation(world, chunk);
		PlanetUtil.PlanetType planetType = PlanetUtil.PlanetType.values()[(int) (PlanetUtil.PlanetType.values().length * Math.random())];
		if (seededLoc.isPresent()) {
			Location loc = seededLoc.get();
			for (int x = 0; x < 16; x++) {
				for (int y = 100; y <= 156; y++) {
					for (int z = 0; z < 16; z++) {
						Location current = AsteroidUtil.chunkCordsToLocation(world, chunk.getX(), chunk.getZ(), x, y, z);
						if (PlanetUtil.validatePosition(current, loc, 7)) {
							Material[] mat;
							if (PlanetUtil.validatePosition(current, loc, 4)) {
								if (PlanetUtil.validatePosition(current, loc, 1)) {
									// Core
									mat = planetType.getCore();
								} else {
									// Inner
									mat = planetType.getInternalLayer();
								}
							} else {
								// Outer
								mat = planetType.getExternalLayer();
							}
							current.getBlock().setType(mat[(int) (mat.length * Math.random())]);
						}
					}
				}
			}
		}
	}
}
