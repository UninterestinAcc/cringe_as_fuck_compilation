/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.worldgen.populator;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import space.craftin.plugin.core.api.asteroids.IAsteroid;
import space.craftin.plugin.core.impl.core.Registrar;
import space.craftin.plugin.worldgen.util.AsteroidUtil;

import java.util.Random;

public class CIS_RareAsteroidPop extends BlockPopulator {
	@Override
	public void populate(World world, Random random, Chunk chunk) {
		if (Math.random() < 0.0002) {
			final Location center = chunk.getBlock(7, (int) AsteroidUtil.randOfRange(32, 97), 7).getLocation();
			IAsteroid asteroid = Registrar.createRareAsteroidAt(center, AsteroidUtil.randOfRange(4, 20));
			Material[] mat = asteroid.getType().getMaterial();

			double size = asteroid.getSize();
			double offset = Math.random() * 16;
			AsteroidUtil.PlaneDirection shiftVec = AsteroidUtil.PlaneDirection.random();

			for (int x = -32; x <= 32; x++) {
				for (int y = -32; y <= 32; y++) {
					for (int z = -32; z <= 32; z++) {
						Location loc = AsteroidUtil.chunkCordsToLocation(world, chunk.getX(), chunk.getZ(), x, center.getY() + y, z);
						if (AsteroidUtil.ellipticValidatePosition(loc, center, size, offset, shiftVec)) {
							loc.getBlock().setType(mat[(int) (mat.length * Math.random())]);
						}
					}
				}
			}
		}
	}
}
