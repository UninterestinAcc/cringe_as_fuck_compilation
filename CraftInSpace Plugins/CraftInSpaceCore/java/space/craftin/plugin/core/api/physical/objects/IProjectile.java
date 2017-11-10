/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.api.physical.objects;

import space.craftin.plugin.core.api.core.functions.ILocatable;
import space.craftin.plugin.core.api.core.functions.IMoveable;

public interface IProjectile extends ILocatable, IMoveable {
	ILocatable getSource();

	ILocatable getTarget();

	double getPower();

	IProjectileType getType();

	enum IProjectileType {
		LASER, MISSILE
	}
}
