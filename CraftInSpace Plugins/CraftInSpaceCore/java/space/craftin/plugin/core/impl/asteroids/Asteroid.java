/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.impl.asteroids;

import org.bukkit.Location;
import space.craftin.plugin.core.api.asteroids.IAsteroid;

import java.util.Arrays;
import java.util.Optional;

public class Asteroid implements IAsteroid {
	private static final double DEFAULT_SIZE = 6;
	private final double size;
	private final AsteroidType type;
	private final Location loc;

	public Asteroid(Location loc) {
		this(loc, generateAsteroidType(0));
	}

	public Asteroid(Location loc, double size) {
		this(loc, generateAsteroidType(size), size);
	}

	public Asteroid(Location loc, AsteroidType type) {
		this(loc, type, Math.random() * (type.getWeight() > 2 ? DEFAULT_SIZE : 2));
	}

	public Asteroid(Location loc, AsteroidType type, double size) {
		this.size = size;
		this.type = type;
		this.loc = loc;
	}

	private static AsteroidType generateAsteroidType(double size) {
		double w = Arrays.stream(AsteroidType.getAsteroidsByCategory(AsteroidCategory.COMMON)).filter(ast -> ast.getWeight() > size).mapToDouble(AsteroidType::getWeight).sum() * Math.random();
		Optional<AsteroidType> typeOpt = Arrays.stream(AsteroidType.getAsteroidsByCategory(AsteroidCategory.COMMON)).filter(ast -> ast.getWeight() <= w).sorted((ast1, ast2) -> ast2.getWeight() > ast1.getWeight() ? 1 : -1).findFirst();
		return typeOpt.isPresent() ? typeOpt.get() : AsteroidType.Carbonaceous;
	}

	@Override
	public double getSize() {
		return size;
	}

	@Override
	public AsteroidType getType() {
		return type;
	}

	@Override
	public Location getLocation() {
		return loc;
	}

	@Override
	public String toString() {
		return loc + "/size=" + size + "/type=" + type;
	}
}
