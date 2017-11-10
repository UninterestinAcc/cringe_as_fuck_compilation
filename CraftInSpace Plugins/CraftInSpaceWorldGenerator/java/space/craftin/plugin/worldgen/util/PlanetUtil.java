/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.worldgen.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Optional;

import static java.lang.Math.*;

public class PlanetUtil {
	public static boolean validatePosition(Location loc, Location center, double size) {
		double x = abs(loc.getX() - center.getX());
		double y = abs(loc.getY() - center.getY());
		double z = abs(loc.getZ() - center.getZ());

		return sqrt(pow(x, 2) + pow(y, 2) + pow(z, 2)) <= size;
	}

	public static Optional<Location> getSeededLocation(World world, Chunk chunk) {
		if (Math.abs(chunk.getX()) % 32 == 0 && Math.abs(chunk.getZ()) % 24 == 0 && chunk.getX() != 0 && chunk.getZ() != 0) {
			Location loc = AsteroidUtil.getChunkCenter(world, chunk.getX(), chunk.getZ());
			loc.setY(128);
			return Optional.of(loc);
		}
		return Optional.empty();
	}

	public enum PlanetType {
		ICY(Biome.ICE_MOUNTAINS, Material.ICE, Material.PACKED_ICE, Material.PACKED_ICE),
		FROST(Biome.ICE_PLAINS, Material.SNOW_BLOCK, Material.ICE, Material.PACKED_ICE),
		VOLCANIC(Biome.HELL, Material.NETHERRACK, Material.QUARTZ_ORE, Material.NETHER_BRICK),
		METALLIC_GOLD(Biome.STONE_BEACH, new Material[]{Material.STONE, Material.STONE, Material.GOLD_ORE},
				new Material[]{Material.STONE, Material.GOLD_ORE, Material.GOLD_ORE},
				Material.GOLD_BLOCK
		),
		METALLIC_IRON(Biome.STONE_BEACH, new Material[]{Material.STONE, Material.STONE, Material.IRON_ORE},
				new Material[]{Material.STONE, Material.IRON_ORE, Material.IRON_ORE},
				Material.IRON_BLOCK
		),
		DIRT_PLAINS(Biome.PLAINS, new Material[]{Material.DIRT}, new Material[]{Material.STONE, Material.IRON_ORE, Material.COAL_ORE, Material.COAL_ORE, Material.GOLD_ORE},
				Material.IRON_BLOCK
		);


		private final Biome biome;
		private final Material[] externalLayer;
		private final Material[] internalLayer;
		private final Material[] core;

		PlanetType(Biome biome, Material externalLayer, Material internalLayer, Material core) {
			this(biome, new Material[]{externalLayer}, new Material[]{internalLayer}, core);
		}

		PlanetType(Biome biome, Material[] externalLayer, Material[] internalLayer, Material... core) {
			this.biome = biome;
			this.externalLayer = externalLayer;
			this.internalLayer = internalLayer;
			this.core = core;
		}

		public Material[] getExternalLayer() {
			return externalLayer.clone();
		}

		public Material[] getInternalLayer() {
			return internalLayer.clone();
		}

		public Material[] getCore() {
			return core.clone();
		}

		public Biome getBiome() {
			return biome;
		}
	}
}
