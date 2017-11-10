/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.impl.physical.station;

import org.bukkit.Location;
import space.craftin.plugin.core.CraftInSpace;
import space.craftin.plugin.core.api.faction.IFaction;
import space.craftin.plugin.core.api.physical.station.IStation;
import space.craftin.plugin.core.impl.core.functions.Datagram;

import java.util.Map;

import static com.sun.deploy.util.SessionState.save;

// Station does not have block association as all blocks are static and station is ran based on radius.
public class Station extends Datagram implements IStation {
	private long snowflake;
	private Location center;
	private long factionSnowflake;
	private double claimRadius;

	public Station(Map<String, Object> deserialize) {
		super(deserialize);
	}

	@Override
	public void claim(IFaction faction) {
		this.factionSnowflake = faction.getSnowflake();
	}

	@Override
	public void detatchFromFaction() {
		this.factionSnowflake = 0;
		save();
	}

	@Override
	public long getSnowflake() {
		return snowflake;
	}

	@Override
	public Location getLocation() {
		return center;
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
	public double getClaimRadius() {
		return claimRadius;
	}

	@Override
	public void setClaimRadius(double r) {
		claimRadius = r;
		save();
	}
}
