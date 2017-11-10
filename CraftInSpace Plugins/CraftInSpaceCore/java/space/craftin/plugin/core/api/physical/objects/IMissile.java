/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.api.physical.objects;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import space.craftin.plugin.core.api.core.functions.IMoveableBlockCollection;


public interface IMissile extends IProjectile, IMoveableBlockCollection {
	IMissile addStage(IBlock.BlockType type);

	default IMissile addStage(IBlock.BlockType... types) {
		if (types != null && types.length > 0) {
			for (IBlock.BlockType stage : types) {
				addStage(stage);
			}
		}
		return this;
	}

	IMissile depleteLast();

	default IProjectileType getType() {
		return IProjectileType.MISSILE;
	}

	default double getPower() {
		return 0;
	}

	Location getLaunchLocation();

	void playParticles();

	void fire();

	boolean isFired();

	Vector getVelocity();

	default void setVelocity(Vector vec) {
		throw new IllegalStateException("Missile velocities are controlled internally!");
	}

	boolean hasExploded();
}
