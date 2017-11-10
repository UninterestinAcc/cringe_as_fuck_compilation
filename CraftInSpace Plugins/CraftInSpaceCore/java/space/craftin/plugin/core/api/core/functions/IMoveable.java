/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.api.core.functions;

import org.bukkit.util.Vector;

public interface IMoveable extends ILocatable {
	Vector getExpectedVelocity();

	Vector getVelocity();

	void setVelocity(Vector vec);
}
