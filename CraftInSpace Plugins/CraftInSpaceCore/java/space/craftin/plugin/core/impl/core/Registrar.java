/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.impl.core;

import org.bukkit.Location;
import space.craftin.plugin.core.api.asteroids.IAsteroid;
import space.craftin.plugin.core.impl.asteroids.Asteroid;

public class Registrar {
	public static IAsteroid createAsteroidAt(Location loc) {
		return new Asteroid(loc);
	}

	public static IAsteroid createRareAsteroidAt(Location center, double size) {
		IAsteroid.AsteroidType[] types = IAsteroid.AsteroidType.getAsteroidsByCategory(IAsteroid.AsteroidCategory.RARE);
		return new Asteroid(center, types[(int) (types.length * Math.random())], size);
	}
}
