/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.api.faction;

import space.craftin.plugin.core.api.core.functions.IClaimable;
import space.craftin.plugin.core.api.core.functions.IIdentifiableByName;
import space.craftin.plugin.core.api.core.players.IAstronaut;
import space.craftin.plugin.core.api.physical.station.IStation;
import space.craftin.plugin.core.api.physical.vehicles.IDrone;
import space.craftin.plugin.core.api.physical.vehicles.IShip;

import java.util.List;

public interface IFaction extends IIdentifiableByName {
	String getDescription();

	void setDescription(String desc);

	IAstronaut getChief();

	void setChief(IAstronaut chief);

	List<IAstronaut> getAstronauts();

	void addAstronaut(IAstronaut warrior);

	void removeAstronaut(IAstronaut warrior);

	List<? extends IClaimable> getClaims();

	void claim(IClaimable claim);

	void declaim(IClaimable claim);

	List<IDrone> getDrones();

	List<IShip> getSpaceships();

	List<IStation> getStations();

	void promote(IAstronaut astronaut);

	void demote(IAstronaut astronaut);

	class FactionRank {
		public static final int CHIEF = 2;
		public static final int MOD = 1;
		public static final int MEMBER = 0;
	}
}
