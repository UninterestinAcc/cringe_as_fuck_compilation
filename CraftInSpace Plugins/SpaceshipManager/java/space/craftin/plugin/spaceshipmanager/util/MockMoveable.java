/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.spaceshipmanager.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import space.craftin.plugin.core.api.core.functions.IMoveableBlockCollection;
import space.craftin.plugin.core.api.physical.objects.IBlock;
import space.craftin.plugin.core.impl.core.utils.AxisAlignedBB;

import java.util.List;

public class MockMoveable implements IMoveableBlockCollection {
	private final Location loc;
	private List<IBlock> blocks;

	public MockMoveable(Player p, List<IBlock> blocks) {
		this.loc = p.getLocation();
		this.blocks = blocks;
	}

	@Override
	public Location getLocation() {
		return loc;
	}

	@Override
	public Vector getExpectedVelocity() {
		return getVelocity();
	}

	@Override
	public Vector getVelocity() {
		return new Vector(0.1, 0.1, 0.1);
	}

	@Override
	public void setVelocity(Vector vec) {

	}

	@Override
	public List<IBlock> getBlocks() {
		return blocks;
	}

	@Override
	public AxisAlignedBB getRestrictionAABB() {
		return new AxisAlignedBB(loc, 8);
	}
}
