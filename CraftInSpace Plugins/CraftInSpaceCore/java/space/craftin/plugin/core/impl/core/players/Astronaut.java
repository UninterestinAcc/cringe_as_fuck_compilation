/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.impl.core.players;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import space.craftin.plugin.core.CraftInSpace;
import space.craftin.plugin.core.api.alliance.IAlliance;
import space.craftin.plugin.core.api.core.players.IAstronaut;
import space.craftin.plugin.core.api.faction.IFaction;
import space.craftin.plugin.core.impl.core.functions.Datagram;

import java.util.Map;
import java.util.UUID;

public class Astronaut extends Datagram implements IAstronaut {

	private String uuid;
	private int particlesQuality;
	private double gainRate;
	private long factionSnowflake;
	private double energy;
	private int factionRank;
	private int allianceRank;

	public Astronaut(Map<String, Object> deserialize) {
		super(deserialize);
	}

	public Astronaut(UUID uuid) {
		this.uuid = uuid.toString();
		particlesQuality = 1;
		gainRate = 1;
		factionSnowflake = 0;
		energy = 1;
		factionRank = 0;
		allianceRank = 0;
	}

	@Override
	public IFaction getFaction() {
		return CraftInSpace.getInstance().getFaction(factionSnowflake);
	}

	@Override
	public boolean setFaction(IFaction faction) {
		if (factionRank == IFaction.FactionRank.CHIEF || allianceRank == IAlliance.AllianceRank.COMMANDER) {
			return false;
		}
		factionSnowflake = faction != null ? faction.getSnowflake() : 0;
		factionRank = IFaction.FactionRank.MEMBER;
		allianceRank = IAlliance.AllianceRank.MEMBER;
		return true;
	}

	@Override
	public UUID getUuid() {
		return UUID.fromString(uuid);
	}

	@Override
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}

	@Override
	public double getGainRate() {
		return gainRate;
	}

	@Override
	public void setGainRate(double rate) {
		gainRate = rate;
	}

	@Override
	public double getEnergy() {
		return energy;
	}

	@Override
	public void setEnergy(double energy) {
		this.energy = energy;
	}

	@Override
	public boolean depositEnergy(double energyAmount) {
		if (Double.MAX_VALUE - energy < Math.abs(energyAmount)) {
			return false;
		}
		energy += energyAmount;
		return true;
	}

	@Override
	public boolean withdrawEnergy(double energyAmount) {
		if (Math.abs(energyAmount) > energy) {
			return false;
		}
		energy -= energyAmount;
		return true;
	}

	@Override
	public int getParticlesQuality() {
		return particlesQuality;
	}

	@Override
	public void setParticlesQuality(int quality) {
		particlesQuality = quality;
	}

	@Override
	public long getFactionSnowflake() {
		return factionSnowflake;
	}

	@Override
	public int getFactionRank() {
		return factionRank;
	}

	@Override
	public void setFactionRank(int rank) {
		factionRank = rank;
	}

	@Override
	public int getAllianceRank() {
		return allianceRank;
	}

	@Override
	public void setAllianceRank(int rank) {
		allianceRank = rank;
	}
}
