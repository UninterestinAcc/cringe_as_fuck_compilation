package space.craftin.plugin.core.api.core.functions;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import space.craftin.plugin.core.api.physical.objects.IBlock;
import space.craftin.plugin.core.impl.core.utils.AxisAlignedBB;
import space.craftin.plugin.utils.VectorUtil;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface IMoveableBlockCollection extends IMoveable {
	AxisAlignedBB getRestrictionAABB();

	List<IBlock> getBlocks();

	default void move() {
		Location loc = getLocation();
		final Location originalLoc = loc.clone();

		VectorUtil.shiftLocation(loc, VectorUtil.strength(getExpectedVelocity()));
		loc.setYaw(loc.getYaw() - 1);

		double pitchDiff = Math.toRadians(originalLoc.getPitch() - loc.getPitch());
		double yawDiff = Math.toRadians(originalLoc.getYaw() - loc.getYaw());

		EulerAngle ea = new EulerAngle(Math.toRadians(loc.getPitch()), 0, 0);
		getBlocks().forEach(block -> {
			Location blockLoc = block.getEntity().getLocation();
			Vector vec = VectorUtil.draw(blockLoc, originalLoc);
			Vector newVec = VectorUtil.rotate(vec, -pitchDiff, -yawDiff);

			block.teleport(loc.clone().subtract(newVec));
			block.getEntity().setHeadPose(ea);
		});
	}

	default boolean hasBlock(IBlock b) {
		return getBlocks().parallelStream().filter(tb -> tb.getEntity().getUniqueId().equals(b.getEntity().getUniqueId())).findFirst().isPresent();
	}

	default void removeBlock(IBlock b) {
		UUID uuid = b.getEntity().getUniqueId();
		final List<IBlock> blocks = getBlocks();
		blocks.remove(blocks.stream().filter(block -> {
			final ArmorStand as = block.getEntity();
			return as == null || !as.isValid() || as.getUniqueId().equals(uuid);
		}).collect(Collectors.toList()));
	}
}
