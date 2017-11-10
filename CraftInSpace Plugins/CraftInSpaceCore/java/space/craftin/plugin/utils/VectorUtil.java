/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import space.craftin.plugin.core.api.physical.objects.IBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.lang.Math.*;

public class VectorUtil {
	public static double strengthSquared(Vector vec) {
		return pow(vec.getX(), 2) + pow(vec.getY(), 2) + pow(vec.getZ(), 2);
	}

	public static double strength(Vector vec) {
		return sqrt(strengthSquared(vec));
	}

	public static Vector setForce(Vector vec, double to) {
		double factor = to / strength(vec);
		return vec.multiply(factor);
	}

	public static Vector draw(Location from, Location to) {
		return to.toVector().subtract(from.toVector());
	}

	public static Location shiftLocation(Location loc, double force) {
		return loc.add(draw(toRadians(loc.getPitch() + 90) + Math.PI * 1.5, toRadians(loc.getYaw() + 90) - PI / 2, force));
	}

	/**
	 * @return Pitch in Radians.
	 */
	public static double getPitch(Location from, Location to) {
		return getPitch(draw(from, to));
	}

	/**
	 * @param vec Vector
	 * @return Pitch in Radians.
	 */
	public static double getPitch(Vector vec) {
		return atan2(sqrt(pow(vec.getX(), 2) + pow(vec.getZ(), 2)), vec.getY()) + Math.PI * 1.5;
	}

	/**
	 * @return Yaw in Radians.
	 */
	public static double getYaw(Location from, Location to) {
		return getYaw(draw(from, to));
	}

	/**
	 * @param vec Vector
	 * @return Yaw in Radians.
	 */
	public static double getYaw(Vector vec) {
		return atan2(vec.getZ(), vec.getX()) - PI / 2;
	}

	/**
	 * Draws a vector with the pitch, yaw and the strength of the vector.
	 */
	public static Vector draw(double pitch, double yaw, double strength) {
		pitch -= Math.PI * 1.5;
		yaw += Math.PI / 2;
		Vector vec = new Vector(Math.sin(pitch) * Math.cos(yaw), Math.cos(pitch), Math.sin(pitch) * Math.sin(yaw));
		vec.multiply(strength);
		return vec;
	}

	public static Vector drawDegrees(double pitch, double yaw, double strength) {
		return draw(toRadians(pitch), toRadians(yaw), strength);
	}

	/**
	 * Rotates a current vector by pitch and yaw radians
	 */
	public static Vector rotate(Vector vec, double pitch, double yaw) {
		double rotPitch = getPitch(vec) + pitch;
		double rotYaw = getYaw(vec) + yaw;
		return draw(rotPitch, rotYaw, strength(vec));
	}

	/**
	 * Find which surface of the ArmorStand is getting aimed on by the player, then return that location. (Poorly and coarsely.)
	 */
	public static Location getAimedLocation(Player p, ArmorStand as) {
		Location asLoc = as.getLocation().add(0, IBlock.FULL_ARMORSTAND_HEIGHT * 6.25 / 7, 0);

		Location pLoc = p.getEyeLocation();
		Function<Double, Double> adjustPitch = d -> abs(d) > 90 ? d + (d < 0 ? 180 : -180) : d;

		final double asPitch = adjustPitch.apply((double) (asLoc.getPitch() % 180));
		final double asYaw = (asLoc.getYaw() + 360) % 360;

		List<Vector> vectors = new ArrayList<>();
		for (int sign = -1; sign <= 1; sign += 2) {
			vectors.add(drawDegrees(adjustPitch.apply(asPitch + 90 % 180), asYaw, IBlock.HEAD_WIDTH).multiply(sign));
			vectors.add(drawDegrees(0, asYaw + 90 * sign, IBlock.HEAD_WIDTH));
			vectors.add(drawDegrees(asPitch, asYaw, IBlock.HEAD_WIDTH).multiply(sign));
		}

		Location projectedPlayerLoc = shiftLocation(pLoc.clone(), pLoc.distance(asLoc) - IBlock.HEAD_WIDTH);

		return asLoc.clone().add(vectors.stream().sorted((vec1, vec2) -> asLoc.clone().add(vec1).distanceSquared(projectedPlayerLoc) > asLoc.clone().add(vec2).distanceSquared(projectedPlayerLoc) ? 1 : -1).findFirst().get()).subtract(0, IBlock.EYE_HEIGHT, 0);
	}

	public static double absDistance(double num1, double num2) {
		return abs(num2 - num1);
	}

	public static double hypotenuse(double a, double b) {
		return sqrt(hypotenuseSquared(a, b));
	}

	public static double hypotenuse(double a, double b, double c) {
		return sqrt(hypotenuseSquared(a, b, c));
	}

	public static double hypotenuseSquared(double a, double b) {
		return hypotenuseSquared(a, b, 0);
	}

	public static double hypotenuseSquared(double a, double b, double c) {
		return pow(a, 2) + pow(b, 2) + pow(c, 2);
	}
}
