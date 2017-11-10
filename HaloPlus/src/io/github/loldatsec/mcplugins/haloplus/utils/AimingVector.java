package io.github.loldatsec.mcplugins.haloplus.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class AimingVector {

	private Location location;
	private double power = 1;

	public AimingVector(Location l) {
		this.location = l;
	}

	public AimingVector setPower(double power) {
		this.power = power;
		return this;
	}

	public Vector toVector() {
		double pitch = ((location.getPitch() + 90) * Math.PI) / 180;
		double yaw = ((location.getYaw() + 90) * Math.PI) / 180;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.sin(pitch) * Math.sin(yaw);
		double z = Math.cos(pitch);
		Vector vel = new Vector(x, z, y);
		vel.multiply(power);
		return vel;
	}

	public Vector toOppositeVector(Vector v) {
		double pitch = ((location.getPitch() - 90) * Math.PI) / 180;
		double yaw = ((location.getYaw() + 90) * Math.PI) / 180;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.sin(pitch) * Math.sin(yaw);
		double z = Math.cos(pitch);
		Vector vel = new Vector(x, z, y);
		vel.multiply(power);
		return vel;
	}
}
