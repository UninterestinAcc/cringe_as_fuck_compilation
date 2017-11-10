package org.reborncraft.gtowny.data;


import org.bukkit.Location;
import org.reborncraft.gtowny.data.inheritable.TownLocation;

public final class TownSpawn extends TownLocation {
	public TownSpawn(Location loc) {
		super(loc);
	}

	public TownSpawn(int x, int y, int z, double pitch, double yaw, String world) {
		super(x, y, z, pitch, yaw, world);
	}
}
