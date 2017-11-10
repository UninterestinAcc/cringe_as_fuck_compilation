/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.worldgen.util;

import org.bukkit.Location;
import org.bukkit.World;

import static java.lang.Math.*;

public class AsteroidUtil {
	public static boolean validatePosition(Location loc, Location center, double size) {
		double x = abs(loc.getX() - center.getX());
		double y = abs(loc.getY() - center.getY());
		double z = abs(loc.getZ() - center.getZ());

		return sqrt(pow(x, 2) + pow(y, 2) + pow(z, 2)) <= size;
	}

	public static boolean ellipticValidatePosition(Location loc, Location center, final double size, final double offset, final PlaneDirection offsetVector) {
		/*
		Function<Double, Double> signCapping = (i) -> abs(i) > offset ? i > 0 ? offset : -offset : i;

		Location cLoc = center.clone();

		double xo = center.getX() - loc.getX();
		double yo = center.getY() - loc.getY();
		double zo = center.getZ() - loc.getZ();

		if ((xo >= 0 ? PlaneDirection.X_POSITIVE : PlaneDirection.X_NEGATIVE) == offsetVector) {
			cLoc.setX(cLoc.getX() + signCapping.apply(xo));
		} else if ((yo >= 0 ? PlaneDirection.Y_POSITIVE : PlaneDirection.Y_NEGATIVE) == offsetVector) {
			cLoc.setY(cLoc.getY() + signCapping.apply(yo));
		} else if ((zo >= 0 ? PlaneDirection.Z_POSITIVE : PlaneDirection.Z_NEGATIVE) == offsetVector) {
			cLoc.setZ(cLoc.getZ() + signCapping.apply(zo));
		}

		double x = abs(loc.getX() - cLoc.getX());
		double y = abs(loc.getY() - cLoc.getY());
		double z = abs(loc.getZ() - cLoc.getZ());

		double radius = sqrt(pow(x, 2) + pow(y, 2) + pow(z, 2));

//		return radius <= size - sqrt(pow(center.getX() - cLoc.getX(), 2) + (pow(center.getY() - cLoc.getY(), 2) + pow(center.getZ() - cLoc.getZ(), 2))) / 2;
		return radius <= size;
		*/

		double x = abs(center.getX() - loc.getX());
		double y = abs(center.getY() - loc.getY());
		double z = abs(center.getZ() - loc.getZ());
/*
		return ellipticRadius(z,
				ellipticRadius(
						x,
						y,
						size + (offsetVector == PlaneDirection.X ? offset : 0),
						size + (offsetVector == PlaneDirection.Y ? offset : 0)
				),
				size + (offsetVector == PlaneDirection.Z ? offset : 0),
				ellipticRadius(
						x,
						y,
						size + (offsetVector == PlaneDirection.X ? offset : 0),
						size + (offsetVector == PlaneDirection.Y ? offset : 0)
				)
		) <= size;
		*/

		double xz = ellipticRadius(x, z, size + (offsetVector == PlaneDirection.X ? offset : 0), size + (offsetVector == PlaneDirection.Z ? offset : 0));

		return sqrt(pow(x, 2) + pow(y, 2) + pow(z, 2)) <= ellipticRadius(xz, y, xz, size + (offsetVector == PlaneDirection.Y ? offset : 0));
	}

	/**
	 * Not actually elliptic, but works.
	 */
	public static double ellipticRadius(double x, double y, double mX, double mY) {
		return ellipticRadius(atan2(y, x), mX, mY);
	}

	/**
	 * Not actually elliptic, but works.
	 */
	public static double ellipticRadius(double theta, double mX, double mY) {
		return (mX * mY) / sqrt(pow(mX * sin(theta), 2) + pow(mY * cos(theta), 2));
	}

	public static Location getChunkCenter(World world, int chunkX, int chunkZ) {
		double x = chunkX * 16;
		double z = chunkZ * 16;

		x += x >= 0 ? 8 : -8;
		z += z >= 0 ? 8 : -8;

		return new Location(world, x, 64, z);
	}

	public static Location chunkCordsToLocation(World world, int chunkX, int chunkZ, double x, double y, double z) {
		double wX = (chunkX >= 0 ? chunkX : chunkX - 1) * 16 + x;
		double wZ = (chunkZ >= 0 ? chunkZ : chunkZ - 1) * 16 + z;

		return new Location(world, wX, y, wZ);
	}

	public static double randOfRange(double min, double max) {
		return min + Math.random() * (max - min);
	}

	public enum PlaneDirection {
		X, Y, Z;

		public static PlaneDirection random() {
			PlaneDirection[] v = values();
			return v[(int) (v.length * Math.random())];
		}
	}
}
