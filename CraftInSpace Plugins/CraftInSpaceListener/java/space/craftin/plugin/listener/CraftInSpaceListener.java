/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import space.craftin.plugin.core.CraftInSpace;
import space.craftin.plugin.core.api.IListener;
import space.craftin.plugin.core.api.core.players.IAstronaut;
import space.craftin.plugin.core.api.physical.objects.IBlock;
import space.craftin.plugin.core.api.physical.vehicles.IVehicle;
import space.craftin.plugin.core.impl.physical.block.Block;
import space.craftin.plugin.utils.VectorUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CraftInSpaceListener implements IListener {
	private Map<Player, Location> lastValid = new HashMap<>();

	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		if (e.getPlayer().getName().toLowerCase().startsWith(IBlock.NAME_PREFIX.toLowerCase())) {
			e.setResult(PlayerLoginEvent.Result.KICK_BANNED);
			e.setKickMessage("Your name starts with a reserved name range: " + IBlock.NAME_PREFIX + ".");
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) { // Plan ...
		if (e.getTo().getY() < -16 || e.getTo().getY() > 272) {
			final Location validLoc = lastValid.get(e.getPlayer());
			validLoc.setPitch(e.getTo().getPitch());
			validLoc.setYaw(e.getTo().getYaw());
			e.getPlayer().teleport(validLoc);
			e.getPlayer().setVelocity(new Vector(0, 0, 0));
		} else {
			lastValid.put(e.getPlayer(), e.getTo());
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!p.hasPlayedBefore()) {
			p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		}
		p.setAllowFlight(true);
		p.setFlying(true);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) { // Garbage cleaner
		if (lastValid.containsKey(e.getPlayer())) {
			lastValid.remove(e.getPlayer());
		}
	}

	private Location retrieveLocation(Player p, AtomicReference<ArmorStand> as) {
		Location aimedLoc = VectorUtil.shiftLocation(p.getEyeLocation(), p.getEyeLocation().distance(as.get().getEyeLocation()));
		if (aimedLoc.distanceSquared(as.get().getEyeLocation()) > 1.5 * 3 * Math.pow(IBlock.HEAD_WIDTH / 2, 2)) {
			Optional<ArmorStand> opt = CraftInSpace.getNearbyArmorStands(p, 6).stream()
					.sorted((stand1, stand2) -> {
						double ps1Dist = p.getEyeLocation().distance(stand1.getEyeLocation());
						double ps2Dist = p.getEyeLocation().distance(stand2.getEyeLocation());

						double ps1Accuracy = stand1.getEyeLocation().distanceSquared(
								VectorUtil.shiftLocation(
										p.getEyeLocation(),
										ps1Dist
								)
						);
						double ps2Accuracy = stand2.getEyeLocation().distanceSquared(
								VectorUtil.shiftLocation(
										p.getEyeLocation(),
										ps2Dist
								)
						);
						if (Math.abs(ps1Accuracy - ps2Accuracy) < Math.pow(IBlock.HEAD_WIDTH, 2) * 3) {
							return ps1Accuracy > ps2Accuracy ? 1 : -1;
						}
						return ps1Dist > ps2Dist ? 1 : -1;
					}).findFirst();
			if (opt.isPresent()) {
				as.set(opt.get());
				aimedLoc = VectorUtil.shiftLocation(p.getEyeLocation(), p.getEyeLocation().distance(as.get().getEyeLocation()));
			}
		}
		if (aimedLoc.distanceSquared(as.get().getEyeLocation()) <= Math.pow(IBlock.HEAD_WIDTH / 2, 2) * 3) {
			return VectorUtil.getAimedLocation(p, as.get());
		}
		return null;
	}

	@EventHandler
	public void shipAddBlock(PlayerInteractAtEntityEvent e) {
		if (e.getRightClicked() instanceof ArmorStand) {
			e.setCancelled(true);
			Player p = e.getPlayer();
			final IAstronaut astronaut = CraftInSpace.getInstance().getOrCreateAstronaut(p);
			final ItemStack itemInHand = e.getPlayer().getItemInHand();
			if (itemInHand != null && itemInHand.getAmount() > 0) {
				IBlock.BlockType bt = IBlock.BlockType.forItem(itemInHand);
				if (bt != null) {
					AtomicReference<ArmorStand> as = new AtomicReference<>((ArmorStand) e.getRightClicked());
					Location placeLoc = retrieveLocation(p, as);
					Optional<IVehicle> vehicle = CraftInSpace.getInstance().searchForVehicle(v -> v.hasBlock(new Block(as.get())), "ships", "drones");
					if (placeLoc != null && vehicle.isPresent()) {
						decrementStackNumber(p);
						if (vehicle.get().getFactionSnowflake() == astronaut.getFactionSnowflake()) {
							vehicle.get().getBlocks().add(new Block(bt, placeLoc));
						}
					}
				}
			}
		}
	}

	private void decrementStackNumber(Player p) {
		final ItemStack is = p.getItemInHand();
		is.setAmount(is.getAmount() - 1);
		p.setItemInHand(is);
	}

	@EventHandler
	public void shipRemoveBlock(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof ArmorStand) {
			e.setCancelled(true);
			Player p = (Player) e.getDamager();
			final IAstronaut astronaut = CraftInSpace.getInstance().getOrCreateAstronaut(p);

			AtomicReference<ArmorStand> asRef = new AtomicReference<>((ArmorStand) e.getEntity());
			retrieveLocation(p, asRef); // Confirms the ArmorStand

			final ArmorStand as = asRef.get();
			Optional<IVehicle> vehicle = CraftInSpace.getInstance().searchForVehicle(v -> v.hasBlock(new Block(as)), "ships", "drones");
			if (vehicle.isPresent()) {
				if (vehicle.get().getFactionSnowflake() == astronaut.getFactionSnowflake()) {
					IBlock.BlockType type = IBlock.BlockType.forItem(as.getHelmet());
					if (type == IBlock.BlockType.SHIP_CONTROLLER) {
						// TODO Confirm destroy ship
					} else if (type == IBlock.BlockType.DRONE_COMPUTER) {
						// TODO Confirm destroy drone
					} else {
						vehicle.get().removeBlock(new Block(as));
					}
				} else {
					return; // Not the person interacting's faction's property.
				}
			}
			e.getEntity().remove();
			// TODO Add Item to Inventory
		}
	}
}
