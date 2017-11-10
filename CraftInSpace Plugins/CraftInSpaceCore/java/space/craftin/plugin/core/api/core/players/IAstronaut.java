/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.api.core.players;

import org.bukkit.OfflinePlayer;
import space.craftin.plugin.core.api.faction.IFaction;
import space.craftin.plugin.core.api.physical.objects.IBlock;

import java.util.UUID;

public interface IAstronaut {
	IFaction getFaction();

	boolean setFaction(IFaction faction);

	UUID getUuid();

	OfflinePlayer getPlayer();

	double getGainRate();

	void setGainRate(double rate);

	double getEnergy();

	void setEnergy(double energy);

	boolean depositEnergy(double energyAmount);

	boolean withdrawEnergy(double energyAmount);

	int getParticlesQuality();

	void setParticlesQuality(int quality);

	default boolean canDestroy(IBlock block) {
		return true; // Plan fix.
	}

	long getFactionSnowflake();

	int getFactionRank();

	void setFactionRank(int rank);

	int getAllianceRank();

	void setAllianceRank(int rank);
}
