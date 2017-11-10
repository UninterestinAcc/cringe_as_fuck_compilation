/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.impl.physical.vehicles;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import space.craftin.plugin.core.CraftInSpace;
import space.craftin.plugin.core.api.faction.IFaction;
import space.craftin.plugin.core.api.physical.objects.IBlock;
import space.craftin.plugin.core.api.physical.vehicles.IDrone;
import space.craftin.plugin.core.impl.core.functions.Datagram;
import space.craftin.plugin.core.impl.core.utils.AxisAlignedBB;
import space.craftin.plugin.core.impl.physical.block.Block;
import space.craftin.plugin.utils.VectorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sun.deploy.util.SessionState.save;

public class Drone extends Datagram implements IDrone {
	private long snowflake;
	private DroneType type;
	private Location loc;
	private AxisAlignedBB operatingAABB;
	private double radius;
	private double pitch;
	private double yaw;
	private double strength;
	private long factionSnowflake;
	private List<IBlock> blocks;

	public Drone(Map<String, Object> deserialize) {
		super(deserialize);
	}

	public Drone(long snowflake, Player p, long factionSnowflake) {
		this.snowflake = snowflake;
		this.type = DroneType.GENERIC;
		this.loc = VectorUtil.shiftLocation(p.getEyeLocation(), 4);
		this.radius = 8;
		this.operatingAABB = new AxisAlignedBB(loc, radius);
		this.pitch = 0;
		this.yaw = 0;
		this.strength = 0;
		this.factionSnowflake = factionSnowflake;
		this.blocks = new ArrayList<>();
		getBlocks().add(new Block(IBlock.BlockType.DRONE_COMPUTER, loc));
	}

	@Override
	public long getSnowflake() {
		return snowflake;
	}

	@Override
	public Location getLocation() {
		return loc.clone();
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
	public void setVelocity(org.bukkit.util.Vector vec) {
		pitch = VectorUtil.getPitch(vec);
		yaw = VectorUtil.getYaw(vec);
		strength = VectorUtil.strength(vec);
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
		save();
	}

	@Override
	public void detatchFromFaction() {
		factionSnowflake = 0;
		save();
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
	public AxisAlignedBB getOperatingArea() {
		return operatingAABB;
	}

	@Override
	public boolean setOperatingArea(AxisAlignedBB aabb) {
		if (this.operatingAABB.intersectsWith(aabb)) {
			this.operatingAABB = aabb;
			return true;
		}
		return false;
	}

	@Override
	public DroneType getType() {
		return type;
	}

	@Override
	public void setType(DroneType type) {
		this.type = type;
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
