/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.impl.physical.vehicles;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import space.craftin.plugin.core.CraftInSpace;
import space.craftin.plugin.core.api.core.functions.NoSerialization;
import space.craftin.plugin.core.api.faction.IFaction;
import space.craftin.plugin.core.api.physical.objects.IBlock;
import space.craftin.plugin.core.api.physical.vehicles.IShip;
import space.craftin.plugin.core.impl.core.functions.Datagram;
import space.craftin.plugin.core.impl.core.utils.AxisAlignedBB;
import space.craftin.plugin.core.impl.physical.block.Block;
import space.craftin.plugin.utils.VectorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Ship extends Datagram implements IShip {
	private long snowflake;
	private Location loc;
	private List<IBlock> blocks;
	private double radius;
	private double pitch;
	private double yaw;
	private double strength;
	private long factionSnowflake;
	@NoSerialization
	private Player boundPlayer;

	public Ship(Map<String, Object> deserialize) {
		super(deserialize);
	}

	public Ship(long snowflake, Player p, long factionSnowflake) {
		this.snowflake = snowflake;
		this.loc = VectorUtil.shiftLocation(p.getEyeLocation(), 4).subtract(0, IBlock.EYE_HEIGHT, 0);
		this.blocks = new ArrayList<>();
		getBlocks().add(new Block(IBlock.BlockType.SHIP_CONTROLLER, loc));
		this.radius = 8;
		this.pitch = 0;
		this.yaw = 0;
		this.strength = 0;
		this.factionSnowflake = factionSnowflake;
	}

	@Override
	public long getSnowflake() {
		return snowflake;
	}

	@Override
	public Location getLocation() {
		return loc;
	}

	@Override
	public long getFactionSnowflake() {
		return factionSnowflake;
	}

	@Override
	public IFaction getFaction() {
		return CraftInSpace.getInstance().getFaction(factionSnowflake);
	}

	@Override
	public void claim(IFaction faction) {
		this.factionSnowflake = faction.getSnowflake();
	}

	@Override
	public void detatchFromFaction() {
		this.factionSnowflake = 0;
	}

	@Override
	public double getClaimRadius() {
		return radius;
	}

	@Override
	public void setClaimRadius(double r) {
		radius = r;
	}

	@Override
	public Vector getExpectedVelocity() {
		return VectorUtil.draw(pitch, yaw, strength);
	}

	@Override
	public Vector getVelocity() {
		return VectorUtil.draw(pitch, yaw, strength);
	}

	@Override
	public void setVelocity(Vector vec) {
		pitch = VectorUtil.getPitch(vec);
		yaw = VectorUtil.getYaw(vec);
		strength = VectorUtil.strength(vec);
	}

	@Override
	public void bindSteeringToPlayer(Player p) { // TODO Steering
		this.boundPlayer = p;
	}

	@Override
	public void unbindSteering() {
		this.boundPlayer = null;
	}

	@Override
	public void steer() {
		if (this.boundPlayer != null) {
			int totalBlocks = blocks.size();
			long gyroscopes = blocks.stream().filter(blocks -> blocks.getType() == IBlock.BlockType.GYROSCOPE).count();
			double ratio = Math.abs(totalBlocks / (gyroscopes * 30));
			ratio = (ratio >= 1 ? 1 : ratio <= 0 ? 0 : ratio);
			double steerPitch = boundPlayer.getEyeLocation().getPitch() - pitch * ratio;
			double steerYaw = boundPlayer.getEyeLocation().getYaw() - yaw * ratio;
			this.pitch += steerPitch;
			this.yaw += steerYaw;
		}
	}

	@Override
	public List<IBlock> getBlocks() {
		return blocks;
	}

	@Override
	public AxisAlignedBB getRestrictionAABB() {
		return new AxisAlignedBB(loc, radius);
	}
}