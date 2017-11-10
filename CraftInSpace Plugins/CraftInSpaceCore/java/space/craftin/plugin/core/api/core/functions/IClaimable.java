/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.api.core.functions;

import space.craftin.plugin.core.api.faction.IFaction;

public interface IClaimable extends ILocatable, IIdentifiable {
	long getFactionSnowflake();

	IFaction getFaction();

	void claim(IFaction faction);

	void detatchFromFaction();

	double getClaimRadius();

	void setClaimRadius(double r);
}
