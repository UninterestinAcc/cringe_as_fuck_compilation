/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.weapons.impl;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import space.craftin.plugin.core.CraftInSpace;
import space.craftin.plugin.core.api.core.functions.ILocatable;
import space.craftin.plugin.core.api.physical.objects.IBlock;
import space.craftin.plugin.core.api.physical.objects.IMissile;
import space.craftin.plugin.core.impl.core.utils.AxisAlignedBB;
import space.craftin.plugin.core.impl.physical.block.Block;
import space.craftin.plugin.utils.VectorUtil;
import space.craftin.plugin.weapons.WeaponsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Missile implements IMissile {
	private final List<IBlock> stages = new ArrayList<>();
	private final Location launchLoc;
	private Location loc;
	private ILocatable source;
	private ILocatable target;
	private boolean fired = false;
	private boolean exploded = false;
	private int ticks;

	public Missile(Location loc, ILocatable source, ILocatable target) {
		this.loc = loc;
		this.launchLoc = loc.clone();
		this.source = source;
		this.target = target;
		updateLocation();
	}

	private Location updateLocation() {
		Location loc = stages.size() > 0 ? stages.get(0).getLocation().add(0, IBlock.EYE_HEIGHT, 0) : this.loc;
		Vector targetVec = VectorUtil.draw(loc, target.getLocation());
		loc.setPitch((float) Math.toDegrees(VectorUtil.getPitch(targetVec)));
		loc.setYaw((float) Math.toDegrees(VectorUtil.getYaw(targetVec)));
		this.loc = loc;
		return loc;
	}

	@Override
	public Vector getExpectedVelocity() {
		return VectorUtil.setForce(VectorUtil.draw(getCurrentStage().getLocation(), target.getLocation()), 1);
	}

	private Location getRelativeLocation(int order) {
		return shiftLocation(getLocation().clone(), order * -IBlock.HEAD_WIDTH).subtract(0, IBlock.EYE_HEIGHT, 0);
		// return loc.add(VectorUtil.draw(Math.toRadians(loc.getPitch() + 90), Math.toRadians(loc.getYaw() + 90), order * -IBlock.HEAD_WIDTH)).subtract(0, IBlock.EYE_HEIGHT, 0);
		// return loc.add(VectorUtil.draw(Math.toRadians(loc.getPitch() + 90) + Math.PI * 1.5, Math.toRadians(loc.getYaw() + 90) - Math.PI / 2, order * -IBlock.HEAD_WIDTH)).subtract(0, IBlock.EYE_HEIGHT, 0);
	}

	private Location shiftLocation(Location loc, double force) {
		return loc.add(VectorUtil.draw(Math.toRadians(loc.getPitch()), Math.toRadians(loc.getYaw()), force));
	}


	@Override
	public void playParticles() {
		getCurrentStage().playParticle(VectorUtil.setForce(getVelocity(), 5).multiply(-1), Effect.LAVA_POP);
	}

	@Override
	public void fire() {
		WeaponsManager.getInstance().registerMissile(this);
		fired = true;
	}

	@Override
	public boolean isFired() {
		return fired;
	}

	@Override
	public Vector getVelocity() {
		return fired ? getExpectedVelocity() : new Vector(0, 0, 0);
	}

	@Override
	public boolean hasExploded() {
		return exploded || stages.stream().filter(b -> !b.getEntity().isValid()).findAny().isPresent();
	}

	@Override
	public IMissile addStage(IBlock.BlockType type) {
		IBlock stage = new Block(type, getRelativeLocation(stages.size()));

		stage.getEntity().setGravity(false);

		Vector targetVec = VectorUtil.draw(loc, getTarget().getLocation());
		stage.getEntity().setHeadPose(new EulerAngle(VectorUtil.getPitch(targetVec), 0, 0));
		stages.add(stage);
		return this;
	}

	@Override
	public IMissile depleteLast() {
		IBlock b = getCurrentStage();
		stages.remove(b);
		b.delete();
		if (stages.size() <= 1) {
			explode();
		}
		return this;
	}

	@Override
	public void move() {
		updateLocation();
		Vector targetVec = VectorUtil.draw(loc, getTarget().getLocation());
		VectorUtil.setForce(targetVec, 1);
		loc = loc.add(targetVec);

		EulerAngle e = new EulerAngle(VectorUtil.getPitch(targetVec), 0, 0);
		AtomicInteger index = new AtomicInteger(0);
		stages.forEach(stage -> {
			ArmorStand as = stage.getEntity();
			as.teleport(getRelativeLocation(index.getAndIncrement()));
			as.setHeadPose(e);
		});

		if (++ticks % 50 == 0) {
			depleteLast();
		}

		if (Math.random() < 0.25) {
			playParticles();
		}

		IBlock.BlockType mt = stages.get(0).getType();
		if (mt == IBlock.BlockType.TRIDENT_WARHEAD && loc.distanceSquared(getTarget().getLocation()) < 400) { // Which means 20 blocks away, and I am not square rooting because performance.
			for (int i = 0; i < 3; i++) {
				new Missile(loc.clone().add(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1), source, target).addStage(IBlock.BlockType.TRIDENT_PAYLOAD).fire();
			}
		}
		if (loc.distanceSquared(getTarget().getLocation()) < 4 || checkBlocksNearby()) { // Which means 2 blocks away, and I am not square rooting because performance.
			explode();
		}
	}

	private void explode() {
		CraftInSpace.getInstance().playMissileExplodingParticles(getLocation().add(0, IBlock.EYE_HEIGHT, 0));
		stages.forEach(IBlock::delete);
		exploded = true;
	}

	// Plan Cause destruction with WorldGuard integration.

	private boolean checkBlocksNearby() {
		boolean nearby = loc.getWorld().getNearbyEntities(loc, 2, 3, 2).stream().filter(e -> e.getType() == EntityType.ARMOR_STAND && e.getLocation().add(0, IBlock.EYE_HEIGHT, 0).distanceSquared(loc) < 4).count() > stages.size();
		if (nearby) {
			return true;
		}
		for (int x = -2; x <= 2; x++) {
			for (int y = -2; y <= 2; y++) {
				for (int z = -2; z <= 2; z++) {
					Location l = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
					if (l.getBlock().getType() != Material.AIR) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public Location getLaunchLocation() {
		return launchLoc;
	}

	@Override
	public ILocatable getSource() {
		return source;
	}

	@Override
	public ILocatable getTarget() {
		return target;
	}

	public IBlock getCurrentStage() {
		return stages.get(stages.size() - 1);
	}

	@Override
	public Location getLocation() {
		return loc;
	}

	@Override
	public List<IBlock> getBlocks() {
		return stages;
	}

	@Override
	public AxisAlignedBB getRestrictionAABB() {
		return new AxisAlignedBB(loc, 1);
	}
}
