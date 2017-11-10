/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.worldgen.generator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import space.craftin.plugin.core.api.asteroids.IAsteroid;
import space.craftin.plugin.core.impl.core.Registrar;
import space.craftin.plugin.worldgen.populator.CIS_AbandonedFlagshipPop;
import space.craftin.plugin.worldgen.populator.CIS_AbandonedStationPop;
import space.craftin.plugin.worldgen.populator.CIS_RareAsteroidPop;
import space.craftin.plugin.worldgen.util.AsteroidUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CIS_AsteroidGen extends CIS_VoidGen {

	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return Arrays.asList(new CIS_AbandonedStationPop(), new CIS_AbandonedFlagshipPop(), new CIS_RareAsteroidPop());
	}

	@Override
	public byte[] generate(World world, Random rand, int chunkX, int chunkZ) {
		byte[] result = super.generate(world, rand, chunkX, chunkZ);

		if (Math.random() < 0.1) {
			Location asteroidLoc = AsteroidUtil.getChunkCenter(world, chunkX, chunkZ);
			asteroidLoc.setY(AsteroidUtil.randOfRange(16, 112));
			IAsteroid asteroid = Registrar.createAsteroidAt(asteroidLoc);
			double size = asteroid.getSize();

			Byte[] asteroidMat = asteroid.getType().getMaterialID();
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 128; y++) {
					for (int z = 0; z < 16; z++) {
						if (AsteroidUtil.validatePosition(AsteroidUtil.chunkCordsToLocation(world, chunkX, chunkZ, x, y, z), asteroidLoc, size)) {
							result[coordSerialize(x, y, z)] = asteroidMat[(int) (asteroidMat.length * Math.random())];
						}
					}
				}
			}
		}
		return result;
	}
}
