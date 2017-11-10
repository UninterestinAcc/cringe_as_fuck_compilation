/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.api.core.functions;

import org.bukkit.entity.Player;

public interface IControllable extends IMoveableBlockCollection, ILocatable {
	void bindSteeringToPlayer(Player p);

	void unbindSteering();

	void steer();
}
