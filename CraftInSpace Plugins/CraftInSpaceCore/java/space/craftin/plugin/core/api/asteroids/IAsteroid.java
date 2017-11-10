/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.api.asteroids;

import org.bukkit.Material;
import space.craftin.plugin.core.api.core.functions.ILocatable;

import java.util.Arrays;

public interface IAsteroid extends ILocatable {
	double getSize();

	AsteroidType getType();

	enum AsteroidCategory {
		COMMON, RARE
	}

	enum AsteroidType {
		Composite(new Material[]{
				Material.COAL_ORE, Material.COAL_ORE, Material.COAL_ORE, Material.COAL_ORE, Material.COAL_ORE, Material.COAL_ORE,
				Material.IRON_ORE, Material.IRON_ORE, Material.IRON_ORE, Material.IRON_ORE, Material.IRON_ORE, Material.IRON_ORE,
				Material.EMERALD_ORE,
				Material.GOLD_ORE,
				Material.REDSTONE_ORE,
				Material.STONE, Material.STONE, Material.STONE, Material.STONE, Material.STONE, Material.STONE, Material.STONE, Material.STONE, Material.STONE, Material.STONE, Material.STONE, Material.STONE
		}, 8, AsteroidCategory.RARE),
		Golden(new Material[]{Material.GOLD_ORE, Material.STONE, Material.STONE}, 2, AsteroidCategory.RARE),
		Energy(new Material[]{Material.REDSTONE_ORE, Material.REDSTONE_ORE, Material.REDSTONE_ORE, Material.EMERALD_ORE, Material.STONE}, 0.00025, AsteroidCategory.RARE),


		Carbonaceous(new Material[]{Material.COAL_ORE, Material.COAL_ORE, Material.STONE}, 3, AsteroidCategory.COMMON),
		Iron(new Material[]{Material.IRON_ORE, Material.IRON_ORE, Material.STONE}, 1, AsteroidCategory.COMMON),
		Comet(new Material[]{Material.STONE, Material.COAL_ORE, Material.PACKED_ICE, Material.PACKED_ICE}, 0.5, AsteroidCategory.COMMON),
		Radioactive(new Material[]{Material.EMERALD_ORE, Material.STONE, Material.STONE}, 0.00025, AsteroidCategory.COMMON);

		private final Material[] material;
		private final double weight;
		private final AsteroidCategory category;

		AsteroidType(Material[] material, double weight, AsteroidCategory category) {
			this.material = material;
			this.weight = weight;
			this.category = category;
		}

		public static AsteroidType[] getAsteroidsByCategory(AsteroidCategory category) {
			return Arrays.stream(values()).filter((asteroidType) -> asteroidType.getCategory() == category).toArray(AsteroidType[]::new);
		}

		public Material[] getMaterial() {
			return material;
		}

		public Byte[] getMaterialID() {
			return Arrays.stream(material).map(m -> ((byte) m.getId())).toArray(Byte[]::new);
		}

		public double getWeight() {
			return weight;
		}

		public AsteroidCategory getCategory() {
			return category;
		}
	}
}
