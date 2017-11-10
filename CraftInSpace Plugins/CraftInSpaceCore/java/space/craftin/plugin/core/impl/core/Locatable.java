/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.impl.core;

import org.bukkit.Location;
import space.craftin.plugin.core.api.core.functions.ILocatable;

public class Locatable implements ILocatable {
	protected final Location loc;

	public Locatable(Location loc) {
		this.loc = loc;
	}

	@Override
	public Location getLocation() {
		return loc;
	}
}
