/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.impl.physical.block;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.EulerAngle;
import space.craftin.plugin.core.api.core.functions.NoSerialization;
import space.craftin.plugin.core.api.physical.objects.IBlock;
import space.craftin.plugin.core.impl.core.functions.Datagram;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Block extends Datagram implements IBlock {
	private String uuid;
	private String worldName;
	@NoSerialization
	private ArmorStand ent;

	public Block(Map<String, Object> deserialize) {
		this(Bukkit.getWorld((String) deserialize.get("worldName")), (String) deserialize.get("uuid"));
	}

	public Block(BlockType block, Location loc) {
		this(block, loc, new EulerAngle(Math.toRadians(loc.getPitch()), 0, 0));
	}

	public Block(BlockType block, Location loc, EulerAngle ea) {
		ent = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		ent.setHelmet(block.getSkull());
		ent.setVisible(false);
		ent.setGravity(false);
		ent.setCustomNameVisible(false);
		ent.setHeadPose(ea);
		this.worldName = ent.getWorld().getName();
		this.uuid = ent.getUniqueId().toString();
	}

	public Block(ArmorStand entity) {
		ent = entity;
		this.uuid = ent.getUniqueId().toString();
		this.worldName = ent.getWorld().getName();
	}

	public Block(World world, String uuid) throws IllegalArgumentException {
		if (world != null) {
			UUID realUUID = UUID.fromString(uuid);
			Optional<ArmorStand> armorStand = world.getEntitiesByClass(ArmorStand.class).parallelStream().filter(as -> as.getUniqueId().equals(realUUID)).findAny();
			if (armorStand.isPresent()) {
				ent = armorStand.get();
				this.worldName = world.getName();
				this.uuid = realUUID.toString();
			}
		}
	}

	@Override
	public void delete() {
		ent.remove();
	}

	@Override
	public Location getLocation() {
		return ent.getLocation();
	}

	@Override
	public ArmorStand getEntity() {
		return ent;
	}

	@Override
	public BlockType getType() {
		return BlockType.forItem(ent.getHelmet());
	}
}
