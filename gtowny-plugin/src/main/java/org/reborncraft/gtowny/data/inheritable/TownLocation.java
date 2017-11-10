package org.reborncraft.gtowny.data.inheritable;


import org.bukkit.Bukkit;
import org.bukkit.Location;

public class TownLocation {
	private int x;
	private int y;
	private int z;
	private double pitch;
	private double yaw;
	private String world;

	public TownLocation(Location loc) {
		this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getPitch(), loc.getYaw(), loc.getWorld().getName());
	}

	public TownLocation(int x, int y, int z, double pitch, double yaw, String world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.world = world;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public String getWorld() {
		return world;
	}

	public Location getLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	public void setLocation(Location loc) {
		setLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getPitch(), loc.getYaw(), loc.getWorld().getName());
	}

	public void setLocation(int x, int y, int z, float pitch, float yaw, String world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.world = world;
	}

	public double getPitch() {
		return pitch;
	}

	public double getYaw() {
		return yaw;
	}

	@Override
	public String toString() {
		return "TownLocation[x=" + x + ",y=" + y + ",z=" + z + ",pitch=" + pitch + ",yaw=" + yaw + ",world=" + world + "]";
	}
}
