package org.reborncraft.gtowny.cmds.utils;

import org.reborncraft.gtowny.data.TownyDataHandler;
import org.reborncraft.gtowny.data.local.ChunkLocation;
import org.reborncraft.gtowny.data.Chunk;
import org.reborncraft.gtowny.data.Town;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.pow;

public class ClaimUtils {

	public static int claimRect(ChunkLocation middle, int radii, String world, int townId, boolean overwrite, boolean actuallyClaim) {
		int claims = 0;
		for (int x = -radii; x <= radii; x++) {
			for (int z = -radii; z <= radii; z++) {
				ChunkLocation loc = new ChunkLocation(x + middle.getX(), z + middle.getZ());
				if (loc.hashCode() != Integer.MAX_VALUE) {
					Chunk chunk = TownyDataHandler.getOrCreateChunk(loc, world);
					if (chunk.getTownId() == -1 || overwrite) {
						if (actuallyClaim) TownyDataHandler.getTownById(townId).claimChunk(chunk);
						claims++;
					}
				}
			}
		}
		return claims;
	}


	public static int claimCirc(ChunkLocation middle, int radii, String world, int townId, boolean overwrite, boolean actuallyClaim) {
		int claims = 0;
		for (int x = -radii; x <= radii; x++) {
			for (int z = -radii; z <= radii; z++) {
				ChunkLocation loc = new ChunkLocation(x + middle.getX(), z + middle.getZ());
				if (loc.hashCode() != Integer.MAX_VALUE && pow(loc.getX(), 2) + pow(loc.getZ(), 2) <= pow(radii, 2)) {
					Chunk chunk = TownyDataHandler.getOrCreateChunk(loc, world);
					if (chunk.getTownId() == -1 || overwrite) {
						if (actuallyClaim) TownyDataHandler.getTownById(townId).claimChunk(chunk);
						claims++;
					}
				}
			}
		}
		return claims;
	}

	public static Map<Town, Integer> getChunkMapBreakdown(ChunkLocation loc, String world) {
		Map<Town, Integer> map = new HashMap<>();
		getChunkMap(loc, world).values().forEach(v -> v.values().forEach(c -> {
			int count = 1;
			Town town = c.getTown();
			if (map.containsKey(town)) {
				count = map.get(town);
			}
			map.put(town, count);
		}));
		return map;
	}

	public static Map<Integer, Map<Integer, Chunk>> getChunkMap(ChunkLocation loc, String world) {
		Map<Integer, Map<Integer, Chunk>> map = new HashMap<>();
		for (int z = -6; z <= 6; z++) {
			Map<Integer, Chunk> row = new HashMap<>();
			for (int x = -14; x <= 14; x++) {
				row.put(x, TownyDataHandler.getOrCreateChunk(new ChunkLocation(loc.getX() + x, loc.getZ() + z), world));
			}
			map.put(z, row);
		}
		return map;
	}
}
