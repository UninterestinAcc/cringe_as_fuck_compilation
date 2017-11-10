/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.impl.core.utils;

import org.bukkit.Location;
import space.craftin.plugin.core.impl.core.functions.Datagram;

import static java.lang.Math.abs;

public class AxisAlignedBB extends Datagram {
	private Location loc;
	private double radius;

	public AxisAlignedBB(Location location, double radius) {
		this.loc = location;
		this.radius = radius;
	}

	public String toString() {
		return "AABB{" + loc + "," + radius + "}";
	}

	public Location getLocation() {
		return loc.clone();
	}

	public void setLocation(Location loc) {
		this.loc = loc;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Location getPositiveVertice() {
		return getLocation().add(radius, radius, radius);
	}

	public Location getNegativeVertice() {
		return getLocation().subtract(radius, radius, radius);
	}

	public boolean isInside(Location loc) {
		return abs(this.loc.getX() - loc.getX()) <= radius && abs(this.loc.getY() - loc.getY()) <= radius && abs(this.loc.getZ() - loc.getZ()) <= radius;
	}

	public boolean intersectsWith(AxisAlignedBB aabb) {
		double distance = aabb.getRadius() + radius;
		Location loc = aabb.getLocation();
		return abs(this.loc.getX() - loc.getX()) < distance && abs(this.loc.getY() - loc.getY()) < distance && abs(this.loc.getZ() - loc.getZ()) < distance;
	}

	/**
	 * Predicts where the ray will collide with the AABB. (location must have valid pit/yaw data)
	 */
	public Location getCollisionPoint(Location look) { // TODO
		return null;
	}
}
