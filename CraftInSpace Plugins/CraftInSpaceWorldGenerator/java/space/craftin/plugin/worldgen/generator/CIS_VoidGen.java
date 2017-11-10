/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.worldgen.generator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import space.craftin.plugin.worldgen.populator.CIS_EmptyPop;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CIS_VoidGen extends ChunkGenerator {
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return Collections.singletonList(new CIS_EmptyPop());
	}

	@Override
	public boolean canSpawn(World world, int x, int z) {
		return true;
	}

	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
		world.setSpawnLocation(0, 100, 0);
		return new Location(world, 0, 100, 0);
	}

	public int coordSerialize(int x, int y, int z) {
		return (x * 16 + z) * 128 + y;
	}


	@Override
	public byte[] generate(World world, Random rand, int chunkX, int chunkZ) {
		byte[] result = new byte[32768];
		if (chunkX == 0 && chunkZ == 0) result[99] = 1;
		return result;
	}
}
