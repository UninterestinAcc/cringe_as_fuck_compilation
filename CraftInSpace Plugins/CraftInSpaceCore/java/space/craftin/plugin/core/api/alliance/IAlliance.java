/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.api.alliance;

import space.craftin.plugin.core.api.core.functions.IIdentifiableByName;
import space.craftin.plugin.core.api.core.players.IAstronaut;
import space.craftin.plugin.core.api.faction.IFaction;

import java.util.List;

public interface IAlliance extends IIdentifiableByName {
	String getDescription();

	void setDescription(String desc);

	IAstronaut getCommander();

	void setCommander(IAstronaut commander);

	List<IFaction> getFactions();

	boolean addFaction(IFaction faction);

	boolean removeFaction(IFaction faction);

	void promote(IAstronaut astronaut);

	void demote(IAstronaut astronaut);


	class AllianceRank {
		public static final int COMMANDER = 2;
		public static final int MOD = 1;
		public static final int MEMBER = 0;
	}
}
