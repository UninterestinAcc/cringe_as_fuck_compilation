package org.reborncraft.gtowny.data.local;


import org.bukkit.Location;

import static java.lang.Math.abs;

public final class ChunkLocation {
	private final int x;
	private final int z;

	public ChunkLocation(int x, int z) throws IllegalStateException {
		this.x = x;
		this.z = z;
	}

	@Override
	public boolean equals(Object anotherObject) {
		return anotherObject instanceof ChunkLocation && this.hashCode() == anotherObject.hashCode();
	}

	@Override
	// Guaranteed unique up to 32767 in both directions and extremes, unpredictable and returns 2^31-1 afterwards.
	public int hashCode() {
		if (abs(x) >= 16384 || abs(z) >= 16384) {
			return Integer.MAX_VALUE;
		}
		return ((abs(x) + ((x > 0 ? 1 : 0) << 14)) << 15) + abs(z) + ((z > 0 ? 1 : 0) << 14);
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public static ChunkLocation forWorldCords(int x, int z) {
		return new ChunkLocation((x + (x > 0 ? 14 : -14)) / 29, (z + (z > 0 ? 14 : -14)) / 29);
	}

	public static ChunkLocation forLocation(Location location) {
		return ChunkLocation.forWorldCords(location.getBlockX(), location.getBlockZ());
	}

	@Override
	public String toString() {
		return "ChunkLocation[x=" + x + ",z=" + z + ",hash=" + hashCode() + "]";
	}
}
