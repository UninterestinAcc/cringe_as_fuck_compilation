package org.reborncraft.gtowny.data;


import org.bukkit.Location;
import org.reborncraft.gtowny.data.inheritable.TownLocation;

public final class TownWarp extends TownLocation {
	private final String name;

	public TownWarp(String name, Location loc) {
		this(name, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getPitch(), loc.getYaw(), loc.getWorld().getName());
	}

	public TownWarp(String name, int x, int y, int z, double pitch, double yaw, String world) {
		super(x, y, z, pitch, yaw, world);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "TownWarp[name=" + name + "]+" + super.toString();
	}
}
