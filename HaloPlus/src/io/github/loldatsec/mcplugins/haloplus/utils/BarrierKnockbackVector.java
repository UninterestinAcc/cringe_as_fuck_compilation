package io.github.loldatsec.mcplugins.haloplus.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BarrierKnockbackVector {

	private Location location;
	private double power = 1;

	public BarrierKnockbackVector(Location l) {
		this.location = l;
	}

	public BarrierKnockbackVector setPower(double power) {
		this.power = power;
		return this;
	}

	public Vector toVector() {
		Location spawn = location.getWorld().getWorldBorder().getCenter();
		double wb = spawn.getWorld().getWorldBorder().getSize();
		double x = spawn.getX() - location.getX();
		double z = spawn.getZ() - location.getZ();
		x = x / wb * 3;
		z = z / wb * 3;
		Vector vel = new Vector(x, 0.7, z);
		vel.multiply(power);
		return vel;
	}
}
